package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.uif.widget.LightBox;

/**
 * Content element that renders a link
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Link extends ContentElementBase {
    private static final long serialVersionUID = 8989868231938336068L;

    private String linkText;
    private String target;
    private String href;

    private LightBox lightBox;

    public Link() {
        super();
    }

    /**
     * Returns the label of the link
     *
     * @return The link label
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * Setter for the link label
     *
     * @param linkText
     */
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * Returns the target that will be used to specify where to open the href
     *
     * @return The target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Setter for the link target
     *
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Returns the href text
     *
     * @return The href text
     */
    public String getHref() {
        return href;
    }

    /**
     * Setter for the hrefText
     *
     * @param href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Returns the <code>LightBox</code> used to open the link in
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
