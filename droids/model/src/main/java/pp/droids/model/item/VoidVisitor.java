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
public interface VoidVisitor {
    /**
     * Convenience method calling item.accept(this)
     */
    default void accept(Item item) {
        item.accept(this);
    }

    /**
     * Visit method for any object of the type {@link Robot}.
     */
    void visit(Robot robot);

    /**
     * Visit method for any object of the type {@link Obstacle}.
     */
    void visit(Obstacle obstacle);

    /**
     * Visit method for any object of the type {@link Projectile}.
     */
    void visit(Projectile proj);

    /**
     * Visit method for any object of the type {@link Polygon}.
     */
    void visit(Polygon poly);

    /**
     * Visit method for any object of the type {@link FinishLine}.
     */
    void visit(FinishLine line);
}
