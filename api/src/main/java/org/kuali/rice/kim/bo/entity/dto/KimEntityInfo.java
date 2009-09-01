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

import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;

/**
 * This is a data transfer objects containing all information related to a KIM entity.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityInfo extends KimInactivatableInfo implements KimEntity {

	private static final long serialVersionUID = 1L;

	private List<KimEntityAffiliationInfo> affiliations;
	private KimEntityAffiliationInfo defaultAffiliation;
	private KimEntityBioDemographicsInfo bioDemographics;
	private List<KimEntityCitizenshipInfo> citizenships;
	private List<KimEntityEmploymentInformationInfo> employmentInformation;
	private KimEntityEmploymentInformationInfo primaryEmployment;
	private String entityId;
	private List<KimEntityEntityTypeInfo> entityTypes;
	private List<KimEntityExternalIdentifierInfo> externalIdentifiers;
	private List<KimEntityNameInfo> names;
	private KimEntityNameInfo defaultName;
	private List<KimPrincipalInfo> principals;
	private KimEntityPrivacyPreferencesInfo privacyPreferences;
	private List<KimEntityEthnicityInfo> ethnicities;
	private List<KimEntityResidencyInfo> residencies;
	private List<KimEntityVisaInfo> visas;

	public KimEntityInfo() {
		super();
		active = true;
	}

	/** {@inheritDoc} */
    public void refresh(){}

    /** {@inheritDoc} */
    public void prepareForWorkflow(){}

	/**
	 * @return the affiliations
	 */
	public List<KimEntityAffiliationInfo> getAffiliations() {
		return this.affiliations;
	}

	/**
	 * @param affiliations the affiliations to set
	 */
	public void setAffiliations(List<KimEntityAffiliationInfo> affiliations) {
		this.affiliations = affiliations;
	}

	/**
	 * @return the defaultAffiliation
	 */
	public KimEntityAffiliationInfo getDefaultAffiliation() {
		return this.defaultAffiliation;
	}

	/**
	 * @param defaultAffiliation the defaultAffiliation to set
	 */
	public void setDefaultAffiliation(KimEntityAffiliationInfo defaultAffiliation) {
		this.defaultAffiliation = defaultAffiliation;
	}

	/**
	 * @return the bioDemographics
	 */
	public KimEntityBioDemographicsInfo getBioDemographics() {
		return this.bioDemographics;
	}

	/**
	 * @param bioDemographics the bioDemographics to set
	 */
	public void setBioDemographics(KimEntityBioDemographicsInfo bioDemographics) {
		this.bioDemographics = bioDemographics;
	}

	/**
	 * @return the citizenships
	 */
	public List<KimEntityCitizenshipInfo> getCitizenships() {
		return this.citizenships;
	}

	/**
	 * @param citizenships the citizenships to set
	 */
	public void setCitizenships(List<KimEntityCitizenshipInfo> citizenships) {
		this.citizenships = citizenships;
	}

	/**
	 * @return the employmentInformation
	 */
	public List<KimEntityEmploymentInformationInfo> getEmploymentInformation() {
		return this.employmentInformation;
	}

	/**
	 * @param employmentInformation the employmentInformation to set
	 */
	public void setEmploymentInformation(
			List<KimEntityEmploymentInformationInfo> employmentInformation) {
		this.employmentInformation = employmentInformation;
	}

	/**
	 * @return the primaryEmployment
	 */
	public KimEntityEmploymentInformationInfo getPrimaryEmployment() {
		return this.primaryEmployment;
	}

	/**
	 * @param primaryEmployment the primaryEmployment to set
	 */
	public void setPrimaryEmployment(
			KimEntityEmploymentInformationInfo primaryEmployment) {
		this.primaryEmployment = primaryEmployment;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return this.entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entityTypes
	 */
	public List<KimEntityEntityTypeInfo> getEntityTypes() {
		return this.entityTypes;
	}

	/**
	 * @param entityTypes the entityTypes to set
	 */
	public void setEntityTypes(List<KimEntityEntityTypeInfo> entityTypes) {
		this.entityTypes = entityTypes;
	}

	/**
	 * @return the externalIdentifiers
	 */
	public List<KimEntityExternalIdentifierInfo> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}

	/**
	 * @param externalIdentifiers the externalIdentifiers to set
	 */
	public void setExternalIdentifiers(
			List<KimEntityExternalIdentifierInfo> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}

	/**
	 * @return the names
	 */
	public List<KimEntityNameInfo> getNames() {
		return this.names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(List<KimEntityNameInfo> names) {
		this.names = names;
	}

	/**
	 * @return the defaultName
	 */
	public KimEntityNameInfo getDefaultName() {
		return this.defaultName;
	}

	/**
	 * @param defaultName the defaultName to set
	 */
	public void setDefaultName(KimEntityNameInfo defaultName) {
		this.defaultName = defaultName;
	}

	/**
	 * @return the principals
	 */
	public List<KimPrincipalInfo> getPrincipals() {
		return this.principals;
	}

	/**
	 * @param principals the principals to set
	 */
	public void setPrincipals(List<KimPrincipalInfo> principals) {
		this.principals = principals;
	}

	/**
	 * @return the privacyPreferences
	 */
	public KimEntityPrivacyPreferencesInfo getPrivacyPreferences() {
		return this.privacyPreferences;
	}

	/**
	 * @param privacyPreferences the privacyPreferences to set
	 */
	public void setPrivacyPreferences(
			KimEntityPrivacyPreferencesInfo privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}

	/**
	 * @return the ethnicities
	 */
	public List<KimEntityEthnicityInfo> getEthnicities() {
		return this.ethnicities;
	}

	/**
	 * @param ethnicities the ethnicities to set
	 */
	public void setEthnicities(List<KimEntityEthnicityInfo> ethnicities) {
		this.ethnicities = ethnicities;
	}

	/**
	 * @return the residencies
	 */
	public List<KimEntityResidencyInfo> getResidencies() {
		return this.residencies;
	}

	/**
	 * @param residencies the residencies to set
	 */
	public void setResidencies(List<KimEntityResidencyInfo> residencies) {
		this.residencies = residencies;
	}

	/**
	 * @return the visas
	 */
	public List<KimEntityVisaInfo> getVisas() {
		return this.visas;
	}

	/**
	 * @param visas the visas to set
	 */
	public void setVisas(List<KimEntityVisaInfo> visas) {
		this.visas = visas;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityExternalIdentifier(java.lang.String)
	 */
	public KimEntityExternalIdentifier getEntityExternalIdentifier(String externalIdentifierTypeCode) {
		return null;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityType(java.lang.String)
	 */
	public KimEntityEntityType getEntityType(String entityTypeCode) {
		return null;
	}

}
