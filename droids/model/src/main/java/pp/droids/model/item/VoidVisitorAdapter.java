//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

/**
 * Default implementation of the VoidVisitor that calls {@linkplain #handle(AbstractItem)}
 * for each item.
 */
public abstract class VoidVisitorAdapter implements VoidVisitor {
    /**
     * Called for each AbstractItem.
     *
     * @param item an AbstractItem
     */
    protected abstract void handle(AbstractItem item);

    @Override
    public void visit(Robot robot) {
        handle(robot);
    }

    @Override
    public void visit(Obstacle obstacle) {
        handle(obstacle);
    }

    @Override
    public void visit(Projectile proj) {
        handle(proj);
    }

    @Override
    public void visit(Polygon poly) {
        handle(poly);
    }

    @Override
    public void visit(FinishLine line) {
        handle(line);
    }
}
