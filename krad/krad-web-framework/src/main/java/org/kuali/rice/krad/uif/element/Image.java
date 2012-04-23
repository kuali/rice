package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.view.View;

/**
 * Content element that renders as Image
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Image extends ContentElementBase {
    private static final long serialVersionUID = -3911849875276940507L;

    private String source;
    private String altText = "";
    private String height;
    private String width;

    private boolean captionHeaderAboveImage;

    private String captionHeaderText;
    private Header captionHeader;

    private String cutlineText;
    private MessageField cutline;

    public Image() {
        super();
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
     * <p>The altText property specifies an alternate text for an image, if the image cannot be displayed.
     * This is especially important for accessibility, because screen readers can't understand images,
     * but rather read aloud the alternative text assigned to them.
     * <br>
     * Some best practices:
     * <ul>
     *     <li>spacer images, bullets, and icons should have the altText set to null or the empty string. This
     *     will prevent screen readers from will ignore the presence of the item and will not announce it.</li>
     *     <li>Make the altText message as short and succinct as possible</li>
     *     <li>Describe the content of the image and nothing more</li>
     * </ul>
     * </p>
     *
     * @return a String representing alternative information about the image.
     */
    public String getAltText() {
        return this.altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
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

    public boolean isCaptionHeaderAboveImage() {
        return captionHeaderAboveImage;
    }

    public void setCaptionHeaderAboveImage(boolean captionHeaderAboveImage) {
        this.captionHeaderAboveImage = captionHeaderAboveImage;
    }
}
