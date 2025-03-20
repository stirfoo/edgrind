/*
  BGColor.java
  S. Edward Dolan
  Friday, August 25 2023
*/

package edgrind;

import java.awt.Color;

/**
   Background color for use with a numeric input component.
   <p>
   The three colors are used to inform the user if the input is good, bad, or
   other (meh, or don't care).
   </p>
 */
public class BGColor {
    /** Input is good. */
    public static final Color goodColor = new Color(.85f, 1.f, .85f);
    /** Intpus is bad. */
    public static final Color badColor = new Color(1.f, .85f, .85f);
    /** Don't care, For instance if empty imput is premitted. */
    public static final Color mehColor = Color.WHITE;
}

