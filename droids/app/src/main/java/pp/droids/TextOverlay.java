//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.item.Robot;

import java.text.MessageFormat;

import static java.lang.Math.max;
import static pp.util.config.Resources.lookup;

/**
 * An application state that shows texts on top of the game view.
 */
class TextOverlay extends AbstractAppState {
    private DroidsApp app;
    private final Node overlayGuiNode = new Node("gui.overlay");
    private BitmapText topText;
    private BitmapText bottomText;
    private BitmapText centerText;
    private float remainingCenterTime;
    private float remainingTopTime;
    private float remainingBottomTime;
    private DroidsMap map;

    /**
     * Sets up the text overlay.
     * <p>
     * It overrides {@link com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)}
     *
     * @param stateManager The state manager
     * @param application  The application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        app = (DroidsApp) application;
        final int height = app.getContext().getSettings().getHeight();
        final BitmapFont normalFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt"); //NON-NLS
        final BitmapFont bigFont = app.getAssetManager().loadFont("Interface/Fonts/Metropolis/Metropolis-Bold-64.fnt"); //NON-NLS
        final int size = normalFont.getCharSet().getLineHeight();

        topText = new BitmapText(normalFont);
        topText.setLocalTranslation(10f, height - 10f, 0f);
        topText.setColor(app.getConfig().getTopColor());

        bottomText = new BitmapText(normalFont);
        bottomText.setLocalTranslation(10f, size + 10f, 0f);
        bottomText.setColor(app.getConfig().getBottomColor());

        centerText = new BitmapText(bigFont);
        centerText.setColor(app.getConfig().getCenterColor());

        reset();
    }

    /**
     * Puts text in the center of the window and shows them infinite.
     *
     * @param text the text that will be shown
     */
    public void setCenterText(String text) {
        setCenterText(text, Float.POSITIVE_INFINITY);
    }

    /**
     * Puts text in the center of the window and shows them for a certain time.
     *
     * @param text     the text that will be shown
     * @param duration the duration at which the text is shown
     */
    public void setCenterText(String text, float duration) {
        if (!centerText.getText().equals(text)) {
            final AppSettings settings = app.getContext().getSettings();
            centerText.setText(text);
            centerText.setLocalTranslation(max(0f, 0.5f * (settings.getWidth() - centerText.getLineWidth())),
                                           max(0f, 0.5f * (settings.getHeight() + centerText.getSize())),
                                           0f);
            centerText.getLineHeight();
        }
        remainingCenterTime = duration;
    }

    /**
     * Puts text in the top of the window and shows them infinite.
     *
     * @param text the text that will be shown
     */
    public void setTopText(String text) {
        setTopText(text, Float.POSITIVE_INFINITY);
    }

    /**
     * Puts text in the top of the window and shows them for a certain time.
     *
     * @param text     the text that will be shown
     * @param duration the duration at which the text is shown
     */
    public void setTopText(String text, float duration) {
        if (!topText.getText().equals(text))
            topText.setText(text);
        remainingTopTime = duration;
    }

    /**
     * Puts text in the bottom of the window and shows them infinite.
     *
     * @param text the text that will be shown
     */
    public void setBottomText(String text) {
        setBottomText(text, Float.POSITIVE_INFINITY);
    }

    /**
     * Puts text in the bottom of the window and shows them for a certain time.
     *
     * @param text     the text that will be shown
     * @param duration the duration at which the text is shown
     */
    public void setBottomText(String text, float duration) {
        if (!bottomText.getText().equals(text))
            bottomText.setText(text);
        remainingBottomTime = duration;
    }

    /**
     * Method to get text at the top.
     *
     * @return text at the top
     */
    public String getTopText() {
        return topText.getText();
    }

    /**
     * Method to get text at the bottom.
     *
     * @return text at the bottom
     */
    public String getBottomText() {
        return bottomText.getText();
    }

    /**
     * Updates the remaining time for showing the text.
     *
     * @param delta Time since the last call to update(), in seconds.
     */
    @Override
    public void update(float delta) {
        updateContents();
        if (remainingCenterTime > 0f)
            remainingCenterTime -= delta;
        if (remainingTopTime > 0f)
            remainingTopTime -= delta;
        if (remainingBottomTime > 0f)
            remainingBottomTime -= delta;
        if (remainingTopTime <= 0f)
            setTopText("");
        if (remainingCenterTime <= 0f)
            setCenterText("");
        if (remainingBottomTime <= 0f)
            setBottomText("");
    }

    private GameState getGameState() {
        return app.getStateManager().getState(GameState.class);
    }

    /**
     * Updates the remaining lives. When a new map is loaded, the text is reset.
     * When the game is over, it shows if the game is won or lost.
     */
    private void updateContents() {
        final DroidsModel model = getGameState().getModel();
        if (map != model.getDroidsMap()) {
            map = model.getDroidsMap();
            reset();
        }
        final Robot droid = model.getDroidsMap().getDroid();
        setTopText(MessageFormat.format(lookup("view.top-text"),
                                        droid.getLives()));
        if (model.isGameOver())
            gameOver(model.isGameWon() ? "game-over.won" : "game-over.lost");
    }

    /**
     * Shows a key in the center, when the is over and "view.hint" at the bottom.
     *
     * @param key the shown message
     */
    private void gameOver(String key) {
        setCenterText(lookup(key));
        setBottomText(lookup("view.hint"));
    }

    /**
     * Resets all text overlays.
     */
    private void reset() {
        if (Float.isInfinite(remainingTopTime)) setTopText("");
        if (Float.isInfinite(remainingCenterTime)) setCenterText("");
        if (Float.isInfinite(remainingBottomTime)) setBottomText("");
        overlayGuiNode.detachAllChildren();
        overlayGuiNode.attachChild(topText);
        overlayGuiNode.attachChild(bottomText);
        overlayGuiNode.attachChild(centerText);
    }

    /**
     * Enables or disables to show a hint at the bottom.
     *
     * @param enabled activate the AppState or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) return;
        super.setEnabled(enabled);
        if (app != null) {
            if (enabled)
                enableState();
            else
                disableState();
        }
    }

    /**
     * Enables to show a hint at the bottom.
     */
    private void enableState() {
        app.getGuiNode().attachChild(overlayGuiNode);
        setBottomText(lookup("view.hint"), app.getConfig().getHintTime());
    }

    /**
     * Disables to show a hint at the bottom.
     */
    private void disableState() {
        app.getGuiNode().detachChild(overlayGuiNode);
    }
}
