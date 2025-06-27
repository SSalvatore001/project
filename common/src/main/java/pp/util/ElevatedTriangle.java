//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import java.util.Objects;

/**
 * A triangle whose corners are points with additional height information.
 */
public class ElevatedTriangle implements Triangle {
    private final ElevatedPoint p1;
    private final ElevatedPoint p2;
    private final ElevatedPoint p3;
    private final Height bottom;
    private final Height top;

    private class Height {
        private final float a;
        private final float b;
        private final float c;

        public Height(float z1, float z2, float z3) {
            final float x1 = p1.getX();
            final float y1 = p1.getY();
            final float dx2 = p2.getX() - x1;
            final float dx3 = p3.getX() - x1;
            final float dy2 = p2.getY() - y1;
            final float dy3 = p3.getY() - y1;
            final float dz2 = z2 - z1;
            final float dz3 = z3 - z1;
            final float d = dx2 * dy3 - dy2 * dx3;
            a = (dz2 * dy3 - dy2 * dz3) / d;
            b = (dx2 * dz3 - dz2 * dx3) / d;
            c = z1 - a * x1 - b * y1;
        }

        public float get(Position p) {
            return a * p.getX() + b * p.getY() + c;
        }
    }

    public ElevatedTriangle(ElevatedPoint a, ElevatedPoint b, ElevatedPoint c) {
        this.p1 = a;
        this.p2 = b;
        this.p3 = c;
        bottom = new Height(p1.bottom(), p2.bottom(), p3.bottom());
        top = new Height(p1.top(), p2.top(), p3.top());
    }

    @Override
    public ElevatedPoint a() {
        return p1;
    }

    @Override
    public ElevatedPoint b() {
        return p2;
    }

    @Override
    public ElevatedPoint c() {
        return p3;
    }

    /**
     * Returns the interpolated elevation of the specified point
     * in the hyperplane defined by the bottom height information of
     * the triangle's corners.
     */
    public float bottom(Position p) {
        return bottom.get(p);
    }

    /**
     * Returns the interpolated elevation of the specified point
     * in the hyperplane defined by the top height information of
     * the triangle's corners.
     */
    public float top(Position p) {
        return top.get(p);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElevatedTriangle that)) return false;
        return Objects.equals(p1, that.p1) && Objects.equals(p2, that.p2) && Objects.equals(p3, that.p3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p1, p2, p3);
    }
}
