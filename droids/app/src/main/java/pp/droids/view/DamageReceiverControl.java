//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view;

import com.jme3.scene.Spatial.CullHint;
import pp.droids.model.item.DamageReceiver;

/**
 * This class controls the robot.
 */
class DamageReceiverControl extends ItemControl<DamageReceiver> {
    private static final float FLASH_TIME = 1f;
    private static final float FLASH_INTERVAL = .1f;
    private CullHint initialCullHint;
    private boolean visible = true;

    /**
     * Constructor to set up the robot.
     *
     * @param item given robot
     */
    public DamageReceiverControl(DamageReceiver item) {
        super(item, 0f);
    }

    /**
     * Updates the position of the damage receiver.
     *
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        if (spatial == null)
            return;
        if (initialCullHint == null)
            initialCullHint = spatial.getCullHint();
        else if (computeVisibility() != visible) {
            visible = !visible;
            spatial.setCullHint(visible ? initialCullHint : CullHint.Always);
        }
    }

    /**
     * Returns true if the damage receiver is visible
     */
    private boolean computeVisibility() {
        if (item.isDestroyed())
            return false;
        if (item.getTimeSinceLastHit() < 0f || item.getTimeSinceLastHit() > FLASH_TIME)
            return true;
        return Math.round(item.getTimeSinceLastHit() / FLASH_INTERVAL) % 2f == 0;
    }
}
