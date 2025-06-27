//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

import pp.util.Position;

/**
 * Contains information about a ray hitting some half edge.
 */
public class HitInfo {
    /**
     * The half edge that is hit, or null if no half edge is ever hit by the ray.
     */
    public final HalfEdge halfEdge;
    /**
     * The face from where the ray comes. If the ray starts at a half edge, this is the face when
     * the ray starting point is moved back (against its ray direction). The face is null
     * if this moved starting point is on the half edge e, i.e., if the ray is collinear to e.
     */
    public final Face face;
    /**
     * Parameter of the ray indicating the hit point
     */
    public final float mu;
    /**
     * Parameter of the half edge indicating the hit point
     */
    public final float lambda;
    /**
     * The ray hitting e, or hitting no half edge at all.
     */
    public final Ray ray;

    private HitInfo(HalfEdge halfEdge, Face face, float mu, float lambda, Ray ray) {
        this.halfEdge = halfEdge;
        this.face = face;
        this.mu = mu;
        this.lambda = lambda;
        this.ray = ray;
    }

    public static HitInfo startsAtHalfEdge(HalfEdge e, Face f, float lambda, Ray ray) {
        return new HitInfo(e, f, 0, lambda, ray);
    }

    public static HitInfo noHit(Face f, Ray ray) {
        return new HitInfo(null, f, 0, 0, ray);
    }

    public static HitInfo hitsHalfEdge(HalfEdge e, float mu, float lambda, Ray ray) {
        return new HitInfo(e, e.getIncidentFace(), mu, lambda, ray);
    }

    public Position hitPoint() {
        return ray.at(mu);
    }

    @Override
    public String toString() {
        if (halfEdge == null)
            return ray + " runs in " + face + " without hit"; //NON-NLS
        else
            return ray + " hits half edge " + halfEdge + ", lambda = " + lambda + ", face " +  //NON-NLS
                   face + ", mu = " + mu + " at " + hitPoint(); //NON-NLS
    }
}
