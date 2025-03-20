/*
  IllegalArgumentError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

@SuppressWarnings("serial")
public class IllegalArgumentError extends EdError {
    public IllegalArgumentError(String message) {
        super(message);
    }
}
