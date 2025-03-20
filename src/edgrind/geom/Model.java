/*
  Model.java
  S. Edward Dolan
  Tuesday, November 28 2023
*/

package edgrind.geom;

import java.awt.Color;
// 
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
   A collection of Mesh instances.
 */
public class Model implements Iterable<Mesh> {
    protected Color color = Color.white;
    protected List<Mesh> meshes = new ArrayList<Mesh>();
    protected Mat4 m = new Mat4();
    public Model() {
    }
    public Model(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public AABBox getBBox() {
        return getBBox(new Mat4());
    }
    public AABBox getBBox(Mat4 m) {
        AABBox bbox = new AABBox();
        for (Mesh mesh : meshes)
            bbox.add(mesh.getBBox(this.m.mul(m)));
        return bbox;
    }
    public void add(Mesh mesh) {
        meshes.add(mesh);
    }
    @Override
    public Iterator<Mesh> iterator() {
        return meshes.iterator();
    }
    public Mat4 getMatrix() {
        return m;
    }
    public void setMatrix(Mat4 m) {
        this.m = m;
    }
}
