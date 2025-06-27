//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import static pp.util.FloatMath.PI;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * An enumeration of walking directions.
 */
public enum Walk {
    /**
     * Walk forward.
     */
    FORWARD,
    /**
     * Walk backward.
     */
    BACKWARD;

    /**
     * Set the position of the specified item so that it
     * walked the specified distance in this direction.
     *
     * @param item     the item that shall walk
     * @param distance the distance to walk
     */
    void walk(Item item, float distance) {
        final float rot = this == FORWARD ? item.getRotation() : item.getRotation() + PI;
        final float newX = item.getX() + distance * cos(rot);
        final float newY = item.getY() + distance * sin(rot);
        if (item.canBePlacedAt(newX, newY))
            item.setPos(newX, newY);
    }
}
