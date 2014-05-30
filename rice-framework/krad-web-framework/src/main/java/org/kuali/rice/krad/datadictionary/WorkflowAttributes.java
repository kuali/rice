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
package org.kuali.rice.krad.datadictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;

/**
 * A container that holds all of the {@link WorkflowAttributeDefinition} for a document for both document searches
 * and routing that depends on the values that exist on the document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "workflowAttributes")
public class WorkflowAttributes extends UifDictionaryBeanBase {
    private static final long serialVersionUID = 6435015497886060280L;

    private List<SearchingTypeDefinition> searchingTypeDefinitions;
    private Map<String, RoutingTypeDefinition> routingTypeDefinitions;

    public WorkflowAttributes() {
        searchingTypeDefinitions = new ArrayList<SearchingTypeDefinition>();
        routingTypeDefinitions = new HashMap<String, RoutingTypeDefinition>();
    }

    /**
     * @return the searchingTypeDefinitions
     */
    @BeanTagAttribute(name = "searchingTypeDefinitions", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<SearchingTypeDefinition> getSearchingTypeDefinitions() {
        return this.searchingTypeDefinitions;
    }

    /**
     * @param searchingTypeDefinitions the searchingTypeDefinitions to set
     */
    public void setSearchingTypeDefinitions(List<SearchingTypeDefinition> searchingTypeDefinitions) {
        this.searchingTypeDefinitions = searchingTypeDefinitions;
    }

    @BeanTagAttribute(name = "routingTypeDefinitions", type = BeanTagAttribute.AttributeType.MAPBEAN)
    public Map<String, RoutingTypeDefinition> getRoutingTypeDefinitions() {
        return this.routingTypeDefinitions;
    }

    public void setRoutingTypeDefinitions(Map<String, RoutingTypeDefinition> routingTypeDefinitions) {
        this.routingTypeDefinitions = routingTypeDefinitions;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#dataDictionaryPostProcessing()
     */
    @Override
    public void dataDictionaryPostProcessing() {
        for (SearchingTypeDefinition definition : searchingTypeDefinitions) {
            definition.dataDictionaryPostProcessing();
        }
        for (RoutingTypeDefinition definition : routingTypeDefinitions.values()) {
            definition.dataDictionaryPostProcessing();
        }
    }

    public void completeValidation(Class<?> rootBusinessObjectClass, Class<?> otherBusinessObjectClass, ValidationTrace tracer) {
        for (SearchingTypeDefinition definition : searchingTypeDefinitions) {
            definition.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass,tracer);
        }
        for (RoutingTypeDefinition definition : routingTypeDefinitions.values()) {
            definition.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass,tracer);
        }
    }

}
