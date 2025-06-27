//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.model.item;

import pp.droids.model.DroidsModel;
import pp.util.Position;
import pp.util.Segment;

import java.util.ArrayList;
import java.util.List;

import static pp.util.FloatPoint.p;

/**
 * Represents a finish line.
 */
public class FinishLine extends AbstractItem implements PolygonItem {
    /**
     * A builder class for creating instances of FinishLine.
     * <p>
     * This builder class provides a convenient way to create instances of FinishLine
     * with various parameters. It allows setting the model, position, size, and elevation
     * of the FinishLine object.
     * <p>
     * Usage:
     * <pre>
     * Builder builder = new Builder();
     * FinishLine finishLine = builder.setModel(model)
     *                                .setPos(pos)
     *                                .setSize(dx, dy)
     *                                .setElevation(elevation)
     *                                .build();
     * </pre>
     */
    public static class Builder {
        private DroidsModel model;
        private float x;
        private float y;
        private float dx = 1f;
        private float dy = 1f;
        private float elevation;

        /**
         * Sets the DroidsModel for the Builder.
         *
         * @param model the DroidsModel to set
         * @return the Builder instance
         */
        public Builder setModel(DroidsModel model) {
            this.model = model;
            return this;
        }

        /**
         * Sets the position of the builder.
         *
         * @param pos the new position to set
         * @return the updated Builder instance
         */
        public Builder setPos(Position pos) {
            return setPos(pos.getX(), pos.getY());
        }

        /**
         * Sets the position of the builder to the specified x, y coordinates.
         *
         * @param x the x coordinate of the new position
         * @param y the y coordinate of the new position
         * @return the updated Builder instance
         */
        public Builder setPos(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * Sets the size of the builder.
         *
         * @param dx the x size value
         * @param dy the y size value
         * @return the modified builder instance
         */
        public Builder setSize(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
            return this;
        }

        /**
         * Sets the elevation of a component.
         *
         * @param elevation the elevation value to be set
         * @return the Builder object with the elevation value set
         */
        public Builder setElevation(float elevation) {
            this.elevation = elevation;
            return this;
        }

        /**
         * Builds a FinishLine object with the given parameters.
         *
         * @return the built FinishLine object
         */
        public FinishLine build() {
            final List<Segment> segments = new ArrayList<>(4);
            final var it = List.of(p(x - 0.5f * dx, y - 0.5f * dy),
                                   p(x + 0.5f * dx, y - 0.5f * dy),
                                   p(x + 0.5f * dx, y + 0.5f * dy),
                                   p(x - 0.5f * dx, y + 0.5f * dy),
                                   p(x - 0.5f * dx, y - 0.5f * dy))
                               .iterator();
            Position prev = it.next();
            while (it.hasNext()) {
                final Position next = it.next();
                segments.add(new Segment(prev, next));
                prev = next;
            }

            return new FinishLine(model, x, y, dx, dy, elevation, segments);
        }
    }

    private final float dx;
    private final float dy;
    private final List<Segment> segments;
    private final float elevation;

    /**
     * Creates a finish line with the specified polygon.
     *
     * @param model the model
     * @param dx    x-stretch of the finish line
     * @param dy    y-stretch of the finish line
     */
    private FinishLine(DroidsModel model, float x, float y, float dx, float dy, float elevation, List<Segment> segments) {
        super(model, x, y);
        this.dx = dx;
        this.dy = dy;
        this.elevation = elevation;
        this.segments = segments;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    @Override
    public float getElevation() {
        return elevation;
    }

    @Override
    public void setPos(float x, float y) {
        throw new UnsupportedOperationException("Cannot move a finish line");
    }

    @Override
    public void setRotation(float rotation) {
        throw new UnsupportedOperationException("Cannot turn a finish line");
    }

    /**
     * Checks whether the specified point lies within the rectangle of the finish line.
     *
     * @param p a point
     * @return true iff the point lies within the rectangle
     */
    public boolean contains(Position p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * Checks whether the specified point lies within the rectangle of the finish line.
     *
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return true iff the point lies within the rectangle
     */
    public boolean contains(float x, float y) {
        final float x1 = x - getX();
        final float y1 = y - getY();
        return -0.5f * dx <= x1 && x1 <= 0.5f * dx && -0.5f * dy <= y1 && y1 <= 0.5f * dy;
    }

    /**
     * Returns the outer segments of the polygon.
     */
    @Override
    public List<Segment> getAllSegments() {
        return segments;
    }

    public float distanceFrom(Position pos) {
        if (contains(pos)) return 0f;
        return (float) getAllSegments().stream()
                                       .mapToDouble(s -> s.distanceTo(pos))
                                       .min()
                                       .getAsDouble();
    }

    /**
     * Accept method of the visitor pattern.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    /**
     * Accept method of the {@link pp.droids.model.item.VoidVisitor}.
     */
    @Override
    public void accept(VoidVisitor v) {
        v.visit(this);
    }
}
