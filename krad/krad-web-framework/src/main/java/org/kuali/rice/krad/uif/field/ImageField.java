/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

/**
 * Field that encloses an image element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ImageField extends FieldBase {
    private static final long serialVersionUID = -7994212503770623408L;

    private String source;
    private String altText;
    private String height;
    private String width;

    private boolean captionHeaderAboveImage;

    private String captionHeaderText;
    private HeaderField captionHeader;

    private String cutlineText;
    private MessageField cutline;

    public ImageField() {
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

    public HeaderField getCaptionHeader() {
        return captionHeader;
    }

    public void setCaptionHeader(HeaderField captionHeader) {
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
