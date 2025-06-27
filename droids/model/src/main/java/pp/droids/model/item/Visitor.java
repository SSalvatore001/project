//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * Interface for an item visitor following the <em>visitor design pattern</em>.
 */
public interface Visitor<T> {
    /**
     * Convenience method calling item.accept(this)
     */
    default T accept(Item item) {
        return item.accept(this);
    }

    /**
     * Visit method for any object of the type {@link Robot}.
     */
    T visit(Robot robot);

    /**
     * Visit method for any object of the type {@link Obstacle}.
     */
    T visit(Obstacle obstacle);

    /**
     * Visit method for any object of the type {@link Projectile}.
     */
    T visit(Projectile proj);

    /**
     * Visit method for any object of the type {@link Polygon}.
     */
    T visit(Polygon poly);

    /**
     * Visit method for any object of the type {@link FinishLine}.
     */
    T visit(FinishLine line);
}
