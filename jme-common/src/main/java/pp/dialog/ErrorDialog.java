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

import static com.simsilica.lemur.component.BorderLayout.Position.West;
import static pp.util.config.Resources.lookup;

/**
 * Represents an error dialog.
 * <p>
 * This dialog displays an error header, the provided error message, and an OK button.
 * Clicking the OK button will close the dialog.
 * </p>
 *
 * @see Dialog
 */
public class ErrorDialog extends Dialog {

    /**
     * Creates an error dialog with the specified error message.
     *
     * @param errorMessage the error message to display in the dialog
     */
    public ErrorDialog(String errorMessage) {
        this(errorMessage, () -> {});
    }

    /**
     * Creates an error dialog with the specified error message.
     *
     * @param errorMessage the error message to display in the dialog
     * @param onClose      specifies an additional action taken wen this dialog is closed.
     */
    public ErrorDialog(String errorMessage, Runnable onClose) {
        addChild(new Label(lookup("dialog.error.header"), new ElementId("header"))); // NON-NLS
        addChild(new Label(errorMessage));
        final Container buttons = this.addChild(new Container(new BorderLayout()));
        buttons.addChild(new Button(lookup("button.ok")), West)
               .addClickCommands(b -> {close(); onClose.run();});
    }
}
