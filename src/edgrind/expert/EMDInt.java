/*
  EMDInt.java
  S. Edward Dolan
  Wednesday, August  9 2023
*/

package edgrind.expert;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JTextField;
import java.io.BufferedOutputStream;
//
import edgrind.BGColor;

/**
   An Expert Mode integral data element.

   @param value the int value
 */
public class EMDInt extends EMData {
    private int value;
    private int defaultValue;
    private boolean allowNegative = true;
    private boolean allowEmpty = true;
    class IntInputListener implements DocumentListener {
        JTextField tf;
        IntInputListener(JTextField tf) {
            this.tf = tf;
        }
        public void changedUpdate(DocumentEvent e) {onChange();}
        public void removeUpdate(DocumentEvent e) {onChange();}
        public void insertUpdate(DocumentEvent e) {onChange();}
        public void onChange() {
            try {
                String s = tf.getText();
                if (s.isEmpty()) {
                    if (allowEmpty) {
                        value = defaultValue;
                        tf.setBackground(BGColor.mehColor);
                        initialized = false;
                    }
                    else
                        throw new Exception("fubar number");
                }
                else {
                    value = Integer.parseInt(s);
                    if (!allowNegative && value < 0)
                        throw new Exception("fubar number");
                    tf.setBackground(BGColor.goodColor);
                    initialized = true;
                }
            }
            catch (Exception e) {
                tf.setBackground(BGColor.badColor);
            }
        }
    }
    EMDInt(int index, int type) {
        super.index = index;
        super.type = type;
        JTextField tf = new JTextField(10);
        widget = tf;
        tf.setBackground(BGColor.mehColor);
        tf.getDocument().addDocumentListener(new IntInputListener(tf));
    }
    void load(int index, int type, int value) {
        super.index = index;
        super.guiId = index;
        super.type = type;
        setValue(value);
    }
    void clear() {
        value = defaultValue;
        if (value == 0) {
            // just clear it
            initialized = false; // dont write to dat file
            ((JTextField)widget).setText("");
        }
        else {
            initialized = true; // write to dat file
            ((JTextField)widget).setText("" + defaultValue);
        }
    }
    void loadDefault() {
        value = defaultValue;
        ((JTextField)widget).setText("" + value);
        super.initialized = true; // write to dat file
    }
    int value() {
        return value;
    }
    public void setValue(int x) {
        value = x;
        ((JTextField)widget).setText("" + x);
        super.initialized = true;
    }
    void setDefaultValue(int x) {
        defaultValue = x;
    }
    void allowNegative(boolean x) {
        allowNegative = x;
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
            throw new Exception("INTERNAL ERROR: EMDInt unknown type: " +
                                type);
    }
    public String toString() {
        return String.format("class:%-9s index:%02x type:%02x value:%d",
                             this.getClass().getSimpleName(), index, type,
                             value);
    }
}
