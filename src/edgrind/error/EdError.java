/*
  EdError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

import java.awt.Component;
//
import javax.swing.JOptionPane;

/**
   The abstract base class for all EdGrind errors.
 */
@SuppressWarnings("serial")
public abstract class EdError extends RuntimeException {
    public EdError(String message) {
        super(message);
    }
    public static void showError(Component c, String msg) {
        msg = "<html><body><p style='width:200px;'>"
            + msg + "</p></body></html>";
        JOptionPane.showMessageDialog(c, msg, "EdGrind",
                                      JOptionPane.ERROR_MESSAGE);
    }
    public static void showWarning(Component c, String msg) {
        msg = "<html><body><p style='width:200px;'>"
            + msg + "</p></body></html>";
        JOptionPane.showMessageDialog(c, msg, "EdGrind",
                                      JOptionPane.WARNING_MESSAGE);
    }
    public static void showInfo(Component c, String msg) {
        msg = "<html><body><p style='width:200px;'>"
            + msg + "</p></body></html>";
        JOptionPane.showMessageDialog(c, msg, "EdGrind",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}



