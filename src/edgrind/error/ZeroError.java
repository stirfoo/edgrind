/*
  ZeroError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

/**
   Thrown if a value is the forbidden zero!
 */
@SuppressWarnings("serial")
public class ZeroError extends EdError {
    public ZeroError(String message) {
        super(message);
    }
}
