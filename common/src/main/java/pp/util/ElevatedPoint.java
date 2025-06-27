//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

/**
 * A point that has additional elevation information, i.e., a bottom and a top height.
 *
 * @param x      x-coordinate of the point
 * @param y      y-coordinate of the point
 * @param bottom bottom height information of the point
 * @param top    top height information of the point
 */

public record ElevatedPoint(float x, float y, float bottom, float top) implements Position {
    /**
     * Creates an ElevatedPoint from the specified Position and the specified additional elevation information
     */
    public ElevatedPoint(Position p, float bottom, float top) {
        this(p.getX(), p.getY(), bottom, top);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
