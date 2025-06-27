//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.navigation;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Base class for computing an optimal path from a start to a target position using the A* algorithm.
 * <p>
 * The type parameter {@code P} defines the type of position in the search space (e.g., grid cell, node, line segment).
 * Implementing classes must define how positions are connected, how to estimate costs, and how to determine targets.
 *
 * @param <P> the position type in the search space
 */
public abstract class AbstractNavigator<P> {

    /**
     * Logger used for debugging and tracing pathfinding operations.
     */
    private static final Logger LOGGER = System.getLogger(AbstractNavigator.class.getName());

    /**
     * Maps positions to their corresponding search nodes during pathfinding.
     */
    private final Map<P, Node> nodes = new HashMap<>();

    /**
     * Priority queue of open nodes to explore, ordered by estimated total cost.
     */
    private final Queue<Node> openQueue = new PriorityQueue<>(Comparator.comparing(Node::getOverallCost));

    /**
     * Set of already processed positions.
     */
    private final Set<P> closedSet = new HashSet<>();

    /**
     * Checks whether a given position is a valid target.
     *
     * @param pos the position to check
     * @return {@code true} if the position is a target; {@code false} otherwise
     */
    protected abstract boolean isTargetPosition(P pos);

    /**
     * Returns all directly reachable positions from the given one.
     *
     * @param pos the current position
     * @return a collection of reachable positions
     */
    protected abstract Collection<P> reachablePositions(P pos);

    /**
     * Computes the cost for moving from {@code prevPos} to {@code nextPos}.
     * If {@code prevPos} is {@code null}, this is considered an initial cost for the start position.
     *
     * @param prevPos the previous position (may be {@code null})
     * @param nextPos the next position
     * @return the step cost
     */
    protected abstract float costsForStep(P prevPos, P nextPos);

    /**
     * Estimates the remaining cost from a given position to any target.
     *
     * @param node the position to estimate from
     * @return an optimistic estimate of remaining path cost
     */
    protected abstract float estimateCostsToTarget(P node);

    /**
     * Computes a minimal cost path from the given start position to a target using A*.
     *
     * @param start the starting position
     * @return a list of positions representing the path, or an empty list if no path was found
     */
    public List<P> findPathFrom(P start) {
        LOGGER.log(Level.TRACE, "find path from {0}", start); //NON-NLS
        nodes.clear();
        openQueue.clear();
        closedSet.clear();

        nodes.put(start, new Node(start, null));

        try {
            while (!openQueue.isEmpty()) {
                final Node bestNode = openQueue.poll();
                LOGGER.log(Level.ALL, "open queue: {0}\nclosedSet: {1}\nbest node: {2}",
                           openQueue, closedSet, bestNode); //NON-NLS
                closedSet.add(bestNode.pos);

                if (isTargetPosition(bestNode.pos))
                    return buildPath(bestNode);

                for (P reachable : reachablePositions(bestNode.pos)) {
                    if (!closedSet.contains(reachable)) {
                        final Node successor = nodes.get(reachable);
                        if (successor == null)
                            nodes.put(reachable, new Node(reachable, bestNode));
                        else
                            successor.tryNewPredecessor(bestNode);
                    }
                }
            }

            // No path found
            return Collections.emptyList();
        }
        finally {
            LOGGER.log(Level.DEBUG, "navigator produced {0} and checked {1} positions", //NON-NLS
                       openQueue.size() + closedSet.size(), closedSet.size());
        }
    }

    /**
     * Determines whether the intermediate position {@code p2} in a path from {@code p1} to {@code p3}
     * can be skipped or replaced for a more optimal direct connection.
     * <p>
     * Override to enable path simplification logic.
     *
     * @param p1 start position
     * @param p2 intermediate position
     * @param p3 end position
     * @return {@code true} if the path can skip {@code p2}; {@code false} otherwise
     */
    protected boolean shortcut(P p1, P p2, P p3) {
        return false;
    }

    /**
     * Reconstructs the full path from a target node by following its predecessors.
     *
     * @param end the final node of the path
     * @return the list of positions from start to target
     */
    private List<P> buildPath(Node end) {
        List<P> plan = new LinkedList<>();
        for (Node n = end; n != null; n = n.predecessor)
            plan.add(0, n.pos);
        return plan;
    }

    /**
     * Represents a node used during A* pathfinding.
     */
    private class Node {
        /**
         * Position in the search space.
         */
        final P pos;

        /**
         * Estimated cost to reach the target from this position.
         */
        final float costToEnd;

        /**
         * Actual cost from the start to this position.
         */
        float costFromStart;

        /**
         * Reference to the predecessor node in the path.
         */
        Node predecessor;

        /**
         * Constructs a new node and inserts it into the open queue.
         *
         * @param pos  the position of this node
         * @param pred the predecessor node
         */
        Node(P pos, Node pred) {
            this.pos = pos;

            // Apply shortcut logic if enabled
            while (pred != null && pred.predecessor != null &&
                   shortcut(pred.predecessor.pos, pred.pos, pos)) {
                pred = pred.predecessor;
            }

            this.predecessor = pred;
            this.costToEnd = estimateCostsToTarget(pos);

            if (predecessor == null)
                this.costFromStart = costsForStep(null, pos);
            else
                this.costFromStart = predecessor.costFromStart + costsForStep(predecessor.pos, pos);

            openQueue.add(this);
        }

        /**
         * Attempts to update this node's path via a better predecessor.
         *
         * @param node the new potential predecessor
         */
        void tryNewPredecessor(Node node) {
            float newCosts = node.costFromStart + costsForStep(node.pos, pos);
            if (newCosts < costFromStart) {
                LOGGER.log(Level.TRACE, "better path to {0} via {1}", this, node); //NON-NLS
                openQueue.remove(this);
                predecessor = node;
                costFromStart = newCosts;
                openQueue.add(this);
            }
        }

        /**
         * Returns the total estimated cost of the path through this node.
         *
         * @return the combined start-to-here and here-to-target cost
         */
        float getOverallCost() {
            return costFromStart + costToEnd;
        }

        @Override
        public String toString() {
            return pos + "<-" + (predecessor == null ? "null" : predecessor.pos);
        }
    }
}
