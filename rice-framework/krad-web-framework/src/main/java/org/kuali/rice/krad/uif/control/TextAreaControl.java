/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Represents a HTML TextArea control. Generally used for values that are very
 * large (such as a description)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "textAreaControl", parent = "Uif-TextAreaControl"),
        @BeanTag(name = "smallTextAreaControl", parent = "Uif-SmallTextAreaControl"),
        @BeanTag(name = "mediumTextAreaControl", parent = "Uif-MediumTextAreaControl"),
        @BeanTag(name = "largeTextAreaControl", parent = "Uif-LargeTextAreaControl")})
public class TextAreaControl extends ControlBase {
    private static final long serialVersionUID = -4664558047325456844L;

    private int rows;
    private int cols;
    private Integer maxLength;
    private Integer minLength;

    private boolean textExpand;
    private String watermarkText = StringUtils.EMPTY;

    public TextAreaControl() {
        super();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Defaults maxLength, minLength (if not set) to maxLength of parent field</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (parent instanceof InputField) {
            InputField field = (InputField) parent;
            if (getMaxLength() == null) {
                setMaxLength(field.getMaxLength());
            }

            if (getMinLength() == null) {
                setMinLength(field.getMinLength());
            }

            if (textExpand) {
                field.setRenderInputAddonGroup(true);
            }
        }
    }

    /**
     * Number of rows the control should span (horizontal length)
     *
     * @return number of rows
     */
    @BeanTagAttribute
    public int getRows() {
        return this.rows;
    }

    /**
     * Setter for the number of rows the control should span (horizontal length)
     *
     * @param rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Number of columns the control should span (vertical length)
     *
     * @return number of columns
     */
    @BeanTagAttribute
    public int getCols() {
        return this.cols;
    }

    /**
     * Setter for the number of columns the control should span (vertical length)
     *
     * @param cols
     */
    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * Maximum number of characters that can be inputted
     *
     * <p>If not set on control, max length of field will be used</p>
     *
     * @return max number of characters
     */
    @BeanTagAttribute
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Setter for the max number of input characters
     *
     * @param maxLength
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Minimum number of characters that can be inputted
     *
     * <p>If not set on control, min length of field will be used</p>
     *
     * @return max number of characters
     */
    @BeanTagAttribute
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * Setter for the min number of input characters
     *
     * @param minLength
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * @return the watermarkText
     */
    @BeanTagAttribute
    public String getWatermarkText() {
        return this.watermarkText;
    }

    /**
     * @param watermarkText the watermarkText to set
     */
    public void setWatermarkText(String watermarkText) {
        //to avoid users from putting in the same value as the watermark adding some spaces here
        //see watermark troubleshooting for more info
        if (StringUtils.isNotEmpty(watermarkText)) {
            watermarkText = watermarkText + "   ";
        }
        this.watermarkText = watermarkText;
    }

    /**
     * If set to true, this control will have a button which can be clicked to expand the text area through
     * a popup window so the user has more space to type and see the data they are entering in this text field
     *
     * @return the textExpand
     */
    @BeanTagAttribute
    public boolean isTextExpand() {
        return this.textExpand;
    }

    /**
     * Setter for the text expand flag
     *
     * @param textExpand the textExpand to set
     */
    public void setTextExpand(boolean textExpand) {
        this.textExpand = textExpand;
    }
}
