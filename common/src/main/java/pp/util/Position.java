//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import static pp.util.FloatMath.sqrt;

/**
 * Interface for all objects that provide a position in the plane.
 */
public interface Position extends Comparable<Position> {
    /**
     * Returns the x-coordinate of the position
     *
     * @return x-coordinate as float
     */
    float getX();

    /**
     * Returns the y-coordinate of the position
     *
     * @return y-coordinate as float
     */
    float getY();

    /**
     * Returns the distance of this position from the specified position.
     *
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @return distance
     */
    default float distanceTo(float x, float y) {
        return sqrt(distanceSquaredTo(x, y));
    }

    /**
     * Returns the distance of this position from the specified position.
     * This is just a convenience method for {@linkplain #distanceTo(float, float)}.
     *
     * @param other the other position
     * @return distance
     */
    default float distanceTo(Position other) {
        return distanceTo(other.getX(), other.getY());
    }

    /**
     * Returns the squared distance of this position from the specified position.
     *
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @return squared distance
     */
    default float distanceSquaredTo(float x, float y) {
        final float dx = getX() - x;
        final float dy = getY() - y;
        return dx * dx + dy * dy;
    }

    /**
     * Returns the squared distance of this position from the specified position.
     *
     * @param p the other position
     * @return squared distance
     */
    default float distanceSquaredTo(Position p) {
        return distanceSquaredTo(p.getX(), p.getY());
    }

    /**
     * Compares positions in the plane from top to bottom
     * (y coordinates grow downwards) and then from left ro right.
     */
    @Override
    default int compareTo(Position other) {
        final int c = Float.compare(getY(), other.getY());
        return c != 0 ? c : Float.compare(getX(), other.getX());
    }
}
