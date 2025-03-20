/*
  Util.java
  S. Edward Dolan
  Thursday, November 23 2023
*/

package edgrind;

import java.util.List;
import java.util.ArrayList;
//
import java.awt.Color;
//
import edgrind.geom.Algo;

public class Util {
    public static void printArray(Object[] a) {
        System.out.print("[");
        for (int i=0; i<a.length; ++i) {
            if (i > 0)
                System.out.print(", ");
            Object x = a[i];
            if (x instanceof Object[])
                printArray((Object[])x);
            else
                System.out.print(a[i]);
        }
        System.out.println("]");
    }
    /**
       Uniformly adjust a color's intensity.

       @param color the color to scale
       @param s the scalar which will be clamped to [0, 1].
       @return a new Color
    */
    static public Color scaleColor(Color color, double s) {
        s = Algo.clamp(s, 0, 1);
        return new Color((int)(color.getRed() * s),
                         (int)(color.getGreen() * s),
                         (int)(color.getBlue() * s));
    }
    /**
       Create a new ArrayList containing the given elements.
    */
    static public List<Object> objList(Object... objs) {
        List<Object> out = new ArrayList<Object>();
        for (Object x : objs)
            out.add(x);
        return out;
    }
}
