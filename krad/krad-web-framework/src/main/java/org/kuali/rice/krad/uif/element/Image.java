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
