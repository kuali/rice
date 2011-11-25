package org.kuali.rice.krad.uif.control;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.widget.Spinner;

import java.util.List;

/**
 * Text control that as decorated with a spinner widget (allowing the control value to be modified using the
 * spinner)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SpinnerControl extends TextControl {
    private static final long serialVersionUID = -8267606288443759880L;

    private Spinner spinner;

    public SpinnerControl() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(getSpinner());

        return components;
    }

    /**
     * Spinner widget that should decorate the control
     *
     * @return Spinner
     */
    public Spinner getSpinner() {
        return spinner;
    }

    /**
     * Setter for the control's spinner widget instance
     *
     * @param spinner
     */
    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }
}
