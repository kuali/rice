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
package org.kuali.rice.krad.uif.util;

import org.kuali.rice.core.api.util.AbstractKeyValue;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * KeyValue that has an additional label property for hierarchical dropdowns.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "optionGroupLabel-bean", parent = "Uif-OptionGroupLabel")
public class UifOptionGroupLabel extends AbstractKeyValue {
    private static final long serialVersionUID = -839111602450208876L;

    private String label;

    /**
     * Base constructor
     */
    public UifOptionGroupLabel() {
        super();
        this.key = "NA";
        this.value = "NA";
    }

    public UifOptionGroupLabel(String label) {
        this.key = "NA";
        this.value = "NA";
        this.label = label;
    }

    /**
     * Get the label for the option group.
     *
     * @return the label
     */
    @BeanTagAttribute(name = "label")
    public String getLabel() {
        return label;
    }

    /**
     * @see #getLabel()
     */
    public void setLabel(String label) {
        this.label = label;
    }

}
