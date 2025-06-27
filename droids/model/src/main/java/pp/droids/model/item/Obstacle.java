//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;

/**
 * A class representing an obstacle
 */
public class Obstacle extends AbstractCircularItem {

    /**
     * The standard bounding radius of an obstacle.
     */
    public static final float BOUNDING_RADIUS = .45f;

    /**
     * Creates an obstacle
     *
     * @param model  the model
     * @param radius the obstacle's radius
     */
    public Obstacle(DroidsModel model, float radius) {
        super(model, radius);
    }

    /**
     * Creates an obstacle with a default radius
     *
     * @param model the model
     */
    public Obstacle(DroidsModel model) {
        this(model, BOUNDING_RADIUS);
    }

    /**
     * Accept method of the visitor pattern.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accept method of the {@link VoidVisitor}.
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }
}
