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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.util.Utilities;

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
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class DocumentContentVO implements Serializable {

	private static final long serialVersionUID = -1008441110733007106L;
    private String attributeContent = "";
    private String applicationContent = "";
    private String searchableContent = "";
    private List<WorkflowAttributeDefinitionVO> attributeDefinitions = new ArrayList<WorkflowAttributeDefinitionVO>();
    private List<WorkflowAttributeDefinitionVO> searchableDefinitions = new ArrayList<WorkflowAttributeDefinitionVO>();
    private Long routeHeaderId;
    
    private static final WorkflowAttributeDefinitionVO[] ARRAY_TYPE = new WorkflowAttributeDefinitionVO[0];
    
    public DocumentContentVO() {}    
    
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
        fullContent.append("<").append(EdenConstants.DOCUMENT_CONTENT_ELEMENT).append(">");
        if (!Utilities.isEmpty(getApplicationContent())) {
            fullContent.append("<").append(EdenConstants.APPLICATION_CONTENT_ELEMENT).append(">");
            fullContent.append(getApplicationContent());
            fullContent.append("</").append(EdenConstants.APPLICATION_CONTENT_ELEMENT).append(">");        	
        }
        fullContent.append(getAttributeContent());
        fullContent.append(getSearchableContent());
        fullContent.append("</").append(EdenConstants.DOCUMENT_CONTENT_ELEMENT).append(">");
        return fullContent.toString();
    }
    
    public WorkflowAttributeDefinitionVO[] getAttributeDefinitions() {
        return (WorkflowAttributeDefinitionVO[])attributeDefinitions.toArray(ARRAY_TYPE);
    }
    public void setAttributeDefinitions(WorkflowAttributeDefinitionVO[] attributeDefinitions) {
        this.attributeDefinitions = new ArrayList<WorkflowAttributeDefinitionVO>();
        for (int index = 0; index < attributeDefinitions.length; index++) {
            this.attributeDefinitions.add(attributeDefinitions[index]);
        }
    }
    public WorkflowAttributeDefinitionVO[] getSearchableDefinitions() {
        return (WorkflowAttributeDefinitionVO[])searchableDefinitions.toArray(ARRAY_TYPE);
    }
    public void setSearchableDefinitions(WorkflowAttributeDefinitionVO[] searchableDefinitions) {
        this.searchableDefinitions = new ArrayList<WorkflowAttributeDefinitionVO>();
        for (int index = 0; index < searchableDefinitions.length; index++) {
            this.searchableDefinitions.add(searchableDefinitions[index]);
        }
    }
    
    public void addAttributeDefinition(WorkflowAttributeDefinitionVO definition) {
        attributeDefinitions.add(definition);
    }
    
    public void removeAttributeDefinition(WorkflowAttributeDefinitionVO definition) {
        attributeDefinitions.remove(definition);
    }
    
    public void addSearchableDefinition(WorkflowAttributeDefinitionVO definition) {
        searchableDefinitions.add(definition);
    }
    
    public void removeSearchableDefinition(WorkflowAttributeDefinitionVO definition) {
        searchableDefinitions.remove(definition);
    }
    
    public Long getRouteHeaderId() {
		return routeHeaderId;
	}
	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}

}
