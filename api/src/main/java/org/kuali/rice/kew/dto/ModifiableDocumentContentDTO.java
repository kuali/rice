/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.dto;

/**
 * Tracks changed to document content for lazy loading
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ModifiableDocumentContentDTO extends DocumentContentDTO {

	private static final long serialVersionUID = 5174192500065617616L;

	private boolean modified = false;
	
	public ModifiableDocumentContentDTO(DocumentContentDTO documentContentVO) {
		super.setDocumentId(documentContentVO.getDocumentId());
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

	public void addAttributeDefinition(WorkflowAttributeDefinitionDTO definition) {
		modified = true;
		super.addAttributeDefinition(definition);
	}

	public void addSearchableDefinition(WorkflowAttributeDefinitionDTO definition) {
		modified = true;
		super.addSearchableDefinition(definition);
	}

	public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO definition) {
		modified = true;
		super.removeAttributeDefinition(definition);
	}

	public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO definition) {
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

	public void setAttributeDefinitions(WorkflowAttributeDefinitionDTO[] attributeDefinitions) {
		modified = true;
		super.setAttributeDefinitions(attributeDefinitions);
	}

	public void setDocumentId(String documentId) {
		modified = true;
		super.setDocumentId(documentId);
	}

	public void setSearchableContent(String searchableContent) {
		modified = true;
		super.setSearchableContent(searchableContent);
	}

	public void setSearchableDefinitions(WorkflowAttributeDefinitionDTO[] searchableDefinitions) {
		modified = true;
		super.setSearchableDefinitions(searchableDefinitions);
	}	
	
}
