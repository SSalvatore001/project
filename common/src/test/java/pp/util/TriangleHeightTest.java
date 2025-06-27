//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pp.util.FloatPoint.p;

public class TriangleHeightTest {
    private static final float EPS = 1e-6f;

    @Test
    public void triangleHeight() {
        final ElevatedTriangle t = new ElevatedTriangle(ep(1, 1, 1), ep(4, 2, 2), ep(3, 3, 3));
        assertEquals(1f, t.top(p(1, 1)), EPS);
        assertEquals(2f, t.top(p(4, 2)), EPS);
        assertEquals(3f, t.top(p(3, 3)), EPS);
        assertEquals(1.5f, t.top(p(2.5f, 1.5f)), EPS);
        assertEquals(2.5f, t.top(p(3.5f, 2.5f)), EPS);
        assertEquals(2f, t.top(p(2, 2)), EPS);
    }

    public static ElevatedPoint ep(float x, float y, float h) {
        return new ElevatedPoint(x, y, h, h);
    }
}