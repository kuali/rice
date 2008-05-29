/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.actionitem;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.web.RowStyleable;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * This is the model for action items. These are displayed as the action list as well.  Mapped to ActionItemService.
 * NOTE: This object contains denormalized fields that have been copied from related ActionRequestValue and DocumentRouteHeaderValue
 * objects for performance reasons.  These should be preserved and their related objects should not be added to the OJB
 * mapping as we do not want them loaded for each ActionItem object.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
@Entity
@Table(name="EN_ACTN_ITM_T")
public class ActionItem implements WorkflowPersistable, RowStyleable {

    private static final long serialVersionUID = -1079562205125660151L;

	@Id
	@Column(name="ACTN_ITM_ID")
	private Long actionItemId;
    @Column(name="ACTN_ITM_PRSN_EN_ID")
	private String workflowId;
	@Column(name="ACTN_ITM_ASND_DT")
	private Timestamp dateAssigned;
    @Column(name="ACTN_ITM_RQST_CD")
	private String actionRequestCd;
    @Column(name="ACTN_RQST_ID", nullable=false)
	private Long actionRequestId;
    @Column(name="DOC_HDR_ID")
	private Long routeHeaderId;
    @Column(name="WRKGRP_ID")
	private Long workgroupId;
    @Column(name="DOC_TTL")
	private String docTitle;
    @Column(name="DOC_TYP_LBL_TXT")
	private String docLabel;
    @Column(name="DOC_TYP_HDLR_URL_ADDR")
	private String docHandlerURL;
    @Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
    @Column(name="DOC_TYP_NM")
	private String docName;
    @Column(name="ACTN_ITM_RESP_ID")
    private Long responsibilityId = new Long(1);
    @Transient
    private String rowStyleClass;
    @Column(name="ROLE_NM")
	private String roleName;
    @Column(name="ACTN_ITM_DLGN_PRSN_EN_ID")
	private String delegatorWorkflowId;
    @Column(name="ACTN_ITM_DLGN_WRKGRP_ID")
	private Long delegatorWorkgroupId;
    @Transient
    private String dateAssignedString;
    @Transient
    private String actionToTake;
    @Column(name="DLGN_TYP")
	private String delegationType;
    @Transient
    private Integer actionItemIndex;
    @Transient
    private Map customActions = new HashMap();
    
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DOC_HDR_ID")
	private transient DocumentRouteHeaderValue routeHeader;
    
    @Column(name="DTYPE", insertable=false, updatable=false, nullable=true)
    private String dtype = "ActionItem";

    private Workgroup getWorkgroup(Long workgroupId) {
        return KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId)); 
    }
    
    public Workgroup getWorkgroup() {
        return getWorkgroup(workgroupId);
    }
    
    public Workgroup getDelegatorWorkgroup() {
        return getWorkgroup(delegatorWorkgroupId);
    }

    private WorkflowUser getUser(String workflowId) throws EdenUserNotFoundException {
        return KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(workflowId));
    }
    
    public WorkflowUser getUser() throws EdenUserNotFoundException {
        return getUser(workflowId);
    }
    
    public WorkflowUser getDelegatorUser() throws EdenUserNotFoundException {
        return getUser(delegatorWorkflowId);
    }

    public String getRecipientTypeCode() {
        String recipientTypeCode = EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD;
        if (getRoleName() != null) {
            recipientTypeCode = EdenConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD;
        }
        if (getWorkgroupId() != null) {
            recipientTypeCode = EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD;
        }
        return recipientTypeCode;
    }

    public boolean isWorkgroupItem() {
        return getWorkgroupId() != null;
    }

    public Object copy(boolean preserveKeys) {
        ActionItem clone = new ActionItem();
        try {
            BeanUtils.copyProperties(clone, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    public void setRowStyleClass(String rowStyleClass) {
        this.rowStyleClass = rowStyleClass;
    }

    public String getRowStyleClass() {
        return rowStyleClass;
    }

    public Long getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getActionRequestLabel() {
        return CodeTranslator.getActionRequestLabel(getActionRequestCd());
    }

    public DocumentRouteHeaderValue getRouteHeader() {
        return routeHeader;
    }

    public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.routeHeader = routeHeader;
    }

    public String getActionRequestCd() {
        return actionRequestCd;
    }

    public void setActionRequestCd(String actionRequestCd) {
        this.actionRequestCd = actionRequestCd;
    }

    public Timestamp getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Timestamp dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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

    public Long getActionItemId() {
        return actionItemId;
    }

    public void setActionItemId(Long actionItemId) {
        this.actionItemId = actionItemId;
    }

    public Long getActionRequestId() {
        return actionRequestId;
    }

    public void setActionRequestId(Long actionRequestId) {
        this.actionRequestId = actionRequestId;
    }

    public String getDocHandlerURL() {
        return docHandlerURL;
    }

    public void setDocHandlerURL(String docHandlerURL) {
        this.docHandlerURL = docHandlerURL;
    }

    public Long getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(Long workgroupId) {
        this.workgroupId = workgroupId;
    }

    public String getDocLabel() {
        return docLabel;
    }

    public void setDocLabel(String docLabel) {
        this.docLabel = docLabel;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
    
    public String getDateAssignedString() {
        if(dateAssignedString == null || dateAssignedString.trim().equals("")){
            return EdenConstants.getDefaultDateFormat().format(getDateAssigned());
        } else {
            return dateAssignedString;
        }
    }
    public void setDateAssignedString(String dateAssignedString) {
        this.dateAssignedString = dateAssignedString;
    }
    
    public String getActionToTake() {
        return actionToTake;
    }

    public void setActionToTake(String actionToTake) {
        this.actionToTake = actionToTake;
    }

    public Integer getActionItemIndex() {
        return actionItemIndex;
    }

    public void setActionItemIndex(Integer actionItemIndex) {
        this.actionItemIndex = actionItemIndex;
    }

    public Map getCustomActions() {
        return customActions;
    }

    public void setCustomActions(Map customActions) {
        this.customActions = customActions;
    }

    public String getDelegationType() {
        return delegationType;
    }

    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }
    
    public String toString() {
        return new ToStringBuilder(this).append("actionItemId", actionItemId)
                                        .append("workflowId", workflowId)
                                        .append("actionItemId", actionItemId)
                                        .append("workflowId", workflowId)
                                        .append("dateAssigned", dateAssigned)
                                        .append("actionRequestCd", actionRequestCd)
                                        .append("actionRequestId", actionRequestId)
                                        .append("routeHeaderId", routeHeaderId)
                                        .append("workgroupId", workgroupId)
                                        .append("docTitle", docTitle)
                                        .append("docLabel", docLabel)
                                        .append("docHandlerURL", docHandlerURL)
                                        .append("lockVerNbr", lockVerNbr)
                                        .append("docName", docName)
                                        .append("responsibilityId", responsibilityId)
                                        .append("rowStyleClass", rowStyleClass)
                                        .append("roleName", roleName)
                                        .append("delegatorWorkflowId", delegatorWorkflowId)
                                        .append("delegatorWorkgroupId", delegatorWorkgroupId)
                                        .append("dateAssignedString", dateAssignedString)
                                        .append("actionToTake", actionToTake)
                                        .append("delegationType", delegationType)
                                        .append("actionItemIndex", actionItemIndex)
                                        .append("customActions", customActions)
                                        .toString();
    }
}
