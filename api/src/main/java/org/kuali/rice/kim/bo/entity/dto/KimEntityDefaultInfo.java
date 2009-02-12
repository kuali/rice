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

import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityDefaultInfo extends KimInactivatableInfo {

	protected String entityId;
	protected KimEntityName defaultName;
	protected List<? extends KimPrincipal> principals;
	protected List<KimEntityEntityTypeDefaultInfo> entityTypes;
	protected List<? extends KimEntityAffiliation> affiliations;
	protected KimEntityAffiliation defaultAffiliation;
	protected KimEntityEmploymentInformation primaryEmployment;
	protected List<? extends KimEntityExternalIdentifier> externalIdentifiers;
	
	public String getEntityId() {
		return this.entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public KimEntityName getDefaultName() {
		return this.defaultName;
	}
	public void setDefaultName(KimEntityName defaultName) {
		this.defaultName = defaultName;
	}
	public List<KimEntityEntityTypeDefaultInfo> getEntityTypes() {
		return this.entityTypes;
	}
	public void setEntityTypes(List<KimEntityEntityTypeDefaultInfo> entityTypes) {
		this.entityTypes = entityTypes;
	}
	public List<? extends KimEntityAffiliation> getAffiliations() {
		return this.affiliations;
	}
	public void setAffiliations(List<? extends KimEntityAffiliation> affiliations) {
		this.affiliations = affiliations;
	}
	public KimEntityAffiliation getDefaultAffiliation() {
		return this.defaultAffiliation;
	}
	public void setDefaultAffiliation(KimEntityAffiliation defaultAffiliation) {
		this.defaultAffiliation = defaultAffiliation;
	}
	public KimEntityEmploymentInformation getPrimaryEmployment() {
		return this.primaryEmployment;
	}
	public void setPrimaryEmployment(KimEntityEmploymentInformation primaryEmployment) {
		this.primaryEmployment = primaryEmployment;
	}
	public List<? extends KimEntityExternalIdentifier> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}
	public void setExternalIdentifiers(List<? extends KimEntityExternalIdentifier> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}
	public List<? extends KimPrincipal> getPrincipals() {
		return this.principals;
	}
	public void setPrincipals(List<? extends KimPrincipal> principals) {
		this.principals = principals;
	}
	
	public KimEntityEntityTypeDefaultInfo getEntityType(String entityTypeCode) {
		for ( KimEntityEntityTypeDefaultInfo entType : entityTypes ) {
			if ( entType.getEntityTypeCode().equals( entityTypeCode ) ) {
				return entType;
			}
		}
		return null;
	}
	
}
