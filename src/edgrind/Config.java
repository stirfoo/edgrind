/*
  Config.java
  S. Edward Dolan
  Friday, December 15 2023
*/

package edgrind;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
//
import java.awt.Color;

public class Config {
    // defaults
    static Color backgroundColor = new Color(0x2d, 0x35, 0x61);
    static Color sketchLineColor = Color.white;
    static Color dimLineColor = new Color(255, 255, 0);
    static Color dimArrowColor = Color.green;
    static Color sketchFillColor = Color.gray;
    static Color wheelFillColor = Color.gray;
    static Color wheelAdapterFillColor = Color.gray;
    static Color chuckFillColor = Color.gray;
    static Color colletFillColor = Color.gray;
    /**
       The config map.
    */
    static Map<String, Object> map = new HashMap<String, Object>();
    /**
       Find if the grinder's ref-data is enabled.
       <p>
       This is used my MainFrame to enable/disable the entries in the Grinder
       menu.
       </p>
       @return true if the name is found in the "menu-grinder" list.
    */
    static public boolean hasGrinder(String name) {
        Object list = map.get("menu-grinder");
        if (list instanceof ArrayList<?>)
            return ((List<?>)list).contains(name);
        return false;
    }
    /**
       Get the graphics background color.
    */
    static public Color getBackgroundColor() {
        Object color = map.get("background-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return backgroundColor;
    }
    /**
       Get the sketch line color.
    */
    static public Color getSketchLineColor() {
        Object color = map.get("sketch-line-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return sketchLineColor;
    }
    /**
       Get the sketch dimension line color.
    */
    static public Color getDimLineColor() {
        Object color = map.get("dim-line-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return dimLineColor;
    }
    /**
       Get the sketch arrow color.
    */
    static public Color getDimArrowColor() {
        Object color = map.get("dim-arrow-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return dimArrowColor;
    }
    /**
       Get the sketch fill color.
    */
    static public Color getSketchFillColor() {
        Object color = map.get("sketch-fill-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return sketchFillColor;
    }
    /**
       Get the wheel adapter fill color.
    */
    static public Color getWheelAdapterFillColor() {
        Object color = map.get("wheel-adapter-fill-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return wheelAdapterFillColor;
    }
    /**
       Get the wheel fill color.
    */
    static public Color getWheelFillColor() {
        Object color = map.get("wheel-fill-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return wheelFillColor;
    }
    /**
       Get the chuck fill color.
    */
    static public Color getChuckFillColor() {
        Object color = map.get("chuck-fill-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return chuckFillColor;
    }
    /**
       Get the collet fill color.
    */
    static public Color getColletFillColor() {
        Object color = map.get("collet-fill-color");
        if (color instanceof String)
            return Color.decode("0x" + (String)color);
        return colletFillColor;
    }
}
