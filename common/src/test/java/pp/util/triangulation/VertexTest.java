//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pp.util.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static pp.util.FloatMath.TWO_PI;
import static pp.util.FloatPoint.p;

public class VertexTest {
    private static final int NUM = 90;
    private static final float EPS = 1e-6f;

    public static List<Float> makeFloats() {
        final List<Float> list = new ArrayList<>();
        for (int i = 0; i < NUM; i++)
            list.add(TWO_PI / NUM * i);
        return list;
    }

    @ParameterizedTest
    @MethodSource("makeFloats")
    public void cornerTest1(float angle) {
        final Matrix m = new Matrix(angle);
        final var v1 = v(m, 0, 0);
        final var v2 = v(m, 0, 1);
        final var v3 = v(m, 1, 1);
        v2.prev = v1;
        v2.next = v3;
        assertEquals(rot(m, 1, 0), v2.corner(), EPS);
    }

    @ParameterizedTest
    @MethodSource("makeFloats")
    public void cornerTest2(float angle) {
        final Matrix m = new Matrix(angle);
        final var v1 = v(m, 0, 0);
        final var v2 = v(m, 0, 1);
        final var v3 = v(m, -1, 1);
        v2.prev = v1;
        v2.next = v3;
        assertEquals(rot(m, 1, 2), v2.corner(), EPS);
    }

    @ParameterizedTest
    @MethodSource("makeFloats")
    public void cornerTest3(float angle) {
        final Matrix m = new Matrix(angle);
        final var v1 = v(m, 0, 0);
        final var v2 = v(m, 0, 1);
        final var v3 = v(m, 0, 2);
        v2.prev = v1;
        v2.next = v3;
        assertEquals(rot(m, 1, 1), v2.corner(), EPS);
    }

    @ParameterizedTest
    @MethodSource("makeFloats")
    public void cornerTest4(float angle) {
        final Matrix m = new Matrix(angle);
        final var v1 = v(m, 0, 0);
        final var v2 = v(m, 0, 1);
        final var v3 = v(m, 1, 0);
        v2.prev = v1;
        v2.next = v3;
        assertEquals(rot(m, (float) 1, -1.414213562f), v2.corner(), EPS);
    }

    @ParameterizedTest
    @MethodSource("makeFloats")
    public void cornerTest5(float angle) {
        final Matrix m = new Matrix(angle);
        final var v1 = v(m, 0, 0);
        final var v2 = v(m, 0, 1);
        final var v3 = v(m, -1, 0);
        v2.prev = v1;
        v2.next = v3;
        assertEquals(rot(m, 1, 3.414213562f), v2.corner(), EPS);
    }

    @ParameterizedTest
    @MethodSource("makeFloats")
    public void cornerTest6(float angle) {
        final Matrix m = new Matrix(angle);
        final var v1 = v(m, 0, 0);
        final var v2 = v(m, 1, 0);
        final var v3 = v(m, 1, -1);
        v2.prev = v1;
        v2.next = v3;
        assertEquals(rot(m, 0, -1), v2.corner(), EPS);
    }

    private Position rot(Matrix m, float x, float y) {
        return m.multiply(p(x, y));
    }

    private Vertex v(Matrix m, int x, int y) {
        return new Vertex(rot(m, x, y));
    }

    private void assertEquals(Position expected, Position actual, float eps) {
        if (expected.distanceTo(actual) > eps)
            fail("expected " + expected + ", but got " + actual);
    }
}