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
package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.element.Message;

import java.io.Serializable;

/**
 * Configures nodes for a {@link org.kuali.rice.krad.uif.container.TreeGroup}
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "nodePrototype")
public class NodePrototype extends UifDictionaryBeanBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private Message labelPrototype;
    private Group dataGroupPrototype;

    public NodePrototype() {
        super();
    }

    /**
     * @return the labelPrototype
     */
    @BeanTagAttribute(type= BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Message getLabelPrototype() {
        return this.labelPrototype;
    }

    /**
     * @param labelPrototype the labelPrototype to set
     */
    public void setLabelPrototype(Message labelPrototype) {
        this.labelPrototype = labelPrototype;
    }

    /**
     * @return the dataGroupPrototype
     */
    @BeanTagAttribute(type= BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Group getDataGroupPrototype() {
        return this.dataGroupPrototype;
    }

    /**
     * @param dataGroupPrototype the dataGroupPrototype to set
     */
    public void setDataGroupPrototype(Group dataGroupPrototype) {
        this.dataGroupPrototype = dataGroupPrototype;
    }

}
