/*
  Vec4.java
  S. Edward Dolan
  Monday, November 13 2023
*/

package edgrind.geom;

/**
   A 3d point or vector with a <em>w</em> component.
 */
public class Vec4 {
    public double x, y, z, w;
    public Vec4() {
        set(0, 0, 0, 1);
    }
    public Vec4(double x, double y, double z, double w) {
        set(x, y, z, w);
    }
    @Override
    public String toString() {
        return String.format("Vec4 %+-15.8f %+-15.8f %+-15.8f %+-15.8f",
                             x, y, z, w);
    }
    public void set(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }
    public Vec4 add(Vec4 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.w += v.w;
        return this;
    }
    // ======================================================================
    // Static Methods
    // ======================================================================
    public static Vec4 add(Vec4 v1, Vec4 v2) {
        return new Vec4(v1.x + v2.x,
                        v1.y + v2.y,
                        v1.z + v2.z,
                        v1.w + v2.w);
    }
    public static Vec4 sub(Vec4 v1, Vec4 v2) {
        return new Vec4(v1.x - v2.x,
                        v1.y - v2.y,
                        v1.z - v2.z,
                        v1.w - v2.w);
    }
    public static Vec4 mul(Vec4 v1, Vec4 v2) {
        return new Vec4(v1.x * v2.x,
                        v1.y * v2.y,
                        v1.z * v2.z,
                        v1.w * v2.w);
    }
    public static Vec4 mul(Vec4 v, double s) {
        return new Vec4(v.x * s,
                        v.y * s,
                        v.z * s,
                        v.w * s);
    }
    public static Vec4 div(Vec4 v1, Vec4 v2) {
        return new Vec4(v1.x / v2.x,
                        v1.y / v2.y,
                        v1.z / v2.z,
                        v1.w / v2.w);
    }
}

