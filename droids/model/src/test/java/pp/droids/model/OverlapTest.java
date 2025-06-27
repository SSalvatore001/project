//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Robot;
import pp.util.FloatPoint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pp.droids.model.Util.makePolygon;

public class OverlapTest {
    private DroidsModel gameModel;

    @BeforeEach
    public void setUp() {
        gameModel = new DroidsModel();
    }

    @AfterEach
    public void tearDown() {
        gameModel.shutdown();
    }

    @Test
    public void overlapTest() {
        final Robot droid = new Robot(gameModel);
        droid.setPos(1f, 1f);
        final Robot enemy = new Robot(gameModel);
        enemy.setPos(4f, 4f);
        final Polygon maze = makePolygon(gameModel, -1, 1, -1, 1);
        assertFalse(droid.overlap(droid, enemy));
        assertFalse(enemy.overlap(enemy, droid));
        assertTrue(droid.overlap(new FloatPoint(3.7f, 3.7f), enemy));
        assertTrue(enemy.overlap(new FloatPoint(1.3f, 1.3f), droid));
        assertTrue(droid.overlap(droid, maze));
        assertTrue(maze.overlap(maze, droid));
        assertFalse(enemy.overlap(enemy, maze));
        assertFalse(maze.overlap(maze, enemy));
        assertTrue(enemy.overlap(new FloatPoint(1f, 1f), maze));
        assertTrue(maze.overlap(new FloatPoint(5f, 5f), enemy));
    }
}
