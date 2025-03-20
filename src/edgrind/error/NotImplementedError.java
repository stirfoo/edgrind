/*
  NotImplementedError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

/**
   Thrown if a method is not implemented.
 */
@SuppressWarnings("serial")
public class NotImplementedError extends EdError {
    public NotImplementedError(String message) {
        super(message);
    }
}
