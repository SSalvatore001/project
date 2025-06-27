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
import pp.droids.model.item.DamageReceiver;
import pp.droids.model.item.Item;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.PathfinderBehavior;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Robot;
import pp.util.FloatPoint;
import pp.util.Position;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static pp.droids.model.Util.getItems;
import static pp.droids.model.Util.makeGround;
import static pp.droids.model.Util.makeItem;
import static pp.util.FloatMath.ZERO_TOLERANCE;

public class GamePlayTest {
    public static final int WIDTH = 30;
    public static final int HEIGHT = 25;
    private static final float EPS = 0.00001f;

    private DroidsModel gameModel;
    private DroidsMap map;
    private Robot droid;
    private Robot enemy;

    @BeforeEach
    public void setUp() {
        gameModel = new DroidsModel();
        map = gameModel.getDroidsMap();
        final int dx = WIDTH / 2;
        final int dh = HEIGHT / 2;
        final Polygon ground = makeGround(gameModel, WIDTH, HEIGHT);
        droid = makeItem(Robot.class, ground, dx, dh);
        enemy = makeItem(Robot.class, ground, 0f, 0f);
        map.add(ground);
        map.add(droid);
        map.setDroid(droid);
        map.add(enemy);
        map.add(makeItem(Robot.class, ground, WIDTH - 1, HEIGHT - 1));
        map.add(makeItem(Obstacle.class, ground, dx, 0f));
    }

    @AfterEach
    public void tearDown() {
        gameModel.shutdown();
    }

    /**
     * Check that enemy is destroyed after four hits
     */
    @Test
    public void enemyDies() {
        // enemy should be alive
        assertFalse(enemy.isDestroyed());
        // first hit
        hit(enemy);
        // enemy should be alive
        assertFalse(enemy.isDestroyed());
        // three more hits
        hit(enemy);
        hit(enemy);
        hit(enemy);
        // enemy should be dead
        assertTrue(enemy.isDestroyed());
    }

    /**
     * Check that droid is destroyed after 40 hits
     */
    @Test
    public void droidDies() {
        // droid should be alive
        assertFalse(droid.isDestroyed());

        // 39 hits for droid
        for (int i = 0; i < 3; i++)
            hit(droid);

        // droid should be alive with only 1 live
        assertFalse(droid.isDestroyed());
        assertEquals(1, droid.getLives());

        // next hit -> dead
        hit(droid);

        // check, if droid is destroyed and not more visible
        assertTrue(droid.isDestroyed());
    }

    /**
     * Check that droid starts in the center of the map
     */
    @Test
    public void playerStartPosition() {
        // Check, if droid on the right position
        final int expectedX = WIDTH / 2;
        final int expectedY = HEIGHT / 2;
        assertPositionEquals(new FloatPoint(expectedX, expectedY), gameModel.getDroidsMap().getDroid(), EPS);
    }

    /**
     * Check that droid cannot move to obstacle position
     */
    @Test
    public void droidMoveToObstacle() {
        final float startX = droid.getX();
        final float startY = droid.getY();

        // try to navigate to obstacle position
        final Obstacle obstacle = getItems(gameModel, Obstacle.class).get(0);
        navigateTo(obstacle);
        droid.update(2);
        // position of droid should not be changed
        assertEquals(startX, droid.getX(), ZERO_TOLERANCE);
        assertEquals(startY, droid.getY(), ZERO_TOLERANCE);
    }

    /**
     * Check that droid can only move to enemy position if enemy has been destroyed
     */
    @Test
    public void droidMoveToEnemy() {
        final float startX = droid.getX();
        final float startY = droid.getY();

        // navigate to enemy position
        navigateTo(enemy); // should not succeed
        droid.update(2);

        // position of droid should not be changed
        assertEquals(startX, droid.getX(), ZERO_TOLERANCE);
        assertEquals(startY, droid.getY(), ZERO_TOLERANCE);

        // destroy enemy with hits
        for (int i = 0; i < 4; i++)
            hit(enemy);

        // enemy should be destroyed
        assertTrue(enemy.isDestroyed());

        // navigate to enemy position
        gameModel.update(3);
        navigateTo(enemy);
        droid.update(25f);

        // position of droid should be changed
        assertPositionEquals(enemy, droid, EPS);
    }

    /**
     * Check that projectile hits the enemy
     */
    @Test
    public void droidProjectileTest() {
        droid.setRotation(-2.466851711f);
        final Item projectile = droid.getWeapon().makeProjectile();

        // add projectile to game
        gameModel.update(3);
        map.add(projectile);

        assertEquals(4, enemy.getLives());

        // loop until enemy was damaged or max time has elapsed
        for (int i = 0; i < 10000 && enemy.getLives() == 4; i++)
            gameModel.update(0.05f);

        // check that enemy has been hit once
        assertEquals(3, enemy.getLives());
    }

    private void hit(DamageReceiver item) {
        final Item projectile = gameModel.getDroidsMap().getDroid().getWeapon().makeProjectile();
        item.hitBy(projectile);
    }

    private void navigateTo(Position pos) {
        final Robot myDroid = gameModel.getDroidsMap().getDroid();
        myDroid.setBehavior(new PathfinderBehavior(myDroid, myDroid.getNavigator().findPathTo(pos)));
    }

    private static void assertPositionEquals(Position expected, Position actual, float eps) {
        if (expected.distanceSquaredTo(actual) > eps)
            fail(String.format(Locale.US, "expected: (%f, %f) but was: (%f, %f))",
                               expected.getX(), expected.getY(),
                               actual.getX(), actual.getY()));
    }
}
