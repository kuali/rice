/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityDefaultInfo extends KimInactivatableInfo implements ExternalizableBusinessObject {

	private static final long serialVersionUID = 1L;
	
	protected String entityId;
	protected KimEntityName defaultName;
	protected List<? extends KimPrincipal> principals;
	protected List<KimEntityEntityTypeDefaultInfo> entityTypes;
	protected List<? extends KimEntityAffiliation> affiliations;
	protected KimEntityAffiliation defaultAffiliation;
	protected KimEntityEmploymentInformation primaryEmployment;
	protected List<? extends KimEntityExternalIdentifier> externalIdentifiers;
	
	public String getEntityId() {
		return unNullify(this.entityId);
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public KimEntityName getDefaultName() {
		return unNullify(this.defaultName, KimEntityNameInfo.class);
	}
	public void setDefaultName(KimEntityName defaultName) {
		this.defaultName = defaultName;
	}
	public List<KimEntityEntityTypeDefaultInfo> getEntityTypes() {
		return unNullify(this.entityTypes);
	}
	public void setEntityTypes(List<KimEntityEntityTypeDefaultInfo> entityTypes) {
		this.entityTypes = entityTypes;
	}
	public List<? extends KimEntityAffiliation> getAffiliations() {
		return unNullify(this.affiliations);
	}
	public void setAffiliations(List<? extends KimEntityAffiliation> affiliations) {
		this.affiliations = affiliations;
	}
	public KimEntityAffiliation getDefaultAffiliation() {
		return unNullify(this.defaultAffiliation, KimEntityAffiliationInfo.class);
	}
	public void setDefaultAffiliation(KimEntityAffiliation defaultAffiliation) {
		this.defaultAffiliation = defaultAffiliation;
	}
	public KimEntityEmploymentInformation getPrimaryEmployment() {
		return unNullify(this.primaryEmployment, KimEntityEmploymentInformationInfo.class);
	}
	public void setPrimaryEmployment(KimEntityEmploymentInformation primaryEmployment) {
		this.primaryEmployment = primaryEmployment;
	}
	public List<? extends KimEntityExternalIdentifier> getExternalIdentifiers() {
		return unNullify(this.externalIdentifiers);
	}
	public void setExternalIdentifiers(List<? extends KimEntityExternalIdentifier> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}
	public List<? extends KimPrincipal> getPrincipals() {
		return unNullify(this.principals);
	}
	public void setPrincipals(List<? extends KimPrincipal> principals) {
		this.principals = principals;
	}
	
	public KimEntityEntityTypeDefaultInfo getEntityType(String entityTypeCode) {
		for ( KimEntityEntityTypeDefaultInfo entType : this.getEntityTypes() ) {
			if ( entType.getEntityTypeCode().equals( entityTypeCode ) ) {
				return entType;
			}
		}
		return new KimEntityEntityTypeDefaultInfo();
	}
	
	/** {@inheritDoc} */
    public void refresh(){
    	
    }
    
    /** {@inheritDoc} */
    public void prepareForWorkflow(){
    	
    }
	
}
