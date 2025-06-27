//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.util.Position;

import java.util.Collections;
import java.util.List;

/**
 * Represents any behavior that can control a {@linkplain Robot},
 * such as AI logic or path-following routines.
 * <p>
 * Implementations of this interface define how a robot updates its state over time
 * and may optionally provide a navigation path to visualize or follow.
 * </p>
 *
 * @see Robot#setBehavior(Behavior)
 */
public interface Behavior {

    /**
     * Called once per frame to update the robot's behavior logic.
     * <p>
     * This method is where the robot makes decisions, moves, or reacts
     * to its environment.
     * </p>
     *
     * @param delta the time elapsed since the last frame update, in seconds
     */
    void update(float delta);

    /**
     * Returns the current navigation path the robot is following, if applicable.
     * <p>
     * This path can be visualized in the radar or main view. If the behavior does
     * not currently follow a path, an empty list is returned.
     * </p>
     *
     * @return a list of {@link Position} objects representing the path,
     *         or an empty list if no path is defined
     */
    default List<Position> getPath() {
        return Collections.emptyList();
    }
}
