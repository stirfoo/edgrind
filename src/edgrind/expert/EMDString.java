/*
  EMDString.java
  S. Edward Dolan
  Wednesday, August  9 2023
*/

package edgrind.expert;

import javax.swing.JTextField;
import java.io.BufferedOutputStream;

/**
   An Expert Mode string data element.

   @param value the string value
 */
public class EMDString extends EMData {
    private String value;
    private String defaultValue;
    EMDString() {
        widget = new JTextField();
    }
    EMDString(int index, int type) {
        this();
        super.index = index;
        super.type = type;
    }
    void load(int index, int type, String value) {
        super.index = index;
        super.guiId = index;
        super.type = type;
        setValue(value);
    }
    void clear() {
        value = defaultValue;
        super.initialized = value == null ? false : true;
        ((JTextField)super.widget).setText(value);
    }
    void loadDefault() {
        clear();
    }
    String value() {
        return value;
    }
    public void setValue(String s) {
        value = s;
        ((JTextField)super.widget).setText(value);
        super.initialized = true;
    }
    public void setDefaultValue(String x) {
        defaultValue = x;
    }
    void write(BufferedOutputStream s) throws Exception {
        if (!super.initialized)
            return;
        writeByte(s, index);
        writeByte(s, type);
        writeByte(s, (byte)value.length());
        writeString(s, value);
    }
    public String toString() {
        return String.format("class:%-9s index:%02x type:%02x value:%s",
                             this.getClass().getSimpleName(), index, type,
                             value);
    }
}
