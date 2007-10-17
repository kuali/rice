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
package edu.iu.uis.eden.actiontaken;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.UserUtils;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Model object mapped to ojb for representing actions taken on documents by 
 * users.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionTakenValue implements WorkflowPersistable {

    /**
	 *
	 */
	private static final long serialVersionUID = -81505450567067594L;
	private Long actionTakenId;
    private Long routeHeaderId;
    private String actionTaken;
    private Timestamp actionDate;
    private String annotation = "";
    private Integer docVersion;
    private Integer lockVerNbr;
    private String workflowId;
    private String delegatorWorkflowId;
    private Long delegatorWorkgroupId;
    private DocumentRouteHeaderValue routeHeader;
    private Collection actionRequests;
    private Boolean currentIndicator = new Boolean(true);
    private String actionDateString;

    public WorkflowUser getWorkflowUser() throws EdenUserNotFoundException {
      return getWorkflowUserForWorkflowId( workflowId );
    }

    public WorkflowUser getDelegatorUser() throws EdenUserNotFoundException {
      return getWorkflowUserForWorkflowId( delegatorWorkflowId );
    }

    public Workgroup getDelegatorWorkgroup() {
        return getWorkgroupService().getWorkgroup(new WorkflowGroupId(delegatorWorkgroupId));
    }

    public void setDelegator(Recipient recipient) {
        if (recipient instanceof WorkflowUser) {
            setDelegatorWorkflowId(((WorkflowUser)recipient).getWorkflowUserId().getWorkflowId());
        } else if (recipient instanceof Workgroup) {
            setDelegatorWorkgroupId(((Workgroup)recipient).getWorkflowGroupId().getGroupId());
        } else {
            setDelegatorWorkflowId(null);
            setDelegatorWorkgroupId(null);
        }
    }

    public boolean isForDelegator() {
        return getDelegatorWorkflowId() != null || getDelegatorWorkgroupId() != null;
    }

    public String getDelegatorDisplayName() throws EdenUserNotFoundException {
        if (! isForDelegator()) {
            return "";
        }
        if (getDelegatorWorkflowId() != null) {
        	// TODO this stinks to have to have a dependency on UserSession here
        	UserSession userSession = UserSession.getAuthenticatedUser();
        	if (userSession != null) {
        		return UserUtils.getDisplayableName(userSession, getDelegatorUser());
        	}
            return getDelegatorUser().getDisplayName();
        } else {
            return getDelegatorWorkgroup().getGroupNameId().getNameId();
        }
    }

    private WorkflowUser getWorkflowUserForWorkflowId( String id ) throws EdenUserNotFoundException {
      WorkflowUser w = null;

      if ( (id != null) && (id.trim().length() > 0) ) {
        w = getUserService().getWorkflowUser(new WorkflowUserId( id ));
      }

      return w;
    }

    public String getActionTakenLabel() {
        return CodeTranslator.getActionTakenLabel(actionTaken);
    }

    public Collection getActionRequests() {
        if (actionRequests == null) {
            setActionRequests(new ArrayList());
        }
        return actionRequests;
    }

    public void setActionRequests(Collection actionRequests) {
        this.actionRequests = actionRequests;
    }

    public DocumentRouteHeaderValue getRouteHeader() {
        return routeHeader;
    }

    public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.routeHeader = routeHeader;
    }

    public Timestamp getActionDate() {
        return actionDate;
    }


    public void setActionDate(Timestamp actionDate) {
        this.actionDate = actionDate;
    }


    public String getActionTaken() {
        return actionTaken;
    }


    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }


    public Long getActionTakenId() {
        return actionTakenId;
    }

    public void setActionTakenId(Long actionTakenId) {
        this.actionTakenId = actionTakenId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getDelegatorWorkflowId() {
        return delegatorWorkflowId;
    }

    public void setDelegatorWorkflowId(String delegatorWorkflowId) {
        this.delegatorWorkflowId = delegatorWorkflowId;
    }

    public Long getDelegatorWorkgroupId() {
        return delegatorWorkgroupId;
    }
    public void setDelegatorWorkgroupId(Long delegatorWorkgroupId) {
        this.delegatorWorkgroupId = delegatorWorkgroupId;
    }
    public Integer getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(Integer docVersion) {
        this.docVersion = docVersion;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public Boolean getCurrentIndicator() {
        return currentIndicator;
    }

    public void setCurrentIndicator(Boolean currentIndicator) {
        this.currentIndicator = currentIndicator;
    }

    public Collection getRootActionRequests() {
        return getActionRequestService().getRootRequests(getActionRequests());
    }

    public Object copy(boolean preserveKeys) {
        ActionTakenValue clone = new ActionTakenValue();
        try {
            BeanUtils.copyProperties(clone, this);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        if (!preserveKeys) {
            clone.setActionTakenId(null);
        }
        return clone;
    }

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }

    private WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }

    private ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
    }

    public String getActionDateString() {
        if(actionDateString == null || actionDateString.trim().equals("")){
            return EdenConstants.getDefaultDateFormat().format(getActionDate());
        } else {
            return actionDateString;
        }
    }
    public void setActionDateString(String actionDateString) {
        this.actionDateString = actionDateString;
    }

    public boolean isApproval() {
    	return EdenConstants.ACTION_TAKEN_APPROVED_CD.equals(getActionTaken());
    }

    public boolean isCompletion() {
    	return EdenConstants.ACTION_TAKEN_COMPLETED_CD.equals(getActionTaken());
    }
}