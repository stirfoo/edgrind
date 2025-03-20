/*
  LineEdit.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind;

import javax.swing.JTextField;
//
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
//
import java.awt.Component;
import java.awt.KeyboardFocusManager;
// 
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

/**
   Base class for a validated text input.
 */
@SuppressWarnings("serial")
class LineEdit extends JTextField implements DocumentListener, FocusListener {
    /**
       Define the optional behavior of this text input.

       The value may be zero or more of the following values, or'd together.
       <ul>
       <li>{@link #ALLOW_EMPTY}</li>
       <li>{@link #ALLOW_NEGATIVE}</li>
       <li>{@link #ALLOW_ZERO}</li>
       </ul>
    */
    protected int flags = 0;
    /** true if the input value is valid */
    protected boolean ok = false;
    /** a valid value may be empty */
    protected final static int ALLOW_EMPTY = 1;
    /** a valid numeric value may be negative */
    protected final static int ALLOW_NEGATIVE = 2;
    /** a valid numeric value may be zero */
    protected final static int ALLOW_ZERO = 4;
    /**
       The only constructor.
    */
    LineEdit() {
        super(10);
        getDocument().addDocumentListener(this);
        addFocusListener(this);
    }
    /**
       @return true if the last entered value is valid;
    */
    public boolean isOk() {
        return ok;
    }
    /**
       @return true if the value may be empty
    */
    protected boolean allowEmpty() {
        // what a stupid pig fuck of a language
        return (flags & ALLOW_EMPTY) == ALLOW_EMPTY ? true : false;
    }
    /**
       @return true if the numeric value may be < 0
    */
    protected boolean allowNegative() {
        return (flags & ALLOW_NEGATIVE) == ALLOW_NEGATIVE ? true : false;
    }
    /**
       @return true if the numeric value may be zero
    */
    protected boolean allowZero() {
        return (flags & ALLOW_ZERO) == ALLOW_ZERO ? true : false;
    }
    public void changedUpdate(DocumentEvent e) {onChange();}
    public void removeUpdate(DocumentEvent e) {onChange();}
    public void insertUpdate(DocumentEvent e) {onChange();}
    public void onChange() {}
    @Override
    public void focusGained(FocusEvent e) {
        selectAll();
    }
    /**
       Trigger a key press event when the field loses focus.
    */
    @Override
    public void focusLost(FocusEvent e) {
        KeyEvent ke = new KeyEvent(this, KeyEvent.KEY_PRESSED,
                                   System.currentTimeMillis(), 0,
                                   KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
        this.dispatchEvent(ke);
    }
}
