/*
  OutOfRangeError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

/**
   Thrown if a value is not within specified bounds.
 */
@SuppressWarnings("serial")
public class OutOfRangeError extends EdError {
    public OutOfRangeError(String message) {
        super(message);
    }
}
