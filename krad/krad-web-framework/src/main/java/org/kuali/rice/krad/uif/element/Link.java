package org.kuali.rice.krad.uif.element;


/**
 * Content element that renders a link
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Link extends ContentElementBase {
    private static final long serialVersionUID = 8989868231938336068L;

    private String linkLabel;
    private String target;
    private String hrefText;

    public Link() {
        super();
    }

    public String getLinkLabel() {
        return linkLabel;
    }

    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getHrefText() {
        return hrefText;
    }

    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ScriptEventSupport#getSupportsOnClick()
     */
    public boolean getSupportsOnClick() {
        return true;
    }
}
