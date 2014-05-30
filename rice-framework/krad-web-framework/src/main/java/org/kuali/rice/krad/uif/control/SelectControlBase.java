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

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;

/**
 * Represents a HTML Select control. Provides preset options for the User to
 * choose from by a drop down
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "dropdownControl", parent = "Uif-DropdownControl"),
        @BeanTag(name = "multiSelectControl", parent = "Uif-MultiSelectControl")})
public class SelectControlBase extends MultiValueControlBase implements SelectControl {
    private static final long serialVersionUID = 6443247954759096815L;

    private int size;
    private boolean multiple;

    public SelectControlBase() {
        size = 1;
        multiple = false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.control.SelectControl#getSize()
     */
    @Override
    @BeanTagAttribute
    public int getSize() {
        return this.size;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.control.SelectControl#setSize(int)
     */
    @Override
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Indicates whether multiple values can be selected. Defaults to false
     * <p>
     * If multiple is set to true, the underlying property must be of Array type
     * </p>
     *
     * @return true if multiple values can be selected, false if only
     *         one value can be selected
     */
    @Override
    @BeanTagAttribute
    public boolean isMultiple() {
        return this.multiple;
    }

    /**
     * Set whether multiple values can be selected
     *
     * @param multiple
     */
    @Override
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
}
