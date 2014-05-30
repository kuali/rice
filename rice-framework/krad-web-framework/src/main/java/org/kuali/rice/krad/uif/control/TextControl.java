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

import org.kuali.rice.krad.uif.widget.DatePicker;

/**
 * Interface representing a text input control component. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface TextControl extends Control {

    /**
     * @see org.kuali.rice.krad.uif.control.SizedControl#getSize()
     */
    int getSize();

    /**
     * @see org.kuali.rice.krad.uif.control.SizedControl#setSize(int)
     */
    void setSize(int size);

    /**
     * Maximum number of characters that can be inputted.
     *
     * <p>If not set on control, max length of field will be used</p>
     *
     * @return max number of characters
     */
    Integer getMaxLength();

    /**
     * @see TextControl#getMaxLength()
     */
    void setMaxLength(Integer maxLength);

    /**
     * Minimum number of characters that can be inputted.
     *
     * <p>If not set on control, min length of field will be used</p>
     *
     * @return max number of characters
     */
    Integer getMinLength();

    /**
     * @see TextControl#getMinLength()
     */
    void setMinLength(Integer minLength);

    /**
     * Renders a calendar that can be used to select a date value for the text control.
     *
     * @return data picker instance
     */
    DatePicker getDatePicker();

    /**
     * @see TextControl#getDatePicker()
     */
    void setDatePicker(DatePicker datePicker);

    /**
     * If set to true, this control will have a button which can be clicked to expand the text area through
     * a popup window so the user has more space to type and see the data they are entering in this text field.
     *
     * @return boolean if control has text expand enabled, false if not
     */
    boolean isTextExpand();

    /**
     * @see TextControl#isTextExpand()
     */
    void setTextExpand(boolean b);

    /**
     * Gets the watermark text for this TextControl.
     *
     * <p>A watermark typically appears as light gray text within the text input element whenever the
     * element is empty and does not have focus. This provides a hint to the user as to what the input
     * is used for, or the type of input that is required.</p>
     *
     * @return the watermarkText
     */
    String getWatermarkText();

    /**
     * @see TextControl#getWatermarkText()
     */
    void setWatermarkText(String watermark);

}
