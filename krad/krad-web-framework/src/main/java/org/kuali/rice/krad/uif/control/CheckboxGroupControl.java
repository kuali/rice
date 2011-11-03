/**
 * Copyright 2005-2011 The Kuali Foundation
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
 * Represents a group of HTML checkbox controls. Provides preset options for the
 * user to choose by a series of checkbox controls. Only or more options can be
 * select
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CheckboxGroupControl extends MultiValueControlBase {
    private static final long serialVersionUID = 8800478332086081970L;

    private String delimiter;

    public CheckboxGroupControl() {
        super();
    }

    /**
     * Delimiter string to be rendered between the checkbox group options,
     * defaults to none
     * 
     * @return String delimiter string
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * Setter for the string delimiter for each checkbox option
     * 
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}
