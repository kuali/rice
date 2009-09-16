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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityCitizenship;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.entity.KimEntityResidency;
import org.kuali.rice.kim.bo.entity.KimEntityVisa;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

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

	public KimEntityInfo(KimEntity entity) {
		this();
		this.setEntityId( entity.getEntityId() );
		this.setActive( entity.isActive() );
		ArrayList<KimPrincipalInfo> principalInfo = new ArrayList<KimPrincipalInfo>( entity.getPrincipals().size() );
		this.setPrincipals( principalInfo );
		for ( KimPrincipal p : entity.getPrincipals() ) {
			principalInfo.add( new KimPrincipalInfo( p ) );
		}
		KimEntityBioDemographicsInfo bioDemo = null;
		if ( entity.getBioDemographics() != null ) {
			bioDemo = new KimEntityBioDemographicsInfo(entity.getBioDemographics());
        }
		this.setBioDemographics(bioDemo);
		KimEntityPrivacyPreferencesInfo privacy = null;
		if ( entity.getPrivacyPreferences() != null ) {
            privacy = new KimEntityPrivacyPreferencesInfo(entity.getPrivacyPreferences());
        }
		this.setPrivacyPreferences(privacy);
		ArrayList<KimEntityNameInfo> nameInfos = new ArrayList<KimEntityNameInfo>( entity.getNames().size() );
		this.setNames(nameInfos);
		for ( KimEntityName p : entity.getNames() ) {
			nameInfos.add( new KimEntityNameInfo( p ) );
		}
		ArrayList<KimEntityEntityTypeInfo> entityTypesInfo = new ArrayList<KimEntityEntityTypeInfo>( entity.getEntityTypes().size() );
		this.setEntityTypes( entityTypesInfo );
		for ( KimEntityEntityType entityEntityType : entity.getEntityTypes() ) {
			KimEntityEntityTypeInfo typeInfo = new KimEntityEntityTypeInfo();
			typeInfo.setEntityTypeCode( entityEntityType.getEntityTypeCode() );
			ArrayList<KimEntityAddressInfo> addresses = new ArrayList<KimEntityAddressInfo>( entityEntityType.getAddresses().size() );
			typeInfo.setAddresses(addresses);
			for (KimEntityAddress kimEntityAddress : entityEntityType.getAddresses()) {
				addresses.add(new KimEntityAddressInfo(kimEntityAddress));
			}
			ArrayList<KimEntityEmailInfo> emailAddresses = new ArrayList<KimEntityEmailInfo>( entityEntityType.getAddresses().size() );
			typeInfo.setEmailAddresses(emailAddresses);
			for (KimEntityEmail kimEntityEmailAddress : entityEntityType.getEmailAddresses()) {
				emailAddresses.add(new KimEntityEmailInfo(kimEntityEmailAddress));
			}
			ArrayList<KimEntityPhoneInfo> phoneNumbers = new ArrayList<KimEntityPhoneInfo>( entityEntityType.getPhoneNumbers().size() );
			typeInfo.setPhoneNumbers(phoneNumbers);
			for (KimEntityPhone kimEntityPhone : entityEntityType.getPhoneNumbers()) {
				phoneNumbers.add(new KimEntityPhoneInfo(kimEntityPhone));
			}
			entityTypesInfo.add( typeInfo );
		}
		ArrayList<KimEntityAffiliationInfo> affInfo = new ArrayList<KimEntityAffiliationInfo>( entity.getAffiliations().size() );
		this.setAffiliations( affInfo );
		for ( KimEntityAffiliation aff : entity.getAffiliations() ) {
			affInfo.add( new KimEntityAffiliationInfo( aff ) );
		}
		ArrayList<KimEntityEmploymentInformationInfo> employmentInfos = new ArrayList<KimEntityEmploymentInformationInfo>( entity.getEmploymentInformation().size() );
		this.setEmploymentInformation( employmentInfos );
		for ( KimEntityEmploymentInformation emp : entity.getEmploymentInformation() ) {
			employmentInfos.add( new KimEntityEmploymentInformationInfo( emp ) );
		}
		this.setPrimaryEmployment(new KimEntityEmploymentInformationInfo(entity.getPrimaryEmployment()));
		ArrayList<KimEntityExternalIdentifierInfo> idInfo = new ArrayList<KimEntityExternalIdentifierInfo>( entity.getExternalIdentifiers().size() );
		this.setExternalIdentifiers( idInfo );
		for ( KimEntityExternalIdentifier id : entity.getExternalIdentifiers() ) {
			idInfo.add( new KimEntityExternalIdentifierInfo( id ) );
		}
		ArrayList<KimEntityCitizenshipInfo> citizenships = new ArrayList<KimEntityCitizenshipInfo>( entity.getCitizenships().size() );
		this.setCitizenships(citizenships);
		for ( KimEntityCitizenship citizenship : entity.getCitizenships() ) {
			citizenships.add( new KimEntityCitizenshipInfo( citizenship ) );
		}
		ArrayList<KimEntityEthnicityInfo> ethnicities = new ArrayList<KimEntityEthnicityInfo>( entity.getEthnicities().size() );
		this.setEthnicities( ethnicities );
		for ( KimEntityEthnicity ethnicity : entity.getEthnicities() ) {
			ethnicities.add( new KimEntityEthnicityInfo( ethnicity ) );
		}
		ArrayList<KimEntityResidencyInfo> residencies = new ArrayList<KimEntityResidencyInfo>( entity.getResidencies().size() );
		this.setResidencies(residencies);
		for ( KimEntityResidency residence : entity.getResidencies() ) {
			residencies.add( new KimEntityResidencyInfo( residence ) );
		}
		ArrayList<KimEntityVisaInfo> visas = new ArrayList<KimEntityVisaInfo>( entity.getVisas().size() );
		this.setVisas( visas );
		for ( KimEntityVisa visa : entity.getVisas() ) {
			visas.add( new KimEntityVisaInfo( visa ) );
		}
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
