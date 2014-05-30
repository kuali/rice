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
import org.kuali.rice.krad.uif.widget.DatePicker;

/**
 * Represents a HTML Text control, generally rendered as a input field of type
 * 'text'. This can display and receive a single value
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "textControl", parent = "Uif-TextControl"),
        @BeanTag(name = "smallTextControl", parent = "Uif-SmallTextControl"),
        @BeanTag(name = "mediumTextControl", parent = "Uif-MediumTextControl"),
        @BeanTag(name = "largeTextControl", parent = "Uif-LargeTextControl"),
        @BeanTag(name = "currencyTextControl", parent = "Uif-CurrencyTextControl"),
        @BeanTag(name = "dateControl", parent = "Uif-DateControl")})
public class TextControlBase extends ControlBase implements TextControl, SizedControl {
    private static final long serialVersionUID = -8267606288443759880L;

    private int size;
    private Integer maxLength;
    private Integer minLength;

    private DatePicker datePicker;
    private String watermarkText = StringUtils.EMPTY;
    private boolean textExpand;

    public TextControlBase() {
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

            if (textExpand || (datePicker != null && datePicker.isRender())) {
                field.setRenderInputAddonGroup(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public int getSize() {
        return this.size;
    }

    /**
     * @see TextControlBase#getSize()
     */
    @Override
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * @see TextControlBase#getMaxLength()
     */
    @Override
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * @see TextControlBase#getMinLength()
     */
    @Override
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public DatePicker getDatePicker() {
        return this.datePicker;
    }

    /**
     * @see TextControlBase#getDatePicker()
     */
    @Override
    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isTextExpand() {
        return this.textExpand;
    }

    /**
     * @see TextControlBase#isTextExpand()
     */
    @Override
    public void setTextExpand(boolean textExpand) {
        this.textExpand = textExpand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(name = "watermarkText")
    public String getWatermarkText() {
        return this.watermarkText;
    }

    /**
     * @see TextControlBase#getWatermarkText()
     */
    @Override
    public void setWatermarkText(String watermarkText) {
        //to avoid users from putting in the same value as the watermark adding some spaces here
        //see watermark troubleshooting for more info
        if (StringUtils.isNotEmpty(watermarkText)) {
            watermarkText = watermarkText + "   ";
        }

        this.watermarkText = watermarkText;
    }
}
