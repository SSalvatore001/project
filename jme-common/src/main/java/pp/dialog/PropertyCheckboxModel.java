//----------------------------------------
// Programming project code
// UniBw M, 2022-2025
// www.unibw.de/inf2
// (c) Mark Minas (mark.minas@unibw.de)
//----------------------------------------

package pp.dialog;

import com.simsilica.lemur.DefaultCheckboxModel;
import pp.util.Property;

/**
 * A checkbox model that synchronizes its state with an underlying property.
 * <p>
 * This model extends {@link DefaultCheckboxModel} and ensures that any changes
 * to the checkbox's state are reflected in the associated {@link Property}.
 * </p>
 */
public class PropertyCheckboxModel extends DefaultCheckboxModel {
    private final Property<Boolean> property;

    /**
     * Constructs a new {@code PropertyCheckboxModel} with the specified property.
     *
     * @param property the property to be synchronized with the checkbox state; must not be {@code null}
     */
    public PropertyCheckboxModel(Property<Boolean> property) {
        this.property = property;
        setChecked(property.getValue());
    }

    /**
     * Sets the checked state of the checkbox and updates the underlying property.
     *
     * @param state the new checked state of the checkbox
     */
    @Override
    public void setChecked(boolean state) {
        super.setChecked(state);
        property.setValue(state);
    }
}
