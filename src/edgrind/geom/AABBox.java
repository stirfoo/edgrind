/*
  AABBox.java
  S. Edward Dolan
  Wednesday, November 22 2023
*/

package edgrind.geom;

import java.util.List;

/**
   An axis-aligned bounding box.
 */
public class AABBox {
    /** Minimum xyz coordinates of this box. */
    protected Vec3 minP;
    /** Maximum xyz coordinates of this box. */
    protected Vec3 maxP;
    /** Construct a box with infinite dimensions. */
    public AABBox() {
        minP = new Vec3(Double.POSITIVE_INFINITY);
        maxP = new Vec3(Double.NEGATIVE_INFINITY);
    }
    public boolean isValid() {
        return minP.x != Double.POSITIVE_INFINITY
            && maxP.x != Double.NEGATIVE_INFINITY;
    }
    /** Get the minimum xyz coords of this box. */
    public Vec3 minP() {
        return minP;
    }
    /** Get the maximum xyz coords of this box. */
    public Vec3 maxP() {
        return maxP;
    }
    /** Get the center coordinate of the box */
    public Vec3 center() {
        return Vec3.add(minP, maxP).mul(.5);
    }
    /** Get the minimum X dimension of the box. */
    public double minX() {
        return minP.x;
    }
    /** Get the minimum Y dimension of the box. */
    public double minY() {
        return minP.y;
    }
    /** Get the minimum Z dimension of the box. */
    public double minZ() {
        return minP.z;
    }
    /** Get the maximum X dimension of the box. */
    public double maxX() {
        return maxP.x;
    }
    /** Get the maximum Y dimension of the box. */
    public double maxY() {
        return maxP.y;
    }
    /** Get the maximum Z dimension of the box. */
    public double maxZ() {
        return maxP.z;
    }
    /** Get the x, y, and z dimentions of the box. */
    public Vec3 size() {
        return Vec3.sub(maxP, minP);
    }
    /** Get the X dimension of the box. */
    public double sizeX() {
        return maxP.x - minP.x;
    }
    /** Get the Y dimension of the box. */
    public double sizeY() {
        return maxP.y - minP.y;
    }
    /** Get the Z dimension of the box. */
    public double sizeZ() {
        return maxP.z - minP.z;
    }
    /** Get the [left, top] coordinates */
    public Vec2 leftTop() {
        return new Vec2(minX(), maxY());
    }
    /** Get the [right, bottom] coordinates */
    public Vec2 rightBottom() {
        return new Vec2(maxX(), minY());
    }
    /** Add the vertice to this box, expanding this size if necessary. */
    public void add(Vec3 v) {
        if (v.x < minP.x) minP.x = v.x;
        if (v.y < minP.y) minP.y = v.y;
        if (v.z < minP.z) minP.z = v.z;
        if (v.x > maxP.x) maxP.x = v.x;
        if (v.y > maxP.y) maxP.y = v.y;
        if (v.z > maxP.z) maxP.z = v.z;
    }
    /** Add the given box to this box, expanding this size if necessary. */
    public void add(AABBox b) {
        add(b.minP());
        add(b.maxP());
    }
    /** Create a new box from a list of vertices. */
    public static AABBox fromVertices(List<Vec3> verts) {
        AABBox b = new AABBox();
        for (Vec3 v : verts)
            b.add(v);
        return b;
    }
}
