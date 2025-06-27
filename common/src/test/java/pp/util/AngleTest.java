//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import org.junit.jupiter.api.Test;

import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pp.util.FloatMath.DEG_TO_RAD;
import static pp.util.FloatMath.ZERO_TOLERANCE;
import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;

public class AngleTest {
    @Test
    public void compareAngles() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                assertEquals(Integer.compare(i, j), (Object) u.compareTo(v), "compare " + i + "° and " + j + "°");
            }
        }
    }

    @Test
    public void addAngles() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                final Angle sum = u.plus(v);
                assertEquals(cos((i + j) * DEG_TO_RAD), sum.x, ZERO_TOLERANCE, i + "° + " + j + "°, x coordinate");
                assertEquals(sin((i + j) * DEG_TO_RAD), sum.y, ZERO_TOLERANCE, i + "° + " + j + "°, y coordinate");
            }
        }
    }

    @Test
    public void subtractAngles() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                final Angle diff = u.minus(v);
                assertEquals(cos((i - j) * DEG_TO_RAD), diff.x, ZERO_TOLERANCE, i + "° - " + j + "°, x coordinate");
                assertEquals(sin((i - j) * DEG_TO_RAD), diff.y, ZERO_TOLERANCE, i + "° - " + j + "°, y coordinate");
            }
        }
    }

    @Test
    public void minAngle() {
        for (int i = 0; i < 360; i++) {
            final Angle u = Angle.fromDegrees(i);
            for (int j = 0; j < 360; j++) {
                final Angle v = Angle.fromDegrees(j);
                final Angle diff = Angle.min(u, v);
                assertEquals(cos(min(i, j) * DEG_TO_RAD), diff.x, ZERO_TOLERANCE, i + "° - " + j + "°, x coordinate");
                assertEquals(sin(min(i, j) * DEG_TO_RAD), diff.y, ZERO_TOLERANCE, i + "° - " + j + "°, y coordinate");
            }
        }
    }

    @Test
    public void bisector() {
        for (int right = 0; right < 360; right++) {
            final Angle rightAngle = Angle.fromDegrees(right);
            for (int add = 0; add < 360; add++) {
                final int left = right + add;
                final Angle bisector = Angle.fromDegrees(left).bisector(rightAngle);
                final float exp = (right + 0.5f * add) * DEG_TO_RAD;
                assertEquals(cos(exp), bisector.x, ZERO_TOLERANCE, "left=" + left + "° / right=" + right + "°, x coordinate");
                assertEquals(sin(exp), bisector.y, ZERO_TOLERANCE, "left=" + left + "° / right=" + right + "°, y coordinate");
            }
        }
    }

    @Test
    public void leftOf() {
        for (int right = 0; right < 360; right++) {
            final Angle rightAngle = Angle.fromDegrees(right);
            for (int add = 1; add < 360; add++)
                if (add != 180) {
                    final int left = right + add;
                    final Angle leftAngle = Angle.fromDegrees(left);
                    assertEquals(add < 180, leftAngle.leftOf(rightAngle), left + "° left of " + right + "°");
                }
        }
    }

    @Test
    public void rightOf() {
        for (int right = 0; right < 360; right++) {
            final Angle rightAngle = Angle.fromDegrees(right);
            for (int add = 1; add < 360; add++)
                if (add != 180) {
                    final int left = right + add;
                    final Angle leftAngle = Angle.fromDegrees(left);
                    assertEquals(add > 180, leftAngle.rightOf(rightAngle), left + "° right of " + right + "°");
                }
        }
    }
}