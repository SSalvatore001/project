//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.collisions;

import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Item;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Projectile;
import pp.droids.model.item.Robot;
import pp.droids.model.item.Visitor;

import java.util.function.Predicate;

/**
 * A predicate that is true for all items that can collide with droids etc.
 * if they are close enough.
 */
public class CollisionPredicate implements Visitor<Boolean>, Predicate<Item> {
    private final Item item;

    @Override
    public boolean test(Item item) {
        return item.accept(this);
    }

    public CollisionPredicate(Item item) {
        this.item = item;
    }

    @Override
    public Boolean visit(Robot robot) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visit(Obstacle obstacle) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visit(Projectile proj) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(Polygon poly) {
        return item.getGround() != poly;
    }

    @Override
    public Boolean visit(FinishLine line) {
        return Boolean.FALSE;
    }
}
