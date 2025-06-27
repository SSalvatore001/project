//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.notifications;

import pp.droids.model.item.PathfinderBehavior;

/**
 * An event that indicates that a {@linkplain PathfinderBehavior} has finished
 * computing a navigation path. This means that the item navigated by this
 * pathfinder now starts to follow this path.
 *
 * @param pathfinder the pathfinder that has finished computing a navigation path.
 */
public record PathComputed(PathfinderBehavior pathfinder) implements GameEvent {
    @Override
    public void notify(GameEventListener listener) {
        listener.received(this);
    }
}
