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
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Projectile;
import pp.droids.model.item.Robot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pp.droids.model.Util.getItems;
import static pp.droids.model.Util.makeGround;
import static pp.droids.model.Util.makeItem;

public class GameMapTest {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 25;

    private DroidsModel gameModel;
    private Polygon ground;

    @BeforeEach
    public void setUp() {
        gameModel = new DroidsModel();
        ground = makeGround(gameModel, WIDTH, HEIGHT);
        final Robot droid = new Robot(gameModel);
        droid.setGround(ground);
        final DroidsMap map = new DroidsMap();
        map.add(ground);
        map.add(droid);
        map.setDroid(droid);
        gameModel.setDroidsMap(map);
    }

    @AfterEach
    public void tearDown() {
        gameModel.shutdown();
    }

    // Check if Droid is in game
    @Test
    public void droidExist() {
        assertNotNull(getDroid());
    }

    private Robot getDroid() {
        return gameModel.getDroidsMap().getDroid();
    }

    @Test
    public void enemyExist() {
        final Robot enemy = makeItem(Robot.class, ground, 2, 2);
        gameModel.getDroidsMap().add(enemy);
        gameModel.update(1);
        final List<Robot> droids = getItems(gameModel, Robot.class, getDroid());
        assertEquals(1, droids.size());
        assertSame(enemy, droids.get(0));
    }

    @Test
    public void projectileExist() {
        // wait until droid can fire
        gameModel.update(0.4f);
        assertTrue(getItems(gameModel, Projectile.class).isEmpty());
        getDroid().getWeapon().fire();
        gameModel.update(0.1f);
        assertEquals(1, getItems(gameModel, Projectile.class).size());
        // droid is still reloading => next try to shoot is ineffective
        getDroid().getWeapon().fire();
        gameModel.update(0.1f);
        assertEquals(1, getItems(gameModel, Projectile.class).size());
        gameModel.update(10);
        // projectile has left the game map
        assertTrue(getItems(gameModel, Projectile.class).isEmpty());
    }

    @Test
    public void obstacleExist() {
        final Obstacle obstacle = makeItem(Obstacle.class, ground, 6, 6);
        gameModel.getDroidsMap().add(obstacle);
        gameModel.update(0.1f);
        assertEquals(1, getItems(gameModel, Obstacle.class).size());
        assertSame(obstacle, getItems(gameModel, Obstacle.class).get(0));
    }
}
