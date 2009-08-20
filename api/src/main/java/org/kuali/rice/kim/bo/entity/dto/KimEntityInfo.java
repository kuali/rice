/*
 * Copyright 2007 The Kuali Foundation
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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.getDefaultAndUnNullify;
import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityBioDemographics;
import org.kuali.rice.kim.bo.entity.KimEntityCitizenship;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * This is a data transfer objects containing all information related to a KIM entity.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityInfo extends KimInactivatableInfo implements KimEntity {

	private static final long serialVersionUID = 1L;

	private List<? extends KimEntityAffiliation> affiliations;
	private KimEntityBioDemographics bioDemographics;
	private List<? extends KimEntityCitizenship> citizenships;
	private List<? extends KimEntityEmploymentInformation> employmentInformation;
	private String entityId;
	private List<? extends KimEntityEntityType> entityTypes;
	private List<? extends KimEntityExternalIdentifier> externalIdentifiers;
	private List<? extends KimEntityName> names;
	private List<? extends KimPrincipal> principals;
	private KimEntityPrivacyPreferences privacyPreferences;
	
	/**
	 * Retrieves all the entity affiliations.
	 * 
	 * @return affiliations
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getAffiliations()
	 */
	public List<? extends KimEntityAffiliation> getAffiliations() {
		return unNullify(this.affiliations);
	}
	
	/**
	 * Sets the entity affiliations.
	 * @param affiliations the affiliations
	 */
	public void setAffiliations(List<? extends KimEntityAffiliation> affiliations) {
		this.affiliations = affiliations;
	}

	/**
	 * Retrieves the entity bio demographics.
	 * 
	 * @return bio demographics
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getBioDemographics()
	 */
	public KimEntityBioDemographics getBioDemographics() {
		return unNullify(this.bioDemographics, KimEntityBioDemographicsInfo.class);
	}
	
	/**
	 * Sets the entity bio demographics.
	 * 
	 * @param bioDemographics the bio demographics
	 */
	public void setBioDemographics(KimEntityBioDemographics bioDemographics) {
		this.bioDemographics = bioDemographics;
	}

	/**
	 * Retrieves all the entity citizenships.
	 * 
	 * @return citizenships
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getCitizenships()
	 */
	public List<? extends KimEntityCitizenship> getCitizenships() {
		return unNullify(this.citizenships);
	}
	
	/**
	 * Sets the entity citizenships.
	 * 
	 * @param citizenships the citizenships
	 */
	public void setCitizenships(List<? extends KimEntityCitizenship> citizenships) {
		this.citizenships = citizenships;
	}

	/**
	 * Retrieves the default affiliation.
	 * 
	 * @return default affiliation
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultAffiliation()
	 */
	public KimEntityAffiliation getDefaultAffiliation() {
		return getDefaultAndUnNullify(this.affiliations, KimEntityAffiliationInfo.class);
	}

	/**
	 * Retrieves the default name.
	 * 
	 * @return default name
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultName()
	 */
	public KimEntityName getDefaultName() {
		return getDefaultAndUnNullify(this.names, KimEntityNameInfo.class);
	}

	/**
	 * Retrieves all the employment information.
	 * 
	 * @return employment information
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEmploymentInformation()
	 */
	public List<? extends KimEntityEmploymentInformation> getEmploymentInformation() {
		return unNullify(this.employmentInformation);
	}
	
	/**
	 * Sets the employment information.
	 * 
	 * @param employmentInformation the employment information
	 */
	public void setEmploymentInformation(List<? extends KimEntityEmploymentInformation> employmentInformation) {
		this.employmentInformation = employmentInformation;
	}

	/**
	 * Retrieves the entity external identifier based on the identifier type code.
	 * 
	 * @return external identifier
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityExternalIdentifier(java.lang.String)
	 */
	public KimEntityExternalIdentifier getEntityExternalIdentifier(String externalIdentifierTypeCode) {
		for (KimEntityExternalIdentifier externalIdentifier : this.getExternalIdentifiers()) {
			if (externalIdentifier.getExternalIdentifierTypeCode().equals(externalIdentifierTypeCode)) {
				return externalIdentifier;
			}
		}
		return new KimEntityExternalIdentifierInfo();
	}

	/**
	 * Retrieves the entity id.
	 * 
	 * @return entity id
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityId()
	 */
	public String getEntityId() {
		return unNullify(this.entityId);
	}
	
	/**
	 * Sets the entity id.
	 * 
	 * @param entityId the entity id
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * Retrieves the entity type object from a type code.
	 * 
	 * @return entity type
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityType(java.lang.String)
	 */
	public KimEntityEntityType getEntityType(String entityTypeCode) {
		for (KimEntityEntityType entityType : this.getEntityTypes()) {
			if (entityType.getEntityTypeCode().equals(entityType)) {
				return entityType;
			}
		}
		return new KimEntityEntityTypeInfo();
	}

	/**
	 * Retrieves all the entity types.
	 * 
	 * @return entity types
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityTypes()
	 */
	public List<? extends KimEntityEntityType> getEntityTypes() {
		return unNullify(this.entityTypes);
	}
	
	/**
	 * Sets the entity types.
	 * 
	 * @param entityTypes the entity types
	 */
	public void setEntityTypes(List<? extends KimEntityEntityType> entityTypes) {
		this.entityTypes = entityTypes;
	}

	/**
	 * Retrieves all the entity external identifiers.
	 * 
	 * @return external identifiers
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getExternalIdentifiers()
	 */
	public List<? extends KimEntityExternalIdentifier> getExternalIdentifiers() {
		return unNullify(this.externalIdentifiers);
	}
	
	/**
	 * Sets the entity external identifiers.
	 * 
	 * @param externalIdentifiers the external identifiers
	 */
	public void setExternalIdentifiers(List<? extends KimEntityExternalIdentifier> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}

	/**
	 * Retrieves all the entity names.
	 * 
	 * @return names
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getNames()
	 */
	public List<? extends KimEntityName> getNames() {
		return unNullify(this.names);
	}
	
	/**
	 * Sets the entity names.
	 * 
	 * @param names the names
	 */
	public void setNames(List<? extends KimEntityName> names) {
		this.names = names;
	}

	/**
	 * Retrieves the primary employment.
	 * 
	 * @return primary employment
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getPrimaryEmployment()
	 */
	public KimEntityEmploymentInformation getPrimaryEmployment() {
		for (KimEntityEmploymentInformation primaryEmployment : this.getEmploymentInformation()) {
			if (primaryEmployment.isPrimary()) {
				return primaryEmployment;
			}
		}
		return new KimEntityEmploymentInformationInfo();
	}

	/**
	 * Retrieves all the entity principals.
	 * 
	 * @return principals
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getPrincipals()
	 */
	public List<? extends KimPrincipal> getPrincipals() {
		return unNullify(this.principals);
	}
	
	/**
	 * Sets the entity principals.
	 * 
	 * @param principals the principals
	 */
	public void setPrincipals(List<? extends KimPrincipal> principals) {
		this.principals = principals;
	}

	/**
	 * Retrieves the entity privacy preferences.
	 * 
	 * @return privacy preferences
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getPrivacyPreferences()
	 */
	public KimEntityPrivacyPreferences getPrivacyPreferences() {
		return unNullify(this.privacyPreferences, KimEntityPrivacyPreferencesInfo.class);
	}
	
	/**
	 * Sets the entity privacy preferences.
	 * 
	 * @param privacyPreferences the privacy preferences
	 */
	public void setPrivacyPreferences(KimEntityPrivacyPreferences privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}
	
	/** {@inheritDoc} */
    public void refresh(){
    	
    }
    
    /** {@inheritDoc} */
    public void prepareForWorkflow(){
    	
    }
}
