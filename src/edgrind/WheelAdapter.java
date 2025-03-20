/*
  WheelAdapter.java
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
   The EdGrind wheel adapter db.
   <p>
   Currently the only adapters used are those described on page 10 of:
   
   https://toolroom.solutions/wp-content/uploads/2019/03/Toolroom-Solutions-GDS-grinding-wheel-adapters-for-WALTER-tool-grinders.pdf
   </p>
   <p> These are 1-1/4" journal diameter. </p
   <p>
   The external representation of the db is a JSONObject with the following
   format:
   
   <pre>
   {"edgrind-type": "WHEEL_ADAPTER",
    "name": string,
    "specs": {"d1": float, ..., "d4": float,
              "l1": float, ..., "l4": float}}
   </pre>
   </p>
 */
@SuppressWarnings("unchecked")
public class WheelAdapter {
    protected static TreeMap<String, WheelAdapter> db
        = new TreeMap<String, WheelAdapter>();
    protected String name;
    protected Dict specs;
    protected WheelAdapter(Dict d, String name) {
        this.specs = d;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public Dict getSpecs() {
        return specs;
    }
    /**
       Get this adapter as a JSONObject.
    */
    protected JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("edgrind-type", "" + EdGrindType.WHEEL_ADAPTER);
        jo.put("name", name);
        jo.put("specs", new JSONObject(specs));
        return jo; 
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    static public WheelAdapter addAdapter(Dict d, String name) {
        WheelAdapter adapter = new WheelAdapter(d, name);
        db.put(adapter.name, adapter);
        return adapter;
    }
    static public WheelAdapter getAdapter(String name) {
        return db.get(name);
    }
    static public String[] allAdapterNames() {
        int i = 0;
        String[] a = new String[db.size()];
        for (String s : db.keySet())
            a[i++] = s;
        return a;
    }
    static public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        for (Map.Entry<String, WheelAdapter> e : db.entrySet())
            jo.put(e.getKey(), e.getValue().getJSONObject());
        return jo;
    }
    static public void fromJSON(JSONObject jo) {
        db.clear();
        Iterator<String> names = jo.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject adapterObj = (JSONObject)jo.get(name);
            JSONObject specsObj = (JSONObject)adapterObj.get("specs");
            Dict d = new Dict();
            d.putAll(specsObj.toMap());
            addAdapter(d, name);
        }
    }
}
