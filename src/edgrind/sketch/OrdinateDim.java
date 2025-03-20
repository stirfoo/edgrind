/*
  OrdinateDim.java
  S. Edward Dolan
  Sunday, September 10 2023
*/

package edgrind.sketch;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
// 
import java.awt.Graphics2D;
//
import edgrind.Dict;
//
import edgrind.geom.*;
// 
import edgrind.error.*;

/**
   An ordinate sketch dimenion, which is ultimately a java.awt.geom.Path2D.

   <p>
   The dim consists of:
   <ul>
   <li> a DimLabel to show the value of the dimension</li>
   <li>one horizontal or vertical line segment</li>
   </ul>

   The specs Dict consists of:
   <dl>

   <dt>name</dt>
   <dd>identification string</dd>
   
   <dt>value</dt>
   <dd>the numeric value of the dimension.</dd>
   
   <dt>pos</dt>
   <dd>the labels center coords in user space</dd>
   
   <dt>ref1</dt>
   <dd> <ul> <li>Point2D</li> <li>Line2D</li> </ul> </dd>
   
   <dt>format</dt>
   <dd>The string to pass to String.format() to display the value of the
   dimension</dd>

   <dt>force</dt>
   <dd>The value may be "horizontal" or "vertical".
   </dl>
   
   SketchDim holds both DimArrow instances and the DimLabel instance. The
   label is a constituent of this dimenions. All line segments are added to
   this Path2D.
   </p>

 */
@SuppressWarnings("serial")
public class OrdinateDim extends SketchDim {
    OrdinateDim(SketchScene scene, Sketch sketch, String name) {
        this(scene, sketch, name, false);
    }
    OrdinateDim(SketchScene scene, Sketch sketch, String name,
              boolean readOnly) {
        this(new Dict("name", name,
                      "value", 1,
                      "pos", new Vec2(-1, 0),
                      "ref1", new Vec2(0, 0),
                      "format", SketchDim.FMT_LIN),
             scene,
             sketch,
             readOnly);
        // neither is used
        arrow1 = null;
        arrow2 = null;
    }
    private OrdinateDim(Dict d, SketchScene scene, Sketch sketch,
                      boolean readOnly) {
        super(scene, sketch, readOnly);
        specs = (Dict)d.clone();
        config(d);
    }
    void config(Dict d) {
        super.config(d);
        if (specs.hasKey("vertical"))
            configVertical();
        else
            configHorizontal();
    }
    /**
       Create the horizontal ordinate line.
     */
    void configHorizontal() {
        // System.out.println("OrdinateDim.configHorizontal()");
        Vec2 pos = specs.vecAt("pos");
        Vec2 ref = specs.vecAt("ref1");
        double ext = scene.getPixelSize(EXT_LINE_EXT);
        doReset();
        moveTo(pos.x, pos.y);
        if (pos.x > ref.x)
            lineTo(ref.x + ext, ref.y);
        else
            lineTo(ref.x - ext, ref.y);
    }
    /**
       Create the vertical ordinate line.
    */
    void configVertical() {
        Vec2 pos = specs.vecAt("pos");
        Vec2 ref = specs.vecAt("ref1");
        double ext = scene.getPixelSize(EXT_LINE_EXT);
        doReset();
        moveTo(pos.x, pos.y);
        if (pos.y > ref.y)
            lineTo(ref.x, ref.y + ext);
        else
            lineTo(ref.x, ref.y - ext);
    }
}
