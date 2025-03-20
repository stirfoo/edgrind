/*
  Tri3.java
  S. Edward Dolan
  Monday, November 13 2023
*/

package edgrind.geom;

import java.awt.Color;

public class Tri3 {
    public Vec3 v1, v2, v3, n1, n2, n3, n;
    // public Color color = new Color(125, 255, 125);
    public Color color = Color.white;
    public Tri3(Vec3 v1, Vec3 v2, Vec3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.n = normal();
        this.n1 = n;
        this.n2 = n;
        this.n3 = n;
    }
    public Tri3(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 normal) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.n1 = this.n2 = this.n3 = this.n = normal;
    }
    public Tri3(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 n1, Vec3 n2, Vec3 n3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        fixupNormals();
    }
    public Tri3(Vec3 v1, Vec3 v2, Vec3 v3, Color color) {
        this(v1, v2, v3);
        this.color = color;
    }
    public String toString() {
        return "Tri3:" + v1 + "\n     " + v2 + "\n     " + v3 + "\n     " +
            n1 + "\n     " + n2 + "\n     " + n3;
    }
    protected void fixupNormals() {
        // compute the face normal using cclw winding
        this.n = normal();
        // now reverse any vertex normals pointing away from the face normal
        double d = n.dot(n1);
        if (d < 0)
            n1.neg();
        d = n.dot(n2);
        if (d < 0)
            n2.neg();
        d = n.dot(n3);
        if (d < 0)
            n3.neg();
    }
    public Vec3 normal() {
        return Vec3.sub(v2, v1).cross(Vec3.sub(v3, v1)).norm();
    }
    /**
       Adjust the triangle color's intensity.

       @param the scalar which will be clamped to [0, 1].
       @return a new Color
    */
    public Color scaleColor(double s) {
        s = Algo.clamp(s, 0, 1);
        return new Color((int)(color.getRed() * s),
                         (int)(color.getGreen() * s),
                         (int)(color.getBlue() * s));
    }
}
