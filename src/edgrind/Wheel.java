/*
  Wheel.java
  S. Edward Dolan
  Saturday, September 30 2023
*/

package edgrind;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
// 
import org.json.JSONObject;
//
import edgrind.sketch.SketchSpecsListener;

/**
  The EdGrind wheel db.
  <p>
  A wheel has a type, name, system flag, and specs Dict. The external
  representation of the db is a JSONObject with the following format:
  <pre>
  {"edgrind-type": "WHEEL"
   "name": string,
   "wheel-type": string,
   "system?": bool,
   "specs": {"d1": float, ..., "dN": float,
             "l1": float, ..., "lN": float,
             "r1": float, ..., "rN": float,
             "a1": float, ..., "aN": float}}
  </pre>
  </p>
 */
@SuppressWarnings("unchecked")
public class Wheel {
    // ======================================================================
    // Static Fields
    // ======================================================================
    /** The internal key, val database */
    protected static TreeMap<String, Wheel> wheels
        = new TreeMap<String, Wheel>();
    /** List of db change listeners */
    protected static List<DBChangeListener> dbListeners
        = new ArrayList<DBChangeListener>();
    // ======================================================================
    // Instance Fields
    // ======================================================================
    /** The type of wheel */
    protected WheelType type;
    /** The name of the wheel */
    protected String name;
    /** The dimensions of the wheel */
    protected Dict specs;
    /**
       A temporary specs dict used by multiple sketches that ref this wheel.
       <p>
       When the dimensions of this wheel are edited (via setSketchSpecs), this
       dict is updated. Any SketchSpecsListener attached are then notified of
       the change.
       </p>
    */
    protected Dict sketchSpecs;
    /**
       A premanently read-only wheel.
       <p>
       A system wheel cannot be edited. They exist as a starter template the
       user may save under a different name and then modify. The dimensions
       will be shown in the wheel editor but they cannot be changed.
       </p>
    */
    protected boolean system;
    protected List<SketchSpecsListener> specsListeners
        = new ArrayList<SketchSpecsListener>();
    /**
       Temporarily prevent the wheel from being modified.

       <p>
       The occurs for instance when two sketches try to edit the same wheel.
       </p>
    */
    protected boolean locked = false;
    // ======================================================================
    // Constructors
    // ======================================================================
    /**
       Create a new wheel.

       @param type the type of wheel
       @param d the wheels dimensions
       @param name the name of the wheel
       @param system true if a read-only system wheel
    */
    protected Wheel(WheelType type, Dict d, String name, boolean system) {
        this.type = type;
        this.specs = d;
        this.sketchSpecs = (Dict)d.clone();
        this.name = name;
        this.system = system;
    }
    // ======================================================================
    // Instance Methods
    // ======================================================================
    public String toString() {
        return "" + new Dict("edgrind-type", "" + EdGrindType.WHEEL,
                             "name", name,
                             "wheel-type", "" + type,
                             "system?", system,
                             "specs", specs);
    }
    /** Get the wheel name. */
    public String getName() {
        return name;
    }
    /** Get the wheel type. */
    public WheelType getType() {
        return type;
    }
    /** Get the wheel dimensions. */
    public Dict getSpecs() {
        return specs;
    }
    /** Get the sketch specs */
    public Dict getSketchSpecs() {
        return sketchSpecs;
    }
    /**
       Set the sketch specs to specs.
       <p>
       Each attached SketchSpecsListener will be notified after the specs
       are updated.
       </p>
    */
    public void setSketchSpecs(Dict specs) {
        this.sketchSpecs = specs;
        for (SketchSpecsListener l : specsListeners)
            l.onSpecsChange(sketchSpecs);
    }
    /**
       Find if the wheel is a system (read-only) wheel.
    */
    public boolean isSystem() {
        return system;
    }
    public void setLocked(boolean b) {
        locked = b;
    }
    public boolean isLocked() {
        return locked;
    }
    /**
       Reload this wheel's specs from the last saved state.
       <p>This will reinitialize the sketchSpecs to the initial state.</p>
     */
    public void reload() {
        sketchSpecs = (Dict)specs.clone();
        for (DBChangeListener l : dbListeners)
            l.onDBChange(DBChangeListener.DBType.WHEEL, getName());
    }
    /**
       Get this wheel as a JSONObject.
    */
    protected JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("edgrind-type", "" + EdGrindType.WHEEL);
        jo.put("name", name);
        jo.put("wheel-type", "" + type);
        jo.put("system?", system);
        /*
          TODO: What about the current value of sketchSpecs? When the app
          shuts down, all wheels are saved to wheels.json. Should I prompt
          here if sketchSpecs differs from specs?
        */
        jo.put("specs", new JSONObject(specs));
        return jo; 
    }
    /**
       Add the listener.
       <p>
       The listener will be notified when the wheel's specs (dimensions) have
       been modified.
       </p>
    */
    public void addSpecsListener(SketchSpecsListener l) {
        specsListeners.add(l);
    }
    /**
       Remove the listener.

       If the listener has not been added, do nothing.
    */
    public void removeSpecsListener(SketchSpecsListener l) {
        specsListeners.remove(l);
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    /**
       Add a wheel to the db.

       @param type the wheel type
       @param d the wheel dimensions
       @param name the wheel name
       @param system true if the wheel is a system wheel
    */
    static public Wheel addWheel(WheelType type, Dict d, String name,
                                 boolean system) {
        Wheel wheel = new Wheel(type, d, name, system);
        wheels.put(wheel.name, wheel);
        for (DBChangeListener l : dbListeners)
            l.onDBChange(DBChangeListener.DBType.WHEEL, wheel.name);
        return wheel;
    }
    static public Wheel addWheel(WheelType type, Dict d, String name) {
        return addWheel(type, d, name, false);
    }
    /**
       Add the listener.
    */
    static public void addDBChangeListener(DBChangeListener l) {
        dbListeners.add(l);
    }
    /**
       Remove the listener.

       If the listener has not been added, do nothing.
    */
    static public void removeDBChangeListener(DBChangeListener l) {
        dbListeners.remove(l);
    }
    /**
       Fetch a wheel from the db.

       @param name the wheel name
    */
    static public Wheel getWheel(String name) {
        return wheels.get(name);
    }
    /**
       Get an array of all wheel names in the db.
    */
    static public String[] allWheelNames() {
        int i = 0;
        String[] a = new String[wheels.size()];
        for (String s : wheels.keySet())
            a[i++] = s;
        return a;
    }
    /**
       Get the wheel db as a JSONObject.
    */
    static public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, Wheel> e : wheels.entrySet())
            jo.put(e.getKey(), e.getValue().getJSONObject());
        return jo;
    }
    /**
       Initialize the wheel db from a JSONObject.
    */
    static public void fromJSON(JSONObject jo) {
        wheels.clear();
        Iterator<String> names = jo.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject wheelObj = (JSONObject)jo.get(name);
            Boolean system = (Boolean)wheelObj.opt("system?");
            JSONObject specsObj = (JSONObject)wheelObj.get("specs");
            Dict d = new Dict();
            d.putAll(specsObj.toMap());
            addWheel(WheelType.valueOf(wheelObj.getString("wheel-type")),
                     d, name, system == null ? false : (boolean)system );
        }
    }
}
