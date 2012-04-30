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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Header;
import org.kuali.rice.krad.uif.element.Image;

import java.util.List;

/**
 * Field that wraps an image content element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ImageField extends FieldBase {
    private static final long serialVersionUID = -7994212503770623408L;

    private Image image;

    public ImageField() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(image);

        return components;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getSource() {
        return image.getSource();
    }

    public void setSource(String source) {
        image.setSource(source);
    }

    /**
     * Get the title attribute of the image
     *
     * @return the image title
     */
    @Override
    public String getTitle() {
        return image.getTitle();
    }

    /**
     * Set the title attribute on the image
     *
     * @param title
     */
    @Override
    public void setTitle(String title) {
        image.setTitle(title);
    }

    public String getAltText() {
        return image.getAltText();
    }

    public void setAltText(String altText) {
        image.setAltText(altText);
    }

    public String getHeight() {
        return image.getHeight();
    }

    public void setHeight(String height) {
        image.setHeight(height);
    }

    public void setWidth(String width) {
        image.setWidth(width);
    }

    public String getWidth() {
        return image.getWidth();
    }

    public String getCaptionHeaderText() {
        return image.getCaptionHeaderText();
    }

    public void setCaptionHeaderText(String captionHeaderText) {
        image.setCaptionHeaderText(captionHeaderText);
    }

    public Header getCaptionHeader() {
        return image.getCaptionHeader();
    }

    public void setCaptionHeader(Header captionHeader) {
        image.setCaptionHeader(captionHeader);
    }

    public String getCutlineText() {
        return image.getCutlineText();
    }

    public void setCutlineText(String cutlineText) {
        image.setCutlineText(cutlineText);
    }

    public MessageField getCutline() {
        return image.getCutline();
    }

    public void setCutline(MessageField cutline) {
        image.setCutline(cutline);
    }

    public boolean isCaptionHeaderAboveImage() {
        return image.isCaptionHeaderAboveImage();
    }

    public void setCaptionHeaderAboveImage(boolean captionHeaderAboveImage) {
        image.setCaptionHeaderAboveImage(captionHeaderAboveImage);
    }
}
