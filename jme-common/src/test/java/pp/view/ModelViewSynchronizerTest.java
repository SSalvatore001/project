//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.view;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModelViewSynchronizerTest {

    private Node rootNode;
    private Node itemNode;
    private ModelViewSynchronizer<String> synchronizer;

    @BeforeEach
    public void setUp() {
        rootNode = new Node("root"); //NON-NLS
        synchronizer = new ModelViewSynchronizer<>(rootNode) {
            @Override
            protected Spatial translate(String item) {
                return new Node(item);
            }
        };
        itemNode = (Node) rootNode.getChild(0);
    }

    @Test
    public void testConstructor() {
        assertNotNull(itemNode);
        assertEquals(1, rootNode.getQuantity());
    }

    @Test
    public void testAdd() {
        String item = "item1"; //NON-NLS
        synchronizer.add(item);

        Spatial spatial = synchronizer.getSpatial(item);
        assertNotNull(spatial);
        assertEquals(item, spatial.getName());
        assertTrue(itemNode.hasChild(spatial));
    }

    @Test
    public void testDelete() {
        String item = "item1"; //NON-NLS
        synchronizer.add(item);
        synchronizer.delete(item);

        Spatial spatial = synchronizer.getSpatial(item);
        assertNull(spatial);
        assertFalse(itemNode.hasChild(spatial));
    }

    @Test
    public void testClear() {
        synchronizer.add("item1"); //NON-NLS
        synchronizer.add("item2"); //NON-NLS
        synchronizer.clear();

        assertNull(synchronizer.getSpatial("item1")); //NON-NLS
        assertNull(synchronizer.getSpatial("item2")); //NON-NLS
        assertEquals(0, itemNode.getQuantity());
    }

    @Test
    public void testAddDuplicate() {
        String item = "item1"; //NON-NLS
        synchronizer.add(item);
        synchronizer.add(item);

        assertEquals(1, itemNode.getQuantity());
    }
}
