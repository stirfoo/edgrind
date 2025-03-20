/*
  Sketch.java
  S. Edward Dolan
  Thursday, September  7 2023
*/

package edgrind.sketch;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
//
import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Dimension;
// 
import java.awt.event.MouseEvent;
// 
import java.util.Map;
//
import java.util.List;
import java.util.ArrayList;
// 
import java.lang.Comparable;
//
import edgrind.Dict;
//
import edgrind.geom.*;
//
import edgrind.error.BadSpecError;
import edgrind.error.NotImplementedError;
//

/**
   A parameterized 2d sketch.

   The sketch extends Path2D.Double whose elements define the geometry of the
   sketch, in sketch coordinates. That is, in right-handed, cartesian
   coordinates. Each sketch has a Dict that contains named parameters that
   are used to define the geometry of the sketch. If the sketch defines a
   single rectangle, the dict may contain the two parameters width:2 and
   height:1.
   
   These parameters may have associated, editable dimensions. In the case of
   the above rectangle, there might be two LinearDim instances.

   Use updateProfile() in a subclass to generate the elements of the Path2D.

   Use updateDims() in a subclass to configure each dimension's position and
   orientation.

   The geometric parameter names are (where N is an integer):

   * dN for diameter
   * lN for length (or width or height... any linear dimension)
   * rN for radius
   * aN for angle
 */
@SuppressWarnings("serial")
abstract public class Sketch extends Path2
    implements SketchSpecsListener, Comparable<Sketch> {
    protected Dict specs;
    protected SketchScene scene;
    protected boolean readOnly = false;
    protected boolean showDims = true;
    protected boolean dictDirty = false;
    protected boolean mirror = false;
    protected boolean fillShape = false;
    protected Color fillColor = Color.GRAY;
    protected boolean visible = true;
    protected Rect2 pathRect; // in user space
    protected List<SketchDim> dims = new ArrayList<SketchDim>();
    protected final static BasicStroke stroke
        = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    protected final static Color color = Color.white;
    protected int zValue = 0;       // render order
    //
    abstract void updateProfile();
    abstract void updateDims();
    public Model getModel() {
        throw new NotImplementedError("getModel not implementd.");
    }
    // 
    public Sketch(Dict specs, SketchScene scene) {
        this(specs, scene, false);
    }
    public Sketch(Dict specs, SketchScene scene, boolean mirror) {
        this.scene = scene;
        this.mirror = mirror;
        this.specs = (Dict)specs.clone();
        updateProfile();
        dictDirty = false;
    }
    public void setZValue(int z) {
        zValue = z;
    }
    public void setFillShape(boolean b) {
        fillShape = b;
    }
    public void setFillColor(Color c) {
        fillColor = c;
    }
    public void setScene(SketchScene scene) {
        this.scene = scene;
        config();
    }
    public Dict getSpecs() {
        return specs;
    }
    public List<SketchDim> getDims() {
        return dims;
    }
    public void setDictDirty(boolean b) {
        dictDirty = b;
    }
    public boolean isDictDirty() {
        return dictDirty;
    }
    public void setReadOnly(boolean b) {
        readOnly = b;
    }
    public boolean isReadOnly() {
        return readOnly;
    }
    public void showDims(boolean b) {
        if (showDims = b)
            config(null);
    }
    public boolean dimsVisible() {
        return showDims;
    }
    public void setVisible(boolean b) {
        visible = b;
    }
    public boolean isVisible() {
        return visible;
    }
    protected boolean checkGeometry(Dict specs) {
        return true;
    }
    public void config(Dict specs) {
        config(specs, false);
    }
    public void config(Dict specs, boolean forceUpdateProfile) {
        // System.out.println("Sketch.config()");
        boolean didUpdate = false;
        if (specs != null) {
            for (Map.Entry<String, Object> e : specs.entrySet()) {
                String key = e.getKey();
                if (this.specs.get(key) != specs.get(key)) {
                    dictDirty = true;
                    this.specs.putAll((Dict)specs.clone());
                    updateProfile();
                    didUpdate = true;
                    break;
                }
            }
        }
        if (!didUpdate && forceUpdateProfile)
            updateProfile();
        updateDims();
    }
    public void config() {
        config(null);
    }
    /**
       Replace the sketches specs with the given dict.
       <p>
       The profile and dimensions are updated.
       </p>
     */
    public void configNew(Dict d) {
        specs = d;
        // updateProfile();
        // updateDims();
    }
    public void render(Graphics2D g) {
        if (!visible)
            return;
        // System.out.println("Sketch.render()");
        g.setStroke(stroke);
        Shape s = xformShape(scene.xform);
        if (fillShape) {
            g.setPaint(fillColor);
            g.fill(s);
        }
        g.setPaint(color);
        g.draw(s);
        if (showDims)
            for (SketchDim d : dims)
                if (d != null)
                    d.render(g);
    }
    public Rect2 getBBox() {
        if (!visible)
            return new Rect2();
        Rect2 r = new Rect2(getBounds2D());
        if (showDims)
            for (SketchDim d : dims)
                if (d != null && d.isVisible())
                    r.add(d.getBBox());
        return r;
    }
    @Override
    public int compareTo(Sketch s) {
        return zValue - s.zValue;
    }
    @Override
    public void onSpecsChange(Dict specs) {
        config(specs);
    }
    /**
       Get a list of all vertices in this sketch's path.
    */
    public Vec2[] getPathVerts() {
        List<Vec2> verts = new ArrayList<Vec2>();
        PathIterator itr = getPathIterator(new AffineTransform(), 1e10);
        double[] coords = new double[2];
        while (!itr.isDone()) {
            int type = itr.currentSegment(coords);
            /*
              switch (type) {
              case PathIterator.SEG_CLOSE:
              System.out.println("SEG_CLOSE");
              break;
              case PathIterator.SEG_CUBICTO:
              System.out.println("SEG_CUBICTO");
              break;
              case PathIterator.SEG_LINETO:
              System.out.println("SEG_LINETO");
              break;
              case PathIterator.SEG_MOVETO:
              System.out.println("SEG_MOVETO");
              break;
              case PathIterator.SEG_QUADTO:
              System.out.println("SEG_QUADTO");
              break;
              default:
              System.out.println("OTHER?");
              break;
              }
            */
            verts.add(new Vec2(coords[0], coords[1]));
            itr.next();
        }
        Vec2[] vs = new Vec2[verts.size()];
        for (int i=0; i<verts.size(); ++i)
            vs[i] = verts.get(i);
        return vs;
    }
}
