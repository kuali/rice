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

/**
 * Transport object for representing the criteria for a routing report.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class ReportCriteriaVO implements Serializable {

	private static final long serialVersionUID = 4390002636101531058L;
	
	private Long routeHeaderId;
	private String targetNodeName;
    
    private UserIdVO[] targetUsers;
    private UserIdVO routingUser;
    private String documentTypeName;
    private String xmlContent;
    private String[] ruleTemplateNames;
    private String[] nodeNames;
    private ReportActionToTakeVO[] actionsToTake;

	public ReportCriteriaVO() {}
	
	public ReportCriteriaVO(Long routeHeaderId) {
		this(routeHeaderId, null);
	}
	
	public ReportCriteriaVO(Long routeHeaderId, String targetNodeName) {
		this.routeHeaderId = routeHeaderId;
        this.targetNodeName = targetNodeName;
	}
	
	public ReportCriteriaVO(String documentTypeName) {
		this.documentTypeName = documentTypeName;
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

    public UserIdVO[] getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(UserIdVO[] targetUsers) {
        this.targetUsers = targetUsers;
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

	public ReportActionToTakeVO[] getActionsToTake() {
		return actionsToTake;
	}

	public void setActionsToTake(ReportActionToTakeVO[] actionsToTake) {
		this.actionsToTake = actionsToTake;
	}

	public UserIdVO getRoutingUser() {
		return routingUser;
	}

	public void setRoutingUser(UserIdVO routingUser) {
		this.routingUser = routingUser;
	}
	
}
