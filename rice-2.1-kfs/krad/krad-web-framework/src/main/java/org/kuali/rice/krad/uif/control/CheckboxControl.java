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
package org.kuali.rice.krad.uif.control;

/**
 * Represents a HTML Checkbox control. Typically used for boolean attributes (where the
 * value is either on/off, true/false)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CheckboxControl extends ControlBase implements ValueConfiguredControl {
    private static final long serialVersionUID = -1397028958569144230L;

    private String value;

    public CheckboxControl() {
       super();
	}

    /**
     * The value that will be submitted when the checkbox control is checked
     *
     * <p>
     * Value can be left blank, in which case the checkbox will submit a boolean value that
     * will populate a boolean property. In cases where the checkbox needs to submit another value (for
     * instance possibly in the checkbox group) the value can be set which will override the default.
     * </p>
     *
     * @return String value for checkbox
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the value that should be submitted when the checkbox is checked
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
