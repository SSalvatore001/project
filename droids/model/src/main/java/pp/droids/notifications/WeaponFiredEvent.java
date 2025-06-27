//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.notifications;

import pp.droids.model.item.Item;
import pp.droids.model.item.Weapon;

/**
 * Event when a weapon fired a projectile.
 *
 * @param weapon     the shooter that fired
 * @param projectile the fired projectile
 */
public record WeaponFiredEvent(Weapon weapon, Item projectile) implements GameEvent {
    /**
     * Notifies the game event listener of this event.
     *
     * @param listener the game event listener
     */
    @Override
    public void notify(GameEventListener listener) {
        listener.received(this);
    }
}
