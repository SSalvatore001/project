//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids.view;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.RectangleMesh;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import pp.droids.GameState;
import pp.droids.model.Spec;
import pp.droids.model.item.FinishLine;
import pp.droids.model.item.Item;
import pp.droids.model.item.Obstacle;
import pp.droids.model.item.Polygon;
import pp.droids.model.item.Projectile;
import pp.droids.model.item.Robot;
import pp.droids.model.item.Visitor;
import pp.util.ElevatedPoint;
import pp.util.ElevatedSegment;
import pp.util.ElevatedTriangle;
import pp.util.Position;
import pp.util.Segment;
import pp.util.SegmentLike;
import pp.view.ModelViewSynchronizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jme3.math.ColorRGBA.Red;
import static com.jme3.math.ColorRGBA.White;
import static com.jme3.math.ColorRGBA.Yellow;
import static com.jme3.math.Vector3f.UNIT_Y;
import static pp.droids.view.CoordinateTransformation.modelToView;
import static pp.droids.view.CoordinateTransformation.modelToViewX;
import static pp.droids.view.CoordinateTransformation.modelToViewY;
import static pp.droids.view.CoordinateTransformation.modelToViewZ;

/**
 * This class is the main synchronizes of droids.
 */
public class MainSynchronizer extends ModelViewSynchronizer<Item> implements Visitor<Spatial> {
    public static final String DROID = "Droid"; //NON-NLS
    public static final String ENEMY = "Enemy"; //NON-NLS
    public static final String PROJECTILE = "Projectile"; //NON-NLS
    public static final String OBSTACLE = "Obstacle"; //NON-NLS
    public static final String POLYGON = "Polygon"; //NON-NLS
    public static final String FLOOR = "Floor"; //NON-NLS
    public static final String FINISH_LINE = "Finish line"; //NON-NLS
    public static final String DROID_MODEL = "Models/Robot1/Robot1.j3o"; //NON-NLS
    public static final String ENEMY_MODEL = "Models/Robot2/Robot2.j3o"; //NON-NLS
    public static final String ROCK_MODEL = "Models/Rock/Rock.j3o"; //NON-NLS
    public static final String CASTLE_WALL_TEXTURE = "Textures/Terrain/BrickWall/BrickWall.j3m"; //NON-NLS
    public static final String EGYPT_WALL_TEXTURE = "Textures/Terrain/Hieroglyphics/Hieroglyphics.j3m"; //NON-NLS
    public static final String CASTLE_FLOOR_TEXTURE = "Textures/Terrain/Stone-Floor/Stone-Floor.j3m"; //NON-NLS
    public static final String EGYPT_FLOOR_TEXTURE = "Textures/Terrain/Sand/Sand.j3m"; //NON-NLS
    public static final String FINISH_LINE_TEXTURE = "Textures/Terrain/Finish-Line/Finish-Line.j3m"; //NON-NLS
    private static final String UNSHADED = "Common/MatDefs/Misc/Unshaded.j3md"; //NON-NLS
    private static final String FAKE_LIGHT = "Common/MatDefs/Misc/fakeLighting.j3md"; //NON-NLS
    private static final String COLOR = "Color"; //NON-NLS
    private static final Vector3f NEG_UNIT_Y = new Vector3f(0, -1, 0);
    private final Application app;

    /**
     * Creates a new synchronizer.
     *
     * @param gameState the game state that uses this synchronizer
     * @param root      root node
     */
    public MainSynchronizer(GameState gameState, Node root) {
        super(root);
        this.app = gameState.getApp();
        gameState.getModel().addGameEventListener(new SyncListener(this));
        gameState.getModel().getDroidsMap().getItems().forEach(this::add);
    }

    /**
     * Creates the spatial for the specified item.
     *
     * @param item the item whose representing spatial is asked for
     * @return the spatial of the item, or null if the item shall not be represented by a spatial.
     */
    @Override
    protected Spatial translate(Item item) {
        return item.accept(this);
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.item.Robot}.
     *
     * @param robot the droid
     * @return created spacial
     */
    @Override
    public Spatial visit(Robot robot) {
        final String robotModel = robot.isDroid() ? DROID_MODEL : ENEMY_MODEL;
        final Spatial spatial = app.getAssetManager().loadModel(robotModel);
        spatial.scale(robot.getRadius() / Robot.BOUNDING_RADIUS);
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.addControl(new DamageReceiverControl(robot));
        spatial.setName(robot.isDroid() ? DROID : ENEMY);
        return spatial;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.item.Projectile}.
     *
     * @param projectile a projectile
     * @return created spacial
     */
    @Override
    public Spatial visit(Projectile projectile) {
        final Sphere sphere = new Sphere(20, 20, projectile.getRadius());
        final Geometry bullet = new Geometry(PROJECTILE, sphere);
        final Material material = predefMaterial(FAKE_LIGHT, Yellow);
        bullet.setMaterial(material);
        bullet.setShadowMode(ShadowMode.Cast);
        bullet.addControl(new ItemControl<>(projectile, 0.8f));
        return bullet;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.item.Obstacle}.
     *
     * @param obstacle an obstacle
     * @return created spacial
     */
    @Override
    public Spatial visit(Obstacle obstacle) {
        Spatial rock = app.getAssetManager().loadModel(ROCK_MODEL);
        rock.scale(obstacle.getRadius() / Obstacle.BOUNDING_RADIUS);
        rock.setShadowMode(ShadowMode.CastAndReceive);
        rock.getLocalRotation().fromAngleAxis(obstacle.getRotation(), UNIT_Y);
        rock.setLocalTranslation(modelToViewX(obstacle),
                                 modelToViewY(obstacle) + obstacle.getElevation(),
                                 modelToViewZ(obstacle));
        rock.setName(OBSTACLE);
        return rock;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.item.Polygon}.
     *
     * @param poly a polygon
     * @return a node that represents the whole structure with the polygon as its floor plan.
     */
    @Override
    public Spatial visit(Polygon poly) {
        final MaterialSpec mat = getMaterial(poly.getSpec());
        final Node parent = new Node(POLYGON);
        parent.attachChild(createPolygonTopBottom(poly, mat));
        parent.attachChild(createWall(poly.getOuterSegmentList(), mat));
        for (List<ElevatedSegment> hole : poly.getHoleSegmentLists())
            parent.attachChild(createWall(hole, mat));
        return parent;
    }

    /**
     * Visit method for any object of the type {@link pp.droids.model.item.FinishLine}.
     *
     * @param line a finish line
     * @return parent node
     */
    @Override
    public Spatial visit(FinishLine line) {
        final var it = line.getAllSegments().iterator();
        final Segment s1 = it.next();
        final Segment s2 = it.next();
        final RectangleMesh rect = new RectangleMesh(modelToView(s2.from()).add(0f, line.getElevation(), 0f),
                                                     modelToView(s2.to()).add(0f, line.getElevation(), 0f),
                                                     modelToView(s1.from()).add(0f, line.getElevation(), 0f));
        rect.setTexCoords(new Vector2f[]{
                new Vector2f(0f, 0f),
                new Vector2f(s2.length(), 0f),
                new Vector2f(s2.length(), s1.length()),
                new Vector2f(0f, s1.length())});
        Geometry finish = new Geometry(FINISH_LINE, rect); //NON-NLS
        MikktspaceTangentGenerator.generate(finish);
        finish.setMaterial(loadMaterial(FINISH_LINE_TEXTURE));
        return finish;
    }

    /**
     * Encapsulates a material with a stretch factor.
     *
     * @param material a material
     * @param stretch  a stretch factor
     */
    private record MaterialSpec(Material material, float stretch) {}

    /**
     * Creates the material with stretch factor from the specified name. A white wireframe
     * material is returned if the name is null, and a red wireframe material if the name
     * is unknown.
     */
    private MaterialSpec getMaterial(String name) {
        if (name == null)
            return new MaterialSpec(wireframeMaterial(White), 1f);
        return switch (name) {
            case Spec.HIEROGLYPHS -> new MaterialSpec(loadMaterial(EGYPT_WALL_TEXTURE), 0.3f);
            case Spec.SAND -> new MaterialSpec(loadMaterial(EGYPT_FLOOR_TEXTURE), 0.1f);
            case Spec.STONE -> new MaterialSpec(loadMaterial(CASTLE_FLOOR_TEXTURE), 0.35f);
            case Spec.WALL -> new MaterialSpec(loadMaterial(CASTLE_WALL_TEXTURE), 0.5f);
            default -> new MaterialSpec(wireframeMaterial(Red), 1f);
        };
    }

    /**
     * creating a Material from color and specification
     *
     * @param spec  specifications for the Material
     * @param color RGB color for the Material
     * @return material
     */
    private Material predefMaterial(String spec, ColorRGBA color) {
        final Material mat = new Material(app.getAssetManager(), spec);
        mat.setColor(COLOR, color);
        return mat;
    }

    /**
     * loading Material from saved Materials with given specification
     *
     * @param spec specifications for the Material
     * @return Material
     */
    private Material loadMaterial(String spec) {
        return app.getAssetManager().loadMaterial(spec);
    }

    /**
     * creating an unshaded Material in the given color
     *
     * @param color RGB color for the Material
     * @return Material
     */
    private Material wireframeMaterial(ColorRGBA color) {
        final Material mat = predefMaterial(UNSHADED, color);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(4);
        return mat;
    }

    /**
     * placing the Walls with material
     *
     * @param segments List of Elevated Segments for Position of the Walls
     * @param mat      Material which is given to the walls
     * @return Geometry (extending {@link com.jme3.scene.Spatial})
     */
    private static Geometry createWall(List<ElevatedSegment> segments, MaterialSpec mat) {
        final float length = (float) segments.stream().mapToDouble(SegmentLike::length).sum();
        final float f = mat.stretch() * Math.round(length) / length;
        final int n = 2 * segments.size();
        final Vector3f[] normals = new Vector3f[2 * n];
        final Vector3f[] vertices = new Vector3f[2 * n];
        final Vector2f[] texCoord = new Vector2f[2 * n];
        final int[] indices = new int[3 * n];
        float len = 0f;
        int i = 0;
        for (var seg : segments) {
            final float bottom1 = seg.from().bottom();
            final float top1 = seg.from().top();
            final float bottom2 = seg.to().bottom();
            final float top2 = seg.to().top();
            final Vector3f v1 = modelToView(seg.from());
            final Vector3f v2 = modelToView(seg.to());
            final float dx = (seg.to().getX() - seg.from().getX()) / seg.length();
            final float dy = (seg.to().getY() - seg.from().getY()) / seg.length();
            final Vector3f nv = new Vector3f(modelToViewX(dy, -dx), modelToViewY(dy, -dx), modelToViewZ(dy, -dx));
            vertices[4 * i] = v1.add(0f, bottom1, 0f);
            vertices[4 * i + 1] = v1.add(0f, top1, 0f);
            vertices[4 * i + 2] = v2.add(0f, bottom2, 0f);
            vertices[4 * i + 3] = v2.add(0f, top2, 0f);
            normals[4 * i] = nv;
            normals[4 * i + 1] = nv;
            normals[4 * i + 2] = nv;
            normals[4 * i + 3] = nv;
            texCoord[4 * i] = new Vector2f(-f * len, -f * bottom1);
            texCoord[4 * i + 1] = new Vector2f(-f * len, -f * top1);
            len += seg.length();
            texCoord[4 * i + 2] = new Vector2f(-f * len, -f * bottom2);
            texCoord[4 * i + 3] = new Vector2f(-f * len, -f * top2);
            final int b0 = 4 * i;
            final int b1 = 4 * i + 1;
            final int b2 = 4 * i + 2;
            final int b3 = 4 * i + 3;
            indices[6 * i] = b2;
            indices[6 * i + 1] = b1;
            indices[6 * i + 2] = b0;
            indices[6 * i + 3] = b1;
            indices[6 * i + 4] = b2;
            indices[6 * i + 5] = b3;
            i++;
        }
        return makeGeometry("Wall", mat, vertices, normals, texCoord, indices); //NON-NLS
    }

    /**
     * placing the floor and roof with material
     *
     * @param poly Polygon representing the floor and/or roof
     * @param mat  Material which is given for the floor and roof
     * @return Geometry (extending {@link com.jme3.scene.Spatial})
     */
    private static Geometry createPolygonTopBottom(Polygon poly, MaterialSpec mat) {
        final List<ElevatedPoint> points = poly.getAllSegments().stream()
                                               .map(ElevatedSegment::from)
                                               .toList();
        final List<ElevatedTriangle> triangles = poly.getTriangles();
        final int n = points.size();
        final Vector3f[] normals = new Vector3f[2 * n];
        final Vector3f[] vertices = new Vector3f[2 * n];
        final Vector2f[] texCoord = new Vector2f[2 * n];
        final int[] indices = new int[6 * triangles.size()];
        final Map<Position, Integer> map = new HashMap<>();
        int i = 0;
        for (var p : points) {
            map.put(p, i);
            final Vector3f v = modelToView(p);
            vertices[i] = v.add(0f, p.top(), 0f);
            vertices[i + n] = v.add(0f, p.bottom(), 0f);
            texCoord[i + n] = texCoord[i] = new Vector2f(p.getX() * mat.stretch(), p.getY() * mat.stretch());
            // Fixed normals are an over-simplification.
            // In fact, they should be computed in terms of triangle slopes.
            normals[i] = UNIT_Y;
            normals[i + n] = NEG_UNIT_Y;
            i++;
        }
        i = 0;
        for (ElevatedTriangle t : triangles) {
            final int p1 = map.get(t.a());
            final int p2 = map.get(t.b());
            final int p3 = map.get(t.c());
            indices[i++] = p1;
            indices[i++] = p2;
            indices[i++] = p3;
            // note the opposite order of corners in the following triangle
            indices[i++] = p1 + n;
            indices[i++] = p3 + n;
            indices[i++] = p2 + n;
        }
        return makeGeometry(FLOOR, mat, vertices, normals, texCoord, indices);
    }

    /**
     * Creates a new {@link Geometry} object from scratch using the provided vertices, normals,
     * texture coordinates, and indices. This method constructs a mesh with the given attributes,
     * sets up the material, and generates the necessary tangents and binormals for proper lighting
     * and shading. The resulting geometry is configured to cast and receive shadows.
     *
     * @param name     the name for the {@link Geometry}, which can be used for identification and debugging
     * @param mat      the {@link MaterialSpec} specifying the material to apply to the geometry
     * @param vertices an array of {@link Vector3f} representing the positions of the geometry's vertices
     * @param normals  an array of {@link Vector3f} representing the normal vectors at each vertex for lighting calculations
     * @param texCoord an array of {@link Vector2f} representing the texture coordinates for mapping textures onto the geometry
     * @param indices  an array of integers specifying the order in which the vertices should be connected to form the mesh's faces
     * @return a new {@link Geometry} object with the specified attributes and material applied
     */
    private static Geometry makeGeometry(String name, MaterialSpec mat,
                                         Vector3f[] vertices, Vector3f[] normals, Vector2f[] texCoord, int[] indices) {
        final Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();
        final Geometry geom = new Geometry(name, mesh);
        MikktspaceTangentGenerator.generate(geom);
        geom.setMaterial(mat.material());
        geom.setShadowMode(ShadowMode.CastAndReceive);
        return geom;
    }
}
