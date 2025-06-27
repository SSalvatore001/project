//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * Interface all items with a (roughly) circular shape.
 */
public interface CircularItem extends Item {
    /**
     * Returns the radius of the entity.
     */
    float getRadius();
}