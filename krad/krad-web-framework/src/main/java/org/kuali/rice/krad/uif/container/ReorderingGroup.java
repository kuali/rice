package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.widget.Reorderer;

import java.util.List;

/**
 * Group implementation that supports reordering of the group items
 *
 * <p>
 * Uses a {@link org.kuali.rice.krad.uif.widget.Reorderer} widget to perform the reordering client side
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReorderingGroup extends Group {
    private static final long serialVersionUID = -9069458348367183223L;

    private Reorderer reorderer;

    public ReorderingGroup() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(reorderer);

        return components;
    }

    /**
     * Widget that will perform the reordering of the group's items client side
     *
     * @return Reorderer widget instance
     */
    public Reorderer getReorderer() {
        return reorderer;
    }

    /**
     * Setter for the groups reorderer widget
     *
     * @param reorderer
     */
    public void setReorderer(Reorderer reorderer) {
        this.reorderer = reorderer;
    }
}
