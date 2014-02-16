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
     * Gets the date picker widget, if applicable.
     * 
     * @return date picker, null if not a date input field
     */
    DatePicker getDatePicker();

    /**
     * Gets the size of the text input.
     * 
     * @return text input size
     */
    int getSize();

    /**
     * Gets the min length of the text input.
     * 
     * @return min length
     */
    Integer getMinLength();

    /**
     * Gets the max length of the text input.
     * 
     * @return max length
     */
    Integer getMaxLength();

    /**
     * Indicates if the text expand popup should be used.
     * 
     * @return true if the text expand popup should be used
     */
    boolean isTextExpand();

    /**
     * Gets the watermark text.
     * 
     * @return watermark text
     */
    String getWatermarkText();

    /**
     * Setter for {@link #getMaxLength()}
     * 
     * @param maxLength property value
     */
    void setMaxLength(Integer maxLength);

    /**
     * Setter for {@link #getSize()}
     * 
     * @param size property value
     */
    void setSize(int size);

    /**
     * Setter for {@link #isTextExpand()}
     * 
     * @param b property value
     */
    void setTextExpand(boolean b);

    /**
     * Setter for {@link #getMinLength()}
     * 
     * @param minLength property value
     */
    void setMinLength(Integer minLength);

    /**
     * Setter for {@link #getWatermarkText()}
     * 
     * @param watermark property value
     */
    void setWatermarkText(String watermark);

}
