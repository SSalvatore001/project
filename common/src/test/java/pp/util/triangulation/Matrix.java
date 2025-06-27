//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util.triangulation;

import pp.util.Position;

import static pp.util.FloatMath.cos;
import static pp.util.FloatMath.sin;
import static pp.util.FloatPoint.p;

class Matrix {
    final float a11, a12, a21, a22;

    public Matrix(float angle) {
        a11 = cos(angle);
        a12 = sin(angle);
        a21 = -a12;
        a22 = a11;
    }

    public Position multiply(Position p) {
        return p(p.getX() * a11 + p.getY() * a12,
                 p.getX() * a21 + p.getY() * a22);
    }

    @Override
    public String toString() {
        return "Matrix{a11=" + a11 + ", a12=" + a12 + //NON-NLS
               ", a21=" + a21 + ", a22=" + a22 + '}'; //NON-NLS
    }
}
