import com.sun.deploy.util.StringUtils;
import java.util.*;

enum  ValueType{
    NUMBER, WORD, LIST, BOOL, UNKNOWN, ERROR, SUCCESS
}

class Value {

    private ValueType type;
    private String content;
    public Value(ValueType _type, String _content) {
        type = _type;
        content = _content;
    }
    public ValueType type(){
        return type;
    }

    public String content(){
        return content;
    }
}

public class main {
    static Scanner systemScan = new Scanner(System.in);
    static Scanner scan;

    static Set<String> keyWord = new HashSet<>();
    static Set<String> arithmeticOperator = new HashSet<>();
    static Set<String> comparativeOperator = new HashSet<>();
    static Set<String> boolOperator = new HashSet<>();

    static Map<String, Value> nameSpace =new HashMap<>();
    static boolean isNumeric(String str) { return str.matches("-?\\d+(\\.?\\d+)?"); }
    static boolean isInterger(String str)
    {
        return str.matches("-?\\d+(\\.?0+)?");
    }
    static boolean isBool(String str)
    {
        return str.equals("true") || str.equals("false");
    }
    static int compare(String s1, String s2) {
        if(isNumeric(s1) && isNumeric(s2)) {
            return Double.compare(Double.parseDouble(s1), Double.parseDouble(s2));
        }
        else return s1.compareTo(s2);
    }
    static Value parse(String cur) {
        if (cur.equals("")){
            System.out.println("Expect more inputs");
            return new Value(ValueType.ERROR, null);
        }
        // is operator
        if (keyWord.contains(cur) || cur.startsWith(":")) {
            if (cur.equals("make")) {
                Value word = parse(scan.next());
                Value value = parse(scan.next());
                nameSpace.put(word.content(), value);
                return new Value(ValueType.SUCCESS, null);
            }
            else if (cur.equals("thing")) {
                Value word = parse(scan.next());
                if (!nameSpace.containsKey(word.content())) {
                    System.out.println(word.content() + " is not a variable!");
                    return new Value(ValueType.ERROR, null);
                }
                return nameSpace.get(word.content());
            }
            else if (cur.startsWith(":")) {
                String v = '"' + cur.substring(1);
                if (!nameSpace.containsKey(v)) {
                    System.out.println(v + " is not a variable!");
                    return new Value(ValueType.ERROR, null);
                }
                return nameSpace.get(v);
            }
            else if (cur.equals("read")) {
                return parse(systemScan.next());
            }
            else if (cur.equals("readlist")) {
                Scanner lscan = new Scanner(parse(systemScan.nextLine()).content());
                String list = "[";
                while (lscan.hasNext()) {
                    String tmp = lscan.next();
                    list = list + " " + tmp;
                }
                return new Value(ValueType.LIST, list + " ]");
            }
            else if (cur.equals("print")) {
                Value value = parse(scan.next());
                System.out.println(value.content());
                return new Value(ValueType.SUCCESS, null);
            }
            else if (cur.equals("isname")) {
                Value word = parse(scan.next());
                Boolean ret = nameSpace.containsKey(word.content());
                return new Value(ValueType.BOOL, ret.toString());
            }
            else if (cur.equals("erase")) {
                Value word = parse(scan.next());
                if (!nameSpace.containsKey(word.content())) {
                    System.out.println("Word wasn't bond to anything!");
                    return new Value(ValueType.ERROR, null);
                }
                nameSpace.remove(word.content());
                return new Value(ValueType.SUCCESS, null);
            }
            // is arithmetic operator
            else if(arithmeticOperator.contains(cur)) {
                Value value1 = parse(scan.next());
                Value value2 = parse(scan.next());
                if (value1.type() == ValueType.WORD) {
                    value1 = nameSpace.get(value1.content());
                }
                if (value2.type() == ValueType.WORD) {
                    value2 = nameSpace.get(value2.content());
                }
                if (!isNumeric(value1.content()) || !isNumeric(value2.content())) {
                    System.out.println("Not a number!");
                    return new Value(ValueType.ERROR, null);
                }

                if (cur.equals("add"))
                    return new Value(ValueType.NUMBER, Double.toString(Double.parseDouble(value1.content()) + Double.parseDouble(value2.content())));
                else if (cur.equals("sub"))
                    return new Value(ValueType.NUMBER, Double.toString(Double.parseDouble(value1.content()) - Double.parseDouble(value2.content())));
                else if (cur.equals("mul"))
                    return new Value(ValueType.NUMBER, Double.toString(Double.parseDouble(value1.content()) * Double.parseDouble(value2.content())));
                else if (cur.equals("div")) {
                    if (Math.abs(Double.parseDouble(value2.content())) < 1e-10) {
                        System.out.println("Dividing zero occurs!");
                        return new Value(ValueType.ERROR, null);
                    }
                    return new Value(ValueType.NUMBER, Double.toString(Double.parseDouble(value1.content()) / Double.parseDouble(value2.content())));
                } else if (cur.equals("mod")) {
                    if (!isInterger(value1.content()) || !isInterger(value2.content())) {
                        System.out.println("Not an integer!");
                        return new Value(ValueType.ERROR, null);
                    }
                    int v1 = (int)Double.parseDouble(value1.content());
                    int v2 = (int)Double.parseDouble(value2.content());
                    if (v2 == 0) {
                        System.out.println("Modulo zero occurs!");
                        return new Value(ValueType.ERROR, null);
                    }
                    return new Value(ValueType.NUMBER, Integer.toString(v1 % v2));
                }

            }
            else if(comparativeOperator.contains(cur)) {
                Value value1 = parse(scan.next());
                Value value2 = parse(scan.next());
                if(value1.type() != value2.type()) {
                    System.out.println("Not comparable!");
                    return new Value(ValueType.ERROR, null);
                }
                if(cur.equals("eq")) {
                    return new Value(ValueType.BOOL, Boolean.toString(compare(value1.content(), value2.content()) == 0));
                }
                else if(cur.equals("gt")) {
                    return new Value(ValueType.BOOL, Boolean.toString(compare(value1.content(), value2.content()) == 1));
                }
                else if(cur.equals("lt")) {
                    return new Value(ValueType.BOOL, Boolean.toString(compare(value1.content(), value2.content()) == -1));
                }
            }
            else if(boolOperator.contains(cur)) {
                // for 1 element operator
                Value value1 = parse(scan.next());

                if (!isBool(value1.content())) {
                    System.out.println("Not a bool!");
                    return new Value(ValueType.ERROR, null);
                }
                if (cur.equals("not"))
                    return new Value(ValueType.BOOL, Boolean.toString(!Boolean.parseBoolean(value1.content())));

                // for 2 elements operator
                Value value2 = parse(scan.next());

                if (!isBool(value2.content())) {
                    System.out.println("Not a bool!");
                    return new Value(ValueType.ERROR, null);
                }
                if (cur.equals("and"))
                    return new Value(ValueType.BOOL, Boolean.toString(Boolean.parseBoolean(value1.content()) & Boolean.parseBoolean(value2.content())));

                if (cur.equals("or"))
                    return new Value(ValueType.BOOL, Boolean.toString(Boolean.parseBoolean(value1.content()) | Boolean.parseBoolean(value2.content())));
            }
        }

        // is WORD
        else if (cur.charAt(0) == '\"') {
            return new Value(ValueType.WORD, cur);
        }

        // is LIST
        else if (cur.charAt(0) == '[') {
            String list = cur;
            int cnt = 1;
            while (scan.hasNext()) {
                String tmp = scan.next();
                list = list + " " + tmp;
                if (tmp.charAt(0) == '[')
                    cnt++;
                if (tmp.endsWith("]") && tmp.charAt(0) != '\"')
                    cnt--;
                if (cnt == 0)
                    break;
            }
            return new Value(ValueType.LIST, list);
        }

        // is NUMBER
        else if (isNumeric(cur)) {
            return new Value(ValueType.NUMBER, cur);
        }
        else if (isBool(cur)) {
            return new Value(ValueType.BOOL, cur);
        }
        else {
            return new Value(ValueType.UNKNOWN, cur);
        }

        return new Value(ValueType.ERROR, null);
    }

    public static void main(String[] args) {

        keyWord.add("make");
        keyWord.add("thing");
        keyWord.add(":");
        keyWord.add("erase");
        keyWord.add("isname");
        keyWord.add("print");
        keyWord.add("read");
        keyWord.add("readlist");

        arithmeticOperator.add("add");
        arithmeticOperator.add("sub");
        arithmeticOperator.add("mul");
        arithmeticOperator.add("div");
        arithmeticOperator.add("mod");

        comparativeOperator.add("eq");
        comparativeOperator.add("gt");
        comparativeOperator.add("lt");

        boolOperator.add("not");
        boolOperator.add("and");
        boolOperator.add("or");

        keyWord.addAll(arithmeticOperator);
        keyWord.addAll(comparativeOperator);
        keyWord.addAll(boolOperator);

        nameSpace.clear();

        while(systemScan.hasNext()) {
            String line = systemScan.nextLine();
            if (line.equals("exit"))
                break;
            if (!line.isEmpty()) {
                scan = new Scanner(line);
                parse(scan.next());
            }
        }
    }
}
