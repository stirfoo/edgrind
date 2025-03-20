/*
  EMData.java
  S. Edward Dolan
  Thursday, July 27 2023
*/

package edgrind.expert;

import javax.swing.JComponent;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.io.BufferedOutputStream;

/**
   Internal representation of Expert Mode data.

   For a thorough explanation of index and type @see EMDataReader.
   
   @param index the index byte read from the element's data file
   @param type the type byte read from the element's data file
   @param guiId the elements semi-unique gui identifier
   @param initialized true if this element may be written when saved
   @param widget the gui component associated with this data element
 */
public abstract class EMData {
    protected int index;
    protected int type;
    protected int guiId;                   // index + 256 * (type & 0x0f)
    protected boolean initialized = false; // TODO: rename this?
    protected JComponent widget;
    // 
    abstract void write(BufferedOutputStream s) throws Exception;
    abstract void clear();
    abstract void loadDefault();
    //
    public JComponent getWidget() {
        return widget;
    }
    protected static void writeByte(BufferedOutputStream bos, int b)
        throws Exception {
        bos.write((byte)b);
    }
    protected static void writeInt(BufferedOutputStream bos, int i)
        throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(i);
        buf.rewind();
        bos.write(buf.array(), 0, 4);
    }
    protected static void writeFloat(BufferedOutputStream bos, float f)
        throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putFloat(f);
        buf.rewind();
        bos.write(buf.array(), 0, 4);
    }
    protected static void writeString(BufferedOutputStream bos, String s)
        throws Exception {
        for (char c : s.toCharArray())
            bos.write((byte)c);
    }
}
