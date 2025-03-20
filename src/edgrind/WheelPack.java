/*
  WheelPack.java
  S. Edward Dolan
  Wednesday, October 11 2023
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

/**
   The EdGrind wheelpack db.
   <p>
   A wheelpack has a name, an adapter, and 1 to 3 wheels.
   </p>
   <p>
   The external representation of the db is a JSONObject with the following
   format:
   <pre>
   {"edgrind-type": "WHEEL_PACK"
    "wheelpack-db-name": string,
    "wheel-adapter-db-name": string,
    "wheel-1-specs": {"wheel-db-name": string,
                      "z-length:" float,
                      "flip?": bool,
                      "front?": bool},
    "wheel-2-specs": {"wheel-db-name": string,
                      "z-length:" float,
                      "flip?": bool,
                      "front?": bool},
    "wheel-3-specs": {"wheel-db-name": string,
                      "z-length:" float,
                      "flip?": bool,
                      "front?": bool}}
                      </pre>
                      </p>
 */
public class WheelPack {
    // ======================================================================
    // Static Fields
    // ======================================================================
    /** The internal key, value database. */
    protected static TreeMap<String, WheelPack> wheelPacks
        = new TreeMap<String, WheelPack>();
    /** List of db change listeners */
    protected static List<DBChangeListener> dbListeners
        = new ArrayList<DBChangeListener>();
    // ======================================================================
    // Instance Fields
    // ======================================================================
    /** true if the wheel pack has been modified since last save */
    protected boolean dirty;
    /** The wheel pack name. */
    protected String name;
    /** The wheel adapter name. */
    protected String adapterName;
    /**
       The spindle the wheel pack was created on, 1 or 2.
       <p>
       This is needed because of the flip flag. If an 11v9 is added to spindle
       1 and the wheel pack is saved with flip off, the sketch will not be
       mirrored. If that wheelpack is loaded onto spindle 2, the 11v9 will
       still not be mirrored and will appear `backwards' on spindle 2. By
       checking this field, SpindlePanel will know to toggle flip on or off.
       </p>
    */
    protected int spindle;
    /** The wheel specs. */
    protected Dict wheel1Specs, wheel2Specs, wheel3Specs;
    // ======================================================================
    // Constructors
    // ======================================================================
    /**
       Create a new wheelpack.
       <p>
       All three wheel specs may be null. This will result in a named, empty
       wheelpack.
       </p>
       @param name the name of the wheelpack
       @param adapterName the wheel adapter name
       @param wheel1Specs the specs dictionary of the 1st wheel
       @param wheel2Specs the specs dictionary of the 2nd wheel
       @param wheel3Specs the specs dictionary of the 3rd wheel
    */
    protected WheelPack(String name, String adapterName, int spindle,
                        Dict wheel1Specs, Dict wheel2Specs, Dict wheel3Specs) {
        assert(wheel1Specs != null ||
               wheel2Specs != null ||
               wheel3Specs != null);
        this.dirty = false;
        this.name = name;
        this.adapterName = adapterName;
        this.spindle = spindle;
        this.wheel1Specs = wheel1Specs;
        this.wheel2Specs = wheel2Specs;
        this.wheel3Specs = wheel3Specs;
    }
    // ======================================================================
    // Instance Methods
    // ======================================================================
    /** Return true if the wheel pack has been modified, but not saved. */
    public boolean isDirty() {
        return dirty;
    }
    /** Set the dirty flag. */
    public void setDirty(boolean b) {
        dirty = b;
    }
    /** Get the wheepacke name. */
    public String getName() {
        return name;
    }
    /** Get the adapter name. */
    public String getAdapterName() {
        return adapterName;
    }
    /** Get the 1st wheel's specs. */
    public Dict getWheel1Specs() {
        return wheel1Specs;
    }
    /** Get the spindle the wheel pack was created on. */
    public int getSpindle() {
        return spindle;
    }
    /** Get the 2nd wheel's specs. */
    public Dict getWheel2Specs() {
        return wheel2Specs;
    }
    /** Get the 3rd wheel's specs. */
    public Dict getWheel3Specs() {
        return wheel3Specs;
    }
    static public void addDBChangeListener(DBChangeListener l) {
        dbListeners.add(l);
    }
    static public void removeDBChangeListener(DBChangeListener l) {
        dbListeners.remove(l);
    }
    /** Get this wheelpack as a JSONObject. */
    protected JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("edgrind-type", "" + EdGrindType.WHEEL_PACK);
        jo.put("db-name", name);
        jo.put("wheel-adapter-db-name", adapterName);
        jo.put("spindle", spindle);
        if (wheel1Specs != null)
            jo.put("wheel-1-specs", new JSONObject(wheel1Specs));
        if (wheel2Specs != null)
            jo.put("wheel-2-specs", new JSONObject(wheel2Specs));
        if (wheel3Specs != null)
            jo.put("wheel-3-specs", new JSONObject(wheel3Specs));
        return jo; 
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    /**
       Add a WheelPack to the db.

       If name is already in the db, it will be overwritten.

       @param name name of the wheel pack
       @param wheelPack the pack to add
    */
    static public void addWheelPack(String name, WheelPack wheelPack) {
        wheelPacks.put(name, wheelPack);
        wheelPack.setDirty(false);
        for (DBChangeListener l : dbListeners)
            l.onDBChange(DBChangeListener.DBType.WHEEL_PACK, name);
    }
    /**
       Fetch a wheelpack from the db.

       @param name the wheelpack name
    */
    static public WheelPack getWheelPack(String name) {
        return wheelPacks.get(name);
    }
    /**
       Get an array of all wheelpack names in the db.
    */
    static public String[] allWheelPackNames() {
        int i = 0;
        String[] a = new String[wheelPacks.size()];
        for (String s : wheelPacks.keySet())
            a[i++] = s;
        return a;
    }
    /**
       Get the wheelpack db as a JSONObject.
    */
    static public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, WheelPack> e : wheelPacks.entrySet())
            jo.put(e.getKey(), e.getValue().getJSONObject());
        return jo;
    }
    /**
       Initialize the wheelpack db from a JSONObject.
    */
    static public void fromJSON(JSONObject jo) {
        wheelPacks.clear();
        Iterator<String> names = jo.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject wpobj = (JSONObject)jo.get(name);
            JSONObject w1Obj = (JSONObject)wpobj.opt("wheel-1-specs");
            JSONObject w2Obj = (JSONObject)wpobj.opt("wheel-2-specs");
            JSONObject w3Obj = (JSONObject)wpobj.opt("wheel-3-specs");
            Dict w1Specs = null, w2Specs = null, w3Specs = null;
            if (w1Obj != null) {
                w1Specs = new Dict();
                w1Specs.putAll(w1Obj.toMap());
            }
            if (w2Obj != null) {
                w2Specs = new Dict();
                w2Specs.putAll(w2Obj.toMap());
            }
            if (w3Obj != null) {
                w3Specs = new Dict();
                w3Specs.putAll(w3Obj.toMap());
            }
            wheelPacks
                .put(name,
                     new WheelPack(wpobj.getString("db-name"),
                                   wpobj.getString("wheel-adapter-db-name"),
                                   wpobj.getInt("spindle"),
                                   w1Specs, w2Specs, w3Specs));
        }
    }
}
