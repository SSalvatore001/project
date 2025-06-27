//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.FloatPoint;
import pp.util.Position;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static pp.util.planar.PlanarMap.EPS;

/**
 * An infinite ray that starts at a specific point.
 */
public class Ray {
    public final float x;
    public final float y;
    public final float dx;
    public final float dy;

    /**
     * Creates a new ray starting at the origin of the specified half edge and pointing along the half edge.
     */
    public Ray(HalfEdge e) {
        this(e.getOrigin(), e.getDx(), e.getDy());
    }

    /**
     * Creates a new ray from the vertex specified first and pointing towards the other specified vertex.
     */
    public Ray(Position from, Position to) {
        this(from.getX(), from.getY(), to.getX() - from.getX(), to.getY() - from.getY());
    }

    /**
     * Creates a new ray with the specified starting point and direction vector.
     */
    public Ray(Position from, float dx, float dy) {
        this(from.getX(), from.getY(), dx, dy);
    }

    /**
     * Creates a new ray with the specified starting point and direction vector.
     */
    public Ray(float x, float y, float dx, float dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public String toString() {
        return String.format("Ray (%f|%f) --> (%f|%f)", x, y, dx, dy); //NON-NLS
    }

    /**
     * Returns the point at the specified parameter position
     */
    public Position at(float mu) {
        return new FloatPoint(x + mu * dx, y + mu * dy);
    }

    /**
     * Returns the closest hit of this ray within the specified planar map. If the ray
     * starts at a half edge, this is return as the closest hit.
     */
    public HitInfo findHit(PlanarMap map) {
        return findHit(map, true);
    }

    /**
     * Returns the closest hit of this ray within the specified planar map. If the ray
     * starts at a half edge, this is returned as the closest hit if includeStart is
     * set to true, otherwise the closest hit with mu>0 is returned.
     */
    public HitInfo findHit(PlanarMap map, boolean includeStart) {
        final HitVertex hitVertex = findHitVertex(map, includeStart);
        if (hitVertex != null && hitVertex.mu == 0)
            return hitVertex.createHitInfo(this);
        final HitHalfEdge hitHalfEdge = findHitHalfEdge(map, hitVertex, includeStart);
        if (hitHalfEdge != null)
            return hitHalfEdge.createHitInfo(this);
        if (hitVertex != null)
            return hitVertex.createHitInfo(this);
        return HitInfo.noHit(map.getOuter(), this);
    }

    private HitHalfEdge findHitHalfEdge(PlanarMap map, HitVertex hitVertex, boolean includeStart) {
        final Vertex hit = hitVertex == null ? null : hitVertex.v;
        HitHalfEdge hitEdge = null;
        for (HalfEdge e : map.getHalfEdges())
            if (hit != e.getOrigin() && hit != e.getTarget() && e.hashCode() < e.getTwin().hashCode()) {
                // consider only edges that are not incident to hit, and only one of its half edges
                final RayHit rayHit = computeHalfEdgeMu(e);
                if (rayHit != null && (includeStart || rayHit.mu >= EPS) &&
                    (hitVertex == null || hitVertex.mu >= rayHit.mu) &&
                    (hitEdge == null || rayHit.mu < hitEdge.mu))
                    hitEdge = new HitHalfEdge(rayHit.mu, e, rayHit.lambda);
            }
        return hitEdge;
    }

    private static class RayHit {
        final float mu;
        final float lambda;

        public RayHit(float mu, float lambda) {
            this.mu = mu < EPS ? 0 : mu;
            this.lambda = max(0, min(lambda, 1));
        }
    }

    private RayHit computeHalfEdgeMu(HalfEdge e) {
        final Vertex o = e.getOrigin();
        final Vertex t = e.getTarget();
        final float x1 = o.getX();
        final float y1 = o.getY();
        final float x2 = t.getX();
        final float y2 = t.getY();
        final float dx1 = x2 - x1;
        final float dy1 = y2 - y1;
        final float mu;
        final float lambda;
        if (abs(dy) < EPS) {
            if (abs(dy1) < EPS)
                return null;
            lambda = (y - y1) / dy1;
            mu = (x1 - x + dx1 * lambda) / dx;
        }
        else if (abs(dx) < EPS) {
            if (abs(dx1) < EPS)
                return null;
            lambda = (x - x1) / dx1;
            mu = (y1 - y + dy1 * lambda) / dy;
        }
        else if (abs(dx1) < EPS) {
            mu = (x1 - x) / dx;
            lambda = (y - y1 + mu * dy) / dy1;
        }
        else if (abs(dy1) < EPS) {
            mu = (y1 - y) / dy;
            lambda = (x - x1 + mu * dx) / dx1;
        }
        else {
            final float d = dx * dy1 - dy * dx1;
            final float e1 = dx1 * (y - y1) - dy1 * (x - x1);
            if (abs(d) < EPS)
                // no solution
                return null;
            mu = e1 / d;
            lambda = (x - x1 + mu * dx) / dx1;
        }
        if (lambda < -EPS || lambda - 1. > EPS || mu < -EPS)
            return null;
        return new RayHit(mu, lambda);
    }

    private HitVertex findHitVertex(PlanarMap map, boolean includeStart) {
        Vertex best = null;
        float minMu = 0;
        final float t = x * dy - y * dx;
        for (final Vertex v : map.getVertices()) {
            final float x1 = v.getX();
            final float y1 = v.getY();
            if (abs(x1 * dy - y1 * dx - t) < EPS) {
                // vertex found
                final float mu = abs(dx) < EPS ? (y1 - y) / dy : (x1 - x) / dx;
                if (mu < EPS) {
                    if (mu > -EPS && includeStart)
                        return new HitVertex(0, v);
                }
                else if (best == null || mu < minMu) {
                    minMu = mu;
                    best = v;
                }
            }
        }
        return best == null ? null : new HitVertex(minMu, best);
    }
}
