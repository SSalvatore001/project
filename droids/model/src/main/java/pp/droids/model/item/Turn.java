//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * An enumeration of turning directions.
 */
public enum Turn {
    /**
     * Turn left.
     */
    LEFT,
    /**
     * Turn right.
     */
    RIGHT;

    /**
     * Set the rotation of the specified item so that it
     * turned the specified angle in this direction.
     *
     * @param item  the item that shall walk
     * @param angle how much the item shall turn (in radians)
     */
    void turn(Item item, float angle) {
        final float rot = this == LEFT ? angle : -angle;
        item.setRotation(item.getRotation() + rot);
    }
}
