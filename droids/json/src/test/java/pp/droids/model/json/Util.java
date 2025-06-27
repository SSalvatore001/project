//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.json;

import pp.droids.model.DroidsModel;
import pp.droids.model.item.Item;
import pp.util.Position;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

class Util {
    static final float EPS = 0.00001f;

    private Util() { /* don't instantiate */ }

    static void assertPositionEquals(Position expected, Position actual, float eps) {
        if (expected.distanceSquaredTo(actual) > eps)
            fail(String.format(Locale.US, "expected: (%f, %f) but was: (%f, %f))", expected.getX(), expected.getY(), actual.getX(), actual.getY()));
    }

    static void checkEqualPositions(List<? extends Position> expected, List<? extends Position> actual) {
        final Iterator<? extends Position> it1 = expected.iterator();
        final Iterator<? extends Position> it2 = actual.iterator();
        while (it1.hasNext() && it2.hasNext())
            assertPositionEquals(it1.next(), it2.next(), EPS);
        assertFalse(it1.hasNext());
        assertFalse(it2.hasNext());
    }

    /**
     * Returns the list of all items of the specified class.
     *
     * @param model the model
     * @param clazz the required class of the items
     */
    @SuppressWarnings("unchecked")
    static <T> List<T> getItems(DroidsModel model, Class<T> clazz) {
        final List<Object> result = new ArrayList<>();
        for (Item item : model.getDroidsMap().getItems())
            if (clazz.isInstance(item))
                result.add(item);
        return (List<T>) result;
    }

    /**
     * Returns the list of all items of the specified class without the specified object.
     *
     * @param model  the model
     * @param clazz  the required class of the items
     * @param ignore the item to be ignored
     */
    static <T> List<T> getItems(DroidsModel model, Class<T> clazz, Object ignore) {
        final List<T> items = getItems(model, clazz);
        items.remove(ignore);
        return items;
    }
}

