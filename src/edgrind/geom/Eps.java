/*
  Eps.java
  S. Edward Dolan
  Sunday, December  3 2023
*/

package edgrind.geom;

/**
   Compare numeric values inexactly using a tolerance.
 */
public class Eps {
    static final double EPSILON = 1e-8;
    public static boolean zero(double x) {
        return Math.abs(x) <= EPSILON;
    }
    public static boolean eq(double x, double y) {
        return Math.abs(x - y) <= EPSILON;
    }
    public static boolean gt(double x, double y) {
        return x - y > EPSILON;
    }
    public static boolean lt(double x, double y) {
        return y - x > EPSILON;
    }
}
