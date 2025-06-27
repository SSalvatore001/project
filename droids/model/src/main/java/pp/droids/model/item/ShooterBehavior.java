//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import java.util.SortedSet;

import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.abs;
import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.normalizeAngle;

/**
 * A {@link Behavior} that causes a robot to aim at and shoot the player-controlled droid.
 * <p>
 * The shooter will rotate toward the droid each update cycle, and once aligned will
 * fire its weapon if it has finished reloading. The shooter does not act when it
 * itself is the player droid or is already co-located with the droid.
 * </p>
 */
public class ShooterBehavior implements Behavior {

    /**
     * The robot that will aim and shoot.
     */
    private final Robot shooter;

    /**
     * Constructs a new {@code ShooterBehavior} for the given robot.
     *
     * @param shooter the robot that will perform shooting actions
     */
    public ShooterBehavior(Robot shooter) {
        this.shooter = shooter;
    }

    /**
     * Called once per frame to update the shooter's aiming and firing logic.
     * <p>
     * If the shooter is not the player droid and is not at the exact same
     * position as the droid, it will turn toward the droid at up to its
     * turning speed. Once aligned (within the time slice), it will attempt
     * to fire its weapon if it is not currently reloading.
     * </p>
     *
     * @param delta time in seconds since the last update
     */
    @Override
    public void update(float delta) {
        final Robot droid = shooter.getModel().getDroidsMap().getDroid();
        // Do nothing if this shooter is the player or exactly at the same spot
        if (shooter == droid || shooter.distanceTo(droid) < FLT_EPSILON)
            return;

        // check whether the shooter can see the Droid
        final SortedSet<DistanceItem> hits = shooter.getHits(droid.getX() - shooter.getX(),
                                                             droid.getY() - shooter.getY());
        if (hits.isEmpty())
            throw new IllegalStateException("Shooter must have at least one hit");

        // If the droid is hidden by anything, do nothing
        if (hits.first().item() != droid)
            return;

        // Compute bearing to the player droid
        final float bearing = atan2(droid.getY() - shooter.getY(),
                                    droid.getX() - shooter.getX());
        final float needToTurnBy = normalizeAngle(bearing - shooter.getRotation());

        // If we cannot complete the turn this frame, turn as much as possible
        final float maxTurn = delta * shooter.getTurningSpeed();
        if (abs(needToTurnBy) >= maxTurn) {
            final float rotation = shooter.getRotation() + Math.signum(needToTurnBy) * maxTurn;
            shooter.setRotation(rotation);
        }
        else {
            // Finish the turn exactly toward the droid
            shooter.setRotation(bearing);

            // Attempt to fire if not reloading
            if (!shooter.getWeapon().isReloading())
                shooter.getWeapon().fire();
        }
    }
}
