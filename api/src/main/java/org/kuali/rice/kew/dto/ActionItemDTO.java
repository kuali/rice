/*
 * Copyright 2007-2008 The Kuali Foundation
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
import java.sql.Timestamp;

/**
 * A transport object representing an ActionItem
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionItemDTO implements Serializable {

    private static final long serialVersionUID = -4545795435037464863L;

    private Long actionItemId;
    private Timestamp dateAssigned;
    private String actionRequestCd;
    private Long actionRequestId;
    private Long routeHeaderId;
    private String docTitle;
    private String docLabel;
    private String docHandlerURL;
    private String docName;
    private Long responsibilityId;
    private String roleName;
    private String dateAssignedString;
    private String actionToTake;
    private String delegationType;
    private Integer actionItemIndex;

    /**
     * Kim group id of the target group (if any... group object will be null if groupId is empty)
     */
    private String groupId;

    /**
     * Principal id of the target user (if any... user object will be null if workflowId is empty)
     */
    private String principalId;
    
    /**
     * Group id of the target delegator group (if any... delegatorgroup object will be null if delegatorgroupId is empty)
     */
    private String delegatorGroupId;

    /**
     * Principal id of the target delegator user (if any... delegatorUser object will be null if delegatorWorkflowId is empty)
     */
    private String delegatorPrincipalId;

    public Long getActionItemId() {
        return this.actionItemId;
    }
    public void setActionItemId(Long actionItemId) {
        this.actionItemId = actionItemId;
    }
    public Timestamp getDateAssigned() {
        return this.dateAssigned;
    }
    public void setDateAssigned(Timestamp dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
    public String getActionRequestCd() {
        return this.actionRequestCd;
    }
    public void setActionRequestCd(String actionRequestCd) {
        this.actionRequestCd = actionRequestCd;
    }
    public Long getActionRequestId() {
        return this.actionRequestId;
    }
    public void setActionRequestId(Long actionRequestId) {
        this.actionRequestId = actionRequestId;
    }
    public Long getRouteHeaderId() {
        return this.routeHeaderId;
    }
    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }
    public String getDocTitle() {
        return this.docTitle;
    }
    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }
    public String getDocLabel() {
        return this.docLabel;
    }
    public void setDocLabel(String docLabel) {
        this.docLabel = docLabel;
    }
    public String getDocHandlerURL() {
        return this.docHandlerURL;
    }
    public void setDocHandlerURL(String docHandlerURL) {
        this.docHandlerURL = docHandlerURL;
    }
    public String getDocName() {
        return this.docName;
    }
    public void setDocName(String docName) {
        this.docName = docName;
    }
    public Long getResponsibilityId() {
        return this.responsibilityId;
    }
    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }
    public String getRoleName() {
        return this.roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public String getDateAssignedString() {
        return this.dateAssignedString;
    }
    public void setDateAssignedString(String dateAssignedString) {
        this.dateAssignedString = dateAssignedString;
    }
    public String getActionToTake() {
        return this.actionToTake;
    }
    public void setActionToTake(String actionToTake) {
        this.actionToTake = actionToTake;
    }
    public String getDelegationType() {
        return this.delegationType;
    }
    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }
    public Integer getActionItemIndex() {
        return this.actionItemIndex;
    }
    public void setActionItemIndex(Integer actionItemIndex) {
        this.actionItemIndex = actionItemIndex;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getPrincipalId() {
        return this.principalId;
    }
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
    public String getDelegatorGroupId() {
        return this.delegatorGroupId;
    }
    public void setDelegatorGroupId(String delegatorGroupId) {
        this.delegatorGroupId = delegatorGroupId;
    }
    public String getDelegatorPrincipalId() {
        return this.delegatorPrincipalId;
    }
    public void setDelegatorPrincipalId(String delegatorPrincipalId) {
        this.delegatorPrincipalId = delegatorPrincipalId;
    }

}
