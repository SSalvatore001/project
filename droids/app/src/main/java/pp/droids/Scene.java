//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import pp.droids.model.MapType;
import pp.util.FloatMath;

/**
 * Manages the static elements of the game scene, such as lighting and skybox appearance.
 * <p>
 * This class configures scene-wide lighting and allows the background sky to be themed
 * according to the selected {@link MapType}. It is attached to a root node and persists
 * throughout the game session.
 */
class Scene {
    private static final String DESERT_SKY = "Textures/Sky/Bright/BrightSky.dds"; //NON-NLS

    /**
     * Reference to the current game state.
     */
    private final GameState gameState;

    /** Node to which all scene elements (e.g. skybox) are attached. */
    private final Node sceneNode = new Node("scene"); //NON-NLS

    /**
     * Constructs a new Scene manager and initializes lighting and root attachment.
     *
     * @param gameState the current game state
     * @param root      the root node to which the scene content will be attached
     */
    public Scene(GameState gameState, Node root) {
        this.gameState = gameState;
        root.attachChild(sceneNode);
        setupLights(gameState, root);
    }

    /**
     * Sets up lighting for the scene, including a directional "sun" light and ambient light,
     * as well as a shadow renderer for improved realism.
     *
     * @param gameState the current game state
     * @param root      the root node where lights will be added
     */
    private static void setupLights(GameState gameState, Node root) {
        final AssetManager assetManager = gameState.getApp().getAssetManager();

        final DirectionalLightShadowRenderer shRend = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        shRend.setLambda(0.55f);
        shRend.setShadowIntensity(0.6f);
        shRend.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        gameState.getApp().getViewPort().addProcessor(shRend);

        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1f, -0.7f, -1f).normalizeLocal());
        root.addLight(sun);
        shRend.setLight(sun);

        final AmbientLight ambientLight = new AmbientLight(new ColorRGBA(0.3f, 0.3f, 0.3f, 0f));
        root.addLight(ambientLight);
    }

    /**
     * Updates the scene based on the selected map type, altering the background skybox accordingly.
     *
     * @param mapType the map type to visually represent (e.g. EGYPT, CASTLE)
     */
    void selectLook(MapType mapType) {
        sceneNode.detachAllChildren();
        switch (mapType) {
            case EGYPT -> selectEgyptianLook();
            case CASTLE -> selectCastleLook();
        }
    }

    /**
     * Applies the castle-themed skybox to the scene.
     */
    private void selectCastleLook() {
        sceneNode.attachChild(lagoonSky());
    }

    /**
     * Applies the Egyptian-themed skybox to the scene.
     */
    private void selectEgyptianLook() {
        sceneNode.attachChild(brightSky());
    }

    /**
     * Creates and returns a spatial representing the lagoon skybox,
     * using individual texture files for each direction.
     *
     * @return the configured skybox spatial for the lagoon theme
     */
    private Spatial lagoonSky() {
        final AssetManager assetManager = gameState.getApp().getAssetManager();
        final Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg"); //NON-NLS
        final Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg"); //NON-NLS
        final Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg"); //NON-NLS
        final Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg"); //NON-NLS
        final Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg"); //NON-NLS
        final Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg"); //NON-NLS

        final Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        sky.rotate(0f, FloatMath.PI, 0f); // Align the skybox correctly
        return sky;
    }

    /**
     * Creates and returns a spatial representing the bright desert skybox,
     * loaded from a DDS cubemap texture.
     *
     * @return the configured skybox spatial for the desert theme
     */
    private Spatial brightSky() {
        final AssetManager assetManager = gameState.getApp().getAssetManager();
        return SkyFactory.createSky(assetManager, DESERT_SKY, EnvMapType.CubeMap);
    }

    /**
     * Called every frame to update the scene state, if necessary.
     * <p>
     * Currently a no-op, but reserved for future time-based visual effects or animations.
     *
     * @param delta time elapsed since the last frame (in seconds)
     */
    void update(float delta) {
        // no dynamic updates needed for static scene
    }
}
