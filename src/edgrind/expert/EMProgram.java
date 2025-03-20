/*
  EMProgram.java
  S. Edward Dolan
  Sunday, July 30 2023
*/

package edgrind.expert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
   Given a float's gui index, find the data element's index and type.
*/
class ITPair {
    int index, type;
    ITPair() {
        index = 0;
        type = 0;
    }
    ITPair(int i, int t) {
        index = i;
        type = t;
    }
    static ITPair findPair(int guiIndex) {
        int n = guiIndex / 256;
        assert n < 7;
        int type = 0x20 | n;
        int index = guiIndex - 256 * n;
        return new ITPair(index, type);
    }
    public String toString() {
        return String.format("ITPair(%02x,%02x)", index, type);
    }
}

/**
   The internal representation of a Walter Expert Mode program.

   <p>A dictionary keeps track of each element. Each key is a string roughly
   named the same as the label in the Expert Mode GUI. Each associated value
   is an {@link EMData} instance. For example, <em>Zeropoint X</em> in the
   <em>Length Probing</em> section of the gui has the key name
   <code>"zero-point-x"</code>. It's associated value is an {@link EMDFloat}
   instance. An EMProgram implements {@link EMDataHandler} so it can be
   called from EMDataReader when the reader encounters a string, integer, or
   floating point number.</p>
 */
public class EMProgram implements EMDataHandler {
    private String fileName;
    private String dataName;
    private TreeMap<String, EMData> dict;
    private String[] straightsNames = {
        "a", "b", "c", "d", "e", "f", "g", "h", "i"
    };
    private String[] radiiNames = {
        "n", "o", "p", "q", "r", "s", "t", "u", "v"
    };
    public EMProgram() {
        initDict();
        // dump();
        clear();                // load default values
    }
    private void initDict() {
        dict = new TreeMap<>();
        ITPair itp;
        // Op Order Panel
        int gi = 180;
        EMDInt ei;
        for (int i=1, j=180; i<16; ++i, j+=100) {
            itp = ITPair.findPair(j);
            ei = new EMDInt(itp.index, itp.type);
            ei.allowNegative(false);
            dict.put("op-order-" + i, ei);
        }
        EMDChoice ec = new EMDChoice(2, 0x60, "No", "Yes", "Load From File");
        ec.setDefaultValue(0);
        dict.put("probe-len", ec);
        ec = new EMDChoice(4, 0x60, "No", "Yes", "Load From File");
        ec.setDefaultValue(0);
        dict.put("probe-radial-pos", ec);
        EMDString es = new EMDString(0, 0xa0);
        es.setDefaultValue("ST DATA");
        dict.put("probe-data-file-name", es);
        dict.put("stock-removal-a", new EMDFloat(4, 0x20, false));
        dict.put("stock-removal-x1", new EMDFloat(9, 0x20, true));
        dict.put("stock-removal-z1", new EMDFloat(8, 0x20, true));
        ec = new EMDChoice(0, 0x60, "HMC400", "HMC500");
        ec.setBaseValue(4);
        ec.setDefaultValue(5);  // HMC500
        dict.put("control", ec);
        EMDBool eb = new EMDBool(5, 0x60);
        eb.setDefaultValue(false);
        dict.put("hmc400-var-speed", eb);
        // Probe Panel
        dict.put("interrupt-after-probing", new EMDBool(1, 0x60));
        dict.put("zeropoint-x", new EMDFloat(1, 0x20, true));
        dict.put("zeropoint-y", new EMDFloat(3, 0x20, true));
        dict.put("zeropoint-z", new EMDFloat(2, 0x20, true));
        itp = ITPair.findPair(464);
        dict.put("max-measure-len-x", new EMDFloat(itp.index, itp.type, true));
        for (int i=1, j=1710; i<7; ++i, j+=10) {
            dict.put("rad-probe-num-teeth-" + i, new EMDInt(170+i, 0x60));
            itp = ITPair.findPair(j);
            dict.put("rad-probe-a-" + i, new EMDFloat(itp.index, itp.type,
                                                      false));
            itp = ITPair.findPair(j+1);
            dict.put("rad-probe-x-" + i, new EMDFloat(itp.index, itp.type,
                                                      true));
            itp = ITPair.findPair(j+2);
            dict.put("rad-probe-z-" + i, new EMDFloat(itp.index, itp.type,
                                                      true));
            itp = ITPair.findPair(j+3);
            dict.put("rad-probe-y-" + i, new EMDFloat(itp.index, itp.type,
                                                      true));
            itp = ITPair.findPair(j+4);
            dict.put("rad-probe-corr-level-a-" + i, new EMDFloat(itp.index,
                                                                 itp.type,
                                                                 false));
            itp = ITPair.findPair(j+5);
            dict.put("rad-probe-rapid-index-" + i, new EMDFloat(itp.index,
                                                                itp.type,
                                                                false));
        }
        // Grind Panels
        for (int i=1; i<16; ++i)
            initGrindPageEntries(i);
        // Contours Panel (iterate by gui row)
        for (int i=0, j=1610; i<9; ++i, j+=10) {
            itp = ITPair.findPair(j+1);
            dict.put("straights-angle-" + straightsNames[i],
                     new EMDFloat(itp.index, itp.type, false));
            itp = ITPair.findPair(j+2);
            dict.put("straights-distance-" + straightsNames[i],
                     new EMDFloat(itp.index, itp.type, true));
            itp = ITPair.findPair(j+4);
            dict.put("radii-start-" + radiiNames[i],
                     new EMDFloat(itp.index, itp.type, false));
            itp = ITPair.findPair(j+5);
            dict.put("radii-rotation-" + radiiNames[i],
                     new EMDFloat(itp.index, itp.type, false));
            itp = ITPair.findPair(j+6);
            dict.put("radii-radius-" + radiiNames[i],
                     new EMDFloat(itp.index, itp.type, true));
        }
    }
    // refactor the float dict entries in initGrindPageEntries()
    private EMDFloat putFloat(String name, int page, int base,
                              boolean metric) {
        ITPair p = ITPair.findPair(page*100+base);
        EMDFloat ef = new EMDFloat(p.index, p.type, metric);
        dict.put(name + "-" + page, ef);
        return ef;
    }
    // refactor the int dict entries in initGrindPageEntries()
    private EMDInt putInt(String name, int page, int base) {
        ITPair p = ITPair.findPair(page*100+base);
        EMDInt ei = new EMDInt(p.index, p.type);
        dict.put(name + "-" + page, ei);
        return ei;
    }
    public void loadOpOrderDefaults() {
        dict.get("stock-removal-a").loadDefault();
        dict.get("stock-removal-x1").loadDefault();
        dict.get("stock-removal-z1").loadDefault();
        dict.get("probe-len").loadDefault();
        dict.get("probe-radial-pos").loadDefault();
        dict.get("control").loadDefault();
        dict.get("hmc400-var-speed").loadDefault();
    }
    public void loadProbeDefaults() {
        dict.get("interrupt-after-probing").loadDefault();
        dict.get("zeropoint-x").loadDefault();
        dict.get("zeropoint-y").loadDefault();
        dict.get("zeropoint-z").loadDefault();
        for (int i=1; i<7; ++i) {
            dict.get("rad-probe-num-teeth-" + i).loadDefault();
            dict.get("rad-probe-a-" + i).loadDefault();
            dict.get("rad-probe-x-" + i).loadDefault();
            dict.get("rad-probe-z-" + i).loadDefault();
            dict.get("rad-probe-y-" + i).loadDefault();
            dict.get("rad-probe-corr-level-a-" + i).loadDefault();
            dict.get("rad-probe-rapid-index-" + i).loadDefault();
        }
    }
    public void loadPageDefaults(int page) {
        dict.get("grind-name-" + page).loadDefault();
        dict.get("spindle-rpm-" + page).loadDefault();
        dict.get("coolant-valves-" + page).loadDefault();
        dict.get("cyl-grind-rpm-" + page).loadDefault();
        dict.get("steadyrest-" + page).loadDefault();
        dict.get("touch-probe-op-" + page).loadDefault();
        dict.get("high-pressure-" + page).loadDefault();
        dict.get("num-flutes-" + page).loadDefault();
        dict.get("quick-a-ret-" + page).loadDefault();
        dict.get("rough-passes-" + page).loadDefault();
        dict.get("finish-passes-" + page).loadDefault();
        dict.get("plunge-feed-" + page).loadDefault();
        dict.get("rough-feed-in-" + page).loadDefault();
        dict.get("rough-feed-out-" + page).loadDefault();
        dict.get("finish-feed-in-" + page).loadDefault();
        dict.get("finish-feed-out-" + page).loadDefault();
        dict.get("lift-off-feed-" + page).loadDefault();
        dict.get("spiral-lead-" + page).loadDefault();
        dict.get("dwell-" + page).loadDefault();
        dict.get("ir-a-" + page).loadDefault();
        dict.get("ir-x-" + page).loadDefault();
        dict.get("ir-y-" + page).loadDefault();
        dict.get("ir-z-" + page).loadDefault();
        dict.get("if-a-" + page).loadDefault();
        dict.get("if-x-" + page).loadDefault();
        dict.get("if-y-" + page).loadDefault();
        dict.get("if-z-" + page).loadDefault();
        dict.get("plg-x-" + page).loadDefault();
        dict.get("plg-y-" + page).loadDefault();
        dict.get("plg-z-" + page).loadDefault();
        dict.get("0pt-a-" + page).loadDefault();
        dict.get("0pt-x-" + page).loadDefault();
        dict.get("0pt-y-" + page).loadDefault();
        dict.get("0pt-z-" + page).loadDefault();
        dict.get("0pt-c-" + page).loadDefault();
        dict.get("gl1-a-" + page).loadDefault();
        dict.get("gl1-x-" + page).loadDefault();
        dict.get("gl1-y-" + page).loadDefault();
        dict.get("gl1-z-" + page).loadDefault();
        dict.get("gl1-c-" + page).loadDefault();
        dict.get("lo-a-" + page).loadDefault();
        dict.get("lo-x-" + page).loadDefault();
        dict.get("lo-y-" + page).loadDefault();
        dict.get("lo-z-" + page).loadDefault();
        dict.get("ret-x-" + page).loadDefault();
        dict.get("ret-y-" + page).loadDefault();
        dict.get("ret-z-" + page).loadDefault();
        dict.get("contours-" + page).loadDefault();
        dict.get("rotation-a-" + page).loadDefault();
        dict.get("rotation-c-" + page).loadDefault();
    }
    private void initGrindPageEntries(int page) {
        ITPair itp;
        int x = page * 100;
        dict.put("grind-name-" + page, new EMDString(10*page, 0xa0));
        putInt("spindle-rpm", page, 82);
        EMDInt ei = putInt("coolant-valves", page, 81);
        ei.allowNegative(false);
        putInt("cyl-grind-rpm", page, 87);
        itp = ITPair.findPair(x+88);
        dict.put("steadyrest-" + page, new EMDBool(itp.index, itp.type));
        itp = ITPair.findPair(x+86);
        dict.put("touch-probe-op-" + page,
                 new EMDChoice(itp.index, itp.type,
                               "0", "1", "2", "3", "4", "5", "6"));
        itp = ITPair.findPair(x+89);
        EMDBool eb = new EMDBool(itp.index, itp.type);
        eb.setDefaultValue(true);
        dict.put("high-pressure-" + page, eb);
        ei = putInt("num-flutes", page, 83);
        ei.allowNegative(false);
        ei.setDefaultValue(1);
        itp = ITPair.findPair(x+6);
        dict.put("quick-a-ret-" + page, new EMDBool(itp.index, itp.type));
        ei = putInt("rough-passes", page, 84);
        ei.allowNegative(false);
        ei = putInt("finish-passes", page, 85);
        ei.allowNegative(false);
        EMDFloat ef = putFloat("plunge-feed", page, 29, true);
        ef.allowNegative(false);
        ef = putFloat("rough-feed-in", page, 39, true);
        ef.allowNegative(false);
        ef = putFloat("rough-feed-out", page, 40, true);
        ef.allowNegative(false);
        ef.setDefaultValue(600);
        ef = putFloat("finish-feed-in", page, 18, true);
        ef.allowNegative(false);
        ef = putFloat("finish-feed-out", page, 19, true);
        ef.allowNegative(false);
        ef.setDefaultValue(600);
        ef = putFloat("lift-off-feed", page, 79, true);
        ef.allowNegative(false);
        ef = putFloat("spiral-lead", page, 38, true);
        ef.allowNegative(false);
        ei = putInt("dwell", page, 41);
        ei.allowNegative(false);
        // ------------------------------------------------------------------
        putFloat("ir-a", page, 0, false);
        putFloat("ir-x", page, 1, true);
        putFloat("ir-y", page, 3, true);
        putFloat("ir-z", page, 2, true);
        
        putFloat("if-a", page, 10, false);
        putFloat("if-x", page, 11, true);
        putFloat("if-y", page, 13, true);
        putFloat("if-z", page, 12, true);
        
        putFloat("plg-x", page, 21, true);
        putFloat("plg-y", page, 23, true);
        putFloat("plg-z", page, 22, true);
        
        putFloat("0pt-a", page, 60, false);
        putFloat("0pt-x", page, 61, true);
        putFloat("0pt-y", page, 63, true);
        putFloat("0pt-z", page, 62, true);
        putFloat("0pt-c", page, 65, false);
        
        putFloat("gl1-a", page, 30, false);
        putFloat("gl1-x", page, 31, true);
        putFloat("gl1-y", page, 33, true);
        putFloat("gl1-z", page, 32, true);
        putFloat("gl1-c", page, 35, false);
        
        putFloat("lo-a", page, 70, false);
        putFloat("lo-x", page, 71, true);
        putFloat("lo-y", page, 73, true);
        putFloat("lo-z", page, 72, true);
        
        putFloat("ret-x", page, 91, true);
        putFloat("ret-y", page, 93, true);
        putFloat("ret-z", page, 92, true);
        // ------------------------------------------------------------------
        dict.put("contours-" + page, new EMDString(page*10+8, 0xa0));
        putFloat("rotation-a", page, 4, false);
        putFloat("rotation-c", page, 5, false);
    }
    public void setNames(String fileName, String dataName) {
        this.fileName = fileName;
        this.dataName = dataName;
    }
    public EMData get(String name) {
        return dict.get(name);
    }
    public void clear() {
        for (EMData d : dict.values())
            d.clear();
    }
    public void onInt(int index, int type, int value) throws Exception {
        EMDBool eb;
        EMDChoice ec;
        switch (index) {
        case 0: {
            // value is 4 or 5
            ec = (EMDChoice)dict.get("control");
            ec.load(index, type, value, 4);
            break;
        }
        case 1: {
            eb = (EMDBool)dict.get("interrupt-after-probing");
            eb.load(index, type, value);
            break;
        }
        case 2: {
            ec = (EMDChoice)dict.get("probe-len");
            ec.load(index, type, value, 0);
            break;
        }
        case 4: {
            ec = (EMDChoice)dict.get("probe-radial-pos");
            ec.load(index, type, value, 0);
            break;
        }
        case 5: {
            eb = (EMDBool)dict.get("hmc400-var-speed");
            eb.load(index, type, value);
            break;
        }
        default: {
            if (index >= 171 && index <= 176) {
                EMDInt ei = (EMDInt)dict.get("rad-probe-num-teeth-" +
                                             (index - 170));
                ei.load(index, type, value);
            }
            else
                throw new Exception("unhandled integer data");
            break;
        }
        }
    }
    public void onFloat(int index, int type, float value) throws Exception {
        // System.out.format("onFloat: %d, %d, %f\n", index, type, value);
        EMDFloat ef;
        int guiId = index + 256 * ((byte)type & 0x0f);
        // System.out.format("onFloat: index:%d, type:%d, value:%f guiId:%d\n",
        //                   index, type, value, guiId);
        // Selection of Operations
        if (onFloatInt(180, 80, index, type, value,
                       "op-order-"))
            return;
        // Stock removal A
        else if (guiId == 4)  {
            ef = (EMDFloat)dict.get("stock-removal-a");
            assert(ef != null);
            ef.load(index, type, value, false);
        }
        // Stock removal X1
        else if (guiId == 9) {
            ef = (EMDFloat)dict.get("stock-removal-x1");
            ef.load(index, type, value, true);
        }
        // Stock removal Z1
        else if (guiId == 8) {
            ef = (EMDFloat)dict.get("stock-removal-z1");
            ef.load(index, type, value, true);
        }
        // Length Probing / Zeropoint X
        if (guiId == 1) {
            ef = (EMDFloat)dict.get("zeropoint-x");
            ef.load(index, type, value, true);
        }
        // Length Probing / Zeropoint Y
        else if (guiId == 3) {
            ef = (EMDFloat)dict.get("zeropoint-y");
            ef.load(index, type, value, true);
        }
        // Length Probing / Zeropoint Z
        else if (guiId == 2) {
            ef = (EMDFloat)dict.get("zeropoint-z");
            ef.load(index, type, value, true);
        }
        // Length Probing / Maximum measuring length X
        else if (guiId == 464) {
            ef = (EMDFloat)dict.get("max-measure-len-x");
            ef.load(index, type, value, true);
        }
        // Radial Probing A
        else if (guiId == 1710 || guiId == 1720 || guiId == 1730 ||
                 guiId == 1740 || guiId == 1750 || guiId == 1760) {
            ef = (EMDFloat)dict.get("rad-probe-a-" + ((guiId - 1700) / 10));
            ef.load(index, type, value, false);
        }
        // Radial Probing X
        else if (guiId == 1711 || guiId == 1721 || guiId == 1731 ||
                 guiId == 1741 || guiId == 1751 || guiId == 1761) {
            ef = (EMDFloat)dict.get("rad-probe-x-" + ((guiId - 1701) / 10));
            ef.load(index, type, value, true);
        }
        // Radial Probing Y
        else if (guiId == 1713 || guiId == 1723 || guiId == 1733 ||
                 guiId == 1743 || guiId == 1753 || guiId == 1763) {
            ef = (EMDFloat)dict.get("rad-probe-y-" + ((guiId - 1703) / 10));
            ef.load(index, type, value, true);
        }
        // Radial Probing Z
        else if (guiId == 1712 || guiId == 1722 || guiId == 1732 ||
                 guiId == 1742 || guiId == 1752 || guiId == 1762) {
            ef = (EMDFloat)dict.get("rad-probe-z-" + ((guiId - 1702) / 10));
            ef.load(index, type, value, true);
        }
        // Radial Probing Rapid Index
        else if (guiId == 1715 || guiId == 1725 || guiId == 1735 ||
                 guiId == 1745 || guiId == 1755 || guiId == 1765) {
            ef = (EMDFloat)dict.get("rad-probe-rapid-index-" +
                                    ((guiId - 1705) / 10));
            ef.load(index, type, value, false);
        }
        // Radial Probing Correction to Level A
        else if (guiId == 1714 || guiId == 1724 || guiId == 1734 ||
                 guiId == 1744 || guiId == 1754 || guiId == 1764) {
            ef = (EMDFloat)dict.get("rad-probe-corr-level-a-" +
                                    ((guiId - 1704) / 10));
            ef.load(index, type, value, false);
        }
        // Spindle speed (rpm)
        else if (onFloatInt(182, 82, index, type, value,
                            "spindle-rpm-"))
            return;
        // Coolant Valves 1,2,3 for spindle 1, 4,5,6 for spindle 2
        else if (onFloatInt(181, 81, index, type, value,
                            "coolant-valves-"))
            return;
        // Cyl. grinding (0=no >0=rpm)
        else if (onFloatInt(187, 87, index, type, value,
                            "cyl-grind-rpm-"))
            return;
        // Steadyrest
        else if (onFloatBool(188, 88, index, type, value,
                             "steadyrest-"))
            return;
        else if (guiId == 186 || guiId == 286 || guiId == 386 ||
                 guiId == 486 || guiId == 586 || guiId == 686 ||
                 guiId == 786 || guiId == 886 || guiId == 986 ||
                 guiId == 1086 || guiId == 1186 || guiId == 1286 ||
                 guiId == 1386 || guiId == 1486 || guiId == 1586) {
            EMDChoice ec = (EMDChoice)dict.get("touch-probe-op-" +
                                               ((guiId - 86) / 100));
            ec.load(index, type, (int)value, 0);
        }
        // High Pressure
        else if (onFloatBool(189, 89, index, type, value,
                             "high-pressure-"))
            return;
        // Number of flutes
        else if (onFloatInt(183, 83, index, type, value,
                            "num-flutes-"))
            return;
        // Quick A return
        else if (onFloatBool(106, 6, index, type, value,
                             "quick-a-ret-"))
            return;
        // Number of passes rough
        else if (onFloatInt(184, 84, index, type, value,
                            "rough-passes-"))
            return;
        // Number of passes finish
        else if (onFloatInt(185, 85, index, type, value,
                            "finish-passes-"))
            return;
        // Feedrate plunging
        else if (onFloatFloat(129, 29, index, type, value, true,
                              "plunge-feed-"))
            return;
        // Feedrate roughing in
        else if (onFloatFloat(139, 39, index, type, value, true,
                              "rough-feed-in-"))
            return;
        // Feedrate roughing out
        else if (onFloatFloat(140, 40, index, type, value, true,
                              "rough-feed-out-"))
            return;
        // Feedrate finishing in
        else if (onFloatFloat(118, 18, index, type, value, true,
                              "finish-feed-in-"))
            return;
        // Feedrate finishing out
        else if (onFloatFloat(119, 19, index, type, value, true,
                              "finish-feed-out-"))
            return;
        // Feedrate for lift-off (0=rapid)
        else if (onFloatFloat(179, 79, index, type, value, true,
                              "lift-off-feed-"))
            return;
        // Spiral Lead*
        else if (onFloatFloat(138, 38, index, type, value, true,
                              "spiral-lead-"))
            return;
        // Dwell
        else if (onFloatInt(141, 41, index, type, value, "dwell-"))
            return;
        // I/r A X Y Z
        else if (onFloatFloat(100, 0, index, type, value, false,
                              "ir-a-"))
            return;
        else if (onFloatFloat(101, 1, index, type, value, true,
                              "ir-x-"))
            return;
        else if (onFloatFloat(103, 3, index, type, value, true,
                              "ir-y-"))
            return;
        else if (onFloatFloat(102, 2, index, type, value, true,
                              "ir-z-"))
            return;
        // I/f A X Y Z
        else if (onFloatFloat(110, 10, index, type, value, false,
                              "if-a-"))
            return;
        else if (onFloatFloat(111, 11, index, type, value, true,
                              "if-x-"))
            return;
        else if (onFloatFloat(113, 13, index, type, value, true,
                              "if-y-"))
            return;
        else if (onFloatFloat(112, 12, index, type, value, true,
                              "if-z-"))
            return;
        // Plg X Y Z
        else if (onFloatFloat(121, 21, index, type, value, true,
                              "plg-x-"))
            return;
        else if (onFloatFloat(123, 23, index, type, value, true,
                              "plg-y-"))
            return;
        else if (onFloatFloat(122, 22, index, type, value, true,
                              "plg-z-"))
            return;
        // 0pt A X Y Z C
        else if (onFloatFloat(160, 60, index, type, value, false,
                              "0pt-a-"))
            return;
        else if (onFloatFloat(161, 61, index, type, value, true,
                              "0pt-x-"))
            return;
        else if (onFloatFloat(163, 63, index, type, value, true,
                              "0pt-y-"))
            return;
        else if (onFloatFloat(162, 62, index, type, value, true,
                              "0pt-z-"))
            return;
        else if (onFloatFloat(165, 65, index, type, value, false,
                              "0pt-c-"))
            return;
        // GL1 A X Y Z C
        else if (onFloatFloat(130, 30, index, type, value, false,
                              "gl1-a-"))
            return;
        else if (onFloatFloat(131, 31, index, type, value, true,
                              "gl1-x-"))
            return;
        else if (onFloatFloat(133, 33, index, type, value, true,
                              "gl1-y-"))
            return;
        else if (onFloatFloat(132, 32, index, type, value, true,
                              "gl1-z-"))
            return;
        else if (onFloatFloat(135, 35, index, type, value, false,
                              "gl1-c-"))
            return;
        // L/O A X Y Z
        else if (onFloatFloat(170, 70, index, type, value, false,
                              "lo-a-"))
            return;
        else if (onFloatFloat(171, 71, index, type, value, true,
                              "lo-x-"))
            return;
        else if (onFloatFloat(173, 73, index, type, value, true,
                              "lo-y-"))
            return;
        else if (onFloatFloat(172, 72, index, type, value, true,
                              "lo-z-"))
            return;
        // Ret X Y Z
        else if (onFloatFloat(191, 91, index, type, value, true,
                              "ret-x-"))
            return;
        else if (onFloatFloat(193, 93, index, type, value, true,
                              "ret-y-"))
            return;
        else if (onFloatFloat(192, 92, index, type, value, true,
                              "ret-z-"))
            return;
        // Rotation:A
        else if (onFloatFloat(104, 4, index, type, value, false,
                              "rotation-a-"))
            return;
        // Rotation:C
        else if (onFloatFloat(105, 5, index, type, value, false,
                              "rotation-c-"))
            return;
        
        // ==================================================================
        // Contour Segment Data
        // ==================================================================
        
        // Straights/Angle
        else if (guiId == 1611 || guiId == 1621 || guiId == 1631 ||
                 guiId == 1641 || guiId == 1651 || guiId == 1661 ||
                 guiId == 1671 || guiId == 1681 || guiId == 1691) {
            ef = (EMDFloat)dict.get("straights-angle-" +
                                    straightsNames[(guiId - 1601) / 10 - 1]);
            ef.load(index, type, value, false);
        }
        // Straights/Distance
        else if (guiId == 1612 || guiId == 1622 || guiId == 1632 ||
                 guiId == 1642 || guiId == 1652 || guiId == 1662 ||
                 guiId == 1672 || guiId == 1682 || guiId == 1692) {
            ef = (EMDFloat)dict.get("straights-distance-" +
                                    straightsNames[(guiId - 1602) / 10 - 1]);
            ef.load(index, type, value, true);
        }
        // Radii/Start
        else if (guiId == 1614 || guiId == 1624 || guiId == 1634 ||
                 guiId == 1644 || guiId == 1654 || guiId == 1664 ||
                 guiId == 1674 || guiId == 1684 || guiId == 1694) {
            ef = (EMDFloat)dict.get("radii-start-" +
                                    radiiNames[(guiId - 1604) / 10 - 1]);
            ef.load(index, type, value, false);
        }
        // Radii/Rotation
        else if (guiId == 1615 || guiId == 1625 || guiId == 1635 ||
                 guiId == 1645 || guiId == 1655 || guiId == 1665 ||
                 guiId == 1675 || guiId == 1685 || guiId == 1695) {
            ef = (EMDFloat)dict.get("radii-rotation-" +
                                    radiiNames[(guiId - 1605) / 10 - 1]);
            ef.load(index, type, value, false);
        }
        // Radii/Radius
        else if (guiId == 1616 || guiId == 1626 || guiId == 1636 ||
                 guiId == 1646 || guiId == 1656 || guiId == 1666 ||
                 guiId == 1676 || guiId == 1686 || guiId == 1696) {
            ef = (EMDFloat)dict.get("radii-radius-" +
                                    radiiNames[(guiId - 1606) / 10 - 1]);
            ef.load(index, type, value, true);
        }
    }
    /**
       Handle a string read from the data file.

       @param index the index byte read from the file
       @param type the type byte read from the data file
       @param value the string read from the data file
    */
    public void onString(int index, int type, String value) throws Exception {
        EMDString es;
        if (index == 0) {
            es = (EMDString)dict.get("probe-data-file-name");
            es.load(index, type, value);
        }
        else if ((index % 10) == 0 && index >= 10 && index <= 150) {
            es = (EMDString)dict.get("grind-name-" + (index / 10));
            es.load(index, type, value);
        }
        else if (((index - 8) % 10) == 0 && index >= 28 && index <= 178) {
            es = (EMDString)dict.get("contours-" + ((index - 8) / 10));
            es.load(index, type, value);
        }
    }
    /**
       Helper method to return true if the floating point data index and type
       bytes match a set of values.
    */
    private boolean match(int id, int base) {
        return id == base || id == base+100 || id == base+200
            || id == base+300 || id == base+400 || id == base+500
            || id == base+600 || id == base+700 || id == base+800
            || id == base+900 || id == base+1000 || id == base+1100
            || id == base+1200 || id == base+1300 || id == base+1400;
    }
    /**
       Helper method to store floating point data as an integer.
    */
    private boolean onFloatInt(int base, int sub, int index, int type,
                               float value, String prefix) {
        int guiId = index + 256 * ((byte)type & 0x0f);
        if (match(guiId, base)) {
            EMDInt ei = (EMDInt)dict.get(prefix + ((guiId - sub) / 100));
            ei.load(index, type, (int)value);
            return true;
        }
        return false;
    }
    /**
       Helper method to store floating point data as a float.
    */
    private boolean onFloatFloat(int base, int sub, int index, int type,
                                 float value, boolean metric,
                                 String prefix) {
        int guiId = index + 256 * ((byte)type & 0x0f);
        if (match(guiId, base)) {
            EMDFloat ef = (EMDFloat)dict.get(prefix + ((guiId - sub) / 100));
            ef.load(index, type, value, metric);
        }
        return false;
    }
    /**
       Helper method to store floating point data as a boolean.
    */
    private boolean onFloatBool(int base, int sub, int index, int type,
                                float value, String prefix) {
        int guiId = index + 256 * ((byte)type & 0x0f);
        if (match(guiId, base)) {
            EMDBool eb = (EMDBool)dict.get(prefix + ((guiId - sub) / 100));
            eb.load(index, type, (int)value);
            return true;
        }
        return false;
    }
    public void save() throws Exception {
        save(this.fileName);
    }
    public void save(String fileName) throws Exception {
        try {
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int N = 207;
            // magic number
            EMData.writeByte(bos, 0x02);
            EMData.writeByte(bos, 0x7e);
            // 22 zeros
            for (int i=0; i<22; ++i)
                EMData.writeByte(bos, 0x0);
            // data name marker byte
            EMData.writeByte(bos, 0x01);
            // data name (24 bytes)
            EMData.writeString(bos, dataName);
            // the rest of the header
            for (int i=0; i<N; ++i)
                EMData.writeByte(bos, 0x0);
            // the int, float and string data
            // System.out.println("writing " + dict.size() + " elements");
            for (Map.Entry<String, EMData> e : dict.entrySet())
                e.getValue().write(bos);
            EMData.writeByte(bos, 0x0); // index=0
            EMData.writeByte(bos, 0x0); // type=0, signify the end of input?
            bos.flush();
            bos.close();
            fos.close();
        }
        catch (FileNotFoundException e) {
            throw new Exception("error saving data file: " + fileName);
        }
    }
    public void dump() {
        for (Map.Entry<String, EMData> e : dict.entrySet())
            System.out.println(e.getKey() + ":\n" + e.getValue());
        System.out.println("prog size:" + dict.size());
    }
    public static void main(String[] args) throws Exception {
        EMProgram prog = new EMProgram();
        EMDataReader dr = new EMDataReader(args[0]);
        dr.read(prog);
        for (Map.Entry<String, EMData> e : prog.dict.entrySet())
            System.out.println(e.getKey() + ":\n" + e.getValue());
    }
    // shite...
    public void put(String key, String value) {
        ((EMDString)dict.get(key)).setValue(value);
    }
    public void put(String key, int value) {
        ((EMDInt)dict.get(key)).setValue(value);
    }
    public void put(String key, double value) {
        // System.out.println("key:" + key + " value:" + value);
        ((EMDFloat)dict.get(key)).setValue((float)value);
    }
    public void put(String key, boolean value) {
        ((EMDBool)dict.get(key)).setValue(value);
    }
}
