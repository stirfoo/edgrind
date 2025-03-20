/*
  Dict.java
  S. Edward Dolan
  Sunday, September  3 2023
*/

package edgrind;

import java.util.Map;
import java.util.TreeMap;
// 
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
//
import edgrind.geom.*;

/**
   Ease creation of a Hashmap that maps String => Object. A Dictionary.

   The rationale for this class is so literal maps can be /easily/ created.

   The single constructor does not throw on an error. Instead, it prints a
   message to stderr and exits the process.

   See the main() method below for example usage.

   TODO: It looks like I've written a poor version of JSONObject. Maybe just
         use it instead?
 */
@SuppressWarnings("serial")
public class Dict extends TreeMap<String, Object> {
    /*
      The one and only constructor!

      A Dict may still be initialized from a map by:
      Dict d = new Dict();
      d.putAll(map);
    */
    public Dict(Object... keysvals) {
        super();
        if ((keysvals.length % 2) != 0)
            die("Dict constructor missing final value");
        Object k = null, v = null;
        for (Object x : keysvals) {
            if (k == null)
                k = x;
            else {
                if (!(k instanceof String))
                    die("Dict keys must be strings", k);
                else if (((String)k).isEmpty())
                    die("Dict keys cannot be empty strings");
                else {
                    put((String)k, x);
                    k = null;
                }
            }
        }
    }
    /*
     */
    public boolean hasKey(String key) {
        return containsKey(key);
    }
    /*
      Getters...
    */
    public boolean boolAt(String key) {
        Object val = get(key);
        return val != null           // anything but null and false are
            && val != Boolean.FALSE; // considered truthy... (hello 'lisp)
    }
    public int intAt(String key) {
        return ((Number)get(key)).intValue();
    }
    public float floatAt(String key) {
        return ((Number)get(key)).floatValue();
    }
    public double doubleAt(String key) {
        return ((Number)get(key)).doubleValue();
    }
    public String stringAt(String key) {
        return (String)get(key);
    }
    public Point2D.Double poin2DtAt(String key) {
        return (Point2D.Double)get(key);
    }
    public Vec2 vecAt(String key) {
        return (Vec2)get(key);
    }
    public Line2D.Double line2DAt(String key) {
        return (Line2D.Double)get(key);
    }
    public Line2 lineAt(String key) {
        return (Line2)get(key);
    }
    public Arc2 arcAt(String key) {
        return (Arc2)get(key);
    }
    public AffineTransform xformAt(String key) {
        return (AffineTransform)get(key);
    }
    public Wheel wheelAt(String key) {
        return (Wheel)get(key);
    }
    public Dict dictAt(String key) {
        return (Dict)get(key);
    }
    public String toStringAt(String key) {
        return toStringAt(key, "%s", false);
    }
    public String toStringAt(String key, String format) {
        return toStringAt(key, format, false);
    }
    /*
      if stripZeros is true, strip trailing zeros if the formatted number
      contains a decimal. In this case format must be some flavor of "%f".
    */
    public String toStringAt(String key, String format, boolean stripZeros) {
        String s = String.format(format, get(key));
        int i = s.indexOf('.');
        if (stripZeros && i > 0) {
            s = Str.rtrim(s, "0");
            if (s.charAt(s.length() - 1) == '.')
                s += '0';     // 1. => 1.0
        }
        return s;
    }
    /*
      Error handlers
    */
    private static void die(String msg) {
        System.err.println("ERROR: " + msg);
        System.exit(1);
    }
    private static void die(String msg, Object badValue) {
        System.err.println("ERROR: " + msg + ", got: " + badValue);
        System.exit(1);
    }
    /*
      Tester...
    */
    public static void main(String[] args) {
        Dict d = new Dict("one", 1,
                          "two", 2.2,
                          "three", '3',
                          "tri1", new Dict("A", new Vec2(1, 2),
                                           "B", new Vec2(-4, 6.6),
                                           "C", new Vec2(.3, -.4)));
        System.out.println(d);
        System.out.println(d.floatAt("two"));
        System.out.println(d.toStringAt("two", "%.4f", true));
        System.out.println(Str.ltrim("00.0000", "0"));
    }
}

abstract class Str {
    // post format operations, can be binary or'ed
    final static int NONE = 0;              // do nothing!
    final static int STRIP_TRAIL_ZEROS = 1; // 1.23000 => 1.23
    final static int STRIP_LEAD_ZEROS = 2;  // 0042 => 42
    final static int ADD_DOT_ZERO = 4;      // 1 => 1.0
    final static int ADD_ZERO = 8;          // 1. => 1.0
    static String postOp(String s, int ops) {
        String out = s;
        if ((ops & STRIP_TRAIL_ZEROS) > 0)
            out = Str.rtrim(out, "0");
        if ((ops & STRIP_LEAD_ZEROS) > 0)
            out = Str.ltrim(out, "0");
        if ((ops & ADD_DOT_ZERO) > 0)
            out += ".0";
        if ((ops & ADD_ZERO) > 0)
            out += '0';
        return out;
    }
    // 
    static String trim(String s, String chars) {
        return rtrim(ltrim(s, chars), chars);
    }
    static String ltrim(String s, String chars) {
        String out = "";
        for (int i=0; i<s.length(); ++i)
            if (chars.indexOf(s.charAt(i)) == -1)
                return s.substring(i);
        return "";
    }
    static String rtrim(String s, String chars) {
        String out = "";
        for (int i=s.length()-1; i>=0; --i)
            if (chars.indexOf(s.charAt(i)) == -1)
                return s.substring(0, i+1);
        return "";
    }
}
