//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;
import pp.util.Position;
import pp.util.navigation.Navigator;

import java.lang.System.Logger.Level;
import java.util.Collections;
import java.util.List;

/**
 * Represents a robot (or "droid") in the game world.
 * <p>
 * A robot is a circular item that can move, turn, navigate to target positions,
 * receive damage, and perform actions defined by its current {@link Behavior}.
 * Each robot also carries a {@link Weapon} for combat and uses a
 * {@link Navigator} to plan movement paths.
 * </p>
 */
public class Robot extends AbstractCircularItem implements DamageReceiver, Navigable {
    /**
     * The standard bounding radius of a droid.
     */
    public static final float BOUNDING_RADIUS = .45f;

    private final TerminationVisitor terminator = new TerminationVisitor(this);
    private final DamageReceiverSupport damageReceiverSupport;
    private final Weapon weapon;
    private Behavior behavior;

    /**
     * The current direction of turning. {@code null} means no turning.
     */
    private Turn turnDir;

    /**
     * The current direction of walking. {@code null} means the robot is not moving.
     */
    private Walk walkDir;

    /**
     * Constructs a robot with custom physical and combat parameters.
     *
     * @param model          the game model
     * @param boundingRadius the radius of the robot's collision circle
     * @param initialLives   the initial number of lives (hit points)
     * @param reloadTime     cooldown time between shots, in seconds
     */
    public Robot(DroidsModel model, float boundingRadius, int initialLives, float reloadTime) {
        super(model, boundingRadius);
        damageReceiverSupport = new DamageReceiverSupport(this, initialLives);
        weapon = new Weapon(this, reloadTime);
    }

    /**
     * Constructs a standard robot using parameters from the model configuration.
     *
     * @param model the game model that owns this robot
     */
    public Robot(DroidsModel model) {
        this(model, BOUNDING_RADIUS, model.getConfig().getDroidLives(), model.getConfig().getDroidReloadTime());
    }

    /**
     * Returns {@code true} if this robot is the player's controllable droid.
     *
     * @return {@code true} if this is the player-controlled droid
     */
    public boolean isDroid() {
        return model.getDroidsMap().getDroid() == this;
    }

    @Override
    public DamageReceiverSupport getDamageReceiverSupport() {
        return damageReceiverSupport;
    }

    @Override
    public float getWalkingSpeed() {
        return model.getConfig().getDroidWalkingSpeed();
    }

    @Override
    public float getTurningSpeed() {
        return model.getConfig().getDroidTurningSpeed();
    }

    /**
     * Starts or stops walking in the specified direction.
     * If already walking in that direction, stops movement instead.
     *
     * @param dir the direction to walk
     */
    public void walk(Walk dir) {
        if (walkDir == null || walkDir == dir)
            walkDir = dir;
        else
            walkDir = null;
        setBehavior(null);
    }

    /**
     * Starts or stops turning in the specified direction.
     * If already turning in that direction, stops turning instead.
     *
     * @param dir the direction to turn
     */
    public void turn(Turn dir) {
        if (turnDir == null || turnDir == dir)
            turnDir = dir;
        else
            walkDir = null;
        setBehavior(null);
    }

    /**
     * Applies movement and turning for this update cycle.
     *
     * @param delta time in seconds since the last update
     */
    private void updateMovement(float delta) {
        if (walkDir != null)
            walkDir.walk(this, delta * getWalkingSpeed());
        if (turnDir != null)
            turnDir.turn(this, delta * getTurningSpeed());
        walkDir = null;
        turnDir = null;
    }

    /**
     * Returns the current path that the robot is following.
     *
     * @return a list of positions, or an empty list if no path is active
     */
    public List<Position> getPath() {
        return getBehavior() == null ? Collections.emptyList() : getBehavior().getPath();
    }

    /**
     * Starts navigation to a target position using pathfinding.
     *
     * @param target the destination position
     */
    public void navigateTo(Position target) {
        AbstractItem.LOGGER.log(Level.INFO, "Navigating to ({0}|{1})", target.getX(), target.getY());  //NON-NLS
        setBehavior(new PathfinderBehavior(this, target));
    }

    /**
     * Returns this robot's weapon for shooting and combat.
     *
     * @return the weapon instance
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * Returns the robot's current behavior logic.
     *
     * @return the current {@link Behavior}, or {@code null} if none
     */
    public Behavior getBehavior() {
        return behavior;
    }

    /**
     * Assigns a new behavior to the robot, replacing the previous one.
     *
     * @param behavior the behavior to set, or {@code null} to clear
     */
    public void setBehavior(Behavior behavior) {
        LOGGER.log(Level.TRACE, "new behavior {0} of {1}", behavior, this); //NON-NLS
        this.behavior = behavior;
    }

    /**
     * Updates this robot every frame, applying movement, behavior, damage, and weapon logic.
     *
     * @param delta time since the last frame, in seconds
     */
    @Override
    public void update(float delta) {
        if (behavior != null)
            behavior.update(delta);
        weapon.update(delta);
        damageReceiverSupport.update(delta);
        updateMovement(delta);
        if (!isDestroyed())
            checkTermination();
    }

    /**
     * Checks whether the robot should terminate (e.g. due to collisions),
     * and applies a termination visitor if so.
     */
    private void checkTermination() {
        for (Item item : getModel().getDroidsMap().getItems())
            item.accept(terminator);
    }

    /**
     * Returns a navigator object that can compute paths from this robot to other locations.
     *
     * @return a new {@link DroidsNavigator} for pathfinding
     */
    @Override
    public Navigator getNavigator() {
        return new DroidsNavigator(this);
    }

    /**
     * Accepts a generic visitor.
     *
     * @param v the visitor to apply
     * @return result from the visitor
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accepts a void visitor.
     *
     * @param v the visitor to apply
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }
}
