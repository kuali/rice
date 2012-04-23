package org.kuali.rice.krad.uif.element;

/**
 * Content element that renders a non-breaking space HTML <code>&amp;nbsp;</code> entity
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Space extends ContentElementBase {
    private static final long serialVersionUID = 4655642965438419569L;

    public Space() {
        super();
    }

    /**
     * Indicates that this element renders itself and does not use a template
     *
     * <p>
     * Since this method returns true, the renderOutput property provides
     * the HTML string representing this element.
     * </p>
     *
     * @return true - this object renders itself
     * @see org.kuali.rice.krad.uif.component.Component#isSelfRendered()
     */
    @Override
    public boolean isSelfRendered() {
        return true;
    }

    /**
     * Provides the HTML string to be used to render a non-breaking space
     *
     * <p>The HTML for a Space element is <code>&amp;nbsp;</code></p>
     *
     * @return the HTML string for a non-breaking space
     * @see org.kuali.rice.krad.uif.component.Component#getRenderOutput()
     */
    @Override
    public String getRenderOutput() {
        return "&nbsp;";
    }
}
