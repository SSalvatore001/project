//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.dialog;

import com.jme3.app.Application;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.style.BaseStyles;

/**
 * The {@code Dialog} class provides a simple dialog container for the Lemur GUI framework.
 * <p>
 * It offers methods to initialize the GUI styles, and to open and close dialogs.
 * </p>
 */
public class Dialog extends Container {

    /**
     * Path to the styles script for GUI elements.
     */
    private static final String STYLES_SCRIPT = "Interface/Lemur/pp-styles.groovy"; //NON-NLS

    /**
     * Default style identifier for the GUI.
     */
    private static final String DEFAULT_STYLE = "pp"; //NON-NLS

    /**
     * Initializes the Lemur GUI framework and loads the default style.
     * <p>
     * This method must be called before any dialogs are created.
     * </p>
     *
     * @param app the {@link Application} instance used to initialize the GUI framework
     */
    public static void initialize(Application app) {
        GuiGlobals.initialize(app);
        BaseStyles.loadStyleResources(STYLES_SCRIPT);
        GuiGlobals.getInstance().getStyles().setDefaultStyle(DEFAULT_STYLE);
    }

    /**
     * Checks if there is any open dialog.
     *
     * @return {@code true} if any modal popups are currently active; {@code false} otherwise
     */
    public static boolean anyOpenDialog() {
        return GuiGlobals.getInstance().getPopupState().hasActivePopups();
    }

    /**
     * Opens this dialog by centering it in the GUI and displaying it as a modal popup.
     */
    public void open() {
        final PopupState popupState = GuiGlobals.getInstance().getPopupState();
        popupState.centerInGui(this);
        popupState.showModalPopup(this);
    }

    /**
     * Closes this dialog by removing it from the active popups.
     */
    public void close() {
        GuiGlobals.getInstance().getPopupState().closePopup(this);
    }
}
