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
package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.field.MessageField;

import java.io.Serializable;

public class NodePrototype implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageField labelPrototype;
    private Group dataGroupPrototype;

    public NodePrototype() {

    }

    /**
     * @param labelPrototype the labelPrototype to set
     */
    public void setLabelPrototype(MessageField labelPrototype) {
        this.labelPrototype = labelPrototype;
    }

    /**
     * @return the labelPrototype
     */
    public MessageField getLabelPrototype() {
        return this.labelPrototype;
    }

    /**
     * @param dataGroupPrototype the dataGroupPrototype to set
     */
    public void setDataGroupPrototype(Group dataGroupPrototype) {
        this.dataGroupPrototype = dataGroupPrototype;
    }

    /**
     * @return the dataGroupPrototype
     */
    public Group getDataGroupPrototype() {
        return this.dataGroupPrototype;
    }
}