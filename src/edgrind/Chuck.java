/*
  Chuck.java
  S. Edward Dolan
  Monday, October  9 2023
*/

package edgrind;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
//
import org.json.JSONObject;

/**
   The EdGrind chuck db.
   <p>
   The external representation of the db is a JSONObject with the following
   format:
   <pre>
   {"edgrind-type": "CHUCK",
    "chuck-type": string,
    "name": string,
    "specs": {"d1": float, ...}}
   </pre>
   </p>
 */
@SuppressWarnings("unchecked")
public class Chuck {
    // ======================================================================
    // Static Fields
    // ======================================================================
    /**
       The chuck database.

       <p>Each key is the name of the chuck. Each associated value is a Chuck
       instance.</p>
    */
    protected static TreeMap<String, Chuck> db = new TreeMap<String, Chuck>();
    // ======================================================================
    // Instance Fields
    // ======================================================================
    /** The db name of the chuck. */
    protected String name;
    /** The type of the chuck. */
    protected ChuckType type;
    /** The chuck sketch dimensions */
    protected Dict specs;
    // ======================================================================
    // Constructors
    // ======================================================================
    /**
       Create a new chuck.
       @param type the type of the chuck
       @param d the chuck sketch dimensions
       @param name the db name of the chuck
    */
    protected Chuck(ChuckType type, Dict d, String name) {
        this.name = name;
        this.type = type;
        this.specs = d;
    }
    // ======================================================================
    // Instance Methods
    // ======================================================================
    /** Get the chuck name. */
    public String getName() {
        return name;
    }
    /** Get the chuck type. */
    public ChuckType getType() {
        return type;
    }
    /** Get the chuck sketch dimension. */
    public Dict getSpecs() {
        return specs;
    }
    /** Get this chuck as a JSONObject. */
    protected JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("edgrind-type", "" + EdGrindType.CHUCK);
        jo.put("chuck-type", "" + type);
        jo.put("name", name);
        jo.put("specs", new JSONObject(specs));
        return jo; 
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    /**
       Add or replace a chuck in the db.
       @param type the type of the chuck
       @param d the chuck sketch dimensions
       @param name the db name of the chuck
    */
    static public Chuck addChuck(ChuckType type, Dict d, String name) {
        Chuck chuck = new Chuck(type, d, name);
        db.put(chuck.name, chuck);
        return chuck;
    }
    /** Fetch a chuck from the db by name. */
    static public Chuck getChuck(String name) {
        return db.get(name);
    }
    /** Get an array of all chuck names in the db. */
    static public String[] allChuckNames() {
        int i = 0;
        String[] a = new String[db.size()];
        for (String s : db.keySet())
            a[i++] = s;
        return a;
    }
    /** Get the chuck db as a JSON object. */
    static public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, Chuck> e : db.entrySet())
            jo.put(e.getKey(), e.getValue().getJSONObject());
        return jo;
    }
    /** Initialize the chuck db from a JSON object. */
    static public void fromJSON(JSONObject jo) {
        db.clear();
        Iterator<String> names = jo.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject chuckObj = (JSONObject)jo.get(name);
            JSONObject specsObj = (JSONObject)chuckObj.get("specs");
            Dict d = new Dict();
            d.putAll(specsObj.toMap());
            addChuck(ChuckType.valueOf(chuckObj.getString("chuck-type")),
                     d, name);
        }
    }
}
