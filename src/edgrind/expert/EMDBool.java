/*
  EMDBool.java
  S. Edward Dolan
  Wednesday, August  9 2023
*/

package edgrind.expert;

import javax.swing.JCheckBox;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
   An Expert Mode integral data element that specifies a yes/no value.

   @param value the integeral value read from the data file
 */
class EMDBool extends EMData {
    private boolean value;
    private boolean defaultValue;
    EMDBool() {
        super.widget = new JCheckBox();
        ((JCheckBox)widget).addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    value = (e.getStateChange() == ItemEvent.SELECTED);
                    initialized = true;
                }
            });
        defaultValue = false;        // good, bad, meh?
    }
    EMDBool(int index, int type) {
        this();
        super.index = index;
        super.type = type;
    }
    void load(int index, int type, float value) {
        super.index = index;
        super.type = type;
        setValue(value == 0 ? false : true);
        super.initialized = true;
    }
    void clear() {
        value = defaultValue;
        ((JCheckBox)widget).setSelected(value);
        super.initialized = false;
    }
    void loadDefault() {
        clear();
        super.initialized = true;
    }
    boolean value() {
        return value;
    }
    void setValue(boolean b) {
        value = b;
        ((JCheckBox)super.widget).setSelected(value);
        super.initialized = true;
    }
    void setDefaultValue(boolean x) {
        defaultValue = x;
    }
    void write(BufferedOutputStream s) throws Exception {
        if (!super.initialized)
            return;
        writeByte(s, index);
        writeByte(s, type);
        if (type == 0x60)
            writeInt(s, value ? 1 : 0);
        else if (type >= 0x20 && type <= 0x26)
            writeFloat(s, value ? 1.0f : 0.0f);
        else
            throw new Exception("INTERNAL ERROR: EMDBool unknow type: " +
                                type);
    }
    public String toString() {
        return String.format("class:%-9s index:%02x type:%02x value:%s",
                             this.getClass().getSimpleName(), index, type,
                             value ? "true" : "false");
    }
}
