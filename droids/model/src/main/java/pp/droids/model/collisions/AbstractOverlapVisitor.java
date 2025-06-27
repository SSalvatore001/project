//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.collisions;

import pp.droids.model.item.CircularItem;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.PolygonItem;
import pp.droids.model.item.Projectile;
import pp.droids.model.item.Robot;
import pp.droids.model.item.Visitor;

/**
 * This visitor checks if the item overlaps with another item
 *
 * @param <R>
 */
abstract class AbstractOverlapVisitor<R> implements Visitor<R> {
    abstract R handle(CircularItem item);

    abstract R handle(PolygonItem item);

    @Override
    public R visit(Robot robot) {
        return handle(robot);
    }

    @Override
    public R visit(Obstacle obstacle) {
        return handle(obstacle);
    }

    @Override
    public R visit(Projectile proj) {
        return handle(proj);
    }

    @Override
    public R visit(Polygon poly) {
        return handle(poly);
    }

    @Override
    public R visit(FinishLine line) {
        return handle(line);
    }
}
