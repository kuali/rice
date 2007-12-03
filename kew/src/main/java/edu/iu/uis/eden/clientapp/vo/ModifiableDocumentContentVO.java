/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.clientapp.vo;

/**
 * Tracks changed to document content for lazy loading
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ModifiableDocumentContentVO extends DocumentContentVO {

	private static final long serialVersionUID = 5174192500065617616L;

	private boolean modified = false;
	
	public ModifiableDocumentContentVO(DocumentContentVO documentContentVO) {
		super.setRouteHeaderId(documentContentVO.getRouteHeaderId());
		super.setApplicationContent(documentContentVO.getApplicationContent());
		super.setAttributeContent(documentContentVO.getAttributeContent());
		super.setSearchableContent(documentContentVO.getSearchableContent());
		super.setAttributeDefinitions(documentContentVO.getAttributeDefinitions());
		super.setSearchableDefinitions(documentContentVO.getSearchableDefinitions());
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void resetModified() {
		modified = false;
	}

	public void addAttributeDefinition(WorkflowAttributeDefinitionVO definition) {
		modified = true;
		super.addAttributeDefinition(definition);
	}

	public void addSearchableDefinition(WorkflowAttributeDefinitionVO definition) {
		modified = true;
		super.addSearchableDefinition(definition);
	}

	public void removeAttributeDefinition(WorkflowAttributeDefinitionVO definition) {
		modified = true;
		super.removeAttributeDefinition(definition);
	}

	public void removeSearchableDefinition(WorkflowAttributeDefinitionVO definition) {
		modified = true;
		super.removeSearchableDefinition(definition);
	}

	public void setApplicationContent(String applicationContent) {
		modified = true;
		super.setApplicationContent(applicationContent);
	}

	public void setAttributeContent(String attributeContent) {
		modified = true;
		super.setAttributeContent(attributeContent);
	}

	public void setAttributeDefinitions(WorkflowAttributeDefinitionVO[] attributeDefinitions) {
		modified = true;
		super.setAttributeDefinitions(attributeDefinitions);
	}

	public void setRouteHeaderId(Long routeHeaderId) {
		modified = true;
		super.setRouteHeaderId(routeHeaderId);
	}

	public void setSearchableContent(String searchableContent) {
		modified = true;
		super.setSearchableContent(searchableContent);
	}

	public void setSearchableDefinitions(WorkflowAttributeDefinitionVO[] searchableDefinitions) {
		modified = true;
		super.setSearchableDefinitions(searchableDefinitions);
	}	
	
}
