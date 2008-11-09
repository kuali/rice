/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.datadictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container that holds all of the {@link WorkflowAttributeDefinition} for a document for both document searches
 * and routing that depends on the values that exist on the document.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowAttributes extends DataDictionaryDefinitionBase {
    private Map<String, WorkflowAttributeDefinition> searchingAttributeDefinitions;
    private Map<String, List<WorkflowAttributeDefinition>> routingTypeDefinitions;
    
    public WorkflowAttributes() {
    	searchingAttributeDefinitions = new HashMap<String, WorkflowAttributeDefinition>();
    	routingTypeDefinitions = new HashMap<String, List<WorkflowAttributeDefinition>>();
    }
    
	public Map<String, WorkflowAttributeDefinition> getSearchingAttributeDefinitions() {
		return this.searchingAttributeDefinitions;
	}


	public void setSearchingAttributeDefinitions(
			Map<String, WorkflowAttributeDefinition> searchingAttributeDefinitions) {
		this.searchingAttributeDefinitions = searchingAttributeDefinitions;
	}


	public Map<String, List<WorkflowAttributeDefinition>> getRoutingTypeDefinitions() {
		return this.routingTypeDefinitions;
	}


	public void setRoutingTypeDefinitions(
			Map<String, List<WorkflowAttributeDefinition>> routingTypeDefinitions) {
		this.routingTypeDefinitions = routingTypeDefinitions;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
	 */
	public void completeValidation(Class rootBusinessObjectClass,
			Class otherBusinessObjectClass) {
		for (WorkflowAttributeDefinition definition : searchingAttributeDefinitions.values()) { 
			definition.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);
		}
		for (List<WorkflowAttributeDefinition> definitionsList : routingTypeDefinitions.values()) {
			if (definitionsList != null) {
				for (WorkflowAttributeDefinition definition : definitionsList) {
					definition.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);
				}
			}
		}
	}
}
