/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionItemContract;
import org.kuali.rice.kew.api.action.RecipientType;
import org.kuali.rice.kew.api.actionlist.DisplayParameters;
import org.kuali.rice.kew.api.preferences.Preferences;
import org.kuali.rice.kew.api.util.CodeTranslator;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name="KREW_ACTN_ITM_T")
@NamedQueries({
    @NamedQuery(name="ActionItem.DistinctDocumentsForPrincipalId", query="SELECT COUNT(DISTINCT(ai.documentId)) FROM ActionItem ai"
            + "  WHERE ai.principalId = :principalId AND (ai.delegationType IS NULL OR ai.delegationType = 'P')"),
    @NamedQuery(name="ActionItem.GetMaxDateAndCountForPrincipalId", query="SELECT MAX(ai.dateAssigned) AS max_date, COUNT(DISTINCT(ai.documentId)) AS total_records FROM ActionItem ai"
            + "  WHERE ai.principalId = :principalId")
})
public class ActionItem implements ActionItemContract, Serializable {

    private static final long serialVersionUID = -1079562205125660151L;

    @Id
    @GeneratedValue(generator = "KREW_ACTN_ITM_S")
    @PortableSequenceGenerator(name = "KREW_ACTN_ITM_S")
    @Column(name="ACTN_ITM_ID")
	private String id;

    @Column(name="PRNCPL_ID")
	private String principalId;
	@Column(name="ASND_DT")
	private Timestamp dateAssigned;
    @Column(name="RQST_CD")
	private String actionRequestCd;
    @Column(name="ACTN_RQST_ID")
	private String actionRequestId;
    @Column(name="DOC_HDR_ID")
	private String documentId;
    @Column(name="GRP_ID")
	private String groupId;
    @Column(name="DOC_HDR_TTL")
	private String docTitle;
    @Column(name="DOC_TYP_LBL")
	private String docLabel;
    @Column(name="DOC_HDLR_URL")
	private String docHandlerURL;
    @Column(name="DOC_TYP_NM")
	private String docName;
    @Column(name="RSP_ID")
    private String responsibilityId = "1";
    @Column(name="ROLE_NM")
	private String roleName;
    @Column(name="DLGN_PRNCPL_ID")
	private String delegatorPrincipalId;
    @Column(name="DLGN_GRP_ID")
	private String delegatorGroupId;
    @Column(name="DLGN_TYP")
	private String delegationType;
    @Column(name="RQST_LBL")
    private String requestLabel;

    // used by Document Operations screen
    @Transient
    private String dateAssignedStringValue;

    @Transient
    private Timestamp lastApprovedDate;

    @Transient
    private Map<String, String> customActions = new HashMap<String, String>();

    @Transient
    private String rowStyleClass;

    @Transient
    private Integer actionListIndex;

    @Transient
    private String delegatorName = "";

    @Transient
    private String groupName = "";

    @Transient
    private DisplayParameters displayParameters;

    @Transient
    private boolean isInitialized = false;

    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.DETACH})
    @JoinColumn(name="DOC_HDR_ID", insertable=false, updatable=false)
    private DocumentRouteHeaderValue routeHeader;

    @Transient
    private DocumentRouteHeaderValue minimalRouteHeader;

    @Transient
    private boolean lastApprovedDateInitialized = false;

    @Transient
    private boolean delegatorNameInitialized = false;

    @Transient
    private boolean groupNameInitialized = false;

    @Deprecated
    @Override
    public String getActionToTake() {
        // deprecated, always return null (see the contract javadoc for more details)
        return null;
    }

    @Deprecated
    @Override
    public String getDateAssignedString() {
        // deprecated, always return null (see the contract javadoc for more details)
        return null;
    }

    public String getDateAssignedStringValue() {
        if (StringUtils.isBlank(dateAssignedStringValue)) {
            return RiceConstants.getDefaultDateFormat().format(getDateAssigned());
        }
        return dateAssignedStringValue;
    }

    public void setDateAssignedStringValue(String dateAssignedStringValue) {
        this.dateAssignedStringValue = dateAssignedStringValue;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPrincipalId() {
        return principalId;
    }

    public Timestamp getDateAssigned() {
        return dateAssigned;
    }

    @Override
    public DateTime getDateTimeAssigned() {
        return new DateTime(dateAssigned);
    }

    @Override
    public String getActionRequestCd() {
        return actionRequestCd;
    }

    @Override
    public String getActionRequestId() {
        return actionRequestId;
    }

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getDocTitle() {
        return docTitle;
    }

    @Override
    public String getDocLabel() {
        return docLabel;
    }

    @Override
    public String getDocHandlerURL() {
        return docHandlerURL;
    }

    @Override
    public String getDocName() {
        return docName;
    }

    @Override
    public String getResponsibilityId() {
        return responsibilityId;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public String getDelegatorPrincipalId() {
        return delegatorPrincipalId;
    }

    @Override
    public String getDelegatorGroupId() {
        return delegatorGroupId;
    }

    @Override
    public DelegationType getDelegationType() {
        return DelegationType.fromCode(delegationType);
    }

    public String getRequestLabel() {
        return this.requestLabel;
    }

    private Group getGroup(String groupId) {
    	if (StringUtils.isBlank(groupId)) {
    		return null;
    	}
    	return KimApiServiceLocator.getGroupService().getGroup(groupId);
    }

    public Group getGroup(){
    	return getGroup(groupId);
    }

    public String getRecipientTypeCode() {
        String recipientTypeCode = RecipientType.PRINCIPAL.getCode();
        if (getRoleName() != null) {
            recipientTypeCode = RecipientType.ROLE.getCode();
        }
        if (getGroupId() != null) {
            recipientTypeCode = RecipientType.GROUP.getCode();
        }
        return recipientTypeCode;
    }

    public String getActionRequestLabel() {
    	if (StringUtils.isNotBlank(getRequestLabel())) {
    		return getRequestLabel();
    	}
    	return CodeTranslator.getActionRequestLabel(getActionRequestCd());
    }

    public boolean isWorkgroupItem() {
        return getGroupId() != null;
    }

    public Principal getPrincipal(){
        return KimApiServiceLocator.getIdentityService().getPrincipal(principalId);
    }

    public void setResponsibilityId(String responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    public void setDocName(String docName) {
        this.docName = docName;
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

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActionRequestId(String actionRequestId) {
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

    public void setDelegatorPrincipalId(String delegatorPrincipalId) {
        this.delegatorPrincipalId = delegatorPrincipalId;
    }

    public void setDelegatorGroupId(String delegatorGroupId) {
        this.delegatorGroupId = delegatorGroupId;
    }

    public void setDelegationType(DelegationType delegationType) {
        this.delegationType = delegationType == null ? null : delegationType.getCode();
    }

    public void setRequestLabel(String requestLabel) {
        this.requestLabel = requestLabel;
    }

    @Deprecated
    @Override
    public Integer getActionItemIndex() {
        // deprecated, always return null (see the contract javadoc for more details)
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                                        .append("principalId", principalId)
                                        .append("dateAssigned", dateAssigned)
                                        .append("actionRequestCd", actionRequestCd)
                                        .append("actionRequestId", actionRequestId)
                                        .append("documentId", documentId)
                                        .append("groupId", groupId)
                                        .append("docTitle", docTitle)
                                        .append("docLabel", docLabel)
                                        .append("docHandlerURL", docHandlerURL)
                                        .append("docName", docName)
                                        .append("responsibilityId", responsibilityId)
                                        .append("roleName", roleName)
                                        .append("delegatorPrincipalId", delegatorPrincipalId)
                                        .append("delegatorGroupId", delegatorGroupId)
                                        .append("delegationType", delegationType)
                                        .toString();
    }

    public String getRouteHeaderRouteStatus() {
        return getMinimalRouteHeader().getDocRouteStatus();
    }

    public String getRouteHeaderCombinedStatus() {
        return getMinimalRouteHeader().getCombinedStatus();
    }

    public Timestamp getRouteHeaderCreateDate() {
        return getMinimalRouteHeader().getCreateDate();
    }

    public String getRouteHeaderInitiatorName() {
        return getMinimalRouteHeader().getInitiatorDisplayName();
    }

    public Timestamp getRouteHeaderApprovedDate() {
        return getMinimalRouteHeader().getApprovedDate();
    }

    public String getRouteHeaderCurrentRouteLevelName() {
        return getMinimalRouteHeader().getCurrentRouteLevelName();
    }

    public String getRouteHeaderInitiatorWorkflowId() {
        return getMinimalRouteHeader().getInitiatorWorkflowId();
    }

    public Integer getActionListIndex() {
        return actionListIndex;
    }

    public void setActionListIndex(Integer actionListIndex) {
        this.actionListIndex = actionListIndex;
    }

    public Timestamp getLastApprovedDate() {
        initializeLastApprovedDate();
        return this.lastApprovedDate;
    }

    public Map<String, String> getCustomActions() {
        return customActions;
    }

    public void setCustomActions(Map<String, String> customActions) {
        this.customActions = customActions;
    }

    public String getRowStyleClass() {
        return rowStyleClass;
    }

    public void setRowStyleClass(String rowStyleClass) {
        this.rowStyleClass = rowStyleClass;
    }

    public String getDelegatorName() {
        initializeDelegatorName();
        return delegatorName;
    }

    public String getGroupName() {
        initializeGroupName();
        return groupName;
    }

    public void initialize(Preferences preferences) {
        // always re-initialize row style class, just in case they changed a preference!
        initializeRowStyleClass(preferences);
    	if (isInitialized) {
    		return;
    	}
        if (KewApiConstants.PREFERENCES_YES_VAL.equals(preferences.getShowWorkgroupRequest())) {
            initializeGroupName();
        }
        if (KewApiConstants.PREFERENCES_YES_VAL.equals(preferences.getShowDelegator())) {
            initializeDelegatorName();
        }
        if (KewApiConstants.PREFERENCES_YES_VAL.equals(preferences.getShowDateApproved())) {
        	initializeLastApprovedDate();
        }
        isInitialized = true;
    }

    private void initializeRowStyleClass(Preferences preferences) {
        //set background colors for document statuses
        String docRouteStatus = getRouteHeaderRouteStatus();
        if (KewApiConstants.ROUTE_HEADER_CANCEL_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorCanceled()));
        } else if (KewApiConstants.ROUTE_HEADER_DISAPPROVED_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorDisapproved()));
        } else if (KewApiConstants.ROUTE_HEADER_ENROUTE_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorEnroute()));
        } else if (KewApiConstants.ROUTE_HEADER_EXCEPTION_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorException()));
        } else if (KewApiConstants.ROUTE_HEADER_FINAL_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorFinal()));
        } else if (KewApiConstants.ROUTE_HEADER_INITIATED_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorInitiated()));
        } else if (KewApiConstants.ROUTE_HEADER_PROCESSED_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorProcessed()));
        } else if (KewApiConstants.ROUTE_HEADER_SAVED_CD.equalsIgnoreCase(docRouteStatus)) {
            setRowStyleClass(KewApiConstants.ACTION_LIST_COLOR_PALETTE.get(preferences.getColorSaved()));
        }
    }

    private void initializeGroupName() {
        if (!groupNameInitialized) {
            if (getGroupId() != null) {
                Group group = this.getGroup();
                groupName = group.getName();
            }
            groupNameInitialized = true;
        }
    }

    private void initializeDelegatorName() {
        if (!delegatorNameInitialized) {
            if (getDelegatorPrincipalId() != null) {
                EntityNamePrincipalName name = KimApiServiceLocator.getIdentityService().getDefaultNamesForPrincipalId(getDelegatorPrincipalId());
                if (name != null) {
                    delegatorName = name.getDefaultName().getCompositeName();
                }
            }
            if (getDelegatorGroupId() != null) {
                Group delegatorGroup = KimApiServiceLocator.getGroupService().getGroup(getDelegatorGroupId());
                if (delegatorGroup !=null)
                    delegatorName = delegatorGroup.getName();
            }
            delegatorNameInitialized = true;
        }
    }

    private void initializeLastApprovedDate() {
        if (!lastApprovedDateInitialized) {
            lastApprovedDate = KEWServiceLocator.getActionTakenService().getLastApprovedDate(getDocumentId());
            lastApprovedDateInitialized = true;
        }
    }

    public DisplayParameters getDisplayParameters() {
    	return displayParameters;
    }

    public void setDisplayParameters(DisplayParameters displayParameters) {
    	this.displayParameters = displayParameters;
    }

    public DocumentRouteHeaderValue getRouteHeader() {
    	return routeHeader;
    }

    public DocumentRouteHeaderValue getMinimalRouteHeader() {
        if ( minimalRouteHeader == null ) {
            minimalRouteHeader = KEWServiceLocator.getActionListService().getMinimalRouteHeader(documentId);
        }
        return minimalRouteHeader;
    }

    public ActionItem deepCopy(Map<Object, Object> visited) {
        if (visited.containsKey(this)) {
            return (ActionItem)visited.get(this);
        }
        ActionItem copy = new ActionItem();
        visited.put(this, copy);
        copy.id = id;
        copy.principalId = principalId;
        if (dateAssigned != null) {
            copy.dateAssigned = new Timestamp(dateAssigned.getTime());
        }
        copy.actionRequestCd = actionRequestCd;
        copy.actionRequestId = actionRequestId;
        copy.documentId = documentId;
        copy.groupId = groupId;
        copy.docTitle = docTitle;
        copy.docLabel = docLabel;
        copy.docHandlerURL = docHandlerURL;
        copy.docName = docName;
        copy.responsibilityId = responsibilityId;
        copy.roleName = roleName;
        copy.delegatorPrincipalId = delegatorPrincipalId;
        copy.delegatorGroupId = delegatorGroupId;
        copy.delegationType = delegationType;
        copy.requestLabel = requestLabel;
        copy.dateAssignedStringValue = dateAssignedStringValue;
        if (lastApprovedDate != null) {
            copy.lastApprovedDate = new Timestamp(lastApprovedDate.getTime());
        }
        copy.delegatorName = delegatorName;
        copy.groupName = groupName;
        if (routeHeader != null) {
            copy.routeHeader = routeHeader.deepCopy(visited);
        }
        return copy;
    }

    public static org.kuali.rice.kew.api.action.ActionItem to(ActionItem bo) {
        if (bo == null) {
            return null;
        }
        return org.kuali.rice.kew.api.action.ActionItem.Builder.create(bo).build();
    }

    public static List<org.kuali.rice.kew.api.action.ActionItem> to(Collection<ActionItem> bos) {
        if (bos == null)
            return null;
        if (bos.isEmpty())
            return new ArrayList<org.kuali.rice.kew.api.action.ActionItem>();

        List<org.kuali.rice.kew.api.action.ActionItem> dtos = new ArrayList<org.kuali.rice.kew.api.action.ActionItem>(bos.size());
        for (ActionItem bo : bos) {
            dtos.add(ActionItem.to(bo));
        }
        return dtos;
    }
}
