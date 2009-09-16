/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityName;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimEntityDefaultInfo extends KimInactivatableInfo {

	private static final long serialVersionUID = 7930630152792502380L;
	protected String entityId;
	protected KimEntityNameInfo defaultName;
	protected List<KimPrincipalInfo> principals;
	protected List<KimEntityEntityTypeDefaultInfo> entityTypes;
	protected List<KimEntityAffiliationInfo> affiliations;
	protected KimEntityAffiliationInfo defaultAffiliation;
	protected KimEntityEmploymentInformationInfo primaryEmployment;
	protected List<KimEntityExternalIdentifierInfo> externalIdentifiers;
	protected KimEntityPrivacyPreferencesInfo privacyPreferences;
	
	public String getEntityId() {
		return this.entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public KimEntityNameInfo getDefaultName() {
		return this.defaultName;
	}
	public void setDefaultName(KimEntityName defaultName) {
		this.defaultName = new KimEntityNameInfo(defaultName);
	}
	public List<KimEntityEntityTypeDefaultInfo> getEntityTypes() {
		return this.entityTypes;
	}
	public void setEntityTypes(List<KimEntityEntityTypeDefaultInfo> entityTypes) {
		this.entityTypes = entityTypes;
	}
	public List<KimEntityAffiliationInfo> getAffiliations() {
		return this.affiliations;
	}
	public void setAffiliations(List<KimEntityAffiliationInfo> affiliations) {
		this.affiliations = affiliations;
	}
	public KimEntityAffiliationInfo getDefaultAffiliation() {
		return this.defaultAffiliation;
	}
	public void setDefaultAffiliation(KimEntityAffiliation defaultAffiliation) {
		this.defaultAffiliation = new KimEntityAffiliationInfo(defaultAffiliation);
	}
	public KimEntityEmploymentInformationInfo getPrimaryEmployment() {
		return this.primaryEmployment;
	}
	public void setPrimaryEmployment(KimEntityEmploymentInformation primaryEmployment) {
		this.primaryEmployment = new KimEntityEmploymentInformationInfo(primaryEmployment);
	}
	public List<KimEntityExternalIdentifierInfo> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}
	public void setExternalIdentifiers(List<KimEntityExternalIdentifierInfo> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}
	public List<KimPrincipalInfo> getPrincipals() {
		return this.principals;
	}
	public void setPrincipals(List<KimPrincipalInfo> principals) {
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
    public KimEntityPrivacyPreferencesInfo getPrivacyPreferences() {
        return this.privacyPreferences;
    }
    public void setPrivacyPreferences(
            KimEntityPrivacyPreferencesInfo privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }
	
}
