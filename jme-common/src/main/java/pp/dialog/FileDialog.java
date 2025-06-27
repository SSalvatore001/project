//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.dialog;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.style.ElementId;
import pp.util.FileAction;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import static com.simsilica.lemur.component.BorderLayout.Position.East;
import static com.simsilica.lemur.component.BorderLayout.Position.West;
import static pp.util.PreferencesUtils.getPreferences;
import static pp.util.config.Resources.lookup;

/**
 * A dialog for file operations (loading or saving).
 * <p>
 * This dialog displays a text field for the file path and two buttons: OK and Cancel.
 * When the OK button is pressed (or the return key is hit), the specified file action is executed
 * on the file represented by the entered path. The last used file path is stored in the user's
 * preferences for convenience.
 * </p>
 *
 * @see Dialog
 */
public class FileDialog extends Dialog {
    /**
     * Preferences for storing file dialog settings.
     */
    private static final Preferences PREFERENCES = getPreferences(FileDialog.class);

    /**
     * The key for storing the last used file path in preferences.
     */
    private static final String LAST_PATH = "last.file.path";

    private final FileAction fileAction;
    private final TextField textField;
    private final Button okButton;

    /**
     * Creates a file dialog for loading or saving files.
     *
     * @param fileAction the action to perform with the selected file
     * @param label      the label for the dialog
     */
    public FileDialog(FileAction fileAction, String label) {
        this.fileAction = fileAction;
        textField = new TextField(PREFERENCES.get(LAST_PATH, "").trim());
        textField.setSingleLine(true);
        textField.setPreferredWidth(500f);
        // Move the caret right so that it becomes visible at the end of a long text.
        textField.getDocumentModel().right();

        addChild(new Label(label, new ElementId("header"))); // NON-NLS
        final Container line = addChild(new Container(new SpringGridLayout()));
        line.addChild(new Label(lookup("dialog.file.label")));
        line.addChild(textField, 1);
        final Container buttons = addChild(new Container(new BorderLayout()));
        okButton = buttons.addChild(new Button(lookup("button.ok")), West);
        okButton.addClickCommands(b -> ok());
        buttons.addChild(new Button(lookup("button.cancel")), East)
               .addClickCommands(b -> close());

        // Hitting a return key in the text field is like pushing the OK button.
        getClickOkOn(KeyInput.KEY_RETURN);
        getClickOkOn(KeyInput.KEY_NUMPADENTER);
    }

    /**
     * Maps the specified key code to trigger a click on the OK button.
     * <p>
     * This method registers an action on the text field so that when the specified key is pressed,
     * it simulates a click on the OK button.
     * </p>
     *
     * @param keyCode the key code for which the action is mapped
     */
    private void getClickOkOn(int keyCode) {
        textField.getActionMap().put(new KeyAction(keyCode), (c, k) -> okButton.click());
    }

    /**
     * Executes the file action using the file path specified in the text field.
     * <p>
     * This method retrieves the text from the text field, stores it as the last used path in the
     * preferences, and then runs the provided file action with the corresponding file. If an
     * {@code IOException} occurs during the file operation, an error dialog is displayed with the
     * localized error message.
     * </p>
     */
    private void ok() {
        try {
            final String path = textField.getText();
            PREFERENCES.put(LAST_PATH, path);
            fileAction.run(new File(path));
            close();
        }
        catch (IOException e) {
            new ErrorDialog(e.getLocalizedMessage()).open();
        }
    }
}
