//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pp.droids.model.item.Item;

import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

/**
 * Class to control missiles
 */
class ItemControl<T extends Item> extends AbstractControl {
    final T item;
    private final float height;

    /**
     * Constructor to set the item and its height.
     *
     * @param item   the given item
     * @param height height of the item
     */
    public ItemControl(T item, float height) {
        this.item = item;
        this.height = height;
    }

    /**
     * Updates the missile position.
     *
     * @param tpf time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            final float elevation = item.getElevation() + height;
            spatial.getLocalRotation().fromAngleAxis(item.getRotation(), UNIT_Y);
            spatial.setLocalTranslation(modelToViewX(item),
                                        modelToViewY(item) + elevation,
                                        modelToViewZ(item));
        }
    }

    /**
     * Does nothing.
     *
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // nothing
    }
}
