//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.util;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FloatRectangle {
    public final float x;
    public final float y;
    public final float width;
    public final float height;

    public FloatRectangle(Position vec) {
        this(vec.getX(), vec.getY());
    }

    public FloatRectangle(float x, float y) {
        this(x, y, 0, 0);
    }

    public FloatRectangle(float x, float y, float width, float height) {
        if (width < 0)
            throw new IllegalArgumentException("negative width " + width);
        if (height < 0)
            throw new IllegalArgumentException("negative height " + height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getMinX() {
        return x;
    }

    public float getMinY() {
        return y;
    }

    public float getMaxX() {
        return x + width;
    }

    public float getMaxY() {
        return y + height;
    }

    public FloatRectangle add(FloatRectangle that) {
        final float minX = min(this.getMinX(), that.getMinX());
        final float maxX = max(this.getMaxX(), that.getMaxX());
        final float minY = min(this.getMinY(), that.getMinY());
        final float maxY = max(this.getMaxY(), that.getMaxY());
        return new FloatRectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public FloatRectangle add(Position vec) {
        return add(vec.getX(), vec.getY());
    }

    public FloatRectangle add(float x, float y) {
        final float minX = min(this.getMinX(), x);
        final float maxX = max(this.getMaxX(), x);
        final float minY = min(this.getMinY(), y);
        final float maxY = max(this.getMaxY(), y);
        return new FloatRectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public String toString() {
        return "Rectangle{x=" + x + ", y=" + y + //NON-NLS
               ", width=" + width + ", height=" + height + '}'; //NON-NLS
    }

    public boolean contains(Position pos) {
        return contains(pos.getX(), pos.getY());
    }

    public boolean contains(float x, float y) {
        return x >= this.x && x <= this.x + width &&
               y >= this.y && y <= this.y + height;
    }

    public Position getCenter() {
        return new FloatPoint(x + 0.5f * width, y + 0.5f * height);
    }
}
