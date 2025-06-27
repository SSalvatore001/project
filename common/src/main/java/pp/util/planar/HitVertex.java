//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.FloatMath;

import static java.lang.Math.abs;
import static pp.util.FloatMath.atan2;
import static pp.util.planar.PlanarMap.EPS;

/**
 * Represents a vertex hit by a ray in a 2D space.
 */
class HitVertex {
    private static final float FULL_CIRCLE = FloatMath.TWO_PI;
    final float mu;
    final Vertex v;

    HitVertex(float mu, Vertex v) {
        this.mu = mu;
        this.v = v;
    }

    HitInfo createHitInfo(Ray ray) {
        final float rayAngle = atan2(-ray.dy, -ray.dx);
        final HalfEdge start = v.getIncidentEdge();
        HalfEdge e = start;
        do {
            if (collinear(-ray.dx, -ray.dy, e.getDx(), e.getDy()))
                return collinearRay(ray, e);
            final HalfEdge prev = e.getPrev().getTwin();
            final float curAngle = atan2(e.getDy(), e.getDx());
            final float prevAngle = atan2(prev.getDy(), prev.getDx());
            if (isBetween(curAngle, rayAngle, prevAngle))
                return HitInfo.hitsHalfEdge(e, mu, 0, ray);
            e = e.getTwin().getNext();
        } while (e != start);
        throw new IllegalStateException("Didn't find face");
    }

    private HitInfo collinearRay(Ray ray, HalfEdge e) {
        final HalfEdge twin = e.getTwin();
        final float lambda = calculateLambda(ray, twin);
        return HitInfo.startsAtHalfEdge(twin, null, lambda, ray);
    }

    private float calculateLambda(Ray ray, HalfEdge twin) {
        if (mu == 0f)
            return 0f;
        if (abs(twin.getDx()) < abs(twin.getDy()))
            return (ray.y - twin.getOrigin().getY()) / twin.getDy();
        else
            return (ray.x - twin.getOrigin().getX()) / twin.getDx();
    }

    private static boolean isBetween(float a1, float a2, float a3) {
        final float a2b = a2 > a1 ? a2 : a2 + FULL_CIRCLE;
        final float a3b = a3 > a1 ? a3 : a3 + FULL_CIRCLE;
        return a1 < a2b && a2b < a3b;
    }

    private static boolean collinear(float dx1, float dy1, float dx2, float dy2) {
        if (abs(dx1) < EPS) {
            if (abs(dx2) > EPS) return false;
            return dy1 > 0 && dy2 > 0 || dy1 < 0 && dy2 < 0;
        }
        if (abs(dy1) < EPS) {
            if (abs(dy2) > EPS) return false;
            return dx1 > 0 && dx2 > 0 || dx1 < 0 && dx2 < 0;
        }
        // dx1 != 0 && dy1 != 0
        if (abs(dx2) < EPS)
            return false;
        // dx1 != 0 && dy1 != 0 && dx2 != 0
        if (dx1 < 0 && dx2 > 0 || dx1 > 0 && dx2 < 0)
            return false;
        return abs(dx2 * dy1 - dx1 * dy2) < EPS;
    }
}
