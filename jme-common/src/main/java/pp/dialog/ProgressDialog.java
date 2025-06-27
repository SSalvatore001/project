//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.dialog;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.lemur.Label;

import java.text.MessageFormat;

/**
 * A dialog that displays progress information by periodically updating a text label.
 * <p>
 * The progress is displayed based on elapsed time, formatted using a specified message pattern.
 * A custom control is added to the dialog to update the progress at regular intervals.
 * </p>
 */
public class ProgressDialog extends Dialog {
    private final Label label = new Label("");
    private final String pattern;

    /**
     * Creates a progress dialog.
     *
     * @param pattern the {@linkplain MessageFormat} pattern used for formatting the dialog's text
     */
    public ProgressDialog(String pattern) {
        this.pattern = pattern;
        setText(0);
        addChild(label);
        addControl(new ProgressControl());
    }

    /**
     * Updates the label's text with the formatted elapsed time.
     *
     * @param elapsedTime the elapsed time to be formatted and displayed
     */
    private void setText(int elapsedTime) {
        label.setText(MessageFormat.format(pattern, elapsedTime));
    }

    /**
     * A control that updates the progress dialog's label based on elapsed time.
     * <p>
     * This control increments the elapsed time and updates the label whenever the integer
     * value of the elapsed time changes.
     * </p>
     */
    private class ProgressControl extends AbstractControl {
        private float elapsedTime;

        /**
         * Called during the update cycle to update the progress.
         * <p>
         * If the integer part of the elapsed time changes, the dialog's text is updated accordingly.
         * </p>
         *
         * @param delta the time elapsed since the last update (in seconds)
         */
        @Override
        protected void controlUpdate(float delta) {
            final int next = (int) (elapsedTime + delta);
            if (next != (int) elapsedTime) {
                setText(next);
            }
            elapsedTime += delta;
        }

        /**
         * Render callback for the control.
         * <p>
         * This implementation is empty as no rendering is required for the progress update.
         * </p>
         *
         * @param renderManager the RenderManager used for rendering
         * @param viewPort      the ViewPort being rendered
         */
        @Override
        protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
            // No rendering needed for this control.
        }
    }
}
