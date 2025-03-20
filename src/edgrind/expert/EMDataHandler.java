/*
  EMDataHandler.java
  S. Edward Dolan
  Sunday, July 30 2023
*/

package edgrind.expert;

/**
   Handle an element read from an Expert Mode data file.

   When EMDataReader reads for instance, an integer, onInt() will be called
   with its gui index, element type, and value.
 */
public interface EMDataHandler {
    /**
       Recieve integer info from an EMDataReader.

       @param index the gui index
       @param type the type flag of the value
       @param value the value read from the data
    */
    public void onInt(int index, int type, int value) throws Exception;
    /**
       Recieve floating point info from an EMDataReader.

       @param index the gui index
       @param type the type flag of the value
       @param value the value read from the data
    */
    public void onFloat(int index, int type, float value) throws Exception;
    /**
       Recieve string info from an EMDataReader.

       @param index the gui index
       @param type the type flag of the value @see {@link EMDataReader}
       @param value the value read from the data
    */
    public void onString(int index, int type, String value) throws Exception;
}
