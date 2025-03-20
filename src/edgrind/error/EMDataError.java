/*
  EMDataError.java
  S. Edward Dolan
  Sunday, November 6 2023
*/

package edgrind.error;

/**
   Thrown if a EMData encounters a problem with a value.
 */
@SuppressWarnings("serial")
public class EMDataError extends EdError {
    public EMDataError(String message) {
        super(message);
    }
}
