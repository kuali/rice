/*
 * Copyright 2005-2008 The Kuali Foundation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.util.KEWConstants;


/**
 * Represents document content broken up into all it's pieces.  This object is used both
 * to hold the state of the document content returned by the server, as well as to accumulate
 * and marshal changes made by the client application.  In the former case, no attribute
 * definitions will be supplied in this object by the server.  In the latter case, the client
 * may add attribute content to the document content by attaching attribute definitions, which
 * will be marshaled into the document content when an action is taken by the client on the document. 
 * These definition VOs will be transient and will not be returned again when content is refreshed
 * from the server.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentContentDTO implements Serializable {

	private static final long serialVersionUID = -1008441110733007106L;
    private String attributeContent = "";
    private String applicationContent = "";
    private String searchableContent = "";
    private List<WorkflowAttributeDefinitionDTO> attributeDefinitions = new ArrayList<WorkflowAttributeDefinitionDTO>();
    private List<WorkflowAttributeDefinitionDTO> searchableDefinitions = new ArrayList<WorkflowAttributeDefinitionDTO>();
    private String documentId;
    
    private static final WorkflowAttributeDefinitionDTO[] ARRAY_TYPE = new WorkflowAttributeDefinitionDTO[0];
    
    public DocumentContentDTO() {}    
    
    public String getApplicationContent() {
        return applicationContent;
    }
    public void setApplicationContent(String applicationContent) {
        this.applicationContent = applicationContent;
    }
    public String getAttributeContent() {
        return attributeContent;
    }
    public void setAttributeContent(String attributeContent) {
        this.attributeContent = attributeContent;
    }
    public String getSearchableContent() {
        return searchableContent;
    }
    public void setSearchableContent(String searchableContent) {
        this.searchableContent = searchableContent;
    }
    public String getFullContent() {
        StringBuffer fullContent = new StringBuffer();
        fullContent.append("<").append(KEWConstants.DOCUMENT_CONTENT_ELEMENT).append(">");
        if (!isEmpty(getApplicationContent())) {
            fullContent.append("<").append(KEWConstants.APPLICATION_CONTENT_ELEMENT).append(">");
            fullContent.append(getApplicationContent());
            fullContent.append("</").append(KEWConstants.APPLICATION_CONTENT_ELEMENT).append(">");        	
        }
        fullContent.append(getAttributeContent());
        fullContent.append(getSearchableContent());
        fullContent.append("</").append(KEWConstants.DOCUMENT_CONTENT_ELEMENT).append(">");
        return fullContent.toString();
    }
    
    public WorkflowAttributeDefinitionDTO[] getAttributeDefinitions() {
        return (WorkflowAttributeDefinitionDTO[])attributeDefinitions.toArray(ARRAY_TYPE);
    }
    public void setAttributeDefinitions(WorkflowAttributeDefinitionDTO[] attributeDefinitions) {
        this.attributeDefinitions = new ArrayList<WorkflowAttributeDefinitionDTO>();
        for (int index = 0; index < attributeDefinitions.length; index++) {
            this.attributeDefinitions.add(attributeDefinitions[index]);
        }
    }
    public WorkflowAttributeDefinitionDTO[] getSearchableDefinitions() {
        return (WorkflowAttributeDefinitionDTO[])searchableDefinitions.toArray(ARRAY_TYPE);
    }
    public void setSearchableDefinitions(WorkflowAttributeDefinitionDTO[] searchableDefinitions) {
        this.searchableDefinitions = new ArrayList<WorkflowAttributeDefinitionDTO>();
        for (int index = 0; index < searchableDefinitions.length; index++) {
            this.searchableDefinitions.add(searchableDefinitions[index]);
        }
    }
    
    public void addAttributeDefinition(WorkflowAttributeDefinitionDTO definition) {
        attributeDefinitions.add(definition);
    }
    
    public void removeAttributeDefinition(WorkflowAttributeDefinitionDTO definition) {
        attributeDefinitions.remove(definition);
    }
    
    public void addSearchableDefinition(WorkflowAttributeDefinitionDTO definition) {
        searchableDefinitions.add(definition);
    }
    
    public void removeSearchableDefinition(WorkflowAttributeDefinitionDTO definition) {
        searchableDefinitions.remove(definition);
    }
    
    public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	private boolean isEmpty(String value) {
        return value == null || value.trim().equals("");
    }

}
