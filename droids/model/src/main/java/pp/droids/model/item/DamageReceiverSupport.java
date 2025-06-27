//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.notifications.HitEvent;

/**
 * Implementations for the DamageReceiver interface.
 *
 * @see pp.droids.model.item.DamageReceiver
 */
public class DamageReceiverSupport {
    private final DamageReceiver damageReceiver;

    /**
     * The number of lives of the damage receiver.
     */
    private int lives;

    /**
     * The time passed since the damage receiver was hit the last time.
     */
    private float timeSinceLastHit = -1f;

    /**
     * Creates a support object for damage receivers
     *
     * @param damageReceiver the damage receiver
     * @param initialLives   the number of lives that this item initially has
     */
    public DamageReceiverSupport(DamageReceiver damageReceiver, int initialLives) {
        this.damageReceiver = damageReceiver;
        lives = initialLives;
    }

    /**
     * Returns the number of lives this item still has.
     *
     * @return remaining number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Returns the time in seconds since this Item has been hit the last time,
     * or a negative value if it has never been hit.
     */
    public float getTimeSinceLastHit() {
        return timeSinceLastHit;
    }

    /**
     * This method is called whenever the item is hit. This  method reduces the number of lives and
     * destroys it (by calling {@linkplain pp.droids.model.item.Item#destroy()}) if there are no lives left.
     *
     * @param item the item hitting this item
     */
    public void hitBy(Item item) {
        timeSinceLastHit = 0f;
        damageReceiver.getModel().notifyListeners(new HitEvent(damageReceiver, item));
        if (--lives <= 0)
            damageReceiver.destroy();
    }

    /**
     * Updates the item
     *
     * @param delta time in seconds since the last update call
     */
    public void update(float delta) {
        if (timeSinceLastHit >= 0)
            timeSinceLastHit += delta;
    }
}
