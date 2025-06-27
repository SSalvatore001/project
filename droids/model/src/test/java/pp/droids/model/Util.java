//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model;

import pp.droids.model.item.Item;
import pp.droids.model.item.Polygon;
import pp.util.ElevatedPoint;

import java.util.List;

/**
 * Provides utility methods for testing.
 */
class Util {
    private Util() { /* don't instantiate */ }

    static <T extends Item> T makeItem(Class<T> clazz, Polygon ground, float x, float y) {
        try {
            final T item = clazz.getConstructor(DroidsModel.class).newInstance(ground.getModel());
            item.setGround(ground);
            item.setPos(x, y);
            return item;
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends Item> List<T> getItems(DroidsModel gameModel, Class<T> clazz) {
        return getItems(gameModel, clazz, null);
    }

    static <T extends Item> List<T> getItems(DroidsModel gameModel, Class<T> clazz, Item ignore) {
        return gameModel.getDroidsMap().getItems().stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .filter(o -> o != ignore)
                        .toList();
    }

    static Polygon makeGround(DroidsModel model, int width, int height) {
        return makePolygon(model, -0.5f, width - 0.5f, -0.5f, height - 0.5f);
    }

    static Polygon makePolygon(DroidsModel model, float xMin, float xMax, float yMin, float yMax) {
        return new Polygon.Builder().setModel(model)
                                    .setSpec(Spec.WALL)
                                    .setOuter(List.of(pe(xMin, yMin),
                                                      pe(xMax, yMin),
                                                      pe(xMax, yMax),
                                                      pe(xMin, yMax)))
                                    .build();
    }

    private static ElevatedPoint pe(float x, float y) {
        return new ElevatedPoint(x, y, -0.5f, 0f);
    }
}
