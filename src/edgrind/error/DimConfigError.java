/*
  DimConfigError.java
  S. Edward Dolan
  Sunday, October 15 2023
*/

package edgrind.error;

/**
   Thrown if there is an error during a sketch dimension update.
 */
@SuppressWarnings("serial")
public class DimConfigError extends EdError {
    public DimConfigError(String message) {
        super(message);
    }
}
