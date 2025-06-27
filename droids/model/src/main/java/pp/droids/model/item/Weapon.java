//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.notifications.WeaponFiredEvent;

import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

/**
 * A weapon that can be fired and that needs time for reloading.
 */
public class Weapon {
    private final Item shooter;

    /**
     * The bounding radius of a projectile.
     */
    public static final float PROJECTILE_BOUNDING_RADIUS = .05f;

    /**
     * The time a projectile will live.
     */
    public static final float PROJECTILE_LIFE_TIME = 10f;

    /**
     * The distance how far away projectiles will start.
     */
    public static final float PROJECTILE_START_DIST = 1f;

    /**
     * The speed of a projectile.
     */
    public static final float PROJECTILE_SPEED = 20f;

    private final float reloadTime;

    /**
     * The remaining time until reloading is finished.
     */
    private float remainingReloadTime;

    /**
     * Creates a support object for shooters
     *
     * @param shooter    the shooter
     * @param reloadTime the reloading time (in seconds) of this weapon
     */
    public Weapon(Item shooter, float reloadTime) {
        this.shooter = shooter;
        this.reloadTime = reloadTime;
    }

    /**
     * Returns the reloading time (in seconds) of this weapon.
     */
    public float getReloadTime() {
        return reloadTime;
    }

    /**
     * Creates a projectile. This method is called whenever this weapon is fired.
     *
     * @return the new projectile
     */
    public Item makeProjectile() {
        final Projectile projectile = new Projectile(shooter.getModel(), PROJECTILE_BOUNDING_RADIUS);
        if (shooter.getGround() != null)
            projectile.setGround(shooter.getGround());
        projectile.setSpeed(PROJECTILE_SPEED);
        projectile.setLifeTime(PROJECTILE_LIFE_TIME);
        projectile.setPos(shooter.getX() + PROJECTILE_START_DIST * cos(shooter.getRotation()),
                          shooter.getY() + PROJECTILE_START_DIST * sin(shooter.getRotation()));
        projectile.setRotation(shooter.getRotation());
        return projectile;
    }

    /**
     * Lets the weapon fire a projectile if it is not reloading. The projectile is
     * created by calling method {@linkplain Weapon#makeProjectile()}
     */
    public void fire() {
        if (!isReloading()) {
            remainingReloadTime = reloadTime;
            final Item projectile = makeProjectile();
            shooter.getModel().getDroidsMap().add(projectile);
            shooter.getModel().notifyListeners(new WeaponFiredEvent(this, projectile));
        }
    }

    /**
     * Returns whether this weapon is still reloading.
     *
     * @return true if the weapon is still reloading
     */
    public boolean isReloading() {
        return remainingReloadTime > 0f;
    }

    /**
     * Updates the item
     *
     * @param delta time in seconds since the last update call
     */
    public void update(float delta) {
        if (isReloading())
            remainingReloadTime -= delta;
    }
}
