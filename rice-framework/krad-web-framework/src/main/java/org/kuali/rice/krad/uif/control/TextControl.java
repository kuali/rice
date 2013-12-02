/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
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
 * TODO mark don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface TextControl extends Control {

    /**
     * This method ...
     * 
     * @param maxLength
     */
    void setMaxLength(Integer maxLength);

    /**
     * This method ...
     * 
     * @param maxLength
     */
    void setSize(int maxLength);

    /**
     * This method ...
     * 
     * @param b
     */
    void setTextExpand(boolean b);

    /**
     * This method ...
     * 
     * @param minLength
     */
    void setMinLength(Integer minLength);

    /**
     * This method ...
     * 
     * @param watermark
     */
    void setWatermarkText(String watermark);

    /**
     * This method ...
     * 
     * @return
     */
    DatePicker getDatePicker();

    /**
     * This method ...
     * 
     * @return
     */
    int getSize();

    /**
     * This method ...
     * 
     * @return
     */
    Integer getMaxLength();

    /**
     * This method ...
     * 
     * @return
     */
    boolean isTextExpand();

}
