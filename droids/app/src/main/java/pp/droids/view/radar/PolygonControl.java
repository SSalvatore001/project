//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view.radar;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.item.Robot;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * Controls the spatial transformation of a polygon in the radar view
 * relative to the associated robot's position and orientation.
 * <p>
 * This control updates the polygon's local rotation and translation
 * each frame to match the robot's current state, enabling dynamic
 * and accurate visualization in the radar UI.
 * </p>
 */
class PolygonControl extends AbstractControl {
    private final Robot droid;

    /**
     * Constructs a new {@code PolygonControl} for the given robot.
     *
     * @param droid the robot whose position and orientation influence the polygon
     */
    public PolygonControl(Robot droid) {
        this.droid = droid;
    }

    /**
     * Updates the polygon's spatial transformation once per frame.
     * <p>
     * The position is computed as the negative of the robot's position
     * (in model coordinates), rotated according to the robot's orientation,
     * and applied as a local translation. The rotation aligns the polygon
     * with the robot's facing direction.
     * </p>
     *
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float sin = FastMath.sin(droid.getRotation());
            final float cos = FastMath.cos(droid.getRotation());
            final float dx = -droid.getX();
            final float dy = -droid.getY();
            Quaternion quat = new Quaternion();
            quat.fromAngleAxis(HALF_PI - droid.getRotation(), Vector3f.UNIT_Z);
            spatial.setLocalRotation(quat);
            spatial.setLocalTranslation(dx * sin - dy * cos,
                                        dx * cos + dy * sin,
                                        0);
        }
    }

    /**
     * Render-specific behavior. This method is intentionally left empty
     * as no custom rendering is required for polygons in the radar view.
     *
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing
    }
}
