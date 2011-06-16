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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kuali.rice.kim.api.identity.EntityUtils;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameBo;
import org.kuali.rice.kim.impl.identity.personal.EntityBioDemographicsBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo;
import org.kuali.rice.kim.impl.identity.type.EntityTypeDataBo;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityNameBo> names = new AutoPopulatingList(EntityNameBo.class);

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<PrincipalBo> principals = new AutoPopulatingList(PrincipalBo.class);

	@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.ALL})
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityExternalIdentifierImpl> externalIdentifiers = new AutoPopulatingList(KimEntityExternalIdentifierImpl.class);

	@Fetch(value = FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.ALL})
	@JoinColumn(name="ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityAffiliationBo> affiliations; // = new AutoPopulatingList(KimEntityAffiliationImpl.class);

	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name="ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityEmploymentInformationImpl> employmentInformation = new AutoPopulatingList(KimEntityEmploymentInformationImpl.class);

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityTypeDataBo> entityTypes = new AutoPopulatingList(EntityTypeDataBo.class);
	
	@OneToOne(targetEntity=EntityPrivacyPreferencesBo.class, fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected EntityPrivacyPreferencesBo privacyPreferences;

	@OneToOne(targetEntity=EntityBioDemographicsBo.class, fetch = FetchType.EAGER, cascade = { })
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected EntityBioDemographicsBo bioDemographics;
	
	@OneToMany(targetEntity = EntityCitizenshipBo.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<EntityCitizenshipBo> citizenships = new AutoPopulatingList(EntityCitizenshipBo.class);

	@OneToMany(targetEntity = KimEntityEthnicityImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityEthnicityImpl> ethnicities = new AutoPopulatingList(KimEntityEthnicityImpl.class);

	@OneToMany(targetEntity = KimEntityResidencyImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityResidencyImpl> residencies = new AutoPopulatingList(KimEntityResidencyImpl.class);

	@OneToMany(targetEntity = KimEntityVisaImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "ENTITY_ID", insertable = false, updatable = false)
	protected List<KimEntityVisaImpl> visas = new AutoPopulatingList(KimEntityVisaImpl.class);

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
	public List<EntityTypeDataBo> getEntityTypes() {
		return this.entityTypes;
	}

	/**
	 * @param entityTypes
	 *            the entityTypes to set
	 */
	public void setEntityTypes(List<EntityTypeDataBo> entityTypes) {
		this.entityTypes = entityTypes;
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
	public EntityPrivacyPreferencesBo getPrivacyPreferences() {
	    if (ObjectUtils.isNull(this.privacyPreferences)) {
	        return null;
	    }
		return this.privacyPreferences;
	}

	/**
	 * @param privacyPreferences
	 *            the privacyPreferences to set
	 */
	public void setPrivacyPreferences(EntityPrivacyPreferencesBo privacyPreferences) {
		this.privacyPreferences = privacyPreferences;
	}

	/**
	 * @return the bioDemographics
	 */
	public EntityBioDemographicsBo getBioDemographics() {
	    if (ObjectUtils.isNull(this.bioDemographics)) {
            return null;
        }
		return this.bioDemographics;
	}

	/**
	 * @param bioDemographics
	 *            the bioDemographics to set
	 */
	public void setBioDemographics(EntityBioDemographicsBo bioDemographics) {
		this.bioDemographics = bioDemographics;
	}

	/**
	 * @return the affiliations
	 */
	public List<EntityAffiliationBo> getAffiliations() {
		if (this.affiliations == null) {
			return new ArrayList<EntityAffiliationBo>();
		}
		return this.affiliations;
	}

	/**
	 * @param affiliations
	 *            the affiliations to set
	 */
	public void setAffiliations(List<EntityAffiliationBo> affiliations) {
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
	public List<PrincipalBo> getPrincipals() {
		return principals;
	}
	
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getEntityType(java.lang.String)
	 */
	public EntityTypeDataBo getEntityType(String entityTypeCode) {
		for ( EntityTypeDataBo entType : entityTypes ) {
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
	public EntityAffiliationBo getDefaultAffiliation() {
		return EntityUtils.getDefaultItem( affiliations );
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

	public List<EntityNameBo> getNames() {
		return this.names;
	}

	public void setNames(List<EntityNameBo> names) {
		this.names = names;
	}	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntity#getDefaultName()
	 */
	public EntityNameBo getDefaultName() {
		return EntityUtils.getDefaultItem(names);
	}

	/**
	 * @return the citizenships
	 */
	public List<EntityCitizenshipBo> getCitizenships() {
		return this.citizenships;
	}

	/**
	 * @param citizenships the citizenships to set
	 */
	public void setCitizenships(List<EntityCitizenshipBo> citizenships) {
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

	public void setPrincipals(List<PrincipalBo> principals) {
		this.principals = principals;
	}
}
