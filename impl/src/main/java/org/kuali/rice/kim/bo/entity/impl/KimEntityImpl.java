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
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
@SuppressWarnings("unchecked")
@Entity
@Table(name = "KRIM_ENTITY_T")
public class KimEntityImpl extends InactivatableEntityDataBase implements KimEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;

	@OneToMany(targetEntity = EntityNameImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityNameImpl> names = new TypedArrayList(EntityNameImpl.class);
		
	@OneToMany(targetEntity = EntityEntityTypeImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected List<EntityEntityTypeImpl> entityTypes = new TypedArrayList(EntityEntityTypeImpl.class);

	@OneToMany(targetEntity = KimPrincipalImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected List<KimPrincipalImpl> principals = new TypedArrayList(KimPrincipalImpl.class);

	@OneToMany(targetEntity=EntityExternalIdentifierImpl.class,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<EntityExternalIdentifierImpl> externalIdentifiers = new TypedArrayList(EntityExternalIdentifierImpl.class);

	@OneToMany(targetEntity=EntityAffiliationImpl.class,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<EntityAffiliationImpl> affiliations = new TypedArrayList(EntityAffiliationImpl.class);

	@OneToMany(targetEntity=EntityEmploymentInformationImpl.class,fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<EntityEmploymentInformationImpl> employmentInformation = new TypedArrayList(EntityEmploymentInformationImpl.class);

	@OneToOne(targetEntity=EntityPrivacyPreferencesImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected EntityPrivacyPreferencesImpl privacyPreferences;

	@OneToOne(targetEntity=EntityBioDemographicsImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected EntityBioDemographicsImpl bioDemographics;
	
	@OneToMany(targetEntity = EntityCitizenshipImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityCitizenshipImpl> citizenships = new TypedArrayList(EntityCitizenshipImpl.class);

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
	public List<EntityEntityTypeImpl> getEntityTypes() {
		return this.entityTypes;
	}

	/**
	 * @param entityTypes
	 *            the entityTypes to set
	 */
	public void setEntityTypes(List<EntityEntityTypeImpl> entityTypes) {
		this.entityTypes = entityTypes;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put("entityId", entityId);
		return lhm;
	}

	/**
	 * @return the externalIdentifiers
	 */
	public List<EntityExternalIdentifierImpl> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}

	/**
	 * @param externalIdentifiers
	 *            the externalIdentifiers to set
	 */
	public void setExternalIdentifiers(List<EntityExternalIdentifierImpl> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}

	/**
	 * @return the privacyPreferences
	 */
	public EntityPrivacyPreferencesImpl getPrivacyPreferences() {
		return this.privacyPreferences;
	}

	/**
	 * @param privacyPreferences
	 *            the privacyPreferences to set
	 */
	public void setPrivacyPreferences(EntityPrivacyPreferencesImpl privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}

	/**
	 * @return the bioDemographics
	 */
	public EntityBioDemographicsImpl getBioDemographics() {
		return this.bioDemographics;
	}

	/**
	 * @param bioDemographics
	 *            the bioDemographics to set
	 */
	public void setBioDemographics(EntityBioDemographicsImpl bioDemographics) {
		this.bioDemographics = bioDemographics;
	}

	/**
	 * @return the affiliations
	 */
	public List<EntityAffiliationImpl> getAffiliations() {
		return this.affiliations;
	}

	/**
	 * @param affiliations
	 *            the affiliations to set
	 */
	public void setAffiliations(List<EntityAffiliationImpl> affiliations) {
		this.affiliations = affiliations;
	}

	/**
	 * @return the employmentInformation
	 */
	public List<EntityEmploymentInformationImpl> getEmploymentInformation() {
		return this.employmentInformation;
	}

	/**
	 * @param employmentInformation
	 *            the employmentInformation to set
	 */
	public void setEmploymentInformation(List<EntityEmploymentInformationImpl> employmentInformation) {
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
	public EntityEntityTypeImpl getEntityType(String entityTypeCode) {
		for ( EntityEntityTypeImpl entType : entityTypes ) {
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
	public EntityAffiliationImpl getDefaultAffiliation() {
		return (EntityAffiliationImpl)getDefaultItem( affiliations );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityExternalIdentifier(java.lang.String)
	 */
	public EntityExternalIdentifierImpl getEntityExternalIdentifier(String externalIdentifierTypeCode) {
		for ( EntityExternalIdentifierImpl id : externalIdentifiers ) {
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
	public EntityEmploymentInformationImpl getPrimaryEmployment() {
		for ( EntityEmploymentInformationImpl emp : employmentInformation ) {
			if ( emp.isActive() && emp.isPrimary() ) {
				return emp;
			}
		}
		return null;
	}

	public List<EntityNameImpl> getNames() {
		return this.names;
	}

	public void setNames(List<EntityNameImpl> names) {
		this.names = names;
	}	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultName()
	 */
	public EntityNameImpl getDefaultName() {
		return (EntityNameImpl)getDefaultItem( names );
	}

	/**
	 * @return the citizenships
	 */
	public List<EntityCitizenshipImpl> getCitizenships() {
		return this.citizenships;
	}

	/**
	 * @param citizenships the citizenships to set
	 */
	public void setCitizenships(List<EntityCitizenshipImpl> citizenships) {
		this.citizenships = citizenships;
	}

	public void setPrincipals(List<KimPrincipalImpl> principals) {
		this.principals = principals;
	}
	
	
	
}
