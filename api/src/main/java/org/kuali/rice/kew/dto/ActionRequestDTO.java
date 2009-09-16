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
import java.util.Calendar;

import org.kuali.rice.kew.util.KEWConstants;


/**
 * A transport object representing an ActionRequestValue.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionRequestDTO implements Serializable {

    private final static String ACKNOWLEDGE_REQ = "K";
    private final static String FYI_REQ = "F";
    private final static String APPROVE_REQ = "A";
    private final static String COMPLETE_REQ = "C";

    static final long serialVersionUID = 1074824814950100121L;
    private Long actionRequestId;
    private String actionRequested;
    private String status;
    private Boolean currentIndicator = Boolean.TRUE;
    private Calendar dateCreated;
    private Long responsibilityId;
    private Long routeHeaderId;
    private String routeMethodName;
    private Integer priority;
    private String annotation;
    private Long actionTakenId;
    private String groupId;    
    private String recipientTypeCd;
    private String approvePolicy;
    private String responsibilityDesc;
    private Integer routeLevel;
    private Integer docVersion;
    private String roleName;
    private Boolean forceAction;
    private String principalId;
    private String delegationType;
    private Long parentActionRequestId;
    private String qualifiedRoleName;
    private String qualifiedRoleNameLabel;
    private ActionRequestDTO[] childrenRequests;
    private ActionTakenDTO actionTaken;
    private String nodeName;
    private Long nodeInstanceId;
    private String requestLabel;

    public ActionRequestDTO() {}

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getActionRequested() {
        return actionRequested;
    }

    public Long getActionRequestId() {
        return actionRequestId;
    }

    public Long getActionTakenId() {
        return actionTakenId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public Integer getDocVersion() {
        return docVersion;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getResponsibilityDesc() {
        return responsibilityDesc;
    }

    public Long getResponsibilityId() {
        return responsibilityId;
    }

    public Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public Integer getRouteLevel() {
        return routeLevel;
    }

    public String getRouteMethodName() {
        return routeMethodName;
    }

    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }

    public void setRouteMethodName(String routeMethodName) {
        this.routeMethodName = routeMethodName;
    }

    public void setRouteLevel(Integer routeLevel) {
        this.routeLevel = routeLevel;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    public void setResponsibilityDesc(String responsibilityDesc) {
        this.responsibilityDesc = responsibilityDesc;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setDocVersion(Integer docVersion) {
        this.docVersion = docVersion;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setActionTakenId(Long actionTakenId) {
        this.actionTakenId = actionTakenId;
    }

    public void setActionRequestId(Long actionRequestId) {
        this.actionRequestId = actionRequestId;
    }

    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }

    public String getRecipientTypeCd() {
        return recipientTypeCd;
    }

    public void setRecipientTypeCd(String recipientTypeCd) {
        this.recipientTypeCd = recipientTypeCd;
    }

    public String getApprovePolicy() {
        return approvePolicy;
    }

    public void setApprovePolicy(String approvePolicy) {
        this.approvePolicy = approvePolicy;
    }

    public Boolean getForceAction() {
        return forceAction;
    }

    public boolean isNotificationRequest() {
        return isAcknowledgeRequest() || isFyiRequest();
    }

    public boolean isApprovalRequest() {
        return APPROVE_REQ.equals(actionRequested) || COMPLETE_REQ.equals(actionRequested);
    }

    public Boolean isForceAction() {
        return forceAction;
    }

    public void setForceAction(Boolean forceAction) {
        this.forceAction = forceAction;
    }

    public boolean isAcknowledgeRequest() {
        return ACKNOWLEDGE_REQ.equals(actionRequested);
    }

    public boolean isFyiRequest() {
        return FYI_REQ.equals(actionRequested);
    }

    public boolean isPending() {
        return isInitialized() || isActivated();
    }

    public boolean isCompleteRequest() {
        return KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequested);
    }

    public boolean isInitialized() {
        return KEWConstants.ACTION_REQUEST_INITIALIZED.equals(status);
    }

    public boolean isActivated() {
        return KEWConstants.ACTION_REQUEST_ACTIVATED.equals(status);
    }

    public boolean isDone() {
        return KEWConstants.ACTION_REQUEST_DONE_STATE.equals(status);
    }

    public boolean isUserRequest() {
        return KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD.equals(getRecipientTypeCd());
    }

    public boolean isGroupRequest() {
        return KEWConstants.ACTION_REQUEST_GROUP_RECIPIENT_CD.equals(getRecipientTypeCd());
    }

    public boolean isRoleRequest() {
        return KEWConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD.equals(getRecipientTypeCd());
    }

    public String getPrincipalId() {
		return this.principalId;
	}

	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

	public Boolean getCurrentIndicator() {
        return currentIndicator;
    }

    public void setCurrentIndicator(Boolean currentIndicator) {
        this.currentIndicator = currentIndicator;
    }

    public String getDelegationType() {
        return delegationType;
    }
    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }

    public Long getParentActionRequestId() {
        return parentActionRequestId;
    }
    public void setParentActionRequestId(Long parentActionRequestId) {
        this.parentActionRequestId = parentActionRequestId;
    }

    public String getQualifiedRoleName() {
        return qualifiedRoleName;
    }
    public void setQualifiedRoleName(String qualifiedRoleName) {
        this.qualifiedRoleName = qualifiedRoleName;
    }
    public String getQualifiedRoleNameLabel() {
        return qualifiedRoleNameLabel;
    }
    public void setQualifiedRoleNameLabel(String qualifiedRoleNameLabel) {
        this.qualifiedRoleNameLabel = qualifiedRoleNameLabel;
    }

	public ActionTakenDTO getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(ActionTakenDTO actionTaken) {
		this.actionTaken = actionTaken;
	}

    public ActionRequestDTO[] getChildrenRequests() {
        return childrenRequests;
    }
    public void setChildrenRequests(ActionRequestDTO[] childrenRequests) {
        this.childrenRequests = childrenRequests;
    }

    public void addChildRequest(ActionRequestDTO childRequest) {
    	if (getChildrenRequests() == null) {
    		setChildrenRequests(new ActionRequestDTO[0]);
    	}
    	ActionRequestDTO[] newChildrenRequests = new ActionRequestDTO[getChildrenRequests().length+1];
    	System.arraycopy(getChildrenRequests(), 0, newChildrenRequests, 0, getChildrenRequests().length);
    	newChildrenRequests[getChildrenRequests().length] = childRequest;
    	setChildrenRequests(newChildrenRequests);
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Long getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(Long nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }
    
    public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

    public boolean isAdHocRequest() {
    	return KEWConstants.ADHOC_REQUEST_RESPONSIBILITY_ID.equals(getResponsibilityId());
    }

    public boolean isGeneratedRequest() {
    	return KEWConstants.MACHINE_GENERATED_RESPONSIBILITY_ID.equals(getResponsibilityId());
    }

    public boolean isExceptionRequest() {
    	return KEWConstants.EXCEPTION_REQUEST_RESPONSIBILITY_ID.equals(getResponsibilityId());
    }

    public boolean isRouteModuleRequest() {
    	return getResponsibilityId().longValue() > 0;
    }

	public String getRequestLabel() {
		return this.requestLabel;
	}

	public void setRequestLabel(String requestLabel) {
		this.requestLabel = requestLabel;
	}

}
