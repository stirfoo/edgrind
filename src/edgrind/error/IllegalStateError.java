/*
  IllegalStateError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

@SuppressWarnings("serial")
public class IllegalStateError extends EdError {
    public IllegalStateError(String message) {
        super(message);
    }
}
