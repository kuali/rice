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

import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityBioDemographics;
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.EntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.EntityFerpaPreferences;
import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
@Entity
@Table(name = "KR_KIM_ENTITY_T")
public class KimEntityImpl extends InactivatableEntityDataBase implements KimEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;

	@OneToMany(targetEntity = EntityNameImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityName> names = new TypedArrayList(EntityNameImpl.class);
		
	@OneToMany(targetEntity = EntityEntityTypeImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected List<EntityEntityType> entityTypes = new TypedArrayList(EntityEntityTypeImpl.class);

	@OneToMany(targetEntity = KimPrincipalImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected List<KimPrincipal> principals = new TypedArrayList(KimPrincipalImpl.class);

	@OneToMany(targetEntity=EntityExternalIdentifierImpl.class,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<EntityExternalIdentifier> externalIdentifiers = new TypedArrayList(EntityExternalIdentifierImpl.class);

	@OneToMany(targetEntity=EntityAffiliationImpl.class,fetch=FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<EntityAffiliation> affiliations = new TypedArrayList(EntityAffiliationImpl.class);

	@OneToMany(targetEntity=EntityEmploymentInformationImpl.class,fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable=true, updatable=true)
	protected List<EntityEmploymentInformation> employmentInformation = new TypedArrayList(EntityEmploymentInformationImpl.class);

	@OneToOne(targetEntity=EntityFerpaPreferencesImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected EntityFerpaPreferences ferpaPreferences;

	@OneToOne(targetEntity=EntityBioDemographicsImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "ENTITY_ID", insertable = true, updatable = true)
	protected EntityBioDemographics bioDemographics;

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
	public List<EntityEntityType> getEntityTypes() {
		return this.entityTypes;
	}

	/**
	 * @param entityTypes
	 *            the entityTypes to set
	 */
	public void setEntityTypes(List<EntityEntityType> entityTypes) {
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
	public List<EntityExternalIdentifier> getExternalIdentifiers() {
		return this.externalIdentifiers;
	}

	/**
	 * @param externalIdentifiers
	 *            the externalIdentifiers to set
	 */
	public void setExternalIdentifiers(List<EntityExternalIdentifier> externalIdentifiers) {
		this.externalIdentifiers = externalIdentifiers;
	}

	/**
	 * @return the ferpaPreferences
	 */
	public EntityFerpaPreferences getFerpaPreferences() {
		return this.ferpaPreferences;
	}

	/**
	 * @param ferpaPreferences
	 *            the ferpaPreferences to set
	 */
	public void setFerpaPreferences(EntityFerpaPreferences ferpaPreferences) {
		this.ferpaPreferences = ferpaPreferences;
	}

	/**
	 * @return the bioDemographics
	 */
	public EntityBioDemographics getBioDemographics() {
		return this.bioDemographics;
	}

	/**
	 * @param bioDemographics
	 *            the bioDemographics to set
	 */
	public void setBioDemographics(EntityBioDemographics bioDemographics) {
		this.bioDemographics = bioDemographics;
	}

	/**
	 * @return the affiliations
	 */
	public List<EntityAffiliation> getAffiliations() {
		return this.affiliations;
	}

	/**
	 * @param affiliations
	 *            the affiliations to set
	 */
	public void setAffiliations(List<EntityAffiliation> affiliations) {
		this.affiliations = affiliations;
	}

	/**
	 * @return the employmentInformation
	 */
	public List<EntityEmploymentInformation> getEmploymentInformation() {
		return this.employmentInformation;
	}

	/**
	 * @param employmentInformation
	 *            the employmentInformation to set
	 */
	public void setEmploymentInformation(List<EntityEmploymentInformation> employmentInformation) {
		this.employmentInformation = employmentInformation;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getPrincipals()
	 */
	public List<KimPrincipal> getPrincipals() {
		return principals;
	}
	
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityType(java.lang.String)
	 */
	public EntityEntityType getEntityType(String entityTypeCode) {
		for ( EntityEntityType entType : entityTypes ) {
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
	public EntityAffiliation getDefaultAffiliation() {
		return (EntityAffiliation)getDefaultItem( affiliations );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityExternalIdentifier(java.lang.String)
	 */
	public EntityExternalIdentifier getEntityExternalIdentifier(String externalIdentifierTypeCode) {
		for ( EntityExternalIdentifier id : externalIdentifiers ) {
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
	public EntityEmploymentInformation getPrimaryEmployment() {
		for ( EntityEmploymentInformation emp : employmentInformation ) {
			if ( emp.isActive() && emp.isPrimary() ) {
				return emp;
			}
		}
		return null;
	}

	public List<EntityName> getNames() {
		return this.names;
	}

	public void setNames(List<EntityName> names) {
		this.names = names;
	}	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultName()
	 */
	public EntityName getDefaultName() {
		return (EntityName)getDefaultItem( names );
	}
	
	
	
}
