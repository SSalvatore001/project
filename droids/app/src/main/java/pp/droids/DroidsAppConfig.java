//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids;

import com.jme3.math.ColorRGBA;
import pp.droids.model.DroidsConfig;

/**
 * Class for game configurations.
 */
@SuppressWarnings("CanBeFinal")
public class DroidsAppConfig extends DroidsConfig {
    /**
     * Converts a string value found in the properties file into an object of the specified type.
     * Extends the super class method by support of type {@linkplain ColorRGBA}.
     *
     * @param value      the string value that shall be converted
     * @param targetType the target type into which the value string is converted
     */
    @Override
    protected Object convertToType(String value, Class<?> targetType) {
        if (targetType == ColorRGBA.class)
            return makeColorRGBA(value);
        return super.convertToType(value, targetType);
    }

    /**
     * Converts the specified String value to a corresponding {@linkplain ColorRGBA} object.
     *
     * @param value the color in the format "red, green, blue, alpha" with all values in the range [0..1]
     */
    private static ColorRGBA makeColorRGBA(String value) {
        String[] split = value.split(",", -1);
        try {
            if (split.length == 4)
                return new ColorRGBA(Float.parseFloat(split[0]),
                                     Float.parseFloat(split[1]),
                                     Float.parseFloat(split[2]),
                                     Float.parseFloat(split[3]));
        }
        catch (NumberFormatException e) {
            // deliberately left empty
        }
        throw new IllegalArgumentException(value + " should consist of exactly 4 numbers");
    }

    /**
     * The width of the game view resolution.
     */
    @Property("settings.resolution.width") //NON-NLS
    private int resolutionWidth = 1024;

    /**
     * The height of the game view resolution.
     */
    @Property("settings.resolution.height") //NON-NLS
    private int resolutionHeight = 768;

    /**
     * Indicates whether the game shall start in full screen mode.
     */
    @Property("settings.full-screen") //NON-NLS
    private boolean fullScreen = false;

    /**
     * Indicates whether gamma correction shall be enabled. If enabled, the main framebuffer will
     * be configured for sRGB colors, and sRGB images will be linearized.
     * <p>
     * Gamma correction requires a GPU that supports GL_ARB_framebuffer_sRGB;
     * otherwise this setting will be ignored.
     */
    @Property("settings.use-gamma-correction") //NON-NLS
    private boolean useGammaCorrection = true;

    /**
     * Indicates whether to use full resolution framebuffers on Retina displays.
     * This is ignored on other platforms.
     */
    @Property("settings.use-retina-framebuffer") //NON-NLS
    private boolean useRetinaFrameBuffer = false;

    /**
     * Indicates whether the settings window shall be shown
     * for configuring the game.
     */
    @Property("settings.show") //NON-NLS
    private boolean showSettings = false;

    /**
     * Indicates whether the JME statistics window shall be shown in the lower left corner.
     */
    @Property("statistics.show") //NON-NLS
    private boolean showStatistics = false;

    /**
     * the duration how long a hint shall show.
     */
    @Property("hint.show") //NON-NLS
    private int hintTime = 4;

    /**
     * the color of the center text during game play.
     */
    @Property("overlay.center.color") //NON-NLS
    private ColorRGBA centerColor = ColorRGBA.Red;

    /**
     * the color of the top text during game play.
     */
    @Property("overlay.top.color") //NON-NLS
    private ColorRGBA topColor = ColorRGBA.White;

    /**
     * the color of the bottom text during game play.
     */
    @Property("overlay.bottom.color") //NON-NLS
    private ColorRGBA bottomColor = ColorRGBA.Yellow;

    /**
     * returns the width of the game view resolution.
     */
    public int getResolutionWidth() {
        return resolutionWidth;
    }

    /**
     * Returns the height of the game view resolution.
     */
    public int getResolutionHeight() {
        return resolutionHeight;
    }

    /**
     * Returns whether the game shall start in full screen mode.
     */
    public boolean fullScreen() {
        return fullScreen;
    }

    /**
     * Returns whether gamma correction shall be enabled. If enabled, the main framebuffer will
     * be configured for sRGB colors, and sRGB images will be linearized.
     * <p>
     * Gamma correction requires a GPU that supports GL_ARB_framebuffer_sRGB;
     * otherwise this setting will be ignored.
     */
    public boolean useGammaCorrection() {
        return useGammaCorrection;
    }

    /**
     * Returns whether to use full resolution framebuffers on Retina displays.
     * This is ignored on other platforms.
     */
    public boolean useRetinaFrameBuffer() {
        return useRetinaFrameBuffer;
    }

    /**
     * Returns whether the settings window shall be shown
     * for configuring the game.
     */
    public boolean getShowSettings() {
        return showSettings;
    }

    /**
     * Returns whether the JME statistics window shall be shown in the lower left corner.
     */
    public boolean getShowStatistics() {
        return showStatistics;
    }

    /**
     * Returns the duration how long a hint shall show.
     */
    public int getHintTime() {
        return hintTime;
    }

    /**
     * Returns the color of the center text during game play.
     */
    public ColorRGBA getCenterColor() {
        return centerColor;
    }

    /**
     * Returns the color of the top text during game play.
     */
    public ColorRGBA getTopColor() {
        return topColor;
    }

    /**
     * Returns the color of the bottom text during game play.
     */
    public ColorRGBA getBottomColor() {
        return bottomColor;
    }
}
