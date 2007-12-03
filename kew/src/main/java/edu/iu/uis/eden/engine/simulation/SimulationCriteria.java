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
package edu.iu.uis.eden.engine.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;

/**
 * Criteria which acts as input to the {@link SimulationEngine}.
 *
 * @see SimulationEngine
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimulationCriteria {

	// fields related to document simulation
	private Long documentId;
    private String destinationNodeName;
    private List destinationRecipients = new ArrayList();
    
    // fields related to document type simulation
    private String documentTypeName;
    private String xmlContent;
    private List nodeNames = new ArrayList();
    private List ruleTemplateNames = new ArrayList();
    
    // fields related to both simulation types
    private WorkflowUser routingUser;
    private List actionsToTake = new ArrayList();
    
    public SimulationCriteria() {}
    
    public SimulationCriteria(Long documentId) {
    	this.documentId = documentId;
    }
    
    public SimulationCriteria(String documentTypeName) {
    	this.documentTypeName = documentTypeName;
    }
    
    public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDestinationNodeName() {
        return destinationNodeName;
    }

    public void setDestinationNodeName(String destinationNodeName) {
        this.destinationNodeName = destinationNodeName;
    }

    public List getDestinationRecipients() {
        return destinationRecipients;
    }

    public void setDestinationRecipients(List destinationRecipients) {
        this.destinationRecipients = destinationRecipients;
    }

	public String getDocumentTypeName() {
		return documentTypeName;
	}

	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}

	public List getRuleTemplateNames() {
		return ruleTemplateNames;
	}

	public void setRuleTemplateNames(List ruleTemplateNames) {
		this.ruleTemplateNames = ruleTemplateNames;
	}

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}
	
	public List getNodeNames() {
		return nodeNames;
	}

	public void setNodeNames(List nodeNames) {
		this.nodeNames = nodeNames;
	}

	public boolean isDocumentSimulation() {
		return documentId != null;
	}
	
	public boolean isDocumentTypeSimulation() {
		return !Utilities.isEmpty(documentTypeName);
	}

	public List getActionsToTake() {
		return actionsToTake;
	}

	public void setActionsToTake(List actionsToTake) {
		this.actionsToTake = actionsToTake;
	}

	public WorkflowUser getRoutingUser() {
		return routingUser;
	}

	public void setRoutingUser(WorkflowUser routingUser) {
		this.routingUser = routingUser;
	}
    
}
