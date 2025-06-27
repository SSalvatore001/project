//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import org.junit.jupiter.api.Test;
import pp.droids.model.DroidsModel;
import pp.droids.model.Spec;
import pp.util.ElevatedPoint;
import pp.util.FloatPoint;
import pp.util.Position;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pp.util.FloatMath.sqrt;

public class RayTest {
    private final DroidsModel gameModel = new DroidsModel();
    private final Obstacle obstacle = new Obstacle(gameModel, 2f);
    private final Polygon polygon = makePolygon(p(1, 0.5f), p(2, 0.5f), p(2, 1.5f), p(3, 1.5f), p(3, -1.5f), p(2, -1.5f), p(2, -0.5f), p(1, -0.5f));

    Polygon makePolygon(Position... points) {
        final var list = Stream.of(points).map(p -> new ElevatedPoint(p, 0f, 1f)).toList();
        return new Polygon.Builder().setModel(gameModel)
                                    .setSpec(Spec.STONE)
                                    .setOuter(list)
                                    .build();
    }

    private static Position p(float x, float y) {
        return new FloatPoint(x, y);
    }

    @Test
    public void circleTest1() {
        final Ray ray = new Ray(p(0f, 1f), 1f, 0f);
        obstacle.setPos(10f, 0f);
        obstacle.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(obstacle, hit.item());
        assertEquals(8.2679491924f, hit.distance(), 1e-5f);
    }

    @Test
    public void circleTest2() {
        final Ray ray = new Ray(p(0f, 1f), 1f, 0f);
        obstacle.setPos(0f, 0f);
        obstacle.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(obstacle, hit.item());
        assertEquals(1.7320508076f, hit.distance(), 1e-5f);
    }

    @Test
    public void circleTest3() {
        final Ray ray = new Ray(p(0f, 2f), 1f, 0f);
        obstacle.setPos(10f, 0f);
        obstacle.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(obstacle, hit.item());
        assertEquals(10f, hit.distance(), 1e-5f);
    }

    @Test
    public void circleTest4() {
        final Ray ray = new Ray(p(1f, 0f), 0f, 1f);
        obstacle.setPos(0f, 10f);
        obstacle.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(obstacle, hit.item());
        assertEquals(8.2679491924f, hit.distance(), 1e-5f);
    }

    @Test
    public void circleTest5() {
        final Ray ray = new Ray(p(2.5f, 0f), 0f, 1f);
        obstacle.setPos(0f, 10f);
        obstacle.accept(ray);
        assertEquals(0, ray.getItems().size());
    }

    @Test
    public void polygonTest1() {
        final Ray ray = new Ray(p(0f, 1f), 1f, 0f);
        polygon.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(polygon, hit.item());
        assertEquals(2f, hit.distance(), 1e-5f);
    }

    @Test
    public void polygonTest2() {
        final Ray ray = new Ray(p(0f, 0.5f), 1f, 0f);
        polygon.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(polygon, hit.item());
        assertEquals(1f, hit.distance(), 1e-5f);
    }

    @Test
    public void polygonTest3() {
        final Ray ray = new Ray(p(0f, 2f), 1f, -1f);
        polygon.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(polygon, hit.item());
        assertEquals(1.5f * sqrt(2f), hit.distance(), 1e-5f);
    }

    @Test
    public void polygonTest4() {
        final Ray ray = new Ray(p(2f, 0f), 1f, 1f);
        polygon.accept(ray);
        assertEquals(1, ray.getItems().size());
        final DistanceItem hit = ray.getItems().first();
        assertEquals(polygon, hit.item());
        assertEquals(sqrt(2f), hit.distance(), 1e-5f);
    }
}
