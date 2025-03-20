/*
  EMDFloat.java
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
// 
import edgrind.error.EMDataError;

/**
   An Expert Mode floating point data element.

   <p>Some values, like feedrates, and linear dimensions, are stored as
   millimeters in the data file.</p>
 */
public class EMDFloat extends EMData {
    /**
       The value read from the data file.
       <p>May be metric if {@ref metric} is true.</p>
    */
    private float value;
    /**
       Optional default value. This will be set when {@ref clear} is called.
     */
    private float defaultValue;
    /**
       True if the value is stored in the dat file in metric units.
     */
    private boolean metric = false;
    /**
       If true, {@ref value} may be < 0.
     */
    private boolean allowNegative = true;
    /**
       Set the background of the text input based on the text entered.
     */
    class FloatInputListener implements DocumentListener {
        JTextField tf;
        FloatInputListener(JTextField tf) {
            this.tf = tf;
        }
        public void changedUpdate(DocumentEvent e) {onChange();}
        public void removeUpdate(DocumentEvent e) {onChange();}
        public void insertUpdate(DocumentEvent e) {onChange();}
        public void onChange() {
            String s = tf.getText();
            if (s.isEmpty()) {
                value = defaultValue;
                tf.setBackground(BGColor.mehColor);
                initialized = false;
            }
            else
                try {
                    float f = Float.parseFloat(s);
                    if (!allowNegative && f < 0)
                        throw new EMDataError("neg num not allowed");
                    if (metric)
                        value = f * 25.4f;
                    else
                        value = f;
                    tf.setBackground(BGColor.goodColor);
                    initialized = true;
                }
                catch (Exception e) {
                    tf.setBackground(BGColor.badColor);
                }
        }
    }
    EMDFloat() {
        JTextField tf = new JTextField(10);
        widget = tf;
        tf.setBackground(BGColor.mehColor);
        tf.getDocument().addDocumentListener(new FloatInputListener(tf));
        this.defaultValue = 0.0f;
    }
    EMDFloat(int index, int type, boolean metric) {
        this();
        super.index = index;
        super.type = type;
        this.metric = metric;
    }
    void load(int index, int type, float value, boolean metric) {
        super.index = index;
        super.guiId = index + 256 * ((byte)type & 0x0f);
        super.type = type;
        this.metric = metric;
        setValue(metric ? value / 25.4f : value);
        super.initialized = true;
    }
    void clear() {
        value = defaultValue;
        ((JTextField)widget).setText("");
        super.initialized = false; // do not write to dat file
    }
    void loadDefault() {
        // System.out.println("EMDFloat.loadDefault()");
        value = defaultValue;
        ((JTextField)widget).setText("" + value);
        super.initialized = true; // write to dat file
    }
    float value() {
        if (metric)
            return value / 25.4f;
        else
            return value;
    }
    // NOTE: x must always be given in inches
    public void setValue(float x) {
        if (metric)
            value = x * 25.4f;
        else 
            value = x;
        ((JTextField)widget).setText("" + value());
        super.initialized = true;
    }
    public void setValue(double x) {
        setValue((float)x);     // yes it may squash it, don't care...
    }
    void setDefaultValue(float x) {
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
        if (type == 0x60) {
            writeFloat(s, (int)value);
        }
        else if (type >= 0x20 && type <= 0x26)
            writeFloat(s, value);
        else
            throw new EMDataError("INTERNAL ERROR: EMDFloat unknown type: " +
                                  type);
    }
    public String toString() {
        if (metric)
            return String.format("class:%-9s index:%02x type:%02x"
                                 + " value(%.6fmm, %.6fin)",
                                 this.getClass().getSimpleName(), index, type,
                                 value, value / 25.4);
        else
            return String.format("class:%-9s index:%02x type:%02x"
                                 + " value:%.6f",
                                 this.getClass().getSimpleName(), index, type,
                                 value);
    }
}
