/*
  WheelSketch.java
  S. Edward Dolan
  Monday, October  2 2023
*/

package edgrind.sketch;

import java.awt.Shape;
//
import java.awt.geom.AffineTransform;
//
import edgrind.Dict;

/**
   Abstract base class for all wheel sketches.
 */
@SuppressWarnings("serial")
abstract public class WheelSketch extends Sketch {
    protected static final double holeDia = 1.25;
    int spindle = 1;            // 1 or 2
    double zlen = 0;            // >= 0
    boolean front = false;      // zlen is to arbor nut side of wheel, if true
    boolean configDirty = false;
    WheelSketch(Dict d, SketchScene scene, boolean mirror) {
        super(d, scene, mirror);
        configDirty = false;
    }
    /**
       This is called by WheelConfigPanel.
    */
    public void spindleCfg(int spindle, double zlen, boolean flip,
                           boolean front) {
        // System.out.println("WheelSketch.spindleCfg.zlen = " + zlen);
        if (this.spindle != spindle)
            configDirty = true;
        this.spindle = spindle;
        if (this.zlen != zlen)
            configDirty = true;
        this.zlen = zlen;
        if (this.mirror != flip)
            configDirty = true;
        this.mirror = flip;
        if (this.front != front)
            configDirty = true;
        this.front = front;
        config(null, true); // force geom rebuild
    }
    public void spindleCfg(int spindle, boolean flip, boolean front) {
        spindleCfg(spindle, this.zlen, flip, front);
    }
    public boolean isConfigDirty() {
        return configDirty;
    }
    /**
       Regenerate the profile when zlen > 0.
       <p>
       
       A wheel sketch is initally drawn right of the sketch's vertical (Y)
       axis. The left edge of the wheel will be aligned with the Y axis. If a
       non-zero Z length is requested in the SpindlePanel, the wheels profile
       must be updated. The following wheel attributes need to be considered.
       
       <ol>
       <li>the spindle number</li>
       <li>if the wheel has been flipped (mirrored)</li>
       <li>which side of the wheel is <em>front</em></li>
       <li>the current z length</li>
       </ol>
       </p>
    */
    protected void updateZLenProfile(double wheelWidth) {
        // System.out.println("WheelSketch.updateProfile()");
        double offset = zlen;
        if (spindle == 1) {
            if (mirror) {
                if (front)
                    offset = zlen; // no change
                else
                    offset += wheelWidth; // add wheel width
            }
            else {
                if (front)
                    offset -= wheelWidth;
                else
                    offset = zlen; // no change
            }
        }
        else {
            // spindle 2
            if (mirror) {
                if (front)
                    offset = -zlen + wheelWidth;
                else
                    offset = -zlen;
            }
            else {
                if (front)
                    offset = -zlen;
                else
                    offset = -zlen - wheelWidth;
            }
        }
        Shape s = AffineTransform.getTranslateInstance(offset, 0)
            .createTransformedShape(this);
        doReset();
        append(s, false);
    }
}
