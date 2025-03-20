/*
  LinearDim.java
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
   A linear sketch dimenion, which is ultimately a java.awt.geom.Path2D.

   The dim consists of:
   <ul>
   <li> DimArrow (x2) </li>
   <li> a DimLabel to show the value of the dimension</li>
   <li>one or more line segments, depending on the dimension's
   configuration</li>
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
   
   <dt>ref2</dt>
   <dd> <ul> <li>Point2D</li> <li>Line2D</li> <li>null</li></ul> If null, ref1
   must be a Line2D. The line end points will be used as references.</dd>

   <dt>outside</dt>
   <dd>if true, the arrows will point towards each other</dd>

   <dt>format</dt>
   <dd>The string to pass to String.format() to display the value of the
   dimension</dd>

   <dt>force</dt>
   <dd>If the two ref points are diagonal, this will force the dimension to be
   one of the following. A value of null means "don't care".
   <ul><li>"horizontal"</li><li>"vertical"</li><li>null</li></ul></dd>
   
   </dl>

   SketchDim holds both DimArrow instances and the DimLabel instance.

   The arrows and label are constituents of this dimenions. All line segments
   are added to this Path2D.

   <pre>
   DimLabel --------------------.
   line segments (x4)-----.     |
   DimArrow (x2)------.   |     |
                      |   |     |
                     |<------- 1.234 -------->|
                     |                        |
                     |                        |
                     |                        |
                     O                        O
                     |
                     `-- ref1 and ref2, Point2D (x2) in this case. These are
                         not part of the dimension but are used to position
                         the dimension.  <pre>
 */
@SuppressWarnings("serial")
public class LinearDim extends SketchDim {
    LinearDim(SketchScene scene, Sketch sketch, String name) {
        this(scene, sketch, name, false);
    }
    LinearDim(SketchScene scene, Sketch sketch, String name,
              boolean readOnly) {
        this(new Dict("name", name,
                      "value", 1,
                      "pos", new Vec2(0, 0),
                      "ref1", new Vec2(-.5, 0),
                      "ref2", new Vec2(.5, 0),
                      "outside", false,
                      "format", SketchDim.FMT_LIN,
                      "force", null),
             scene,
             sketch,
             readOnly);
    }
    private LinearDim(Dict d, SketchScene scene, Sketch sketch,
                      boolean readOnly) {
        super(scene, sketch, readOnly);
        specs = (Dict)d.clone();
        config(d);
    }
    void config(Dict d) {
        super.config(d);
        Object ref1 = specs.get("ref1");
        Object ref2 = specs.get("ref2");
        if (ref1 instanceof Vec2 && ref2 instanceof Vec2)
            // dim between 2 points
            configTwoPoints((Vec2)ref1, (Vec2)ref2);
        else if (ref1 instanceof Line2 && ref2 == null)
            // dim the line
            configTwoPoints(((Line2)ref1).p1(), ((Line2)ref1).p2());
        else if (ref1 == null && ref2 instanceof Line2)
            // dim the line
            configTwoPoints(((Line2)ref2).p1(), ((Line2)ref2).p2());
        else if (ref1 instanceof Line2 && ref2 instanceof Line2)
            // dim distance between 2 parallel lines
            configTwoLines((Line2)ref1, (Line2)ref2);
        else if (ref2 instanceof Vec2 && ref1 instanceof Line2)
            // dim distance between a point and line
            configPointLine((Vec2)ref1, (Line2)ref2);
        else if (ref2 instanceof Line2 && ref1 instanceof Vec2)
            // dim distance between a line and point
            configPointLine((Vec2)ref1, (Line2)ref2);
    }
    void configTwoPoints(Vec2 p1, Vec2 p2) {
        double p1x = p1.x, p1y = p1.y;
        double p2x = p2.x, p2y = p2.y;
        Vec2 pos = specs.vecAt("pos");
        double posx = pos.x, posy = pos.y;
        boolean outside = specs.boolAt("outside");
        String force = specs.stringAt("force");
        if (p1x == p2x && p1y == p2y)
            throw new DimConfigError("dimension has zero length");
        if (p1x == p2x) {
            if (force == "horizontal")
                throw new DimConfigError("dim cannot be forced horizontal");
            configVertical(p1, p2, posx, posy, outside);
        }
        else if (p1y == p2y) {
            if (force == "vertical")
                throw new DimConfigError("dim cannot be forced vertical");
            configHorizontal(p1, p2, posx, posy, outside);
        }
        else {
            if (force == "vertical")
                configVertical(p1, p2, posx, posy, outside);
            else if (force == "horizontal")
                configHorizontal(p1, p2, posx, posy, outside);
            else {
                Rectangle2D r = new Rectangle2D.Double(p1.x,
                                                       p1.y,
                                                       p2.x - p1.x,
                                                       p2.y - p1.y);
                switch (r.outcode(pos.toPoint2D())) {
                    case Rectangle2D.OUT_LEFT:
                    case Rectangle2D.OUT_RIGHT:
                        configVertical(p1, p2, posx, posy, outside);
                        break;
                    case Rectangle2D.OUT_BOTTOM:
                    case Rectangle2D.OUT_TOP:
                        configHorizontal(p1, p2, posx, posy, outside);
                        break;
                    default:
                        configParallel(p1, p2, posx, posy, outside);
                }
            }
        }
    }
    void configTwoLines(Line2 l1, Line2 l2) {
        // throw if lines art not parallel or colinear
    }
    void configPointLine(Vec2 p, Line2 line) {
        // throw if point is on the line
    }
    /**
       6 configurations, depending on label location and whether the arrows are
       inside/outside the extension lines.

       <pre>
       * inside
       |-- label between the refs
       |-- label above the refs
       `-- label below the refs

       * outside
       |-- label between the refs
       |-- label below the refs
       `-- label above the refs
       </pre>

       @param p1 first reference point
       @param p2 second reference point
       @param posx label center coord
       @param posy label center coord
       @param outside if true, arrows point toward each other
    */
    void configVertical(Vec2 p1, Vec2 p2, double posx, double posy,
                        boolean outside) {
        // System.out.println("LinearDim.configVertical()");
        if (p1.y > p2.y) {
            // put p1 on the bottom
            Vec2 tmp = p1;
            p1 = p2;
            p2 = tmp;
        }
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        // bottom arrow
        arrow1.config(new Dict("pos", new Vec2(posx, p1.y),
                               "dir", new Vec2(0, outside ? 1 : -1)));
        // top
        arrow2.config(new Dict("pos", new Vec2(posx, p2.y),
                               "dir", new Vec2(0, outside ? -1 : 1)));
        double ll = scene.getPixelSize(LEADER_LEN);
        doReset();
        if (outside) {          // arrows pointing toward each other
            if (posy < y1) {    // label below extension lines
                // bottom leader
                moveTo(posx, posy);
                lineTo(posx, y1);
                // top leader
                moveTo(posx, y2);
                lineTo(posx, y2 + ll);
            }
            else if (posy > y2) { // label above extension lines
                // top leader
                moveTo(posx, posy);
                lineTo(posx, y2);
                // bottom leader
                moveTo(posx, y1);
                lineTo(posx, y1 - ll);
            }
            else {              // label between extension lines
                // top leader
                moveTo(posx, y2);
                lineTo(posx, y2 + ll);
                // bottom leader
                moveTo(posx, y1);
                lineTo(posx, y1 - ll);
            }
        }
        else {
            // one line between the inside arrows
            moveTo(posx, y1);
            lineTo(posx, y2);
            if (posy < y1) {
                // extra leader to the label outside the extension lines
                moveTo(posx, y1);
                lineTo(posx, posy);
            }
            else if (posy > y2) {
                // extra leader ...
                moveTo(posx, y2);
                lineTo(posx, posy);
            }
        }
        // extension lines
        double ext = scene.getPixelSize(EXT_LINE_EXT);
        double gap = scene.getPixelSize(EXT_LINE_GAP);
        // bottom line
        if (posx <= p1.x) {
            // label left of ref
            moveTo(p1.x - gap, y1);
            lineTo(posx - ext, y1);
        }
        else {
            // right of
            moveTo(x1 + gap, y1);
            lineTo(posx + ext, y1);
        }
        // top line
        if (posx <= p2.x) {
            // label left of ref
            moveTo(p2.x - gap, y2);
            lineTo(posx - ext, y2);
        }
        else {
            // right of
            moveTo(x2 + gap, y2);
            lineTo(posx + ext, y2);
        }
    }
    /**
       Create the geometry for the horizontal dim, update the arrow heads.
       
       6 configurations, depending on label location and whether the arrows
       are inside/outside the extension lines.

       * inside
       |-- label between the refs
       |-- label above the refs
       `-- label below the refs

       * outside
       |-- label between the refs
       |-- label below the refs
       `-- label above the refs

       @param p1 first reference point
       @param p2 second reference point
       @param posx label center coord
       @param posy label center coord
       @param outside if true, arrows point toward each other
    */
    void configHorizontal(Vec2 p1, Vec2 p2, double posx, double posy,
                          boolean outside) {
        // System.out.println("LinearDim.configHorizontal()");
        if (p1.x > p2.x) {
            // make p1 the left point
            Vec2 tmp = p1;
            p1 = p2;
            p2 = tmp;
        }
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        // left arrow
        arrow1.config(new Dict("pos", new Vec2(x1, posy),
                               "dir", new Vec2(outside ? 1 : -1, 0)));
        // right
        arrow2.config(new Dict("pos", new Vec2(x2, posy),
                               "dir", new Vec2(outside ? -1 : 1, 0)));
        double ll = scene.getPixelSize(LEADER_LEN);
        doReset();
        if (outside) {          // arrows pointing toward each other
            if (posx < x1) {    // label left of extension lines
                // leader from label to left ref
                moveTo(posx, posy);
                lineTo(x1, posy);
                // right leader
                moveTo(x2, posy);
                lineTo(x2 + ll, posy);
            }
            else if (posx > x2) { // label right of extension lines
                // leader from right ref to label
                moveTo(x2, posy);
                lineTo(posx, posy);
                // left leader
                moveTo(x1 - ll, posy);
                lineTo(x1, posy);
            }
            else {              // label bewtween extension lines
                // left leader
                moveTo(x1, posy);
                lineTo(x1 - ll, posy);
                // right leader
                moveTo(x2, posy);
                lineTo(x2 + ll, posy);
            }
        }
        else {                  // arrows pointing away from each other
            // one line between arrows
            moveTo(x1, posy);
            lineTo(x2, posy);
            if (posx < x1) {    // label left of extension lines
                // leader from label to left ref
                moveTo(x1, posy);
                lineTo(posx, posy);
            }
            else if (posx > x2) { // label right of extension lines
                // leader from label to right ref
                moveTo(x2, posy);
                lineTo(posx, posy);
            }
        }
        // extension lines
        double ext = scene.getPixelSize(EXT_LINE_EXT);
        double gap = scene.getPixelSize(EXT_LINE_GAP);
        // left extension line
        if (posy <= y1) {
            // label below or at ref
            moveTo(x1, y1 - gap);
            lineTo(x1, posy - ext);
        }
        else {
            // label above ref
            moveTo(x1, y1 + gap);
            lineTo(x1, posy + ext);
        }
        // right extension line
        if (posy <= y2) {
            // label below or at ref
            moveTo(x2, y2 - gap);
            lineTo(x2, posy - ext);
        }
        else {
            // label above ref
            moveTo(x2, y2 + gap);
            lineTo(x2, posy + ext);
        }
    }
    void configParallel(Vec2 p1, Vec2 p2, double posx, double posy,
                        boolean outside) {
        // System.out.println("LinearDim.configParallel()");
        Vec2 pos = new Vec2(posx, posy);
        // p1 -> pos
        Vec2 v = new Vec2(p1, pos);
        // p1 -> p2 norm
        Vec2 u = new Vec2(p1, p2).norm();
        // project pos onto p1-p2 
        Vec2 mp = Vec2.add(p1, u.mul(v.dot(u)));
        // vector back to pos from mp
        Vec2 sv = new Vec2(mp, pos);
        // arrow1 pos
        Vec2 ap1 = new Vec2(p1.x + sv.x, p1.y + sv.y);
        // arrow2 pos
        Vec2 ap2 = new Vec2(p2.x + sv.x, p2.y + sv.y);
        // from pos to ap1
        Vec2 lv1 = new Vec2(pos, ap1);
        // from pos to ap2
        Vec2 lv2 = new Vec2(pos, ap2);
        // true if lv1 and lv2 point in the same direction
        boolean dimOutside = Vec2.norm(lv1).dot(Vec2.norm(lv2)) >= 0.0;
        Vec2 av1 = outside ? u : Vec2.mul(u, -1);
        Vec2 av2 = outside ? Vec2.mul(u, -1) : u;
        arrow1.config(new Dict("pos", ap1, "dir", av1));
        arrow2.config(new Dict("pos", ap2, "dir", av2));
        doReset();
        if (outside) {
        }
        else {                  // arrows point away from each other
            if (dimOutside) {
                moveTo(posx, posy);
                if (lv1.mag() > lv2.mag())
                    lineTo(ap1.x, ap1.y);
                else
                    lineTo(ap2.x, ap2.y);
            }
            else {
                // dim line between arrows
                moveTo(ap1.x, ap1.y);
                lineTo(ap2.x, ap2.y);
            }
        }
        // extension lines
        double ext = scene.getPixelSize(EXT_LINE_EXT);
        double gap = scene.getPixelSize(EXT_LINE_GAP);
        Vec2 extV = Vec2.norm(sv).mul(ext);
        Vec2 extP = new Vec2(ap2.x + extV.x,
                             ap2.y + extV.y);
        Vec2 gapV = Vec2.norm(sv).mul(gap);
        Vec2 gapP = new Vec2(p2.x + gapV.x,
                             p2.y + gapV.y);
        moveTo(gapP.x, gapP.y);
        lineTo(extP.x, extP.y);
        extP = new Vec2(ap1.x + extV.x,
                        ap1.y + extV.y);
        gapP = new Vec2(p1.x + gapV.x,
                        p1.y + gapV.y);
        moveTo(gapP.x, gapP.y);
        lineTo(extP.x, extP.y);
    }
}
