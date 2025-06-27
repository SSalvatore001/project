//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;

/**
 * Abstract base class of all items with a (roughly) circular shape in a {@linkplain pp.droids.model.DroidsMap}
 */
public abstract class AbstractCircularItem extends AbstractItem implements CircularItem {

    /**
     * The bounding radius of a circular item.
     */
    private final float boundingRadius;

    /**
     * Creates a new item for the specified game model.
     *
     * @param model          the game model whose game map will contain this item.
     * @param boundingRadius the radius of the bounding circle of this item.
     *                       It is used to compute collisions between two items with circular bounds.
     */
    protected AbstractCircularItem(DroidsModel model, float boundingRadius) {
        super(model);
        this.boundingRadius = boundingRadius;
    }

    /**
     * Returns the radius of the item's bounding circle.
     *
     * @return the bounding radius
     */
    @Override
    public float getRadius() {
        return boundingRadius;
    }
}