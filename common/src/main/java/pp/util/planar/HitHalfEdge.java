//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.planar;

class HitHalfEdge {
    final float mu;
    final HalfEdge e;
    final float lambda;

    HitHalfEdge(float mu, HalfEdge e, float lambda) {
        this.mu = mu;
        this.e = e;
        this.lambda = lambda;
    }

    HitInfo createHitInfo(Ray ray) {
        final float x = mu == 0 ? ray.x - ray.dx : ray.x;
        final float y = mu == 0 ? ray.y - ray.dy : ray.y;
        if (e.isLeft(x, y))
            return HitInfo.hitsHalfEdge(e, mu, lambda, ray);
        if (e.getTwin().isLeft(x, y))
            return HitInfo.hitsHalfEdge(e.getTwin(), mu, 1 - lambda, ray);
        throw new IllegalStateException("Not left of any of the twins");
    }
}
