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
package org.kuali.rice.kew.actionrequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowPersistable;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routetemplate.RuleBaseValues;
import org.kuali.rice.kew.routetemplate.RuleService;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.RoleRecipient;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kew.workgroup.WorkgroupService;


/**
 * Bean mapped to DB. Represents ActionRequest to a workgroup, user or role.  Contains
 * references to children/parent if a member of a graph
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_ACTN_RQST_T")
public class ActionRequestValue implements WorkflowPersistable {

	private static final long serialVersionUID = 8781414791855848385L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ActionRequestValue.class);

    private static final String ACTION_CODE_RANK = "FKACB";//B is a hack for allowing blanket approves to count for approve and complete requests in findPreviousAction in ActionTakenService this is a hack and accounts for the -3 on compareActionCode
    private static final String RECIPIENT_TYPE_RANK = "RWU";
    private static final String DELEGATION_TYPE_RANK = "SPN";

    @Id
	@Column(name="ACTN_RQST_ID")
	private java.lang.Long actionRequestId;
    @Column(name="ACTN_RQST_CD")
	private java.lang.String actionRequested;
    @Column(name="DOC_HDR_ID")
	private java.lang.Long routeHeaderId;
    @Column(name="ACTN_RQST_STAT_CD")
	private java.lang.String status;
    @Column(name="ACTN_RQST_RESP_ID")
	private java.lang.Long responsibilityId;
    @Column(name="WRKGRP_ID")
	private java.lang.Long workgroupId;
    @Column(name="ACTN_RQST_RECP_TYP_CD")
	private java.lang.String recipientTypeCd;
    @Column(name="ACTN_RQST_PRIO_NBR")
	private java.lang.Integer priority;
    @Column(name="ACTN_RQST_RTE_LVL_NBR")
	private java.lang.Integer routeLevel;
    @Column(name="ACTN_TKN_ID")
	private java.lang.Long actionTakenId;
    @Column(name="DOC_VER_NBR")
    private java.lang.Integer docVersion = new Integer(1);
    //@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ACTN_RQST_CRTE_DT")
	private java.sql.Timestamp createDate;
    @Column(name="ACTN_RQST_RESP_DESC")
	private java.lang.String responsibilityDesc;
    @Column(name="ACTN_RQST_ANNOTN_TXT")
	private java.lang.String annotation;
    @Column(name="DB_LOCK_VER_NBR")
	private java.lang.Integer jrfVerNbr;
    @Column(name="ACTN_RQST_PRSN_EN_ID")
	private java.lang.String workflowId;
    @Column(name="ACTN_RQST_IGN_PREV_ACTN_IND")
	private Boolean ignorePrevAction;
    @Column(name="ACTN_RQST_PARNT_ID")
	private Long parentActionRequestId;
    @Column(name="QUAL_ROLE_NM")
	private String qualifiedRoleName;
    @Column(name="ROLE_NM")
	private String roleName;
    @Column(name="QUAL_ROLE_NM_LBL_TXT")
	private String qualifiedRoleNameLabel;
    @Transient
    private String displayStatus;
    @Column(name="RULE_BASE_VALUES_ID")
	private Long ruleBaseValuesId;

    @Column(name="DLGN_TYP")
    private String delegationType = KEWConstants.DELEGATION_NONE;
    @Column(name="ACTN_RQST_APPR_PLCY")
	private String approvePolicy;

    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="ACTN_RQST_PARNT_ID", insertable=false, updatable=false)
	private ActionRequestValue parentActionRequest;
    @Transient
    private List<ActionRequestValue> childrenRequests = new ArrayList<ActionRequestValue>();
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="ACTN_TKN_ID", insertable=false, updatable=false)
	private ActionTakenValue actionTaken;
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DOC_HDR_ID", insertable=false, updatable=false)
	private DocumentRouteHeaderValue routeHeader;
    @Transient
    private List<ActionItem> actionItems = new ArrayList<ActionItem>();
    @Column(name="ACTN_RQST_CUR_IND")
    private Boolean currentIndicator = new Boolean(true);
    @Transient
    private String createDateString;

    /* New Workflow 2.1 Field */
    // The node instance at which this request was generated
    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="ACTN_RQST_RTE_NODE_INSTN_ID")
	private RouteNodeInstance nodeInstance;

    public ActionRequestValue() {
        createDate = new Timestamp(System.currentTimeMillis());
    }

    public Workgroup getWorkgroup() throws KEWUserNotFoundException {
        if (getWorkgroupId() == null) {
            LOG.error("Attempting to get a workgroup with a blank workgroup id");
            return null;
        }
        WorkgroupService workgroupSrv = (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
        return workgroupSrv.getWorkgroup(new WorkflowGroupId(getWorkgroupId()));
    }

    public String getRouteLevelName() {
        // this is for backward compatibility of requests which have not been converted
        if (CompatUtils.isRouteLevelRequest(this)) {
            int routeLevelInt = getRouteLevel().intValue();
            if (routeLevelInt == KEWConstants.EXCEPTION_ROUTE_LEVEL) {
                return "Exception";
            }

            List routeLevelNodes = CompatUtils.getRouteLevelCompatibleNodeList(routeHeader.getDocumentType());
            if (!(routeLevelInt < routeLevelNodes.size())) {
                return "Not Found";
            }
            return ((RouteNode)routeLevelNodes.get(routeLevelInt)).getRouteNodeName();
        } else {
            return (nodeInstance == null ? "Exception" : nodeInstance.getName());
        }
    }

    public boolean isUserRequest() {
        return workflowId != null;
    }

    public WorkflowUser getWorkflowUser() throws KEWUserNotFoundException {
        if (getWorkflowId() == null) {
            return null;
        }
        UserService userSrv = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
        return userSrv.getWorkflowUser(new WorkflowUserId(getWorkflowId()));
    }

    public Recipient getRecipient() throws KEWUserNotFoundException {
        if (getWorkflowId() != null) {
            return getWorkflowUser();
        } else if (getWorkgroupId() != null){
            return getWorkgroup();
        } else {
        	return new RoleRecipient(this.getRoleName());
        }
    }

    public boolean isPending() {
        return KEWConstants.ACTION_REQUEST_INITIALIZED.equals(getStatus()) || KEWConstants.ACTION_REQUEST_ACTIVATED.equals(getStatus());
    }

    public DocumentRouteHeaderValue getRouteHeader() {
        return routeHeader;
    }

    public String getStatusLabel() {
        return CodeTranslator.getActionRequestStatusLabel(getStatus());
    }

    public String getActionRequestedLabel() {
        return CodeTranslator.getActionRequestLabel(getActionRequested());
    }

    /**
     * @param routeHeader
     *            The routeHeader to set.
     */
    public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.routeHeader = routeHeader;
    }

    /**
     * @return Returns the actionTaken.
     */
    public ActionTakenValue getActionTaken() {
        return actionTaken;
    }

    /**
     * @param actionTaken
     *            The actionTaken to set.
     */
    public void setActionTaken(ActionTakenValue actionTaken) {
        this.actionTaken = actionTaken;
    }

    /**
     * @return Returns the actionRequested.
     */
    public java.lang.String getActionRequested() {
        return actionRequested;
    }

    /**
     * @param actionRequested
     *            The actionRequested to set.
     */
    public void setActionRequested(java.lang.String actionRequested) {
        this.actionRequested = actionRequested;
    }

    /**
     * @return Returns the actionRequestId.
     */
    public java.lang.Long getActionRequestId() {
        return actionRequestId;
    }

    /**
     * @param actionRequestId
     *            The actionRequestId to set.
     */
    public void setActionRequestId(java.lang.Long actionRequestId) {
        this.actionRequestId = actionRequestId;
    }

    /**
     * @return Returns the actionTakenId.
     */
    public java.lang.Long getActionTakenId() {
        return actionTakenId;
    }

    /**
     * @param actionTakenId
     *            The actionTakenId to set.
     */
    public void setActionTakenId(java.lang.Long actionTakenId) {
        this.actionTakenId = actionTakenId;
    }

    /**
     * @return Returns the annotation.
     */
    public java.lang.String getAnnotation() {
        return annotation;
    }

    /**
     * @param annotation
     *            The annotation to set.
     */
    public void setAnnotation(java.lang.String annotation) {
        this.annotation = annotation;
    }

    /**
     * @return Returns the createDate.
     */
    public java.sql.Timestamp getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     *            The createDate to set.
     */
    public void setCreateDate(java.sql.Timestamp createDate) {
        this.createDate = createDate;
    }

    /**
     * @return Returns the docVersion.
     */
    public java.lang.Integer getDocVersion() {
        return docVersion;
    }

    /**
     * @param docVersion
     *            The docVersion to set.
     */
    public void setDocVersion(java.lang.Integer docVersion) {
        this.docVersion = docVersion;
    }

    public java.lang.String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(java.lang.String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * @return Returns the ignorePrevAction.
     */
    public Boolean getIgnorePrevAction() {
        return ignorePrevAction;
    }

    /**
     * @param ignorePrevAction
     *            The ignorePrevAction to set.
     */
    public void setIgnorePrevAction(Boolean ignorePrevAction) {
        this.ignorePrevAction = ignorePrevAction;
    }

    /**
     * @return Returns the jrfVerNbr.
     */
    public java.lang.Integer getJrfVerNbr() {
        return jrfVerNbr;
    }

    /**
     * @param jrfVerNbr
     *            The jrfVerNbr to set.
     */
    public void setJrfVerNbr(java.lang.Integer jrfVerNbr) {
        this.jrfVerNbr = jrfVerNbr;
    }

    /**
     * @return Returns the priority.
     */
    public java.lang.Integer getPriority() {
        return priority;
    }

    /**
     * @param priority
     *            The priority to set.
     */
    public void setPriority(java.lang.Integer priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the recipientTypeCd.
     */
    public java.lang.String getRecipientTypeCd() {
        return recipientTypeCd;
    }

    /**
     * @param recipientTypeCd
     *            The recipientTypeCd to set.
     */
    public void setRecipientTypeCd(java.lang.String recipientTypeCd) {
        this.recipientTypeCd = recipientTypeCd;
    }

    /**
     * @return Returns the responsibilityDesc.
     */
    public java.lang.String getResponsibilityDesc() {
        return responsibilityDesc;
    }

    /**
     * @param responsibilityDesc
     *            The responsibilityDesc to set.
     */
    public void setResponsibilityDesc(java.lang.String responsibilityDesc) {
        this.responsibilityDesc = responsibilityDesc;
    }

    /**
     * @return Returns the responsibilityId.
     */
    public java.lang.Long getResponsibilityId() {
        return responsibilityId;
    }

    /**
     * @param responsibilityId
     *            The responsibilityId to set.
     */
    public void setResponsibilityId(java.lang.Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    /**
     * @return Returns the routeHeaderId.
     */
    public java.lang.Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(java.lang.Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public java.lang.Integer getRouteLevel() {
        return routeLevel;
    }

    public void setRouteLevel(java.lang.Integer routeLevel) {
        this.routeLevel = routeLevel;
    }

//    public java.lang.String getRouteMethodName() {
//        return routeMethodName;
//    }
//
//    public void setRouteMethodName(java.lang.String routeMethodName) {
//        this.routeMethodName = routeMethodName;
//    }

    public java.lang.String getStatus() {
        return status;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public java.lang.Long getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(java.lang.Long workgroupId) {
        this.workgroupId = workgroupId;
    }

    public Object copy(boolean preserveKeys) {
        ActionRequestValue clone = new ActionRequestValue();
        try {
            BeanUtils.copyProperties(clone, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!preserveKeys) {
            clone.setActionRequestId(null);
        }
        ActionTakenValue actionTakenClone = (ActionTakenValue) getActionTaken().copy(preserveKeys);
        clone.setActionTaken(actionTakenClone);
        return clone;
    }

    public boolean isInitialized() {
        return KEWConstants.ACTION_REQUEST_INITIALIZED.equals(getStatus());
    }

    public boolean isActive() {
        return KEWConstants.ACTION_REQUEST_ACTIVATED.equals(getStatus());
    }

    public boolean isApproveOrCompleteRequest() {
        return KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(getActionRequested()) || KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(getActionRequested());
    }

    public boolean isDone() {
        return KEWConstants.ACTION_REQUEST_DONE_STATE.equals(getStatus());
    }

    public boolean isReviewerUser() {
        return KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD.equals(getRecipientTypeCd());
    }

    public boolean isRecipientRoutedRequest(Recipient recipient) throws KEWUserNotFoundException {
        //before altering this method it is used in checkRouteLogAuthentication
        //don't break that method
        if (recipient == null) {
            return false;
        }
        boolean isRecipientInGraph = false;
        if (isReviewerUser()) {
            if (recipient instanceof WorkflowUser) {
                isRecipientInGraph = getWorkflowId().equals(((WorkflowUser) recipient).getWorkflowUserId().getWorkflowId());
            } else {
                isRecipientInGraph = ((Workgroup) recipient).hasMember(getWorkflowUser());
            }

        } else if (isWorkgroupRequest()) {
            if (recipient instanceof WorkflowUser) {
                Workgroup workgroup = getWorkgroup();
                if (workgroup == null) {
                    LOG.error("Was unable to retrieve workgroup " + getWorkgroupId());
                }
                isRecipientInGraph = workgroup.hasMember((WorkflowUser) recipient);
            } else {
                isRecipientInGraph = ((Workgroup) recipient).getWorkflowGroupId().getGroupId().equals(getWorkgroupId());
            }
        }

        for (Iterator iter = getChildrenRequests().iterator(); iter.hasNext();) {
            ActionRequestValue childRequest = (ActionRequestValue) iter.next();
            isRecipientInGraph = isRecipientInGraph || childRequest.isRecipientRoutedRequest(recipient);
        }

        return isRecipientInGraph;
    }

    public boolean isWorkgroupRequest() {
        return KEWConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD.equals(getRecipientTypeCd());
    }

    public boolean isRoleRequest() {
        return KEWConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD.equals(getRecipientTypeCd());
    }

    public boolean isAcknowledgeRequest() {
        return KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(getActionRequested());
    }

    public boolean isApproveRequest() {
        return KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(getActionRequested()) || KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(getActionRequested());
    }

    public boolean isCompleteRequst() {
        return KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(getActionRequested());
    }

    public boolean isFYIRequest() {
        return KEWConstants.ACTION_REQUEST_FYI_REQ.equals(getActionRequested());
    }

    /**
     * Allows comparison of action requests to see which is greater responsibility. -1 : indicates code 1 is lesser responsibility than code 2 0 : indicates the same responsibility 1 : indicates code1 is greater responsibility than code 2 The priority of action requests is as follows: fyi < acknowledge < (approve == complete)
     *
     * @param code1
     * @param code2
     * @return -1 if less than, 0 if equal, 1 if greater than
     */
    public static int compareActionCode(String code1, String code2) {
        // hacked so that APPROVE and COMPLETE are equal
        int cutoff = ACTION_CODE_RANK.length() - 3;
        Integer code1Index = new Integer(Math.min(ACTION_CODE_RANK.indexOf(code1), cutoff));
        Integer code2Index = new Integer(Math.min(ACTION_CODE_RANK.indexOf(code2), cutoff));
        return code1Index.compareTo(code2Index);
    }

    /**
     * Allows comparison of action requests to see which is greater responsibility. -1 : indicates type 1 is lesser responsibility than type 2 0 : indicates the same responsibility 1 : indicates type1 is greater responsibility than type 2
     *
     * @param type1
     * @param type2
     * @return -1 if less than, 0 if equal, 1 if greater than
     */
    public static int compareRecipientType(String type1, String type2) {
        Integer type1Index = new Integer(RECIPIENT_TYPE_RANK.indexOf(type1));
        Integer type2Index = new Integer(RECIPIENT_TYPE_RANK.indexOf(type2));
        return type1Index.compareTo(type2Index);
    }

    public static int compareDelegationType(String type1, String type2) {
    	if (StringUtils.isEmpty(type1)) {
    		type1 = "N";
    	}
    	if (StringUtils.isEmpty(type2)) {
    		type2 = "N";
    	}
    	Integer type1Index = new Integer(DELEGATION_TYPE_RANK.indexOf(type1));
        Integer type2Index = new Integer(DELEGATION_TYPE_RANK.indexOf(type2));
        return type1Index.compareTo(type2Index);
    }

    public List<ActionItem> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItem> actionItems) {
        this.actionItems = actionItems;
    }

    public Boolean getCurrentIndicator() {
        return currentIndicator;
    }

    public void setCurrentIndicator(Boolean currentIndicator) {
        this.currentIndicator = currentIndicator;
    }

    public Long getParentActionRequestId() {
        return parentActionRequestId;
    }

    public void setParentActionRequestId(Long parentActionRequestId) {
        this.parentActionRequestId = parentActionRequestId;
    }

    public ActionRequestValue getParentActionRequest() {
        return parentActionRequest;
    }

    public void setParentActionRequest(ActionRequestValue parentActionRequest) {
        this.parentActionRequest = parentActionRequest;
    }

    public List<ActionRequestValue> getChildrenRequests() {
        return childrenRequests;
    }

    public void setChildrenRequests(List<ActionRequestValue> childrenRequests) {
        this.childrenRequests = childrenRequests;
    }

    public String getQualifiedRoleName() {
        return qualifiedRoleName;
    }

    public void setQualifiedRoleName(String roleName) {
        this.qualifiedRoleName = roleName;
    }

    public String getDelegationType() {
        return delegationType;
    }

    public void setDelegationType(String delegatePolicy) {
        this.delegationType = delegatePolicy;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getApprovePolicy() {
        return approvePolicy;
    }

    public void setApprovePolicy(String requestType) {
        this.approvePolicy = requestType;
    }

    public boolean getHasApprovePolicy() {
        return getApprovePolicy() != null;
    }

    public boolean isDeactivated() {
        return KEWConstants.ACTION_REQUEST_DONE_STATE.equals(getStatus());
    }

    public boolean hasParent() {
        return getParentActionRequest() != null;
    }

    public boolean hasChild(ActionRequestValue actionRequest) {
        if (actionRequest == null)
            return false;
        Long actionRequestId = actionRequest.getActionRequestId();
        for (Iterator iter = getChildrenRequests().iterator(); iter.hasNext();) {
            ActionRequestValue childRequest = (ActionRequestValue) iter.next();
            if (childRequest.equals(actionRequest) || (actionRequestId != null && actionRequestId.equals(childRequest.getActionRequestId()))) {
                return true;
            }
        }
        return false;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getQualifiedRoleNameLabel() {
        return qualifiedRoleNameLabel;
    }

    public void setQualifiedRoleNameLabel(String qualifiedRoleNameLabel) {
        this.qualifiedRoleNameLabel = qualifiedRoleNameLabel;
    }

    public String getCreateDateString() {
        if (createDateString == null || createDateString.trim().equals("")) {
            return RiceConstants.getDefaultDateFormat().format(getCreateDate());
        } else {
            return createDateString;
        }
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }

    public RouteNodeInstance getNodeInstance() {
		return nodeInstance;
	}

    public String getPotentialNodeName() {
        return (getNodeInstance() == null ? "" : getNodeInstance().getName());
    }

	public void setNodeInstance(RouteNodeInstance nodeInstance) {
		this.nodeInstance = nodeInstance;
	}

	public String getRecipientTypeLabel() {
        return (String) KEWConstants.ACTION_REQUEST_RECIPIENT_TYPE.get(getRecipientTypeCd());
    }

    public RuleBaseValues getRuleBaseValues(){
        if(ruleBaseValuesId != null){
            return getRuleService().findRuleBaseValuesById(ruleBaseValuesId);
        }
        return null;
    }
    public Long getRuleBaseValuesId() {
        return ruleBaseValuesId;
    }

    public void setRuleBaseValuesId(Long ruleBaseValuesId) {
        this.ruleBaseValuesId = ruleBaseValuesId;
    }


//    public java.lang.String getRouteMethodName() {
//		return routeMethodName;
//	}
//
//	public void setRouteMethodName(java.lang.String routeMethodName) {
//		this.routeMethodName = routeMethodName;
//	}

	private RuleService getRuleService() {
        return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
    }

    public boolean isPrimaryDelegator() {
        boolean primaryDelegator = false;
        for (Iterator iter = childrenRequests.iterator(); iter.hasNext();) {
            ActionRequestValue childRequest = (ActionRequestValue) iter.next();
            primaryDelegator = KEWConstants.DELEGATION_PRIMARY.equals(childRequest.getDelegationType()) || primaryDelegator;
        }
        return primaryDelegator;
    }

    /**
     * Used to get primary delegate names on route log in the 'Requested Of' section so primary delegate requests
     * list the delegate and not the delegator as having the request 'IN ACTION LIST'.  This method doesn't recurse
     * and therefore assume an AR structure.
     *
     * @return primary delgate requests
     */
    public List getPrimaryDelegateRequests() {
        List primaryDelegateRequests = new ArrayList();
        for (Iterator iter = childrenRequests.iterator(); iter.hasNext();) {
            ActionRequestValue childRequest = (ActionRequestValue) iter.next();
            if (KEWConstants.DELEGATION_PRIMARY.equals(childRequest.getDelegationType())) {
                if (childRequest.isRoleRequest()) {
                    for (Iterator iterator = childRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
                        primaryDelegateRequests.add(iterator.next());
                    }
                } else {
                	primaryDelegateRequests.add(childRequest);
                }
            }
        }
        return primaryDelegateRequests;
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

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("actionRequestId", actionRequestId)
            .append("actionRequested", actionRequested)
            .append("routeHeaderId", routeHeaderId)
            .append("status", status)
            .append("responsibilityId", responsibilityId)
            .append("workgroupId", workgroupId)
            .append("recipientTypeCd", recipientTypeCd)
            .append("priority", priority)
            .append("routeLevel", routeLevel)
            .append("actionTakenId", actionTakenId)
            .append("docVersion", docVersion)
            .append("createDate", createDate)
            .append("responsibilityDesc", responsibilityDesc)
            .append("annotation", annotation)
            .append("jrfVerNbr", jrfVerNbr)
            .append("workflowId", workflowId)
            .append("ignorePrevAction", ignorePrevAction)
            .append("parentActionRequestId", parentActionRequestId)
            .append("qualifiedRoleName", qualifiedRoleName)
            .append("roleName", roleName)
            .append("qualifiedRoleNameLabel", qualifiedRoleNameLabel)
            .append("displayStatus", displayStatus)
            .append("ruleBaseValuesId", ruleBaseValuesId)
            .append("delegationType", delegationType)
            .append("approvePolicy", approvePolicy)
            .append("childrenRequests", childrenRequests == null ? null : childrenRequests.size())
            .append("actionTaken", actionTaken)
            .append("routeHeader", routeHeader)
            .append("actionItems", actionItems == null ? null : actionItems.size())
            .append("currentIndicator", currentIndicator)
            .append("createDateString", createDateString)
            .append("nodeInstance", nodeInstance).toString();
    }
}
