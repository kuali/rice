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

import org.kuali.rice.core.api.util.KeyValue;

import java.util.List;

/**
 * Base class for controls that accept/display multiple values
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MultiValueControlBase extends ControlBase implements MultiValueControl {
    private static final long serialVersionUID = -8691367056245775455L;

    private List<KeyValue> options;

    public MultiValueControlBase() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.control.MultiValueControl#getOptions()
     */
    public List<KeyValue> getOptions() {
        return this.options;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.MultiValueControl#setOptions(java.util.List<org.kuali.rice.core.api.util.KeyValue>)
     */
    public void setOptions(List<KeyValue> options) {
        this.options = options;
    }

}
