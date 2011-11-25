package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

/**
 * Widget that decorates a control transforming into a spinner
 *
 * <p>
 * Spinners allow the incrementing or decrementing of the controls value with an arrow up and down icon on
 * the right side of the control. How the value is incremented, min/max values, and so on can be configured
 * through the {@link org.kuali.rice.krad.uif.component.Component#getComponentOptions()} property
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Spinner extends WidgetBase {
    private static final long serialVersionUID = -659830874214415990L;

    public Spinner() {
        super();
    }

    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
    }
}
