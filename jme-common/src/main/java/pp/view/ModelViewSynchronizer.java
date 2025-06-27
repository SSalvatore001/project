//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.view;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for synchronizing a model of items with their corresponding
 * visual representations (spatials) in the scene graph.
 * <p>
 * Subclasses are responsible for defining how model items are translated into spatials
 * by implementing the {@link #translate(Object)} method.
 *
 * @param <I> the type of model item to be synchronized with the view
 */
public abstract class ModelViewSynchronizer<I> {
    private static final Logger LOGGER = System.getLogger(ModelViewSynchronizer.class.getName());
    private final Node itemNode = new Node("items"); //NON-NLS
    private final Map<I, Spatial> itemMap = new HashMap<>();

    /**
     * Constructs a new {@code ModelViewSynchronizer} and attaches the internal item node
     * to the specified root node in the scene graph.
     *
     * @param root the root node of the scene graph to which the items node is attached
     */
    protected ModelViewSynchronizer(Node root) {
        root.attachChild(itemNode);
    }

    /**
     * Returns the spatial associated with the given model item.
     *
     * @param item the model item
     * @return the spatial representing the item, or {@code null} if no spatial exists
     */
    public Spatial getSpatial(I item) {
        return itemMap.get(item);
    }

    /**
     * Removes the spatial associated with the specified model item from both
     * the internal map and the scene graph.
     *
     * @param item the model item to remove
     */
    public void delete(I item) {
        final Spatial spatial = itemMap.remove(item);
        if (spatial != null) {
            spatial.removeFromParent();
            LOGGER.log(Level.DEBUG, "removed spatial for {0} in {1}", item, this); //NON-NLS
        }
    }

    /**
     * Adds the given model item to the view by translating it into a spatial and attaching it
     * to the scene graph. If the item is already managed, a warning is logged and it is not added again.
     *
     * @param item the model item to add
     */
    public void add(I item) {
        if (itemMap.containsKey(item)) {
            LOGGER.log(Level.WARNING, "Item {0} already managed by {1}", item, this); //NON-NLS
            return;
        }
        final Spatial spatial = translate(item);
        itemMap.put(item, spatial);
        LOGGER.log(Level.DEBUG, "added spatial for {0} in {1}", item, this); //NON-NLS
        if (spatial != null)
            itemNode.attachChild(spatial);
    }

    /**
     * Clears all items from the view by detaching all spatials and clearing the internal map.
     */
    public void clear() {
        LOGGER.log(Level.DEBUG, "clear"); //NON-NLS
        itemMap.clear();
        itemNode.detachAllChildren();
    }

    /**
     * Translates a model item into a corresponding spatial for representation in the scene graph.
     * Subclasses must implement this method to define how model items are visualized.
     * <p>
     * If this method returns {@code null}, the item will not be represented in the scene.
     *
     * @param item the model item to translate
     * @return the spatial representing the item, or {@code null} if the item should not be visualized
     */
    protected abstract Spatial translate(I item);
}
