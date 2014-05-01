/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.actiontaken;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actiontaken.dao.impl.ActionTakenDaoJpa;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.util.CodeTranslator;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.jpa.converters.Boolean01Converter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Model object mapped to ojb for representing actions taken on documents by users. The type of the action is indicated
 * by the {@link #actionTaken} code which will be a valid code value from {@link ActionType}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KREW_ACTN_TKN_T")
@NamedQueries({
        @NamedQuery(name = ActionTakenDaoJpa.GET_LAST_ACTION_TAKEN_DATE_NAME,
                query = ActionTakenDaoJpa.GET_LAST_ACTION_TAKEN_DATE_QUERY)
})
public class ActionTakenValue implements Serializable {

	private static final long serialVersionUID = -81505450567067594L;

	@Id
	@GeneratedValue(generator = "KREW_ACTN_TKN_S")
    @PortableSequenceGenerator(name = "KREW_ACTN_TKN_S")
	@Column(name="ACTN_TKN_ID", nullable = false)
    private String actionTakenId;

    @Column(name="DOC_HDR_ID", nullable = false)
	private String documentId;

    @Column(name="ACTN_CD", nullable = false)
	private String actionTaken;

	@Column(name="ACTN_DT", nullable = false)
	private Timestamp actionDate;

    @Column(name="ANNOTN")
    private String annotation = "";

    @Column(name="DOC_VER_NBR", nullable = false)
	private Integer docVersion;

    @Column(name="PRNCPL_ID", nullable = false)
	private String principalId;

    @Column(name="DLGTR_PRNCPL_ID")
	private String delegatorPrincipalId;

    @Column(name="DLGTR_GRP_ID")
	private String delegatorGroupId;

    @Column(name="CUR_IND")
    @Convert(converter = Boolean01Converter.class)
    private Boolean currentIndicator = Boolean.TRUE;

    @Version
    @Column(name="VER_NBR")
    private Integer lockVerNbr;

    @OneToMany(mappedBy = "actionTaken")
	private Collection<ActionRequestValue> actionRequests;

    @Transient private String actionDateString;

    public Principal getPrincipal() {
    	return getPrincipalForId( principalId );
    }

    public String getPrincipalDisplayName() {
    	return KEWServiceLocator.getIdentityHelperService().getPerson(getPrincipalId()).getName();
    }

    public Principal getDelegatorPrincipal() {
    	return getPrincipalForId(delegatorPrincipalId);
    }

    public Group getDelegatorGroup()
    {
	    return KimApiServiceLocator.getGroupService()
	            .getGroup(String.valueOf(delegatorGroupId));
    }

    public void setDelegator(Recipient recipient) {
        if (recipient instanceof KimPrincipalRecipient) {
            setDelegatorPrincipalId(((KimPrincipalRecipient)recipient).getPrincipalId());
        } else if (recipient instanceof KimGroupRecipient) {
            setDelegatorGroupId(((KimGroupRecipient)recipient).getGroup().getId());
        } else {
            setDelegatorPrincipalId(null);
            setDelegatorGroupId(null);
        }
    }

    public boolean isForDelegator() {
        return getDelegatorPrincipalId() != null || getDelegatorGroupId() != null || getDelegatorRoleId() != null;
    }

    public String getDelegatorDisplayName() {
        if (getDelegatorPrincipalId() != null) {
        	return KEWServiceLocator.getIdentityHelperService().getPerson(this.getDelegatorPrincipalId()).getName();
        } else if (getDelegatorGroupId() != null) {
            return getDelegatorGroup().getName();
        } else {
            String delegatorRoleId = getDelegatorRoleId();
            if (delegatorRoleId != null) {
                Role role = KimApiServiceLocator.getRoleService().getRole(delegatorRoleId);
                if(role != null) {
                    return role.getName();
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    private Principal getPrincipalForId(String principalId) {
    	Principal principal = null;
    	
    	if (!StringUtils.isBlank(principalId)) {
    		principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId);
    	}
    	
    	return principal;
    }

    public String getActionTakenLabel() {
        return CodeTranslator.getActionTakenLabel(actionTaken);
    }

    public Collection<ActionRequestValue> getActionRequests() {
        if (actionRequests == null) {
            setActionRequests(new ArrayList<ActionRequestValue>());
        }
        return actionRequests;
    }

    public void setActionRequests(Collection<ActionRequestValue> actionRequests) {
        this.actionRequests = actionRequests;
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

    public String getActionTakenId() {
        return actionTakenId;
    }

    public void setActionTakenId(String actionTakenId) {
        this.actionTakenId = actionTakenId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getDelegatorPrincipalId() {
        return delegatorPrincipalId;
    }

    public void setDelegatorPrincipalId(String delegatorPrincipalId) {
        this.delegatorPrincipalId = delegatorPrincipalId;
    }

    public String getDelegatorGroupId() {
        return delegatorGroupId;
    }

    public void setDelegatorGroupId(String delegatorGroupId) {
        this.delegatorGroupId = delegatorGroupId;
    }

    public String getDelegatorRoleId() {
        // this could (perhaps) happen when running a simulation
        if (actionTakenId == null) {
            return null;
        }
        ActionRequestValue actionRequest = KEWServiceLocator.getActionRequestService().getActionRequestForRole(actionTakenId);
        if (actionRequest != null) {
            return actionRequest.getRoleName();
        } else {
            return null;
        }
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

    public String getDocumentId() {
    	return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getPrincipalId() {
    	return principalId;
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

    public String getActionDateString() {
        if(StringUtils.isBlank(actionDateString)) {
            return RiceConstants.getDefaultDateFormat().format(getActionDate());
        } else {
            return actionDateString;
        }
    }
    public void setActionDateString(String actionDateString) {
        this.actionDateString = actionDateString;
    }

    public boolean isApproval() {
    	return KewApiConstants.ACTION_TAKEN_APPROVED_CD.equals(getActionTaken());
    }

    public boolean isCompletion() {
    	return KewApiConstants.ACTION_TAKEN_COMPLETED_CD.equals(getActionTaken());
    }

    public ActionTakenValue deepCopy(Map<Object, Object> visited) {
        if (visited.containsKey(this)) {
            return (ActionTakenValue)visited.get(this);
        }
        ActionTakenValue copy = new ActionTakenValue();
        visited.put(this, copy);
        copy.actionTakenId = actionTakenId;
        copy.documentId = documentId;
        copy.actionTaken = actionTaken;
        if (actionDate != null) {
            copy.actionDate = new Timestamp(actionDate.getTime());
        }
        copy.annotation = annotation;
        copy.docVersion = docVersion;
        copy.principalId = principalId;
        copy.delegatorPrincipalId = delegatorPrincipalId;
        copy.delegatorGroupId = delegatorGroupId;
        copy.currentIndicator = currentIndicator;
        copy.lockVerNbr = lockVerNbr;
        if (actionRequests != null) {
            List<ActionRequestValue> copies = new ArrayList<ActionRequestValue>();
            for (ActionRequestValue actionRequest : actionRequests) {
                copies.add(actionRequest.deepCopy(visited));
            }
            copy.actionRequests = copies;
        }
        copy.actionDateString = actionDateString;
        return copy;
    }
    
    public static ActionTaken to(ActionTakenValue actionTakenBo) {
    	if (actionTakenBo == null) {
    		return null;
    	}
    	ActionTaken.Builder builder = ActionTaken.Builder.create(actionTakenBo.getActionTakenId(),
    			actionTakenBo.getDocumentId(),
    			actionTakenBo.getPrincipalId(),
    			ActionType.fromCode(actionTakenBo.getActionTaken()));
        if (actionTakenBo.getActionDate() != null) {
    	    builder.setActionDate(new DateTime(actionTakenBo.getActionDate().getTime()));
        }
    	builder.setAnnotation(actionTakenBo.getAnnotation());
    	builder.setCurrent(actionTakenBo.getCurrentIndicator().booleanValue());
    	builder.setDelegatorGroupId(actionTakenBo.getDelegatorGroupId());
    	builder.setDelegatorPrincipalId(actionTakenBo.getDelegatorPrincipalId());
    	return builder.build();
    }

    public boolean isSuperUserAction() {
        if ( KewApiConstants.ACTION_TAKEN_SU_ACTION_REQUEST_ACKNOWLEDGED_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_ACTION_REQUEST_FYI_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_ACTION_REQUEST_COMPLETED_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_ACTION_REQUEST_APPROVED_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_ROUTE_LEVEL_APPROVED_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_RETURNED_TO_PREVIOUS_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_DISAPPROVED_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_CANCELED_CD.equals(actionTaken) ||
                KewApiConstants.ACTION_TAKEN_SU_APPROVED_CD.equals(actionTaken)
                ) {
            return true;
        } else {
            return false;
        }
    }
}
