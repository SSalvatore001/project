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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static pp.util.config.Resources.lookup;

/**
 * A control that monitors a background task and updates a progress dialog.
 * <p>
 * This control periodically checks whether the background task represented by a {@link Future}
 * has completed. When the task completes successfully, it closes the associated progress dialog and
 * calls {@link #onSuccess()}. If the task fails, an error dialog is displayed and {@link #onFailure(Throwable)}
 * is invoked.
 * </p>
 *
 * @see ProgressDialog
 */
public class ProgressControl extends AbstractControl {
    private static final Logger LOGGER = System.getLogger(ProgressControl.class.getName());
    private Future<?> future;
    private final ProgressDialog progressDialog;

    /**
     * Creates a new ProgressControl that monitors the provided background task.
     *
     * @param future        the Future representing the background task to be monitored
     * @param progressLabel the message pattern to be used in the progress dialog
     */
    public ProgressControl(Future<?> future, String progressLabel) {
        this.future = future;
        progressDialog = new ProgressDialog(progressLabel);
        progressDialog.addControl(this);
        progressDialog.open();
    }

    /**
     * Called during the update cycle to check the status of the background task.
     * <p>
     * If the task represented by the {@code future} is completed, this method retrieves the result,
     * closes the progress dialog, and calls either {@link #onSuccess()} or {@link #onFailure(Throwable)}
     * depending on whether the task completed successfully or with an exception.
     * </p>
     *
     * @param tpf the time per frame (in seconds)
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (future == null || !future.isDone())
            return;
        try {
            future.get();
            future = null;
            progressDialog.close();
            onSuccess();
        }
        catch (ExecutionException e) {
            future = null;
            progressDialog.close();
            new ErrorDialog(lookup("server.connection.failed")).open();
            onFailure(e.getCause());
        }
        catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted!", e); //NON-NLS
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Render callback for the control.
     * <p>
     * This implementation is empty as no rendering is required.
     * </p>
     *
     * @param renderManager the RenderManager used for rendering
     * @param viewPort      the ViewPort being rendered
     */
    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) { /* empty */ }

    /**
     * Handles a successful completion of the background task.
     * <p>
     * This method is called when the connection attempt completes successfully. Subclasses can
     * override this method to perform additional actions upon success.
     * </p>
     */
    protected void onSuccess() { /* do nothing */ }

    /**
     * Handles a failed background task execution.
     * <p>
     * This method is called when the background task fails with an exception. Subclasses can override
     * this method to provide custom error handling.
     * </p>
     *
     * @param e the cause of the failure
     */
    protected void onFailure(Throwable e) { /* do nothing */ }
}
