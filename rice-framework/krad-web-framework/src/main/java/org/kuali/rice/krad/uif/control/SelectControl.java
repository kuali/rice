/**
 * Copyright 2005-2018 The Kuali Foundation
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

/**
 * Control interface for a select box. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface SelectControl extends Control, MultiValueControl, SizedControl {

    /**
     * Indicates whether multiple values can be selected. Defaults to false
     * <p>
     * If multiple is set to true, the underlying property must be of Array type
     * </p>
     *
     * @return true if multiple values can be selected, false if only
     *         one value can be selected
     */
    boolean isMultiple();

    /**
     * Set whether multiple values can be selected
     *
     * @param multiple
     */
    void setMultiple(boolean multiple);

}
