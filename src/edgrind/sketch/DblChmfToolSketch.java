/*
  DblChmfToolSketch.java
  S. Edward Dolan
  Wednesday, October  4 2023
*/

package edgrind.sketch;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
//
import edgrind.Dict;

/*
  
                                            .-----------------------
                                            |
           .----.                           |
         .'      '.             .-----------'
       .'          '.___________'
       |
       |
       |
       |             -----------.
       '.          .'           '-----------.
         '.      .'                         |
           '----'                           |
                                            `-----------------------
 */

@SuppressWarnings("serial")
public class DblChmfToolSketch extends Sketch {
    static final int D1 = 0, D2 = 1;
    static final int L1 = 2, L2 = 3;
    public DblChmfToolSketch(Dict specs, SketchScene scene) {
        this(specs, scene, false);
    }
    public DblChmfToolSketch(Dict d, SketchScene scene, boolean mirror) {
        super(d, scene, mirror);
    }
    public void updateDims() {
    }
    public void updateProfile() {
    }
}
