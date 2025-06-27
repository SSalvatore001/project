//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.dialog;

import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.style.ElementId;

import static com.simsilica.lemur.component.BorderLayout.Position.East;
import static com.simsilica.lemur.component.BorderLayout.Position.West;
import static pp.util.config.Resources.lookup;

/**
 * Represents a confirmation dialog.
 * <p>
 * This dialog displays a question along with "Yes" and "No" buttons.
 * Selecting "Yes" executes the provided action before closing the dialog,
 * while selecting "No" simply closes the dialog.
 * </p>
 *
 * @see Dialog
 */
public class ConfirmDialog extends Dialog {

    /**
     * Creates a confirmation dialog with a specified question and an action to be
     * executed when "Yes" is selected.
     *
     * @param question  the question to display in the dialog
     * @param yesAction the action to perform if "Yes" is selected
     */
    public ConfirmDialog(String question, Runnable yesAction) {
        addChild(new Label(lookup("dialog.confirmation.header"), new ElementId("header"))); // NON-NLS
        addChild(new Label(question));
        final Container buttons = this.addChild(new Container(new BorderLayout()));
        buttons.addChild(new Button(lookup("button.yes")), West)
               .addClickCommands(b -> {yesAction.run(); close();});
        buttons.addChild(new Button(lookup("button.no")), East)
               .addClickCommands(b -> close());
    }
}
