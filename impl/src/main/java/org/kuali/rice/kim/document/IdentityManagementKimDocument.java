/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.document;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementKimDocument extends TransactionalDocumentBase {

	protected static final Logger LOG = Logger.getLogger(IdentityManagementKimDocument.class);
	
	protected List<RoleDocumentDelegation> delegations = new TypedArrayList(RoleDocumentDelegation.class);
	protected List<RoleDocumentDelegationMember> delegationMembers = new TypedArrayList(RoleDocumentDelegationMember.class);

	protected transient SequenceAccessorService sequenceAccessorService;
	
	protected void addDelegationMemberToDelegation(RoleDocumentDelegationMember delegationMember){
		RoleDocumentDelegation delegation;
		if(KEWConstants.DELEGATION_PRIMARY.equals(delegationMember.getDelegationTypeCode())){
			delegation = getPrimaryDelegation();
		} else{
			delegation = getSecondaryDelegation();
		}
		delegationMember.setDelegationId(delegation.getDelegationId());
		delegation.getMembers().add(delegationMember);
		delegation.setRoleId(delegationMember.getRoleImpl().getRoleId());
		delegation.setKimTypeId(delegationMember.getRoleImpl().getKimTypeId());
	}

	protected RoleDocumentDelegation getPrimaryDelegation(){
		RoleDocumentDelegation primaryDelegation = null;
		for(RoleDocumentDelegation delegation: getDelegations()){
			if(delegation.isDelegationPrimary())
				primaryDelegation = delegation;
		}
		if(primaryDelegation==null){
			primaryDelegation = new RoleDocumentDelegation();
			primaryDelegation.setDelegationId(getDelegationId());
			primaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_PRIMARY);
			getDelegations().add(primaryDelegation);
		}
		return primaryDelegation;
	}

	protected String getDelegationId(){
		return getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_DLGN_ID_S).toString();
	}
	
	protected RoleDocumentDelegation getSecondaryDelegation(){
		RoleDocumentDelegation secondaryDelegation = null;
		for(RoleDocumentDelegation delegation: getDelegations()){
			if(delegation.isDelegationSecondary())
				secondaryDelegation = delegation;
		}
		if(secondaryDelegation==null){
			secondaryDelegation = new RoleDocumentDelegation();
			secondaryDelegation.setDelegationId(getDelegationId());
			secondaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_SECONDARY);
			getDelegations().add(secondaryDelegation);
		}
		return secondaryDelegation;
	}

	/**
	 * @return the delegations
	 */
	public List<RoleDocumentDelegation> getDelegations() {
		return this.delegations;
	}

	/**
	 * @param delegations the delegations to set
	 */
	public void setDelegations(List<RoleDocumentDelegation> delegations) {
		this.delegations = delegations;
	}

	/**
	 * @return the delegationMembers
	 */
	public List<RoleDocumentDelegationMember> getDelegationMembers() {
		return this.delegationMembers;
	}

	/**
	 * @param delegationMembers the delegationMembers to set
	 */
	public void setDelegationMembers(
			List<RoleDocumentDelegationMember> delegationMembers) {
		this.delegationMembers = delegationMembers;
	}
	
	protected SequenceAccessorService getSequenceAccessorService(){
		if(this.sequenceAccessorService==null){
	    	this.sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return this.sequenceAccessorService;
	}

    public String getKimAttributeDefnId(AttributeDefinition definition){
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	}
    }

}
