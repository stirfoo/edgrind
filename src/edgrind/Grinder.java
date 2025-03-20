/*
  Grinder.java
  S. Edward Dolan
  Tuesday, September 19 2023
*/

package edgrind;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
//
import org.json.JSONObject;

/**
  Reference data for a grinder.

  <p>
  This establishes the coordinate system for a given grinder, the position of
  both spindles, the position of the probe plate, and the probe ball to that
  coordinate system.
  </p>

  <p>
  It also holds optional data like:
  <ul>
  <li>Is cylindrical grinding enabled?</li>
  <ul>
  </p>
 */
public class Grinder {
    protected static TreeMap<String, Grinder> db
        = new TreeMap<String, Grinder>();
    // ======================================================================
    // Calibration Arbors (same for all three grinders)
    // ======================================================================
    // arbor mounted in the chuck (called a cylinder here)
    final static double cylinderDia = 1.9691;
    final static double cylinderLen = 7.8793;
    final static double cylinderRad = cylinderDia / 2;
    // arbor mounted in the spindle (called a disk here)
    final static double diskDia = 5.3147;
    final static double diskLen = 3.1460;
    final static double diskWid = 0.3148;
    final static double diskRad = diskDia / 2;
    Dict dict;
    /** radius from C-axis to A-axis face, C = 0 */
    double cradius;
    /** spindle 1 datum to A-axis face, C = +90 */
    double s1zrf90;
    /**
       Spindle 1 centerline to A-axis centerline, along the X-axis, C =
       * +90.
       */
    double s1cl2clx;
    /**
       Spindle 2 centerline to A-axis centerline, along the X-axis, C = -90.
    */
    double s2cl2clx;
    /**
       Spindle 1 centerline to A-axis centerline, along the Y-axis.
    */
    double s1cl2cly;
    /**
       Spindle 2 centerline to A-axis centerline, along the Y-axis.
    */
    double s2cl2cly;
    // probe data initialized from above values
    double ppf2f;   // Plate face to A-axis face along the X-axis, C=0. This
                    // averages ppxb and ppxt and assumes the vertical center
                    // of the plate is touched. It's only .006 difference in
                    // deflection. This could be trigged out exactly and it
                    // might make a difference depending on the grinding
                    // operation. For instance, if .005 is being removed from
                    // the end face. (Stock Removal X1).
    double ppb2cl;  // plate bottom to A-axis centerline along the Y-axis, C=0
    double ppc2cl;  // plate center to A-axis centerline along the Z-axis C=0
    private Grinder(Dict dict) {
        this.dict = dict;
        //
        this.cradius = ((dict.doubleAt("sp1-c0-x") + diskRad + cylinderLen) -
                        (dict.doubleAt("sp1-c+90-x") + diskRad + cylinderRad));
        this.s1zrf90 = dict.doubleAt("sp1-c0-z") + diskLen + cylinderRad
            + cradius;
        this.s1cl2clx = dict.doubleAt("sp1-c+90-x") + diskRad + cylinderRad;
        this.s2cl2clx = dict.doubleAt("sp2-c-90-x") + diskRad + cylinderRad;
        this.s1cl2cly = dict.doubleAt("sp1-c0-y") + diskRad + cylinderRad;
        this.s2cl2cly = dict.doubleAt("sp2-c0-y") + diskRad + cylinderRad;
        //
        double ppxt = dict.doubleAt("plate-x-top");
        double ppxb = dict.doubleAt("plate-x-bottom");
        double ppy = dict.doubleAt("plate-y");
        double ppz = dict.doubleAt("plate-z");
        double pw = dict.doubleAt("plate-width");
        this.ppf2f = ppxb + ((ppxt - ppxb) / 2) + cylinderLen;
        this.ppb2cl = ppy + cylinderRad;
        this.ppc2cl = ppz - cylinderRad - pw / 2;
    }
    /**
       Get the grinder nickname.
    */
    public String getName() {
        return dict.stringAt("name");
    }
    /**
       Find if the grinder has an optional finish feedrate.
    */
    public boolean hasFinishFeedrate() {
        return dict.boolAt("has-finish-feed?");
    }
    /**
       Get the distance from the face of the probe plate to the A-axis face,
       minus the given distance, with C = 0.

       @param d the distance to subtract
    */
    public double getZeropointX(double d) {
        return ppf2f - d;
    }
    /**
       Get the distance from the bottom of the probe plate to the A - axis,
       minus the given distance, with C = 0.

       @parameter endDia the blank diameter at the probed face

       TODO: what about an end chamfer, point, radius, etc?
    */
    public double getZeropointY(double endDia) {
        return ppb2cl - endDia / 2 * .8;
    }
    /**
       Get the distance from the center of the probe plate to the A-axis
       centerline, with C = 0.
    */
    public double getZeropointZ() {
        return ppc2cl;
    }
    /**
       Get the X distance required to align spindle 1 centerline with the
       A-axis centerline, with C = +90.
    */
    public double getS1CenterlineX() {
        return s1cl2clx;
    }
    /**
       Get the X distance required to align spindle 2 centerline with the
       A-axis centerline, with C = -90.
    */
    public double getS2CenterlineX() {
        return s2cl2clx;
    }
    /**
       Get the spindle 1 centerline distance to the A-axis centerline, along
       the Y-axix, minus the given distance.

       <p> For example, if d = spindle 1 wheel radius + blank radius, this
       will return the Y+ movement required to place the bottom of the wheel
       at the top of the blank.</p>
       
       @param d the distance to subtract
    */
    public double getS1CenterlineY(double d) {
        return s1cl2cly - d;
    }
    /**
       Get the spindle 2 centerline distance to the A-axis centerline, along
       the Y-axix, minus the given distance.

       @param d the distance to subtract
    */
    public double getS2CenterlineY(double d) {
        return s2cl2cly - d;
    }
    /**
       Get the spindle 1 distance from the wheel adapter datum (the spindle
       side of the adapter) to the A-axis face, at C=+90, along the Z axis,
       minus the given distance.

       @param d the distance to subtract
    */
    public double getS1RefToAFaceZ(double d) {
        return s1zrf90 - d;
        
    }
    public void dump() {
        System.out.println(dict);
    }
    public JSONObject getJSONObject() {
        JSONObject jo = new JSONObject(dict);;
        jo.put("edgrind-type", "" + EdGrindType.REF_DATA);
        return jo;
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    static public Grinder getGrinder(String name) {
        return db.get(name);
    }
    static public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, Grinder> e : db.entrySet())
            jo.put(e.getKey(), e.getValue().getJSONObject());
        return jo;
    }
    static public void fromJSON(JSONObject jo) {
        db.clear();
        Iterator<String> names = jo.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject grinderObj = (JSONObject)jo.get(name);
            Dict d = new Dict();
            d.putAll(grinderObj.toMap());
            addGrinder(d);
        }
    }
    static private Grinder addGrinder(Dict d) {
        Grinder grinder = new Grinder(d);
        db.put(d.stringAt("name"), grinder);
        return grinder;
    }
}
