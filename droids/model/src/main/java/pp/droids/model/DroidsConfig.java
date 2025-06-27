//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import pp.util.config.Config;

/**
 * Class for game model configurations.
 */
@SuppressWarnings("CanBeFinal")
public class DroidsConfig extends Config {
    /**
     * the width of the map.
     */
    @Property("map.width") //NON-NLS
    private int width = 30;

    /**
     * the height of the map.
     */
    @Property("map.height") //NON-NLS
    private int height = 25;

    /**
     * the number of enemies created in each level of a random map.
     */
    @Property("map.enemies") //NON-NLS
    private int numEnemies = 2;

    /**
     * the number of obstacles created in each level of a random map.
     */
    @Property("map.obstacles") //NON-NLS
    private int numObstacles = 5;

    /**
     * true, if the map contains a maze.
     */
    @Property("map.has-maze") //NON-NLS
    private boolean hasMaze = true;

    /**
     * the initial number of lives of the droid.
     */
    @Property("droid.lives") //NON-NLS
    private int droidLives = 4;

    /**
     * Returns the time (in seconds) the droid needs for reloading after each shot.
     */
    @Property("droid.reload") //NON-NLS
    private float droidReloadTime = 1f;

    /**
     * the speed (in units per second) when the droid is walking.
     */
    @Property("droid.speed.walk") //NON-NLS
    private float droidWalkingSpeed = 1f;

    /**
     * the angular velocity (in radians per second) when the droid is turning.
     */
    @Property("droid.speed.turn") //NON-NLS
    private float droidTurningSpeed = 1f;

    /**
     * Returns the width of the map.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the map.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the number of enemies created in each level of a random map.
     */
    public int getNumEnemies() {
        return numEnemies;
    }

    /**
     * Returns the number of obstacles created in each level of a random map.
     */
    public int getNumObstacles() {
        return numObstacles;
    }

    /**
     * Returns true, if the map contains a maze.
     */
    public boolean hasMaze() {
        return hasMaze;
    }

    /**
     * Returns the initial number of lives of the droid.
     */
    public int getDroidLives() {
        return droidLives;
    }

    /**
     * Returns the time (in seconds) the droid needs for reloading after each shot.
     */
    public float getDroidReloadTime() {
        return droidReloadTime;
    }

    /**
     * Returns the speed (in units per second) when the droid is walking.
     */
    public float getDroidWalkingSpeed() {
        return droidWalkingSpeed;
    }

    /**
     * Returns the angular velocity (in radians per second) when the droid is turning.
     */

    public float getDroidTurningSpeed() {
        return droidTurningSpeed;
    }
}
