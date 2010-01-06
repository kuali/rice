/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.actionitem;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.RowStyleable;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * This is the model for action items. These are displayed as the action list as well.  Mapped to ActionItemService.
 * NOTE: This object contains denormalized fields that have been copied from related ActionRequestValue and DocumentRouteHeaderValue
 * objects for performance reasons.  These should be preserved and their related objects should not be added to the OJB
 * mapping as we do not want them loaded for each ActionItem object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */


@Entity
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name="KREW_ACTN_ITM_T")
@Sequence(name="KREW_ACTN_ITM_S",property="actionItemId")
@NamedQueries({
    @NamedQuery(name="ActionItem.QuickLinks.FindActionListStatsByPrincipalId", query="SELECT docName, COUNT(*) FROM ActionItem WHERE principalId = :principalId " +
        "AND (delegationType IS null OR delegationType != '" + KEWConstants.DELEGATION_SECONDARY + "') GROUP BY docName")
})
public class ActionItem implements WorkflowPersistable, RowStyleable {

    private static final long serialVersionUID = -1079562205125660151L;

    @Id
    @Column(name="ACTN_ITM_ID")
	private Long actionItemId;
    @Column(name="PRNCPL_ID")
	private String principalId;
	@Column(name="ASND_DT")
	private Timestamp dateAssigned;
    @Column(name="RQST_CD")
	private String actionRequestCd;
    @Column(name="ACTN_RQST_ID", nullable=false)
	private Long actionRequestId;
    @Column(name="DOC_HDR_ID", insertable=false, updatable=false)
	private Long routeHeaderId;
    @Column(name="GRP_ID")
	private String groupId;
    @Column(name="DOC_HDR_TTL")
	private String docTitle;
    @Column(name="DOC_TYP_LBL")
	private String docLabel;
    @Column(name="DOC_HDLR_URL")
	private String docHandlerURL;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    @Column(name="DOC_TYP_NM")
	private String docName;
    @Column(name="RSP_ID")
    private Long responsibilityId = new Long(1);
    @Column(name="ROLE_NM")
	private String roleName;
    @Column(name="DLGN_PRNCPL_ID")
	private String delegatorWorkflowId;
    @Column(name="DLGN_GRP_ID")
	private String delegatorGroupId;
    @Column(name="DLGN_TYP")
	private String delegationType;
    @Column(name="RQST_LBL")
    private String requestLabel;
    
	private transient DocumentRouteHeaderValue routeHeader;
	
    @Transient
    private Timestamp lastApprovedDate;
    @Transient
    private Integer actionItemIndex;
    @Transient
    private Map customActions = new HashMap();
    @Transient
    private String dateAssignedString;
    @Transient
    private String actionToTake;
    @Transient
    private String rowStyleClass;


    @PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());
    }
    
    @Id
    @Column(name="ACTN_ITM_ID")
    public Long getActionItemId() {
        return actionItemId;
    }
    
    @Column(name="PRNCPL_ID")
    public String getPrincipalId() {
        return principalId;
    }
    
    @Column(name="ASND_DT")
    public Timestamp getDateAssigned() {
        return dateAssigned;
    }
    
    @Column(name="RQST_CD")
    public String getActionRequestCd() {
        return actionRequestCd;
    }
    
    @Column(name="ACTN_RQST_ID", nullable=false)
    public Long getActionRequestId() {
        return actionRequestId;
    }
    
    @Column(name="DOC_HDR_ID", insertable=false, updatable=false)
    public Long getRouteHeaderId() {
        return routeHeaderId;
    }
    
    @Column(name="GRP_ID")
    public String getGroupId() {
        return groupId;
    }

    @Column(name="DOC_HDR_TTL")
    public String getDocTitle() {
        return docTitle;
    }
    
    @Column(name="DOC_TYP_LBL")
    public String getDocLabel() {
        return docLabel;
    }
    
    @Column(name="DOC_HDLR_URL")
    public String getDocHandlerURL() {
        return docHandlerURL;
    }
    
    @Version
    @Column(name="VER_NBR")
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    @Column(name="DOC_TYP_NM")
    public String getDocName() {
        return docName;
    }

    @Column(name="RSP_ID")
    public Long getResponsibilityId() {
        return responsibilityId;
    }

    @Column(name="ROLE_NM")
    public String getRoleName() {
        return roleName;
    }

    @Column(name="DLGN_PRNCPL_ID")
    public String getDelegatorWorkflowId() {
        return delegatorWorkflowId;
    }

    @Column(name="DLGN_GRP_ID")
    public String getDelegatorGroupId() {
        return delegatorGroupId;
    }

    @Column(name="DLGN_TYP")
    public String getDelegationType() {
        return delegationType;
    }

    @Column(name="RQST_LBL")
    public String getRequestLabel() {
        return this.requestLabel;
    }
    
    
    /**
     * @deprecated as of Rice 1.0.1 (KULRICE-1652), Use {@link #getRouteHeaderId()} instead. 
     */
    @Deprecated
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="DOC_HDR_ID")
    public DocumentRouteHeaderValue getRouteHeader() {
        return routeHeader;
    }

    
    @Transient
    public Timestamp getLastApprovedDate() {
        return this.lastApprovedDate;
    }

    @Transient
    public Integer getActionItemIndex() {
        return actionItemIndex;
    }
    
    @Transient
    public Map getCustomActions() {
        return customActions;
    }

    @Transient
    public String getDateAssignedString() {
        if(dateAssignedString == null || dateAssignedString.trim().equals("")){
            return RiceConstants.getDefaultDateFormat().format(getDateAssigned());
        } else {
            return dateAssignedString;
        }
    }

    @Transient
    public String getActionToTake() {
        return actionToTake;
    }

    @Transient
    public String getRowStyleClass() {
        return rowStyleClass;
    }

    
    private Group getGroup(String groupId) {
    	if( groupId ==null )	return null;
    	return KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
    }

    @Transient
    public Group getGroup(){
    	return getGroup(groupId.toString());
    }

    private Person getPerson(String workflowId) {
    	if (StringUtils.isBlank(workflowId)) {
    		return null;
    	}
    	return KIMServiceLocator.getPersonService().getPerson(workflowId);
    }

    @Transient
    public Person getPerson() {
        return getPerson(principalId);
    }

    @Transient
    public Person getDelegatorPerson() {
        return getPerson(delegatorWorkflowId);
    }

    @Transient
    public String getRecipientTypeCode() {
        String recipientTypeCode = KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD;
        if (getRoleName() != null) {
            recipientTypeCode = KEWConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD;
        }
        if (getGroupId() != null) {
            recipientTypeCode = KEWConstants.ACTION_REQUEST_GROUP_RECIPIENT_CD;
        }
        return recipientTypeCode;
    }
    
    @Transient
    public String getActionRequestLabel() {
    	if (StringUtils.isNotBlank(getRequestLabel())) {
    		return getRequestLabel();
    	}
    	return CodeTranslator.getActionRequestLabel(getActionRequestCd());
    }

    @Transient
    public boolean isWorkgroupItem() {
        return getGroupId() != null;
    }
    
    @Transient
    public KimPrincipal getPrincipal(){
        return KIMServiceLocator.getIdentityManagementService().getPrincipal(principalId);
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
    
    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }
    
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @deprecated as of Rice 1.0.1 (KULRICE-1652), Use {@link #setRouteHeaderId(Long)} instead. 
     */
    @Deprecated
    public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.routeHeader = routeHeader;
    }

    public void setActionRequestCd(String actionRequestCd) {
        this.actionRequestCd = actionRequestCd;
    }

    public void setDateAssigned(Timestamp dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
    
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public void setActionItemId(Long actionItemId) {
        this.actionItemId = actionItemId;
    }

    public void setActionRequestId(Long actionRequestId) {
        this.actionRequestId = actionRequestId;
    }

    public void setDocHandlerURL(String docHandlerURL) {
        this.docHandlerURL = docHandlerURL;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setDocLabel(String docLabel) {
        this.docLabel = docLabel;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public void setDelegatorWorkflowId(String delegatorWorkflowId) {
        this.delegatorWorkflowId = delegatorWorkflowId;
    }
    
    public void setDelegatorGroupId(String delegatorGroupId) {
        this.delegatorGroupId = delegatorGroupId;
    }

    public void setDateAssignedString(String dateAssignedString) {
        this.dateAssignedString = dateAssignedString;
    }

    public void setActionToTake(String actionToTake) {
        this.actionToTake = actionToTake;
    }

    public void setActionItemIndex(Integer actionItemIndex) {
        this.actionItemIndex = actionItemIndex;
    }

    public void setCustomActions(Map customActions) {
        this.customActions = customActions;
    }
    
    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }


    public void setLastApprovedDate(Timestamp lastApprovedDate) {
        this.lastApprovedDate = lastApprovedDate;
    }
    
    public void setRequestLabel(String requestLabel) {
        this.requestLabel = requestLabel;
    }
    
    public String toString() {
        return new ToStringBuilder(this).append("actionItemId", actionItemId)
                                        .append("workflowId", principalId)
                                        .append("actionItemId", actionItemId)
                                        .append("workflowId", principalId)
                                        .append("dateAssigned", dateAssigned)
                                        .append("actionRequestCd", actionRequestCd)
                                        .append("actionRequestId", actionRequestId)
                                        .append("routeHeaderId", routeHeaderId)
                                        .append("groupId", groupId)
                                        .append("docTitle", docTitle)
                                        .append("docLabel", docLabel)
                                        .append("docHandlerURL", docHandlerURL)
                                        .append("lockVerNbr", lockVerNbr)
                                        .append("docName", docName)
                                        .append("responsibilityId", responsibilityId)
                                        .append("rowStyleClass", rowStyleClass)
                                        .append("roleName", roleName)
                                        .append("delegatorWorkflowId", delegatorWorkflowId)
                                        .append("delegatorGroupId", delegatorGroupId)
                                        .append("dateAssignedString", dateAssignedString)
                                        .append("actionToTake", actionToTake)
                                        .append("delegationType", delegationType)
                                        .append("actionItemIndex", actionItemIndex)
                                        .append("customActions", customActions)
                                        .append("lastApprovedDate", lastApprovedDate)
                                        .toString();
    }

}
