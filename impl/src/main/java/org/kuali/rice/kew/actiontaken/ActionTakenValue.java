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
package org.kuali.rice.kew.actiontaken;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Model object mapped to ojb for representing actions taken on documents by
 * users.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
//@Sequence(name="KREW_ACTN_TKN_S", property="actionTakenId")
@Table(name="KREW_ACTN_TKN_T")
public class ActionTakenValue implements Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = -81505450567067594L;
	@Id
	@GeneratedValue(generator="KREW_ACTN_TKN_S")
	@GenericGenerator(name="KREW_ACTN_TKN_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_ACTN_TKN_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="ACTN_TKN_ID")
    private Long actionTakenId;
    @Column(name="DOC_HDR_ID")//,insertable=false, updatable=false)
	private Long routeHeaderId;
    @Column(name="ACTN_CD")
	private String actionTaken;
	@Column(name="ACTN_DT")
	private Timestamp actionDate;
    @Column(name="ANNOTN")
    private String annotation = "";
    @Column(name="DOC_VER_NBR")
	private Integer docVersion;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    @Column(name="PRNCPL_ID")
	private String principalId;
    @Column(name="DLGTR_PRNCPL_ID")
	private String delegatorPrincipalId;
    @Column(name="DLGTR_GRP_ID")
	private String delegatorGroupId;
    //@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
    //@JoinColumn(name="DOC_HDR_ID")
    //private DocumentRouteHeaderValue routeHeader;
    @OneToMany(fetch=FetchType.EAGER, mappedBy="actionTaken")
    @Fetch(value = FetchMode.SELECT)
	private Collection<ActionRequestValue> actionRequests;
    @Column(name="CUR_IND")
    private Boolean currentIndicator = Boolean.TRUE;
    @Transient
    private String actionDateString;

    public KimPrincipal getPrincipal() {
    	return getPrincipalForId( principalId );
    }
    
	//@PrePersist
	public void beforeInsert(){
		OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());		
	}

    public String getPrincipalDisplayName() {
    	// TODO this stinks to have to have a dependency on UserSession here
    	return KEWServiceLocator.getIdentityHelperService().getPerson(getPrincipalId()).getName();
    }

    public KimPrincipal getDelegatorPrincipal() {
    	return getPrincipalForId(delegatorPrincipalId);
    }

    public Group getDelegatorGroup()
    {
	    return KIMServiceLocator.getIdentityManagementService()
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
        return getDelegatorPrincipalId() != null || getDelegatorGroupId() != null;
    }

    public String getDelegatorDisplayName() {
        if (! isForDelegator()) {
            return "";
        }
        if (getDelegatorPrincipalId() != null) {
        	// TODO this stinks to have to have a dependency on UserSession here
        	return KEWServiceLocator.getIdentityHelperService().getPerson(this.getDelegatorPrincipalId()).getName();
        } else {
            return getDelegatorGroup().getName();
      }
    }
    
    private KimPrincipal getPrincipalForId(String principalId) {
    	KimPrincipal principal = null;
    	
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

    //public DocumentRouteHeaderValue getRouteHeader() {
    //    return routeHeader;
    //}

    //public void setRouteHeader(DocumentRouteHeaderValue routeHeader) {
    //    this.routeHeader = routeHeader;
    //}

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

    public Collection getRootActionRequests() {
        return getActionRequestService().getRootRequests(getActionRequests());
    }

    private ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
    }

    public String getActionDateString() {
        if(actionDateString == null || actionDateString.trim().equals("")){
            return RiceConstants.getDefaultDateFormat().format(getActionDate());
        } else {
            return actionDateString;
        }
    }
    public void setActionDateString(String actionDateString) {
        this.actionDateString = actionDateString;
    }

    public boolean isApproval() {
    	return KEWConstants.ACTION_TAKEN_APPROVED_CD.equals(getActionTaken());
    }

    public boolean isCompletion() {
    	return KEWConstants.ACTION_TAKEN_COMPLETED_CD.equals(getActionTaken());
    }
}
