/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@MappedSuperclass
@AttributeOverrides({
	@AttributeOverride(name="documentNumber",column=@Column(name="FDOC_NBR"))
})
public class IdentityManagementKimDocument extends TransactionalDocumentBase {
	private static final long serialVersionUID = 1L;

    protected static final Logger LOG = Logger.getLogger(IdentityManagementKimDocument.class);
	
	@OneToMany(targetEntity=RoleDocumentDelegation.class, fetch=FetchType.EAGER, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
	protected List<RoleDocumentDelegation> delegations = new AutoPopulatingList<RoleDocumentDelegation>(RoleDocumentDelegation.class);
	@Transient
	protected List<RoleDocumentDelegationMember> delegationMembers = new AutoPopulatingList<RoleDocumentDelegationMember>(RoleDocumentDelegationMember.class);
	
	protected void addDelegationMemberToDelegation(RoleDocumentDelegationMember delegationMember){
		// the statement below will lazily load the RoleBo onto our delegation member so that we have access to i
		delegationMember.loadTransientRoleFields();
		// now that we've done that we can fetch the RoleBo
		RoleBo role = delegationMember.getRoleBo();
		RoleDocumentDelegation delegation;
		if (DelegationType.PRIMARY.getCode().equals(delegationMember.getDelegationTypeCode())) {
			delegation = getPrimaryDelegation(role);
			// if the delegation type was changed on the document, we need to make sure we remove the member from the secondary delegation
			getSecondaryDelegation(role).getMembers().remove(delegationMember);
		} else {
			delegation = getSecondaryDelegation(role);
			// if the delegation type was changed on the document, we need to make sure we remove the member from the primary delegation
			getPrimaryDelegation(role).getMembers().remove(delegationMember);
		}
		delegationMember.setDelegationId(delegation.getDelegationId());
		if (!delegation.getMembers().contains(delegationMember)) {
			delegation.getMembers().add(delegationMember);
		}
	}

	protected RoleDocumentDelegation getPrimaryDelegation(RoleBo role) {
		RoleDocumentDelegation primaryDelegation = null;
		for(RoleDocumentDelegation delegation: getDelegations()){
			if(role.getId().equals(delegation.getRoleId()) && delegation.isDelegationPrimary()) {
				primaryDelegation = delegation;
				break;
            }
		}
		if(primaryDelegation == null) {
			primaryDelegation = new RoleDocumentDelegation();
			primaryDelegation.setRoleId(role.getId());
			primaryDelegation.setKimTypeId(role.getKimTypeId());
			primaryDelegation.setDelegationId(getDelegationId());
			primaryDelegation.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
            primaryDelegation.setDocumentNumber(getDocumentNumber());
			getDelegations().add(primaryDelegation);
		}
		return primaryDelegation;
	}

	protected String getDelegationId(){
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_DLGN_ID_S);
        return incrementer.nextStringValue();
	}
	
	protected RoleDocumentDelegation getSecondaryDelegation(RoleBo role) {
		RoleDocumentDelegation secondaryDelegation = null;
		for (RoleDocumentDelegation delegation: getDelegations()) {
			if (role.getId().equals(delegation.getRoleId()) && delegation.isDelegationSecondary()) {
				secondaryDelegation = delegation;
				break;
            }
		}
		if(secondaryDelegation == null) {
			secondaryDelegation = new RoleDocumentDelegation();
			secondaryDelegation.setRoleId(role.getId());
			secondaryDelegation.setKimTypeId(role.getKimTypeId());
			secondaryDelegation.setDelegationId(getDelegationId());
			secondaryDelegation.setDelegationTypeCode(DelegationType.SECONDARY.getCode());
            secondaryDelegation.setDocumentNumber(getDocumentNumber());
			getDelegations().add(secondaryDelegation);
		}
		return secondaryDelegation;
	}

	public List<RoleDocumentDelegation> getDelegations() {
		return this.delegations;
	}

	public void setDelegations(List<RoleDocumentDelegation> delegations) {
		this.delegations = delegations;
	}

	public List<RoleDocumentDelegationMember> getDelegationMembers() {
		return this.delegationMembers;
	}

	public void setDelegationMembers(
			List<RoleDocumentDelegationMember> delegationMembers) {
		this.delegationMembers = delegationMembers;
	}

    public String getKimAttributeDefnId(KimAttributeField definition){
   		return definition.getId();
    }

}
