/*
  EMDChoice.java
  S. Edward Dolan
  Wednesday, August  9 2023
*/

package edgrind.expert;

import java.io.BufferedOutputStream;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
   An Expert Mode integral data element that specifies an enumerated value.

   @param value the integeral value read from the data file
   @param choices the enumerated values
 */
class EMDChoice extends EMData {
    private int value;
    private int defaultValue;
    private int baseValue;
    private String[] choices;
    class ChoiceInputListener implements ItemListener {
        JComboBox<String> cbo;
        ChoiceInputListener(JComboBox<String> cbo) {
            this.cbo = cbo;
        }
        @Override
        public void itemStateChanged(ItemEvent e) {
            value = cbo.getSelectedIndex() + baseValue;
            initialized = true;
        }
    }
    EMDChoice(String... choices) {
        this.choices = choices;
        JComboBox<String> cbo = new JComboBox<String>();
        widget = cbo;
        cbo.setModel(new DefaultComboBoxModel<String>(choices));
        cbo.addItemListener(new ChoiceInputListener(cbo));
    }
    EMDChoice(int index, int type, String... choices) {
        this(choices);
        super.index = index;
        super.type = type;
    }
    void load(int index, int type, int value, int baseValue) {
        super.index = index;
        super.type = type;
        this.value = value;
        this.baseValue = baseValue;
        super.initialized = true;
        ((JComboBox)super.widget).setSelectedIndex(selectIndex());
    }
    void clear() {
        value = defaultValue;
        ((JComboBox)super.widget).setSelectedIndex(selectIndex());
        super.initialized = false;
    }
    void loadDefault() {
        clear();
        super.initialized = true;
    }
    int value() {
        return value;
    }
    // i = base 0
    void setSelectedIndex(int i) {
        ((JComboBox)super.widget).setSelectedIndex(i);
        super.initialized = true;
    }
    int selectIndex() {
        return value - baseValue;
    }
    void setBaseValue(int x) {
        baseValue = 4;
    }
    void setDefaultValue(int x) {
        defaultValue = x;
    }
    void write(BufferedOutputStream s) throws Exception {
        if (!super.initialized)
            return;
        writeByte(s, index);
        writeByte(s, type);
        if (type == 0x60)
            writeInt(s, value);
        else if (type >= 0x20 && type <= 0x26)
            writeFloat(s, (float)value);
        else
            throw new Exception("INTERNAL ERROR: EMDChoice unknown type: " +
                                type);
    }
    public String toString() {
        String s = String.format("class:%-9s index:%02x type:%02x value:%d",
                                 this.getClass().getSimpleName(), index, type,
                                 value);
        s += " choices:";
        boolean b = false;
        for (String x : choices) {
            s += (b ? " " : "") + x;
            b = true;
        }
        return s;
    }
}
