/*
  FloatEdit.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind;

import java.awt.Dimension;
// 
import javax.swing.JTextField;
//
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
// 
// import javax.script.*;
//
import edgrind.sketch.SketchDim;

@SuppressWarnings("serial")
class FloatEdit extends LineEdit {
    protected double value;
    protected double minValue = Double.MIN_VALUE;
    protected double maxValue = Double.MAX_VALUE;
    // protected ScriptEngine expeng;
    FloatEdit(double value, double minValue, double maxValue, int flags) {
        super();
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.flags = flags;
        // ScriptEngineManager sem = new ScriptEngineManager();
        // this.expeng = sem.getEngineByName("JavaScript");
        setText(String.format(SketchDim.FMT_LIN, value));
        Dimension d = getPreferredSize();
        setMinimumSize(d);
    }
    FloatEdit(double value, double minValue, double maxValue) {
        this(value, minValue, maxValue, 0);
    }
    FloatEdit(double value) {
        this(value, Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }
    @Override
    public void onChange() {
        // System.out.println("FloatEdit.onChange()");
        ok = false;
        String s = getText();
        setBackground(BGColor.goodColor);
        if (s.isEmpty())
            setBackground(allowEmpty() ? BGColor.mehColor : BGColor.badColor);
        else {
            try {
                // double x = (double)expeng.eval(s);
                // System.out.println("x:" + x);
                double x = Double.parseDouble(s);
                if ((x < 0 && !allowNegative()) ||
                    (x == 0 && !allowZero()) |
                    (x < minValue || x > maxValue))
                    setBackground(BGColor.badColor);
                else {
                    value = x;
                    ok = true;
                }
            }
            catch (Exception ignore) {
                setBackground(BGColor.badColor);                
            }
        }
    }
    public double getValue() {
        return value;
    }
    public void setValue(double x) {
        setText(String.format(SketchDim.FMT_LIN, x));
    }
}
