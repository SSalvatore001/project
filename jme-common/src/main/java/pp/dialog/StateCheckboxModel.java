//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.dialog;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.simsilica.lemur.DefaultCheckboxModel;

/**
 * A checkbox model that synchronizes the checked state with an {@link AppState}.
 * <p>
 * When the checkbox is toggled, the associated app state is enabled or disabled accordingly.
 * This allows for a simple and direct way to control the state of an application component
 * via a GUI checkbox. The model automatically reflects the initial enabled state of the app state.
 * </p>
 */
public class StateCheckboxModel extends DefaultCheckboxModel {
    private final AppState state;

    /**
     * Constructs a {@code StateCheckboxModel} for the specified app state class.
     * <p>
     * This constructor retrieves the app state from the application's state manager and delegates
     * to the main constructor.
     * </p>
     *
     * @param app        the application instance containing the state manager; must not be {@code null}
     * @param stateClass the class of the app state to be controlled; must not be {@code null}
     * @throws NullPointerException if the specified app state cannot be found in the state manager
     */
    public StateCheckboxModel(Application app, Class<? extends AppState> stateClass) {
        this(app.getStateManager().getState(stateClass));
    }

    /**
     * Constructs a {@code StateCheckboxModel} for the specified app state.
     * <p>
     * The initial checkbox state is set based on the current enabled status of the app state.
     * </p>
     *
     * @param state the app state to be controlled; must not be {@code null}
     */
    public StateCheckboxModel(AppState state) {
        this.state = state;
        setChecked(state.isEnabled());
    }

    /**
     * Sets the checked state of the checkbox and synchronizes the enabled state of the associated app state.
     * <p>
     * When {@code checked} is {@code true}, the app state is enabled; when {@code false},
     * the app state is disabled.
     * </p>
     *
     * @param checked {@code true} to check the box and enable the state, {@code false} to uncheck the box and disable the state
     */
    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        state.setEnabled(checked);
    }
}
