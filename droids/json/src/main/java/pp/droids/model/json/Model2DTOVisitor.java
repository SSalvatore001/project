//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import pp.droids.model.DroidsMap;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Item;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Projectile;
import pp.droids.model.item.Robot;
import pp.droids.model.item.VoidVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor class for creating DTOs for a game map with its items.
 */
class Model2DTOVisitor implements VoidVisitor {
    private final DroidsMapDTO levelMap;
    private final Map<Item, String> idMap = new HashMap<>();

    public Model2DTOVisitor(DroidsMap map, DroidsMapDTO levelMap) {
        this.levelMap = levelMap;
        int ctr = 0;
        for (Item it : map.getItems())
            idMap.put(it, "id" + ++ctr);
    }

    String getId(Item item) {
        return idMap.get(item);
    }

    @Override
    public void visit(Robot robot) {
        levelMap.items.add(new CircularItemDTO(robot, idMap));
    }

    @Override
    public void visit(Obstacle obstacle) {
        levelMap.items.add(new CircularItemDTO(obstacle, idMap));
    }

    @Override
    public void visit(Polygon poly) {
        levelMap.items.add(new PolygonDTO(poly, idMap));
    }

    @Override
    public void visit(FinishLine line) {
        levelMap.items.add(new FinishLineDTO(line, idMap));
    }

    @Override
    public void visit(Projectile proj) {
        /* do nothing */
    }
}