/*
  DBChangeListener.java
  S. Edward Dolan
  Saturday, October 14 2023
*/

package edgrind;

/**
   Called when an EdGrind database changes.
   <p>
   For instance, when a wheel is added to the Wheel db.
   </p>
 */
public interface DBChangeListener {
    /** The type of db that changed. */
    public enum DBType {
        WHEEL,
        WHEEL_PACK,
        CHUCK,
    }
    /**
       Called when an EdGrind database has changed.

       @param type the type of db that changed
       @param name the name of the db item that got added or updated.
    */
    public void onDBChange(DBType type, String name);
}
