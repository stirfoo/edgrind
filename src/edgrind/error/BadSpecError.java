/*
  BadSpecError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

/**
   Thrown if a sketch spec fails the initial check.
 */
@SuppressWarnings("serial")
public class BadSpecError extends EdError {
    public BadSpecError(String message) {
        super(message);
    }
}
