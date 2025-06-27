//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.notifications.PathComputed;
import pp.util.Position;
import pp.util.navigation.Navigator;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Math.signum;
import static pp.util.FloatMath.FLT_EPSILON;
import static pp.util.FloatMath.abs;
import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.normalizeAngle;
import static pp.util.FloatMath.sin;

/**
 * A {@link Behavior} implementation that follows a predefined or asynchronously computed path.
 * <p>
 * The item (must implement {@link Navigable}) will turn and walk toward each successive
 * {@link Position} in the path list, subject to its walking and turning speeds.  If the path
 * was provided as a {@link Callable}, it will be retrieved asynchronously via the model’s
 * executor service.
 * </p>
 */
public class PathfinderBehavior implements Behavior {
    private static final Logger LOGGER = System.getLogger(PathfinderBehavior.class.getName());

    /**
     * The navigable item that will follow this path.
     */
    private final Navigable item;

    /**
     * The current path of positions to follow.
     */
    private final List<Position> path = new LinkedList<>();

    /**
     * Future representing an in-progress asynchronous path computation, if any.
     */
    private Future<List<Position>> futurePath;

    /**
     * Constructs a path-following behavior with an already-computed path.
     *
     * @param item the navigable item to control
     * @param path the list of positions the item will follow, in order
     */
    public PathfinderBehavior(Navigable item, List<Position> path) {
        this.item = item;
        setPath(path);
    }

    /**
     * Constructs a path-following behavior that will compute its path asynchronously
     * to the specified target position.
     * <p>
     * Internally, this submits a callable to the model’s executor which invokes
     * {@link Navigator#findPathTo(Position)} on the item’s navigator. Once the
     * future completes, the resulting path is used for movement in subsequent updates.
     * </p>
     *
     * @param item   the navigable item whose behavior is being controlled
     * @param target the destination position to navigate toward
     */
    public PathfinderBehavior(Navigable item, Position target) {
        this.item = item;
        final Navigator nav = item.getNavigator();
        futurePath = item.getModel().getExecutor().submit(() -> nav.findPathTo(target));
    }

    /**
     * Returns the item navigated by this pathfinder object.
     *
     * @return the item navigated by this pathfinder object.
     */
    public Navigable getItem() {
        return item;
    }

    /**
     * Returns the current navigation path.
     *
     * @return a modifiable list of {@link Position} objects the item will follow
     */
    @Override
    public List<Position> getPath() {
        return path;
    }

    /**
     * Checks whether the item has completely followed the path.
     *
     * @return true if the item as arrived at the end of the navigation path.
     */
    public boolean hasArrived() {
        return futurePath == null && path.isEmpty();
    }

    /**
     * Called once per frame to update the behavior.
     * <p>
     * If a path computation is in progress, this will first check for completion.
     * Then, as long as there is remaining time and positions to follow, it
     * will invoke {@link #followPath(float)} to advance the item.
     * </p>
     *
     * @param delta time elapsed since last update, in seconds
     */
    @Override
    public void update(float delta) {
        checkFuture();
        // move along the path while time remains
        while (delta > 0f && !path.isEmpty())
            delta = followPath(delta);
    }

    /**
     * If an asynchronous path computation was submitted, checks whether it has completed,
     * and if so, retrieves and installs the resulting path.
     */
    private void checkFuture() {
        if (futurePath == null || !futurePath.isDone())
            return;
        try {
            setPath(futurePath.get());
            item.getModel().notifyListeners(new PathComputed(this));
            LOGGER.log(Level.TRACE, "set path to {0}", path); //NON-NLS
        }
        catch (ExecutionException e) {
            LOGGER.log(Level.WARNING, "Error retrieving future path", e); //NON-NLS
        }
        catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Path computation interrupted", e); //NON-NLS
            Thread.currentThread().interrupt();
        }
        finally {
            futurePath = null;
        }
    }

    /**
     * Replaces the current path with the given list.
     *
     * @param newPath the new path to follow
     */
    private void setPath(List<Position> newPath) {
        path.clear();
        path.addAll(newPath);
    }

    /**
     * Advances the item along the path by consuming the given time slice.
     * <p>
     * This method handles both rotation toward the next waypoint and walking toward it,
     * capping movement by the item’s turning and walking speeds.
     * </p>
     *
     * @param delta available time to use, in seconds
     * @return remaining time after moving toward or reaching the next waypoint
     */
    private float followPath(float delta) {
        final Position target = path.get(0);

        // If already at the target (within epsilon), snap to it and remove from path
        if (item.distanceTo(target) < FLT_EPSILON) {
            if (item.canBePlacedAt(target.getX(), target.getY()))
                item.setPos(target.getX(), target.getY());
            path.remove(0);
            return delta;
        }

        // Determine the bearing toward the next waypoint
        final float bearing = atan2(target.getY() - item.getY(),
                                    target.getX() - item.getX());
        final float turnNeeded = normalizeAngle(bearing - item.getRotation());

        // If too far to complete the turn within this time slot, only turn
        float maxTurn = delta * item.getTurningSpeed();
        if (abs(turnNeeded) >= maxTurn) {
            item.setRotation(item.getRotation() + signum(turnNeeded) * maxTurn);
            return 0f;
        }

        // Complete the turn
        item.setRotation(bearing);
        delta -= Math.abs(turnNeeded) / item.getTurningSpeed();

        // Then attempt to walk toward the waypoint
        final float distance = item.distanceTo(target);
        final float maxWalk = delta * item.getWalkingSpeed();
        if (distance > maxWalk) {
            // Move partway toward the target
            final float newX = item.getX() + maxWalk * cos(item.getRotation());
            final float newY = item.getY() + maxWalk * sin(item.getRotation());
            if (item.canBePlacedAt(newX, newY))
                item.setPos(newX, newY);
            else
                path.clear();  // collision or invalid placement, abort path
            return 0f;
        }

        // Reach the waypoint
        if (item.canBePlacedAt(target.getX(), target.getY()))
            item.setPos(target.getX(), target.getY());
        else {
            path.clear();
            return 0f;
        }
        path.remove(0);
        return delta - (distance / item.getWalkingSpeed());
    }

    /**
     * Returns a human-readable representation including the remaining path.
     *
     * @return the class simple name plus the path list
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + path;
    }
}
