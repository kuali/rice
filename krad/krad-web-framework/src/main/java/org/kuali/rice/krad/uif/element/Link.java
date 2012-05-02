package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.uif.widget.LightBox;

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

    private LightBox lightBox;

    public Link() {
        super();
    }

    /**
     *  Returns the label of the link.
     *
     * @return The link label
     */
    public String getLinkLabel() {
        return linkLabel;
    }

    /**
     * Setter for the link label.
     *
     * @param linkLabel
     */
    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }

    /**
     *  Returns the target that will be used to specify where to open the href.
     *
     * @return The target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Setter for the link target.
     *
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     *  Returns the href text.
     *
     * @return The href text
     */
    public String getHrefText() {
        return hrefText;
    }

    /**
     * Setter for the hrefText.
     *
     * @param hrefText
     */
    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }

    /**
     *  Returns the <code>LightBox</code> used to open the link in
     *
     * @return The <code>LightBox</code>
     */
    public LightBox getLightBox() {
        return lightBox;
    }

    /**
     * Setter for the lightBox
     *
     * @param lightBox
     */
    public void setLightBox(LightBox lightBox) {
        this.lightBox = lightBox;
    }

}
