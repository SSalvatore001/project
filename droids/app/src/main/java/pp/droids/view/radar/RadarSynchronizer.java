//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view.radar;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;
import pp.droids.GameState;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Item;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.PolygonItem;
import pp.droids.model.item.Projectile;
import pp.droids.model.item.Robot;
import pp.droids.model.item.Visitor;
import pp.droids.view.SyncListener;
import pp.util.Position;
import pp.view.ModelViewSynchronizer;

import java.text.MessageFormat;

/**
 * Synchronizes model items with their corresponding radar view representations.
 * <p>
 * This class maps game model elements (such as droids, projectiles, obstacles, and polygons)
 * to visual elements rendered in a radar overlay. It reacts to game model changes via a
 * {@link SyncListener} and uses the visitor pattern to determine the visual representation
 * of each item.
 * </p>
 */
class RadarSynchronizer extends ModelViewSynchronizer<Item> implements Visitor<Spatial> {
    public static final String DROID = "Droid"; //NON-NLS
    public static final String ENEMY = "Enemy"; //NON-NLS
    public static final String PROJECTILE = "Projectile"; //NON-NLS
    public static final String OBSTACLE = "Obstacle"; //NON-NLS

    private final GameState gameState;

    /**
     * Constructs a new {@code RadarSynchronizer} and initializes the radar view
     * by attaching to the root node and registering for game model updates.
     *
     * @param gameState the current game state
     * @param root      the root node of the scene graph for radar visualization
     */
    public RadarSynchronizer(GameState gameState, Node root) {
        super(root);
        this.gameState = gameState;
        gameState.getModel().addGameEventListener(new SyncListener(this));
        gameState.getModel().getDroidsMap().getItems().forEach(this::add);
    }

    /**
     * Translates a model item into a spatial using the visitor pattern.
     * <p>
     * Each specific item type (e.g., robot, projectile) is delegated to its respective visit method.
     *
     * @param item the item whose representing spatial is requested
     * @return the spatial of the item, or {@code null} if it should not be represented
     */
    @Override
    protected Spatial translate(Item item) {
        return item.accept(this);
    }

    /**
     * Creates a 2D picture spatial for the given item and assigns appropriate radar control and size.
     *
     * @param item the position of the item
     * @param name the image name (e.g., "Droid", "Enemy")
     * @return a configured picture representing the item
     */
    private Picture getPicture(Position item, String name) {
        final Picture p = new Picture(name);
        p.setImage(gameState.getApp().getAssetManager(),
                   MessageFormat.format("Textures/Pictures/{0}.png", name), true); //NON-NLS
        p.addControl(new ItemControl(item, gameState.getModel().getDroidsMap().getDroid()));
        p.setHeight(1f);
        p.setWidth(1f);
        return p;
    }

    /**
     * Handles visualization for a {@link Robot}. Displays a droid or enemy icon depending on type.
     *
     * @param robot the robot to visualize
     * @return the generated picture representing the robot
     */
    @Override
    public Spatial visit(Robot robot) {
        return getPicture(robot, robot.isDroid() ? DROID : ENEMY);
    }

    /**
     * Handles visualization for a {@link Projectile}. Displays a projectile icon.
     *
     * @param projectile the projectile to visualize
     * @return the generated picture representing the projectile
     */
    @Override
    public Spatial visit(Projectile projectile) {
        return getPicture(projectile, PROJECTILE);
    }

    /**
     * Handles visualization for an {@link Obstacle}. Displays an obstacle icon.
     *
     * @param obstacle the obstacle to visualize
     * @return the generated picture representing the obstacle
     */
    @Override
    public Spatial visit(Obstacle obstacle) {
        return getPicture(obstacle, OBSTACLE);
    }

    /**
     * Handles visualization for a {@link Polygon} object such as a maze.
     *
     * @param poly the polygon to visualize
     * @return a node containing line segments representing the polygon
     */
    @Override
    public Spatial visit(Polygon poly) {
        return handlePoly(poly);
    }

    /**
     * Handles visualization for a {@link FinishLine}. Displays it as a polygon.
     *
     * @param line the finish line to visualize
     * @return a node containing the visual representation of the finish line
     */
    @Override
    public Spatial visit(FinishLine line) {
        return handlePoly(line);
    }

    /**
     * Creates a node composed of line segments to represent a polygonal item (e.g., maze or finish line).
     * The node is also given a {@link PolygonControl} to track the robot’s perspective.
     *
     * @param poly the polygonal item to visualize
     * @return a node with line segment geometry and polygon control
     */
    private Node handlePoly(PolygonItem poly) {
        final Node parent = new Node("RadarPolygon");
        for (var seg : poly.getAllSegments())
            parent.attachChild(gameState.getApp().getDraw().makeLine(seg, 0f, ColorRGBA.Green));
        parent.addControl(new PolygonControl(gameState.getModel().getDroidsMap().getDroid()));
        return parent;
    }
}
