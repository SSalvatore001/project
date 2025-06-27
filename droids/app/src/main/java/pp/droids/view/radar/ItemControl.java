//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view.radar;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.item.Robot;
import pp.util.Position;

/**
 * Controls the spatial representation of an item in the radar view
 * relative to a reference robot's position and rotation.
 * <p>
 * The transformation is based on the robot's rotation and the item's
 * position in the model. This class is primarily used to update
 * the local translation of a spatial to reflect the item's relative
 * position from the robot's perspective.
 * </p>
 */
class ItemControl extends AbstractControl {
    private final Position item;
    private final Robot droid;

    /**
     * Constructs a new {@code ItemControl} to manage the spatial
     * representation of an item relative to the specified robot.
     *
     * @param item  the position of the item to be controlled
     * @param droid the robot from whose perspective the item is displayed
     */
    public ItemControl(Position item, Robot droid) {
        this.item = item;
        this.droid = droid;
    }

    /**
     * Updates the spatial's local translation to reflect the position
     * of the item relative to the droid's position and orientation.
     * This is called once per frame during the update phase.
     *
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float sin = FastMath.sin(droid.getRotation());
            final float cos = FastMath.cos(droid.getRotation());
            final float dx = item.getX() - droid.getX();
            final float dy = item.getY() - droid.getY();
            spatial.setLocalTranslation(dx * sin - dy * cos - 0.5f,
                                        dx * cos + dy * sin - 0.5f,
                                        0);
        }
    }

    /**
     * Render-related logic for the control. This implementation does nothing,
     * as no special rendering behavior is required for the radar items.
     *
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing
    }
}
