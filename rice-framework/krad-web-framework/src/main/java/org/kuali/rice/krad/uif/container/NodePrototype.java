/**
 * Copyright 2005-2013 The Kuali Foundation
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

import com.google.common.collect.Maps;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.element.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@BeanTag(name = "nodePrototype-bean")
public class NodePrototype extends UifDictionaryBeanBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private Message labelPrototype;
    private Group dataGroupPrototype;

    public NodePrototype() {

    }

    /**
     * @param labelPrototype the labelPrototype to set
     */
    public void setLabelPrototype(Message labelPrototype) {
        this.labelPrototype = labelPrototype;
    }

    /**
     * @return the labelPrototype
     */
    @BeanTagAttribute(name="labelPrototype",type= BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Message getLabelPrototype() {
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
    @BeanTagAttribute(name="dataGroupPrototype",type= BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Group getDataGroupPrototype() {
        return this.dataGroupPrototype;
    }

    public <T> T copy() {
        T copiedClass = null;
        try {
            copiedClass = (T)this.getClass().newInstance();
        }
        catch(Exception exception) {
            throw new RuntimeException();
        }

        copyProperties(copiedClass);

        return copiedClass;
    }

    protected <T> void copyProperties(T nodePrototype) {
        NodePrototype nodePrototypeCopy = (NodePrototype) nodePrototype;

        if(this.labelPrototype != null)  {
            nodePrototypeCopy.setLabelPrototype((Message)this.getLabelPrototype().copy());
        }

        if(this.dataGroupPrototype != null)  {
            nodePrototypeCopy.setDataGroupPrototype((Group)this.getDataGroupPrototype().copy());
        }

        //DictionaryBeanBase properties
        nodePrototypeCopy.setComponentCode(this.getComponentCode());
        nodePrototypeCopy.setNamespaceCode(this.getNamespaceCode());

        //UifDictionaryBeanBase properties
        Map<String, String> expressionGraphCopy = Maps.newHashMapWithExpectedSize(this.getExpressionGraph().size());
        for(Map.Entry expressionGraphEntry : getExpressionGraph().entrySet()) {
            expressionGraphCopy.put(expressionGraphEntry.getKey().toString(),expressionGraphEntry.getValue().toString());
        }
        nodePrototypeCopy.setExpressionGraph(expressionGraphCopy);

        Map<String, String> refreshExpressionGraphCopy = Maps.newHashMapWithExpectedSize(this.getRefreshExpressionGraph().size());
        for(Map.Entry refreshExpressionGraphEntry : getRefreshExpressionGraph().entrySet()) {
            expressionGraphCopy.put(refreshExpressionGraphEntry.getKey().toString(),refreshExpressionGraphEntry.getValue().toString());
        }
        nodePrototypeCopy.setRefreshExpressionGraph(refreshExpressionGraphCopy);

        Map<String, String> propertyExpressionsCopy = Maps.newHashMapWithExpectedSize(this.getPropertyExpressions().size());
        for(Map.Entry propertyExpressionsEntry : getPropertyExpressions().entrySet()) {
            propertyExpressionsCopy.put(propertyExpressionsEntry.getKey().toString(),propertyExpressionsEntry.getValue().toString());
        }
        nodePrototypeCopy.setRefreshExpressionGraph(propertyExpressionsCopy);
    }
}
