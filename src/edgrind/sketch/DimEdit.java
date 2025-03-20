/*
  DimEdit.java
  S. Edward Dolan
  Wednesday, September 13 2023
*/

package edgrind.sketch;

import edgrind.Dict;
import edgrind.BGColor;
// 
import java.awt.Color;
// 
import javax.swing.JTextField;
//
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
   A custom JTextField to ensure a valid number is entered.

   <p>
   The background will be updated at each change to the box reflecting the
   validity which can be checked with isOk().
   </p>

   <p>
   Although this is fundamentally the same as {@link edgrind.FloatEdit} it is
   a much more specialized numeric input component closely coupled with {@link
   edgrind.sketch.DimLabel} and {@link edgrind.sketch.SketchScene}.
   </p>
 */
@SuppressWarnings("serial")
public class DimEdit extends JTextField implements DocumentListener {
    /** The associated label. */
    protected DimLabel label;
    /** The parent scene. */
    protected SketchScene scene;
    /** true if the edit box currently holds a valid value. */
    private boolean ok;
    /** The last valid  value entered. */
    private double value; 
    DimEdit(SketchScene scene) {
        this.scene = scene;
        getDocument().addDocumentListener(this);
    }
    boolean isOk() {
        return ok;
    }
    double getValue() {
        return value;
    }
    void setLabel(DimLabel label) {
        this.label = label;
        setText(label.getText());
    }
    public void changedUpdate(DocumentEvent e) {onChange(e);}
    public void removeUpdate(DocumentEvent e) {onChange(e);}
    public void insertUpdate(DocumentEvent e) {onChange(e);}
    /**
       Check the text for a valid number.

       The background is set depending on the text. If the text is good,
       <em>value</em> will be updated and <em>ok</em> will be set true.
    */
    public void onChange(DocumentEvent e) {
        ok = false;
        String s = getText();
        if (s.isEmpty())
            setBackground(BGColor.badColor);
        else
            try {
                value = Double.parseDouble(s);
                Dict d = new Dict(label.getName(), value);
                if (label.sketch.checkGeometry(d)) {
                    setBackground(BGColor.goodColor);
                    ok = true;
                }
                else
                    setBackground(BGColor.badColor);
            }
            catch (Exception ignore) {
                setBackground(BGColor.badColor);
            }
    }
}
