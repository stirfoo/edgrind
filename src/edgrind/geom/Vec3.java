/*
  Vec3.java
  S. Edward Dolan
  Monday, November 13 2023
*/

package edgrind.geom;

import edgrind.error.ZeroError;

/**
   A 3d point or vector.
 */
public class Vec3 {
    /** For Vec3 equality test. The double 1e-8. */
    public double EPSILON = 1e-8; 
    public double x, y, z;
    /**
       Construct a vector with all components equal to 0.0.
    */
    public Vec3() {
        x = 0;
        y = 0;
        z = 0;
    }
    /**
       Construct a vector from the given values.
    */
    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
       Construct a vector from a to b.
    */
    public Vec3(Vec3 a, Vec3 b) {
        this.x = b.x - a.x;
        this.y = b.y - a.y;
        this.z = b.z - a.z;
    }
    /**
       Construct a vector with x, y, and z set to t.
    */
    public Vec3(double t) {
        this(t, t, t);
    }
    /**
       Compare this vector with the given vector.

       @return true if the vectors are equal within {@link #EPSILON}
     */
    @Override
    public boolean equals(Object v) {
        if (v instanceof Vec3) {
            Vec3 vv = (Vec3)v;
            return Math.abs(x - vv.x) <= EPSILON
                && Math.abs(y - vv.y) <= EPSILON
                && Math.abs(z - vv.z) <= EPSILON;
        }
        return false;
    }
    @Override
    public String toString() {
        return String.format("Vec3 %+15.8f %+15.8f %+15.8f", x, y, z);
    }
    /** Get this Vec3 as a Vec4 with the given w component. */
    public Vec4 toVec4(double w) {
        return new Vec4(x, y, z, w);
    }
    /** Get this Vec3 as a Vec4 with the w component set to 1.0. */
    public Vec4 toVec4() {
        return toVec4(1);
    }
    /**
       Subtract v from this vector.
       @param v the vector to subtract
       @return this
    */
    public Vec3 sub(Vec3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }
    /**
       Subtract the components from this vector.
       @param x the x component
       @param y the y component
       @param z the z component
       @return this
    */
    public Vec3 sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    /**
       Multiply this vector by s.
       @param s the scalar to multiply
       @return this
    */
    public Vec3 mul(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }
    /**
       Negate this vector (v * -1).
       @return this
     */
    public Vec3 neg() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }
    /**
       Set all components to t.
     */
    public void set(double t) {
        this.x = this.y = this.z = t;
    }
    /**
       Set this vector's components to the given values.
     */
    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
       Set this vector's component's to the given vector's.
     */
    public void set(Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    /**
       Add the given vector to this vector.
       @return this
     */
    public Vec3 add(Vec3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }
    /**
       Get the length of this vector.
       @return the length
     */
    public double mag() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    /**
       Get the cross product of this and the given vector.
       @return a new Vec3
    */
    public Vec3 cross(Vec3 v) {
        return new Vec3(y * v.z - z * v.y,
                        z * v.x - x * v.z,
                        x * v.y - y * v.x);
    }
    /**
       Find the dot product of this and the given vector.
     */
    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
    /**
       Normalize this vector and return it.
       @throws ZeroError if the magnitude of this vector is zero
     */
    public Vec3 norm() {
        double m = mag();
        if (m == 0)
            throw new ZeroError("Vec3 magnitude is zero");
        x /= m;
        y /= m;
        z /= m;
        return this;
    }
    /**
       Normalize the vector.
       @return a copy of the given vector normalized
     */
    public static Vec3 norm(Vec3 v) {
        return new Vec3(v.x , v.y, v.z).norm();
    }
    /**
       Find the sum of the given vectors.
       @return a new Vec3
     */
    public static Vec3 add(Vec3 v1, Vec3 v2) {
        return new Vec3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }
    /**
       Find the difference of the given vectors (v1 - v2).
       @return a new Vec3
     */
    public static Vec3 sub(Vec3 v1, Vec3 v2) {
        return new Vec3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }
    /**
       Find the product of the given vector and scalar (v * s).
       @return a new Vec3
     */
    public static Vec3 mul(Vec3 v, double s) {
        return new Vec3(v.x * s, v.y * s, v.z * s);
    }
    /**
       Negate the vector.
       @return a copy of the given vector negated (v * -1)
     */
    public static Vec3 neg(Vec3 v) {
        return Vec3.mul(v, -1);
    }
}

