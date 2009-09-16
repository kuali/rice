/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.impl;

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@SuppressWarnings("unchecked")
@Entity
@Table(name = "KRIM_ENTITY_T")
public class KimEntityImpl extends KimInactivatableEntityDataBase implements KimEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;

	@OneToMany(targetEntity = KimEntityNameImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityNameImpl> names = new TypedArrayList(KimEntityNameImpl.class);
		
	@OneToMany(targetEntity = KimEntityEntityTypeImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected List<KimEntityEntityTypeImpl> entityTypes = new TypedArrayList(KimEntityEntityTypeImpl.class);

	@OneToMany(targetEntity = KimPrincipalImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected List<KimPrincipalImpl> principals = new TypedArrayList(KimPrincipalImpl.class);

	@OneToMany(targetEntity=KimEntityExternalIdentifierImpl.class,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<KimEntityExternalIdentifierImpl> externalIdentifiers = new TypedArrayList(KimEntityExternalIdentifierImpl.class);

	@OneToMany(targetEntity=KimEntityAffiliationImpl.class,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<KimEntityAffiliationImpl> affiliations = new TypedArrayList(KimEntityAffiliationImpl.class);

	@OneToMany(targetEntity=KimEntityEmploymentInformationImpl.class,fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<KimEntityEmploymentInformationImpl> employmentInformation = new TypedArrayList(KimEntityEmploymentInformationImpl.class);

	@OneToOne(targetEntity=KimEntityPrivacyPreferencesImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected KimEntityPrivacyPreferencesImpl privacyPreferences;

	@OneToOne(targetEntity=KimEntityBioDemographicsImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected KimEntityBioDemographicsImpl bioDemographics;
	
	@OneToMany(targetEntity = KimEntityCitizenshipImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityCitizenshipImpl> citizenships = new TypedArrayList(KimEntityCitizenshipImpl.class);

	@OneToMany(targetEntity = KimEntityEthnicityImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityEthnicityImpl> ethnicities = new TypedArrayList(KimEntityEthnicityImpl.class);

	@OneToMany(targetEntity = KimEntityResidencyImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityResidencyImpl> residencies = new TypedArrayList(KimEntityResidencyImpl.class);

	@OneToMany(targetEntity = KimEntityVisaImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityVisaImpl> visas = new TypedArrayList(KimEntityVisaImpl.class);

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return this.entityId;
	}

	/**
	 * @param entityId
	 *            the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entityTypes
	 */
	public List<KimEntityEntityTypeImpl> getEntityTypes() {
		return this.entityTypes;
	}

	/**
	 * @param entityTypes
	 *            the entityTypes to set
	 */
	public void setEntityTypes(List<KimEntityEntityTypeImpl> entityTypes) {
		this.entityTypes = entityTypes;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put("entityId", entityId);
		return lhm;
	}

	/**
	 * @return the externalIdentifiers
	 */
	public List<KimEntityExternalIdentifierImpl> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}

	/**
	 * @param externalIdentifiers
	 *            the externalIdentifiers to set
	 */
	public void setExternalIdentifiers(List<KimEntityExternalIdentifierImpl> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}

	/**
	 * @return the privacyPreferences
	 */
	public KimEntityPrivacyPreferencesImpl getPrivacyPreferences() {
	    if (ObjectUtils.isNull(this.privacyPreferences)) {
	        return null;
	    }
		return this.privacyPreferences;
	}

	/**
	 * @param privacyPreferences
	 *            the privacyPreferences to set
	 */
	public void setPrivacyPreferences(KimEntityPrivacyPreferencesImpl privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}

	/**
	 * @return the bioDemographics
	 */
	public KimEntityBioDemographicsImpl getBioDemographics() {
	    if (ObjectUtils.isNull(this.bioDemographics)) {
            return null;
        }
		return this.bioDemographics;
	}

	/**
	 * @param bioDemographics
	 *            the bioDemographics to set
	 */
	public void setBioDemographics(KimEntityBioDemographicsImpl bioDemographics) {
		this.bioDemographics = bioDemographics;
	}

	/**
	 * @return the affiliations
	 */
	public List<KimEntityAffiliationImpl> getAffiliations() {
		return this.affiliations;
	}

	/**
	 * @param affiliations
	 *            the affiliations to set
	 */
	public void setAffiliations(List<KimEntityAffiliationImpl> affiliations) {
		this.affiliations = affiliations;
	}

	/**
	 * @return the employmentInformation
	 */
	public List<KimEntityEmploymentInformationImpl> getEmploymentInformation() {
		return this.employmentInformation;
	}

	/**
	 * @param employmentInformation
	 *            the employmentInformation to set
	 */
	public void setEmploymentInformation(List<KimEntityEmploymentInformationImpl> employmentInformation) {
		this.employmentInformation = employmentInformation;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getPrincipals()
	 */
	public List<KimPrincipalImpl> getPrincipals() {
		return principals;
	}
	
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityType(java.lang.String)
	 */
	public KimEntityEntityTypeImpl getEntityType(String entityTypeCode) {
		for ( KimEntityEntityTypeImpl entType : entityTypes ) {
			if ( entType.getEntityTypeCode().equals( entityTypeCode ) ) {
				return entType;
			}
		}
		return null;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultAffiliation()
	 */
	public KimEntityAffiliationImpl getDefaultAffiliation() {
		return (KimEntityAffiliationImpl)getDefaultItem( affiliations );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityExternalIdentifier(java.lang.String)
	 */
	public KimEntityExternalIdentifierImpl getEntityExternalIdentifier(String externalIdentifierTypeCode) {
		for ( KimEntityExternalIdentifierImpl id : externalIdentifiers ) {
			if ( id.getExternalIdentifierTypeCode().equals(  externalIdentifierTypeCode  ) ) {
				return id;
			}
		}
		return null;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getPrimaryEmployment()
	 */
	public KimEntityEmploymentInformationImpl getPrimaryEmployment() {
		for ( KimEntityEmploymentInformationImpl emp : employmentInformation ) {
			if ( emp.isActive() && emp.isPrimary() ) {
				return emp;
			}
		}
		return null;
	}

	public List<KimEntityNameImpl> getNames() {
		return this.names;
	}

	public void setNames(List<KimEntityNameImpl> names) {
		this.names = names;
	}	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultName()
	 */
	public KimEntityNameImpl getDefaultName() {
		return (KimEntityNameImpl)getDefaultItem( names );
	}

	/**
	 * @return the citizenships
	 */
	public List<KimEntityCitizenshipImpl> getCitizenships() {
		return this.citizenships;
	}

	/**
	 * @param citizenships the citizenships to set
	 */
	public void setCitizenships(List<KimEntityCitizenshipImpl> citizenships) {
		this.citizenships = citizenships;
	}

	/**
	 * @return the ethnicities
	 */
	public List<KimEntityEthnicityImpl> getEthnicities() {
		return this.ethnicities;
	}

	/**
	 * @param ethnicities the ethnicities to set
	 */
	public void setEthnicities(List<KimEntityEthnicityImpl> ethnicities) {
		this.ethnicities = ethnicities;
	}

	/**
	 * @return the residencies
	 */
	public List<KimEntityResidencyImpl> getResidencies() {
		return this.residencies;
	}

	/**
	 * @param residencies the residencies to set
	 */
	public void setResidencies(List<KimEntityResidencyImpl> residencies) {
		this.residencies = residencies;
	}

	/**
	 * @return the visas
	 */
	public List<KimEntityVisaImpl> getVisas() {
		return this.visas;
	}

	/**
	 * @param visas the visas to set
	 */
	public void setVisas(List<KimEntityVisaImpl> visas) {
		this.visas = visas;
	}

	public void setPrincipals(List<KimPrincipalImpl> principals) {
		this.principals = principals;
	}
	
	/** This possibly fixes an issue with afterLookup not following references and calling afterLookup?
	@Override
	public void afterLookup(PersistenceBroker persistenceBroker)
			throws PersistenceBrokerException {
		// afterLookup is not called recursively on all referenced objects, external entities can be encrypted coming out of the db, so let's resolve this by manaully walking down
		super.afterLookup(persistenceBroker);
		if (getExternalIdentifiers() != null) {
			for (KimEntityExternalIdentifierImpl extId : getExternalIdentifiers()) {
				extId.afterLookup(persistenceBroker);
			}
		}
	}	
	*/
	
}
