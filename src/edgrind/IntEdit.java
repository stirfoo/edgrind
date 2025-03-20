/*
  IntEdit.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind;

import javax.swing.JTextField;
//
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
   A validated integer input component.
 */
@SuppressWarnings("serial")
class IntEdit extends LineEdit {
    protected int value;
    protected int minValue = Integer.MIN_VALUE;
    protected int maxValue = Integer.MAX_VALUE;
    IntEdit(int value, int minValue, int maxValue, int flags) {
        super();
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.flags = flags;
        setText("" + value);
    }
    IntEdit(int value, int minValue, int maxValue) {
        this(value, minValue, maxValue, 0);
    }
    IntEdit(int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }
    @Override
    public void onChange() {
        String s = getText();
        setOk(true);
        if (s.isEmpty() && !allowEmpty())
            setOk(false);
        else {
            try {
                long x = Integer.parseInt(s);
                if ((x < 0 && !allowNegative()) ||
                    (x < minValue || x > maxValue) ||
                    (x == 0 && !allowZero())) {
                    setOk(false);
                }
                else {
                    value = (int)x;
                    setOk(true);
                }
            }
            catch (Exception ignore) {
                setOk(false);
            }
        }
    }
    protected void setOk(boolean b) {
        ok = b;
        setBackground(ok ? BGColor.goodColor : BGColor.badColor);
    }
    public int getValue() {
        return value;
    }
}
