//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.collisions.NavigablePredicate;
import pp.util.FloatPoint;
import pp.util.Position;
import pp.util.navigation.AbstractNavigator;
import pp.util.navigation.Navigator;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pp.util.FloatMath.atan2;
import static pp.util.FloatMath.normalizeAngle;

/**
 * A navigator that computes an optimal path for a {@link Navigable} item (such as a droid)
 * on a {@link pp.droids.model.DroidsMap}, accounting for both translational and rotational motion.
 * <p>
 * The pathfinding uses an orientation-aware A* algorithm and avoids collisions
 * with static and dynamic obstacles by checking against copies of other items.
 */
class DroidsNavigator implements Navigator {
    /**
     * Logger for debugging and trace-level pathfinding output.
     */
    private static final Logger LOGGER = System.getLogger(DroidsNavigator.class.getName());

    /**
     * The item being navigated.
     */
    private final Navigable item;

    /**
     * The starting position (with orientation) at the time this navigator was created.
     */
    private final OrientedPosition start;

    /**
     * A copy of all relevant map items that might obstruct the navigating item.
     */
    private final List<Item> itemListCopy;

    /**
     * Constructs a new {@code DroidsNavigator} instance for the given item.
     * Copies all potential collision items in the map for safe offline path evaluation.
     *
     * @param item the item to navigate
     */
    public DroidsNavigator(Navigable item) {
        this.item = item;
        this.start = new OrientedPosition(item);
        this.itemListCopy = new ArrayList<>();

        final var pred = new NavigablePredicate(item);
        for (Item it : item.getModel().getDroidsMap().getItems())
            if (it != item && pred.test(it))
                itemListCopy.add(it.copy());
    }

    /**
     * Computes a least-cost path from the item's current position to the given target,
     * avoiding collisions and considering turn and walk costs.
     *
     * @param target the target destination
     * @return a list of positions forming the optimal path; empty if unreachable
     */
    @Override
    public List<Position> findPathTo(Position target) {
        LOGGER.log(Level.TRACE,
                   () -> String.format("look for path from (%f|%f) to (%f|%f)",
                                       start.getX(), start.getY(), target.getX(), target.getY()));

        final List<Position> path = new ArrayList<>(new LocalNavigator(target).findPathFrom(start));

        LOGGER.log(Level.TRACE,
                   () -> "found path: " +
                         path.stream()
                             .map(p -> String.format("(%f|%f)", p.getX(), p.getY()))
                             .collect(Collectors.joining(", ")));

        return path;
    }

    /**
     * A position including orientation (angle in radians), used during pathfinding.
     */
    private record OrientedPosition(float x, float y, float rot) implements Position {
        OrientedPosition(Position p, float rot) {
            this(p.getX(), p.getY(), normalizeAngle(rot));
        }

        OrientedPosition(Item item) {
            this(item, item.getRotation());
        }

        @Override
        public float getX() {return x;}

        @Override
        public float getY() {return y;}
    }

    /**
     * Inner class implementing an orientation-aware A* search for the robot's path.
     */
    private class LocalNavigator extends AbstractNavigator<OrientedPosition> {
        private static final float EPS = 1e-4f;
        private static final float EPS2 = EPS * EPS;

        private final Item itemCopy;
        private final Position target;

        /**
         * Creates a new search instance toward the given target.
         *
         * @param target the goal position
         */
        public LocalNavigator(Position target) {
            this.itemCopy = item.copy();
            this.target = target;
        }

        @Override
        public List<OrientedPosition> findPathFrom(OrientedPosition start) {
            if (start.distanceSquaredTo(target) < EPS2 ||
                collisionAt(start) ||
                collisionAt(target)) {
                return Collections.emptyList();
            }
            return super.findPathFrom(start);
        }

        /**
         * Checks whether the given position is sufficiently close to the target.
         */
        @Override
        protected boolean isTargetPosition(OrientedPosition pos) {
            LOGGER.log(Level.TRACE, "check best position {0}", pos); //NON-NLS
            return pos.distanceSquaredTo(target) < EPS2;
        }

        /**
         * Calculates the cost of moving from {@code prevPos} to {@code nextPos}, including walk and turn cost.
         */
        @Override
        protected float costsForStep(OrientedPosition prevPos, OrientedPosition nextPos) {
            final OrientedPosition from = prevPos != null ? prevPos : start;
            return turnCosts(nextPos.rot() - from.rot()) + walkCosts(from.distanceTo(nextPos));
        }

        /**
         * Estimates the remaining cost from {@code pos} to the target.
         */
        @Override
        protected float estimateCostsToTarget(OrientedPosition pos) {
            return walkCosts(target.distanceTo(pos));
        }

        /**
         * Computes all reachable neighbor positions from a given point, considering 8-way movement and the target.
         */
        @Override
        protected Collection<OrientedPosition> reachablePositions(OrientedPosition pos) {
            final List<OrientedPosition> outgoing = new ArrayList<>(9);
            final int x = Math.round(pos.getX());
            final int y = Math.round(pos.getY());

            for (int toX = x - 1; toX <= x + 1; toX++) {
                for (int toY = y - 1; toY <= y + 1; toY++) {
                    makeOptionalPos(pos, new FloatPoint(toX, toY)).ifPresent(outgoing::add);
                }
            }

            makeOptionalPos(pos, target).ifPresent(outgoing::add);

            LOGGER.log(Level.ALL, "outgoing of {0} : {1}", pos, outgoing); //NON-NLS
            return outgoing;
        }

        /**
         * Computes cost for walking a given distance.
         *
         * @param distance the distance to walk
         * @return cost based on walking speed
         */
        private float walkCosts(float distance) {
            return distance / item.getWalkingSpeed();
        }

        /**
         * Computes cost for rotating by a given angle.
         *
         * @param delta angle in radians
         * @return cost based on turning speed
         */
        private float turnCosts(float delta) {
            float turningSpeed = item.getTurningSpeed();
            return turningSpeed <= 0f ? 0f : Math.abs(normalizeAngle(delta) / turningSpeed);
        }

        /**
         * Attempts to create a new reachable position with heading.
         * Filters out illegal or colliding transitions.
         *
         * @param from source position
         * @param to   candidate target position
         * @return optional new position with heading, if valid
         */
        private Optional<OrientedPosition> makeOptionalPos(Position from, Position to) {
            float dx = to.getX() - from.getX();
            float dy = to.getY() - from.getY();
            float d = dx * dx + dy * dy;

            if (d < EPS2 || collision(from, to))
                return Optional.empty();
            else
                return Optional.of(new OrientedPosition(to, atan2(dy, dx)));
        }

        /**
         * Checks for a collision if the item is placed directly at the given position.
         *
         * @param p the position to check
         * @return true if a collision would occur
         */
        private boolean collisionAt(Position p) {
            return itemListCopy.stream().anyMatch(it -> itemCopy.overlap(p, it));
        }

        /**
         * Checks for a collision if the item moves from {@code from} to {@code to}.
         *
         * @param from start position
         * @param to   end position
         * @return true if a collision would occur during movement
         */
        private boolean collision(Position from, Position to) {
            return itemListCopy.stream().anyMatch(it -> itemCopy.overlapWhenMoving(from, to, it));
        }
    }
}
