//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

/**
 * An interface for triangles.
 */
public interface Triangle {
    /**
     * Returns a triangle corner.
     */
    Position a();

    /**
     * Returns a triangle corner.
     */
    Position b();

    /**
     * Returns a triangle corner.
     */
    Position c();

    /**
     * Returns true if the triangle points make a left turn (with y growing downwards!).
     */
    default boolean turnsLeft() {
        return isLeftTurn(a(), b(), c());
    }

    /**
     * Returns true if the specified point lies within this triangle or on its border.
     *
     * @param pos a point
     * @param eps a non-negative epsilon value used as a threshold when checking whether a point lies on the triangle border.
     */
    default boolean contains(Position pos, float eps) {
        return contains(pos.getX(), pos.getY(), eps);
    }

    /**
     * Returns true if the specified point lies within this triangle or on its border.
     *
     * @param x   x-coordinate of the point
     * @param y   y-coordinate of the point
     * @param eps a non-negative epsilon value used as a threshold when checking whether a point lies on the triangle border.
     */
    default boolean contains(float x, float y, float eps) {
        if (turnsLeft())
            return right(x, y, a(), c(), eps) && right(x, y, c(), b(), eps) && right(x, y, b(), a(), eps);
        return right(x, y, a(), b(), eps) && right(x, y, b(), c(), eps) && right(x, y, c(), a(), eps);
    }

    private static boolean right(float x, float y, Position p1, Position p2, float eps) {
        return (p2.getY() - p1.getY()) * (x - p1.getX()) - (p2.getX() - p1.getX()) * (y - p1.getY()) < eps;
    }

    /**
     * Returns true if the specified points make a right turn (with y growing downwards!).
     */
    static boolean isRightTurn(Position p1, Position p2, Position p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) > (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }

    /**
     * Returns true if the specified points make a left turn  (with y growing downwards).
     */
    static boolean isLeftTurn(Position p1, Position p2, Position p3) {
        return isRightTurn(p1, p3, p2);
    }
}
