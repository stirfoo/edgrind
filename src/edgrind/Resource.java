/*
  Resource.java
  S. Edward Dolan
  Tuesday, October 10 2023
*/

package edgrind;

import java.util.HashMap;
// 
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
// 
import java.nio.file.Paths;
import java.nio.file.Files;
// 
import org.json.JSONObject;

/**
   Read and write presistent EdGrind data.

   All fields and methods are static.
 */
public final class Resource {
    /** Full path to EdGrind installation folder. */
    private static String egRoot = "/home/ed/prg/java/edgrind/";
    /** The DB folder name. */
    private static final String datFolderName = "dat";
    /** The wheels database file. */
    private static String wheelDB = "wheels.json";
    /** The wheel adapter database file. */
    private static String wheelAdapterDB = "wheel_adapters.json";
    /** The chuck database file. */
    private static String chuckDB = "chucks.json";    
    /** The wheel pack database file. */
    private static String wheelPackDB = "wheel_packs.json";
    /** The collet database file. */
    private static String colletDB = "collets.json";
    /** The grinder ref data database file. */
    private static String refDataDB = "ref_data.json";
    /** The config database file. */
    private static String configDB = "config.json";
    /** true if init() has been called */
    private static boolean initialized = false;
    /**
       Set the correct path to the DB files.
       <p>
       The os path seperator is queried as well as the <em>egroot</em> system
       property. This property must be set when calling java using the -D
       option:
       <pre>
       java -Degroot=C:\\path\to\Edgrind ...
       </pre>
       </p>
    */
    private static void init() {
        if (initialized)
            return;
        String s = System.getProperty("egroot");
        String sep = System.getProperty("file.separator");
        if (s != null) {
            egRoot = s;
            if (!egRoot.endsWith(sep))
                egRoot += sep;
        }
        wheelDB = egRoot + datFolderName + sep + wheelDB;
        wheelAdapterDB = egRoot + datFolderName + sep + wheelAdapterDB;
        chuckDB = egRoot + datFolderName + sep + chuckDB;
        wheelPackDB = egRoot + datFolderName + sep + wheelPackDB;
        colletDB = egRoot + datFolderName + sep + colletDB;
        refDataDB = egRoot + datFolderName + sep + refDataDB;
        configDB = egRoot + datFolderName + sep + configDB;
        initialized = true;
    }
    /**
       Read a JSON file.

       @param fileName the name of the JSON file to read
    */
    public static JSONObject readJSON(String fileName) {
        String jsonSource = null;
        try {
            jsonSource = new String(Files.readAllBytes(Paths.get(fileName)));
        }
        catch (IOException e) {
            System.err.println("EdGrind ERROR: " + e.getMessage());
            System.exit(1);
        }
        return new JSONObject(jsonSource);
    }
    /**
       Write a JSON file.
       
       @param fileName the name of the JSON file to write (overwrite)
    */
    public static void writeJSON(String fileName, JSONObject jo) {
        try {
            FileWriter f = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(f);
            jo.write(bw, 4, 0);
            bw.flush();
            f.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // ======================================================================
    // Load All
    // ======================================================================
    public static void loadAll() {
        init();
        // do config first, as something below might need a config parameter
        loadConfig();
        loadWheels();
        loadWheelAdapters();
        loadChucks();
        loadWheelPacks();
        loadCollets();
        loadRefData();
    }
    private static void loadConfig() {
        JSONObject jo = readJSON(configDB);
        if (!jo.isEmpty())
            Config.map = jo.toMap();
        else
            Config.map = new HashMap<String, Object>();
    }
    private static void loadWheels() {
        JSONObject jo = readJSON(wheelDB);
        if (!jo.isEmpty())
            Wheel.fromJSON(jo);
    }
    private static void loadWheelAdapters() {
        JSONObject jo = readJSON(wheelAdapterDB);
        if (!jo.isEmpty())
            WheelAdapter.fromJSON(jo);
    }
    private static void loadChucks() {
        JSONObject jo = readJSON(chuckDB);
        if (!jo.isEmpty())
            Chuck.fromJSON(jo);
    }
    private static void loadWheelPacks() {
        JSONObject jo = readJSON(wheelPackDB);
        if (!jo.isEmpty())
            WheelPack.fromJSON(jo);
    }
    private static void loadCollets() {
        JSONObject jo = readJSON(colletDB);
        if (!jo.isEmpty())
            Collet.fromJSON(jo);
    }
    private static void loadRefData() {
        JSONObject jo = readJSON(refDataDB);
        if (!jo.isEmpty())
            Grinder.fromJSON(jo);
    }
    // ======================================================================
    // Save All
    // ======================================================================
    public static void saveAll() {
        saveWheels();
        saveWheelAdapters();
        saveChucks();
        saveWheelPacks();
        saveCollets();
        saveRefData();
        // do config last, as something above might write a parameter
        saveConfig();
    }
    private static void saveWheels() {
        JSONObject jo = Wheel.toJSON();
        if (!jo.isEmpty())
            writeJSON(wheelDB, jo);
    }
    private static void saveWheelAdapters() {
        JSONObject jo = WheelAdapter.toJSON();
        if (!jo.isEmpty())
            writeJSON(wheelAdapterDB, jo);
    }
    private static void saveChucks() {
        JSONObject jo = Chuck.toJSON();
        if (!jo.isEmpty())
            writeJSON(chuckDB, jo);
    }
    private static void saveWheelPacks() {
        JSONObject jo = WheelPack.toJSON();
        if (!jo.isEmpty())
            writeJSON(wheelPackDB, jo);
    }
    private static void saveCollets() {
        JSONObject jo = Collet.toJSON();
        if (!jo.isEmpty())
            writeJSON(colletDB, jo);
    }
    private static void saveRefData() {
        JSONObject jo = Grinder.toJSON();
        if (!jo.isEmpty())
            writeJSON(refDataDB, jo);
    }
    private static void saveConfig() {
        JSONObject jo = new JSONObject(Config.map);
        writeJSON(configDB, jo);
    }
}
