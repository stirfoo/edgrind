/*
  Collet.java
  S. Edward Dolan
  Monday, October  9 2023
*/

package edgrind;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
//
import java.util.List;
import java.util.ArrayList;
//
import org.json.JSONObject;
import org.json.JSONArray;

/**
   The EdGrind collet db.
   <p>
   The external representation of the db is a JSONObject with the following
   format:
   <pre>
   {"edgrind-type": "COLLET",
    "collet-type": string,
    "name": string,
    "specs": {"d1": float, ...},
    "chucks": ["SK50 7090", ...]}
   </pre>
   </p>
 */
@SuppressWarnings("unchecked")
public class Collet {
    // ======================================================================
    // Static Fields
    // ======================================================================
    protected static TreeMap<String, Collet> db
        = new TreeMap<String, Collet>();
    // ======================================================================
    // Instance Fields
    // ======================================================================
    protected String name;
    protected ColletType type;
    protected Dict specs;
    /**
       Chucks that this collet will work with.
       <p>
       The items in the list are the db names of the chucks.
       </p>
    */
    protected List<String> chucks;
    // ======================================================================
    // Constructors
    // ======================================================================
    protected Collet(ColletType type, Dict d, String name,
                     List<String> chucks) {
        this.name = name;
        this.type = type;
        this.specs = d;
        this.chucks = chucks;
    }
    // ======================================================================
    // Instance Methods
    // ======================================================================
    public String getName() {
        return name;
    }
    public ColletType getType() {
        return type;
    }
    public Dict getSpecs() {
        return specs;
    }
    public List<String> getChucks() {
        return chucks;
    }
    /**
       Get this collet as a JSONObject.
    */
    protected JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("edgrind-type", "" + EdGrindType.COLLET);
        jo.put("collet-type", "" + type);
        jo.put("name", name);
        jo.put("specs", new JSONObject(specs));
        jo.put("chucks", new JSONArray(chucks));
        return jo; 
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    static public Collet addCollet(ColletType type, Dict d, String name,
                                   List<String> chucks) {
        Collet collet = new Collet(type, d, name, chucks);
        db.put(collet.name, collet);
        return collet;
    }
    static public Collet getCollet(String name) {
        return db.get(name);
    }
    static public String[] allColletNames() {
        int i = 0;
        String[] a = new String[db.size()];
        for (String s : db.keySet())
            a[i++] = s;
        return a;
    }
    /**
       Get a list of collets that will work with the given chuck.
       
       @param chuckName db name of the chuck to match
    */
    static public List<String> getChuckCollets(String chuckName) {
        ArrayList<String> collets = new ArrayList<>();
        for (Map.Entry<String, Collet> e : db.entrySet()) {
            Collet c = e.getValue();
            for (String s : c.getChucks())
                if (s.equals(chuckName))
                    collets.add(c.getName());
        }
        return collets;
    }
    static public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, Collet> e : db.entrySet())
            jo.put(e.getKey(), e.getValue().getJSONObject());
        return jo;
    }
    static public void fromJSON(JSONObject jo) {
        db.clear();
        Iterator<String> names = jo.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject colletObj = (JSONObject)jo.get(name);
            JSONObject specsObj = (JSONObject)colletObj.get("specs");
            Dict d = new Dict();
            d.putAll(specsObj.toMap());
            JSONArray chucksArray = (JSONArray)colletObj.get("chucks");
            List<String> chucksList = new ArrayList<>();
            for (Object obj : chucksArray.toList())
                chucksList.add((String)obj);
            addCollet(ColletType.valueOf(colletObj.getString("collet-type")),
                      d, name, chucksList);
        }
    }
}
