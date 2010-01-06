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

import java.io.Serializable;

/**
 * Transport object for representing the criteria for a routing report.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReportCriteriaDTO implements Serializable {

	private static final long serialVersionUID = 4390002636101531058L;
	
	private Long routeHeaderId;
	private String targetNodeName;
    
    private String[] targetPrincipalIds;
    private String routingPrincipalId;
    private String documentTypeName;
    private String xmlContent;
    private String[] ruleTemplateNames;
    private String[] nodeNames;
    private ReportActionToTakeDTO[] actionsToTake;
    private Boolean activateRequests = null;
    private boolean flattenNodes = false;

	public ReportCriteriaDTO() {}
	
	public ReportCriteriaDTO(Long routeHeaderId) {
		this(routeHeaderId, null);
	}
	
	public ReportCriteriaDTO(Long routeHeaderId, String targetNodeName) {
		this.routeHeaderId = routeHeaderId;
        this.targetNodeName = targetNodeName;
	}
	
	public ReportCriteriaDTO(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}
		
	public Boolean getActivateRequests() {
        return this.activateRequests;
    }

    public void setActivateRequests(Boolean activateRequests) {
        this.activateRequests = activateRequests;
    }

    public Long getRouteHeaderId() {
		return routeHeaderId;
	}

	public void setRouteHeaderId(Long routeHeaderId) {
		this.routeHeaderId = routeHeaderId;
	}

	public String getTargetNodeName() {
        return targetNodeName;
    }

    public void setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
    }

    public String toString() {
		return super.toString()+"[routeHeaderId="+routeHeaderId+
		",targetNodeName="+targetNodeName+"]";
	}

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String[] getRuleTemplateNames() {
        return ruleTemplateNames;
    }

    public void setRuleTemplateNames(String[] ruleTemplateNames) {
        this.ruleTemplateNames = ruleTemplateNames;
    }

    public String[] getTargetPrincipalIds() {
        return targetPrincipalIds;
    }

    public void setTargetPrincipalIds(String[] targetPrincipalIds) {
        this.targetPrincipalIds = targetPrincipalIds;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }
    
	public String[] getNodeNames() {
		return nodeNames;
	}

	public void setNodeNames(String[] nodeNames) {
		this.nodeNames = nodeNames;
	}

	public ReportActionToTakeDTO[] getActionsToTake() {
		return actionsToTake;
	}

	public void setActionsToTake(ReportActionToTakeDTO[] actionsToTake) {
		this.actionsToTake = actionsToTake;
	}

	public String getRoutingPrincipalId() {
		return routingPrincipalId;
	}

	public void setRoutingPrincipalId(String routingPrincipalId) {
		this.routingPrincipalId = routingPrincipalId;
	}

	public boolean isFlattenNodes() {
		return this.flattenNodes;
	}

	public void setFlattenNodes(boolean flattenNodes) {
		this.flattenNodes = flattenNodes;
	}
	
}
