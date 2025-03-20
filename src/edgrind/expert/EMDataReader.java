/*
  EMDataReader.java
  S. Edward Dolan
  Thursday, February  3 2022
*/

package edgrind.expert;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

/**
  Read a Walter Expert Mode data file extracting strings, integers and
  floating point numbers, along with their associated integer index and type
  value. The indices identify an associated field in the (DOS!) Expert Mode
  GUI.

  <dl>
  <dt>NOTE:</dt>
  <dd>
  The data file has been reverse-engineered with no specification
  available. It may very well be some flavour of an existing database, but I'm
  not a db guy.
  </dd>
  </dl>

  <h4>The Header</h4>
  <p>
  The first 256 bytes are what I call the `header'.
  </p>
  <ol>
  <li>
  The file begins with 0x027E. I'm using this as a magic number, not sure if
  that's its purpose though.
  <dl>
  <dt>NOTE:</dt>
  <dd>The "ST DATA" file has a magic number of 0x027F but seems to have the
  same data format.</dd>
  <dl>
  </li>
  <li>
  The next 22 bytes are zeros (purpose unknown).
  </li>
  <li>
  The next byte is a 1 (purpose unknown). Possibly to signify the start of the
  next entry.
  </li>
  <li>
  The next 24 byte are the name of the data file. This is handled by the
     Andron data base manager. AFAIK It cannot be modified by the user except
     when saving a new file in Expert Mode.
     </li>
  <li>
  The following bytes up to, but not including byte 256 (0x100), are zeros
     and their purpose is unknown. Possibly reserved space.
  </li>
  </ol>

  <h4>The Data Section</h4>
  <p>
  The data section starts at byte 256.
  </p>
  <p>
  Each data element starts with a byte, we'll call it the index. The next byte
  is the type of the data. As far as I know, there are only 3 types stored in
  an Expert Mode Program:
  <ol>
  <li>fixed-length ASCII string</li>
  <li>32 bit signed integral number</li>
  <li>32 bit signed floating point number</li>
  </ol>
  </p>

  <dl>
  <dt> String (type = 0xA0) </dt>
  <dd>
    The 3rd byte is the length of the string. There is no NUL terminator. The
    following bytes are the characters of the string. A string's index is
    restricted to one byte, the range 0-255.
    <p> The string "Foo" with index 42: </p>
    <pre>
    . . . . . . . . 2A A0 03 46 6F 6F . . . . 
                    |  |  |  |  |  |  |
    index ----------'  |  |  |  |  |  |
    type  -------------'  |  |  |  |  |
    character count ------'  |  |  |  |
    'F' ---------------------'  |  |  |
    'o' ------------------------'  |  |
    'o' ---------------------------'  |
    index of next element ------------'
    </pre>
    </dd>
    </dl>

    <dl>
    <dt> Integer (type = 0x60) <dt>
    <dd>
    An integer is written in signed, 32bit, little-endian format. An integer's
    index is restricted to one byte, the range 0-255.
    </dd>
    </dl>

    <dl>
    <dt>Floating Point (types 0x20 to 0x26 inclusive)</dt>
    <dd>
    The lion's share of data elements are floating point numbers. A float is
    written in IEEE 754, 32bit, little-endian format. The 7 type values all
    mean the same thing, a floating point number follows. The 7 enumerations
    are used to extend the range of the index. The lower 4 bits of the type
    byte are a multiple of 256 to add to the index. If the index byte is 0x1f
    and the type is 0x23, the final index is 0x1f + 0xff * 3.
    </dd>
    </dl>

    <h4>The End</h4>
    
    <p>From my investigation, a type value of 0 signifies the end of the data
    section. Currently, everything after this byte is ignored and the read is
    complete.</p>
    
    <dl>
    <dt>TODO:</dt>
    <dd>The "ST DATA" file may be left blank, if so, me thinks the probe data
    is stored in the data file itself? Possibly in all those zeros in the
    header. Need to investigate that possibility...</dd>
    <dl>
*/
public class EMDataReader {
    String fileName;
    BufferedInputStream s;
    static final int FILE_NAME_N_BYTES = 24;
    /**
       Construct an EMDataReader opening the Walter Expert Mode data file.

       @param fileName the data file name
    */
    public EMDataReader(String fileName) throws Exception {
        try {
            FileInputStream fis;
            fis = new FileInputStream(new File(fileName));
            s = new BufferedInputStream(fis);
        }
        catch (FileNotFoundException e) {
            throw new Exception("data file not found: " + fileName);
        }
        this.fileName = fileName;
    }
    /**
       Read the file, verbosely. That is, print each element's info as it is
       read. This is a debugging method.
    */
    public void read() throws Exception {
        read(null, true);
    }
    /**
       Read the file notifying the handler at each element.

       @param h the EMDataHandler to notify
    */
    public void read(EMDataHandler h) throws Exception {
        read(h, false);
    }
    /**
       Read the Expert Mode data file notifying the EMDataHandler when a
       string, integer, or floating point number is read.

       @param h the EMDataHandler to notify
       @param verbose if true, print each element's info it's read
    */
    public void read(EMDataHandler h, boolean verbose) throws Exception {
        try {
            ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder
                                                          .LITTLE_ENDIAN);
            // read magic number 0x027e
            int i = s.read();
            if (i == -1 || i != 0x02)
                throw new Exception(fileName + " does not appear to be a" +
                                    " Walter Expert Mode program");
            i = s.read();
            if (i == -1 || i != 0x7e)
                throw new Exception(fileName + " does not appear to be a" +
                                    " Walter Expert Mode program");
            for (int j=0; j<22; ++j) {
                i = s.read();
                if (i == -1)
                    throw new Exception("end of file expecting data in: " +
                                        fileName);
            }
            if (s.read() != 0x01)
                throw new Exception("expected 0x01 in data file: " +
                                    fileName);
            byte[] bytes = new byte[FILE_NAME_N_BYTES];
            int n = s.read(bytes, 0, FILE_NAME_N_BYTES);
            if (n != FILE_NAME_N_BYTES)
                throw new Exception("end of file expecting data in: "
                                    + fileName);
            if (h instanceof EMProgram)
                ((EMProgram)h).setNames(fileName, new String(bytes));
            for (int j=0; j<207; ++j) {
                i = s.read();
                if (i == -1)
                    throw new Exception("end of file expecting data in: "
                                        + fileName);
            }
            // read the values
            int index, type;
            while ((index = s.read()) != -1) {
                type = s.read();
                if (type == -1 || type == 0) // 0 is trailing pad byte, done!
                    break;
                // integer
                else if (type == 0x60) {
                    int ival = readInteger();
                    if (verbose)
                        System.out.format("name:%-14s|index:0x%02x|type:0x%02x"
                                          + "|gui-index:%-4d|value:%d\n",
                                          "Integer", index, type, index, ival);
                    if (h != null)
                        h.onInt(index, type, ival);
                }
                // float
                else if (type >= 0x20 && type <= 0x26) {
                    int guiIndex = index + 256 * ((byte)type & 0x0f);
                    float fval = readFloat();
                    if (verbose)
                        System.out.format("name:%-14s|index:0x%02x|type:0x%02x"
                                          + "|gui-index:%-4d|value:%-12.6f\n",
                                          "Floating Point", index, type,
                                          guiIndex,
                                          fval);
                    if (h != null)
                        h.onFloat(index, type, fval);
                }
                // string
                else if (type == 0xa0) {
                    String sval = readString();
                    sval = sval.trim();
                    if (verbose)
                        System.out.format("name:%-14s|index:0x%02x|type:0x%02x"
                                          + "|gui-index:%-4d|value:\"%s\"\n",
                                          "ASCII", index, type, index, sval);
                    if (h != null)
                        h.onString(index, type, sval);
                }
                else
                    throw new Exception(String.format("unknown data type" +
                                                      " (%02x) in data" +
                                                      " file: %s",
                                                      (byte)type, fileName));
            }
        }
        finally {
            s.close();
        }
    }
    /**
       Read one 32 bit little-endian integer from the Expert Mode data file.
       
       @return the integer
    */
    protected int readInteger() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        if (s.read(buf.array()) != 4)
            throw new Exception("end of file expecting integer in data"
                                + " file: " + fileName);
        buf.rewind();
        return buf.getInt();
    }
    /**
       Read one 32 bit little-endian floating point number from the Expert
       Mode data file.

       @return the float
    */
    protected float readFloat() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        if (s.read(buf.array()) != 4)
            throw new Exception("end of file expecting floating point"
                                + " number in data file: " + fileName);
        buf.rewind();
        return buf.getFloat();
    }
    /**
       Read one fixed-length string from the Expert Mode data file.

       @return the string
    */
    protected String readString() throws Exception {
        int len = s.read();
        if (len == -1)
            throw new Exception("end of file expecting string in data file: "
                                + fileName);
        byte[] bytes = new byte[len];
        int n = s.read(bytes, 0, len);
        if (n != len)
            throw new Exception("end of file while reading ASCII"
                                + " string in data file: " + fileName);
        return new String(bytes);
    }
    /**
       For testing. Read the data file given on the cl.

       @param args cl args when EMDataReader is run as the main entry point
    */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("EdGrind Error: give me a Walter Expert Mode"
                               + " data file");
            System.exit(1);
        }
        EMProgram prog = new EMProgram();
        EMDataReader ds = new EMDataReader(args[0]);
        ds.read();
        // ds.read(prog, true);
    }
}
