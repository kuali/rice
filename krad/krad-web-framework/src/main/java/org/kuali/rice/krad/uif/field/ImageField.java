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
import org.kuali.rice.krad.uif.element.Message;

import java.util.List;

/**
 * Field that wraps an image content element.
 *
 * <p>
 * Puts a <code>&lt;DIV&gt;</code> tag around an image element. This allows for labeling, styling, etc.
 * </p>
 *
 * @see org.kuali.rice.krad.uif.element.Image
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

    /**
     * Retrieves the {@link Image} element wrapped by this field
     *
     * @return Image - the Image element representing the HTML IMG element
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the Image to be wrapped by this field
     *
     * @param image - the Image element to be wrapped by this field
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Retrieves the URL the image wrapped by this field
     *
     * @see org.kuali.rice.krad.uif.element.Image#getSource()
     * @return String containing the URL for the image
     */
    public String getSource() {
        return image.getSource();
    }

    /**
     * Sets the source URL for the Image associated with this field
     *
     * @param source - String URL for the image
     */
    public void setSource(String source) {
        image.setSource(source);
    }

    /**
     * Provides alternate information for the image element
     *
     * <p>The altText property specifies an alternate text for an image. It is displayed by the browser
     * if the image cannot be displayed.  This is especially important for accessibility, because screen
     * readers can't understand images, but rather will read aloud the alternative text assigned to them.
     * </p>
     *
     * @see org.kuali.rice.krad.uif.element.Image#getAltText()
     * @return a String representing alternative information about this image
     */
    public String getAltText() {
        return image.getAltText();
    }

    /**
     * Sets the alternate text attribute of the image assosiated with this field
     *
     * @param altText - a String containing the alternative information about the image
     */
    public void setAltText(String altText) {
        image.setAltText(altText);
    }

    /**
     * Gets the height of the image
     *
     * @return String height
     */
    public String getHeight() {
        return image.getHeight();
    }

    /**
     * Sets the height of the image
     *
     * @param height
     */
    public void setHeight(String height) {
        image.setHeight(height);
    }

    /**
     * Sets the width of the image
     *
     * @param width
     */
    public void setWidth(String width) {
        image.setWidth(width);
    }

    /**
     * Gets the width of the image
     *
     * @return String width
     */
    public String getWidth() {
        return image.getWidth();
    }

    /**
     * Gets the caption header text
     *
     * @return String captionHeaderText
     */
    public String getCaptionHeaderText() {
        return image.getCaptionHeaderText();
    }

    /**
     * Sets the caption header text
     *
     * @param captionHeaderText
     */
    public void setCaptionHeaderText(String captionHeaderText) {
        image.setCaptionHeaderText(captionHeaderText);
    }

    /**
     * Gets the caption header
     *
     * @return Header captionHeader
     */
    public Header getCaptionHeader() {
        return image.getCaptionHeader();
    }

    /**
     * Sets the caption header
     *
     * @param captionHeader
     */
    public void setCaptionHeader(Header captionHeader) {
        image.setCaptionHeader(captionHeader);
    }

    /**
     * Gets the cutline text
     *
     * @return String cutlineText
     */
    public String getCutlineText() {
        return image.getCutlineText();
    }

    /**
     * Sets the cutline text
     *
     * @param cutlineText
     */
    public void setCutlineText(String cutlineText) {
        image.setCutlineText(cutlineText);
    }

    /**
     * Gets the cutline
     *
     * @return Message cutline
     */
    public Message getCutline() {
        return image.getCutlineMessage();
    }

    /**
     * Sets the cutline
     *
     * @param cutline
     */
    public void setCutline(Message cutline) {
        image.setCutlineMessage(cutline);
    }

    /**
     * Gets boolen of whether the caption header is above the image
     *
     * @return boolean captionHeaderAboveImage
     */
    public boolean isCaptionHeaderAboveImage() {
        return image.isCaptionHeaderPlacementAboveImage();
    }

    /**
     * Sets boolen of whether the caption header is above the image
     *
     * @param captionHeaderAboveImage
     */
    public void setCaptionHeaderAboveImage(boolean captionHeaderAboveImage) {
        image.setCaptionHeaderPlacementAboveImage(captionHeaderAboveImage);
    }
}
