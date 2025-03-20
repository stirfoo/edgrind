/*
  Mat3.java
  S. Edward Dolan
  Monday, November 13 2023
*/

package edgrind.geom;

import edgrind.error.ZeroError;

/**
   A 3x3 Matrix.
 */
public class Mat3 {
    /** The 9 matrix components */
    double[] a;
    /** Construct an identity matrix */
    public Mat3() {
        a = new double[] {
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
        };
    }
    /** Construct from the given 9 components. */
    public Mat3(double[] a) {
        this.a = a;
    }
    public Mat3(Mat4 m) {
        a = new double[] {
            // TODO: row/col order correct?
            m.a[0][0], m.a[1][0], m.a[2][0],
            m.a[0][1], m.a[1][1], m.a[2][1],
            m.a[0][2], m.a[1][2], m.a[2][2],
        };
    }
    public String toString() {
        return String.format("Mat3 %+-15.8f %+-15.8f %+-15.8f\n" +
                             "     %+-15.8f %+-15.8f %+-15.8f\n" +
                             "     %+-15.8f %+-15.8f %+-15.8f",
                             a[0], a[1], a[2],
                             a[3], a[4], a[5],
                             a[6], a[7], a[8]);
    }
    static public Mat3 ident() {
        return new Mat3();
    }
    /**
       Get a new rotation matrix about the X axis.
       @param rads angle to rotate in radians
    */
    static public Mat3 rotX(double rads) {
        double s = Math.sin(rads);
        double c = Math.cos(rads);
        return new Mat3(new double[] {
                1, 0,  0,
                0, c, -s,
                0, s,  c,
            });
    }
    /**
       Get a new rotation matrix about the Y axis.
       @param rads the degrees to rotate
    */
    static public Mat3 rotY(double angle) {
        double rads = Math.toRadians(angle);
        double s = Math.sin(rads);
        double c = Math.cos(rads);
        return new Mat3(new double[] {
                c, 0, s,
                0, 1, 0,
                -s, 0, c,
            });
    }
    /**
       Get a new rotation matrix about the Z axis.
       @param rads the degrees to rotate
    */
    static public Mat3 rotZ(double angle) {
        double rads = Math.toRadians(angle);
        double s = Math.sin(rads);
        double c = Math.cos(rads);
        return new Mat3(new double[] {
                c, -s, 0,
                s,  c, 0,
                0,  0, 1,
            });
    }
    /**
       Get a new matrix rotated about an axis.

       <p>The method will normalze the axis and return an identity matrix if
       the axis (vector) has zero length.</p>
       
       @param axis the [x, y, z] axis to rotate about
       @param angle the degrees to rotate
    */
    static public Mat3 axisAngle(Vec3 axis, double angle) {
        // System.out.println("axis:" + axis + " angle:" + angle);
        try {
            axis = Vec3.norm(axis);
        }
        catch (ZeroError e) {
            return new Mat3();  // axis has zero length, return identity
        }
        double rads = Math.toRadians(angle);
        double c = Math.cos(rads);
        double s = Math.sin(rads);
        double C = 1 - c;
        double x = axis.x, y = axis.y, z = axis.z;
        double x2 = x * x, y2 = y * y, z2 = z * z;
        /*
        // original from the wiki
        // 
        // return new Mat3(new double[] {
        //         c+x2*C, x*y*C-z*s, x*z*C+y*s,
        //         y*x*C+z*s, c+y2*C, y*z*C-x*s,
        //         z*x*C-y*s, z*y*C+x*s, c+z2*C
        //     });
        */
        return new Mat3(new double[] { // with rows/cols swapped
                c+x2*C, y*x*C+z*s, z*x*C-y*s,
                x*y*C-z*s, c+y2*C, z*y*C+x*s,
                x*z*C+y*s, y*z*C-x*s, c+z2*C
            });
    }
    /**
       Get the concatenation of this matrix and another.
       @param the matrix to concatenate.
       @return a new matrix
    */
    public Mat3 mul(Mat3 m) {
        double[] ret = new double[9];
        for (int r=0; r<3; ++r)
            for (int c=0; c<3; ++c)
                for (int i=0; i<3; ++i)
                    ret[r * 3 + c] +=
                        a[r * 3 + i] * m.a[i * 3 + c];
        return new Mat3(ret);
    }
    /**
       Apply this transform to a vertex.
       @param v the vertex to tramsform
       @return a new vertex
    */
    public Vec3 xform(Vec3 v) {
        return new Vec3(v.x * a[0] + v.y * a[3] + v.z * a[6],
                        v.x * a[1] + v.y * a[4] + v.z * a[7],
                        v.x * a[2] + v.y * a[5] + v.z * a[8]);
    }
}
