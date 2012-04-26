package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.view.View;

/**
 * Content element that renders a HTML <code>&lt;IMG&gt;</code> tag
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Image extends ContentElementBase {
    private static final long serialVersionUID = -3911849875276940507L;

    private String source;
    private String altText;
    private String height;
    private String width;

    private boolean captionHeaderAboveImage;

    private String captionHeaderText;
    private Header captionHeader;

    private String cutlineText;
    private MessageField cutline;

    public Image() {
        super();
        altText = "";
    }

    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (StringUtils.isNotBlank(captionHeaderText)) {
            captionHeader.setHeaderText(captionHeaderText);
        }

        if (StringUtils.isNotBlank(cutlineText)) {
            cutline.setMessageText(cutlineText);
        }
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /** Provides alternate information for the image element
     *
     * <p>The altText property specifies an alternate text for an image. It is displayed by the browser
     * if the image cannot be displayed.  This is especially important for accessibility, because screen
     * readers can't understand images, but rather will read aloud the alternative text assigned to them.
     * <br>
     * Some best practices:
     * <ul>
     *     <li>spacer images, bullets, and icons should have the altText set to null or the empty string. This
     *     will prevent screen readers from announcing it.</li>
     *     <li>Make the altText message as short and succinct as possible</li>
     *     <li>Describe the content of the image and nothing more</li>
     * </ul>
     * </p>
     *
     * @return a String representing alternative information about this image
     */
    public String getAltText() {
        return this.altText;
    }

    /**
     * Sets the alternate text property for this image
     *
     * @param altText a String containing the alternative information about the image
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    /**
     * Returns the height style attribute of this image
     *
     * <p>
     * The default unit of measure is pixels.<br>
     * It is good practice to specify both the height and width attributes for an image.
     * If these attributes are set, the space required for the image is reserved when the page is loaded.
     * However, without these attributes, the browser does not know the size of the image. The effect will
     * be that the page layout will change while the images load.
     * </p>
     *
     * @return a String representation of the height of this image
     */
    public String getHeight() {
        return this.height;
    }

    /**
     * Sets the height of the image.
     *
     * @param height a String containing the height of the image
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Returns the width style property of the image
     *
     * <p>
     * The default unit of measure is pixels.<br>
     * It is good practice to specify both the height and width attributes for an image.
     * If these attributes are set, the space required for the image is reserved when the page is loaded.
     * However, without these attributes, the browser does not know the size of the image. The effect will
     * be that the page layout will change while the images load.
     * <p>
     *
     * @return a string containing the width of this image
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the width style attribute of the image
     *
     * @param width - a String containing the width of this image
     */
    public void setWidth(String width) {
        this.width = width;
    }

    public String getCaptionHeaderText() {
        return captionHeaderText;
    }

    public void setCaptionHeaderText(String captionHeaderText) {
        this.captionHeaderText = captionHeaderText;
    }

    public Header getCaptionHeader() {
        return captionHeader;
    }

    public void setCaptionHeader(Header captionHeader) {
        this.captionHeader = captionHeader;
    }

    public String getCutlineText() {
        return cutlineText;
    }

    public void setCutlineText(String cutlineText) {
        this.cutlineText = cutlineText;
    }

    public MessageField getCutline() {
        return cutline;
    }

    /**
     * A cutline is the text describing the image in detail (this is also often confusingly called a caption).
     */
    public void setCutline(MessageField cutline) {
        this.cutline = cutline;
    }

    /**
     * Specifies whether the image caption is to be displayed above or below the image
     *
     * @return  true if the caption is to be displayed above the image. false if displayed below the image.
     */
    public boolean isCaptionHeaderAboveImage() {
        return captionHeaderAboveImage;
    }

    /**
     * Sets whether the image caption is to be displayed above or below the image
     *
     * @param captionHeaderAboveImage true displays above image, false displays below image
     */
    public void setCaptionHeaderAboveImage(boolean captionHeaderAboveImage) {
        this.captionHeaderAboveImage = captionHeaderAboveImage;
    }
}
