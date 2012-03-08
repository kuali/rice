package org.kuali.rice.krad.uif.element;

/**
 * Content element that renders the HTML &nbsp; entity
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Space extends ContentElementBase {
    private static final long serialVersionUID = 4655642965438419569L;

    public Space() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#isSelfRendered()
     */
    @Override
    public boolean isSelfRendered() {
        return true;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getRenderOutput()
     */
    @Override
    public String getRenderOutput() {
        return "&nbsp;";
    }
}
