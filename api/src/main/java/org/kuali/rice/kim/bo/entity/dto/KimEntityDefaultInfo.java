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

import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.EntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityDefaultInfo extends KimInactivatableInfo {

	protected String entityId;
	protected EntityName defaultName;
	protected List<? extends KimPrincipal> principals;
	protected List<KimEntityEntityTypeDefaultInfo> entityTypes;
	protected List<? extends EntityAffiliation> affiliations;
	protected EntityAffiliation defaultAffiliation;
	protected EntityEmploymentInformation primaryEmployment;
	protected List<? extends EntityExternalIdentifier> externalIdentifiers;
	
	public String getEntityId() {
		return this.entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public EntityName getDefaultName() {
		return this.defaultName;
	}
	public void setDefaultName(EntityName defaultName) {
		this.defaultName = defaultName;
	}
	public List<KimEntityEntityTypeDefaultInfo> getEntityTypes() {
		return this.entityTypes;
	}
	public void setEntityTypes(List<KimEntityEntityTypeDefaultInfo> entityTypes) {
		this.entityTypes = entityTypes;
	}
	public List<? extends EntityAffiliation> getAffiliations() {
		return this.affiliations;
	}
	public void setAffiliations(List<? extends EntityAffiliation> affiliations) {
		this.affiliations = affiliations;
	}
	public EntityAffiliation getDefaultAffiliation() {
		return this.defaultAffiliation;
	}
	public void setDefaultAffiliation(EntityAffiliation defaultAffiliation) {
		this.defaultAffiliation = defaultAffiliation;
	}
	public EntityEmploymentInformation getPrimaryEmployment() {
		return this.primaryEmployment;
	}
	public void setPrimaryEmployment(EntityEmploymentInformation primaryEmployment) {
		this.primaryEmployment = primaryEmployment;
	}
	public List<? extends EntityExternalIdentifier> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}
	public void setExternalIdentifiers(List<? extends EntityExternalIdentifier> externalIdentifiers) {
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
