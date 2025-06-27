//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.droids;

import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.ElementId;
import pp.dialog.ConfirmDialog;
import pp.dialog.Dialog;
import pp.dialog.FileDialog;
import pp.dialog.PropertyCheckboxModel;
import pp.dialog.StateCheckboxModel;
import pp.droids.model.DroidsModel;
import pp.droids.view.radar.RadarView;

import static pp.util.config.Resources.lookup;

/**
 * An application state that shows and controls the game menu.
 */
public class Menu extends Dialog {
    private final DroidsApp app;

    /**
     * Shows the main dialog container.
     */
    public Menu(DroidsApp app) {
        this.app = app;
        addChild(new Label(lookup("game.name"),
                           new ElementId("header"))); //NON-NLS
        addChild(new Button(lookup("menu.map.create-random")))
                .addClickCommands(s -> getModel().loadRandomMap());
        addChild(new Button(lookup("menu.map.load")))
                .addClickCommands(s -> loadMap());
        addChild(new Button(lookup("menu.map.save")))
                .addClickCommands(s -> saveMap());
        addChild(new Button(lookup("menu.return-to-game")))
                .addClickCommands(s -> returnToGame());
        addChild(new Checkbox(lookup("menu.sound-enabled"),
                              new StateCheckboxModel(app, GameSound.class)));
        addChild(new Checkbox(lookup("menu.radar-view-enabled"),
                              new StateCheckboxModel(app, RadarView.class)));
        addChild(new Checkbox(lookup("menu.radar-shows-path"),
                              new PropertyCheckboxModel(getRadarView().getShowPathProperty())));
        addChild(new Button(lookup("menu.quit")))
                .addClickCommands(s -> quit());
    }

    private void returnToGame() {
        close();
        app.getStateManager().getState(GameState.class).setEnabled(true);
    }

    private void saveMap() {
        new FileDialog(getModel()::saveMap, lookup("menu.map.save")).open();
    }

    private void loadMap() {
        new FileDialog(getModel()::loadMap, lookup("menu.map.load")).open();
    }

    private void quit() {
        new ConfirmDialog(lookup("confirm.leaving"), app::stop).open();
    }

    /**
     * Method to get the model.
     *
     * @return model
     */
    private DroidsModel getModel() {
        return app.getStateManager().getState(GameState.class).getModel();
    }

    private RadarView getRadarView() {
        return app.getStateManager().getState(RadarView.class);
    }
}
