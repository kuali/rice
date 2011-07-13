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
package org.kuali.rice.kim.service.impl;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.entity.EntityDefaultQueryResults;
import org.kuali.rice.kim.api.identity.entity.EntityQueryResults;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographics;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicity;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.residency.EntityResidency;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.identity.visa.EntityVisa;
import org.kuali.rice.kim.api.services.IdentityArchiveService;
import org.kuali.rice.kim.api.services.IdentityService;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;
import java.util.Map;

/**
 * This IdentityService implementation is largely just a knee-jerk delegator, except for
 * getters returning {@link EntityDefault} in which case the IdentityArchiveService
 * will be invoked if the inner IndentityService impl returns null.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@WebService(endpointInterface = KIMWebServiceConstants.IdentityService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.IdentityService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.IdentityService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class IdentityCurrentAndArchivedServiceImpl implements IdentityService, IdentityUpdateService {

	private final IdentityArchiveService identityArchiveService;
	private final IdentityService innerIdentityService;
	
	/**
	 * This constructs a IdentityCurrentAndArchivedServiceImpl, injecting the
	 * needed services.
	 */
	public IdentityCurrentAndArchivedServiceImpl(IdentityService innerIdentityService, 
			IdentityArchiveService identityArchiveService) {
		this.innerIdentityService = innerIdentityService;
		this.identityArchiveService = identityArchiveService;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getAddressType(java.lang.String)
	 */
	public Type getAddressType(String code) {
		return getInnerIdentityService().getAddressType(code);
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getAffiliationType(java.lang.String)
	 */
	public EntityAffiliationType getAffiliationType(String code) {
		return getInnerIdentityService().getAffiliationType(code);
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getCitizenshipStatus(java.lang.String)
	 */
	public Type getCitizenshipStatus(String code) {
		return Type.Builder.create(getInnerIdentityService().getCitizenshipStatus(code)).build();
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getDefaultNamesForEntityIds(java.util.List)
	 */
	public Map<String, EntityNamePrincipalName> getDefaultNamesForEntityIds(
			List<String> entityIds) {
		return getInnerIdentityService().getDefaultNamesForEntityIds(entityIds);
	}

    @Override
    public EntityName addNameToEntity(EntityName name) {
        return getInnerIdentityService().addNameToEntity(name);
    }

    @Override
    public EntityName updateName(EntityName name) {
        return getInnerIdentityService().updateName(name);
    }

    @Override
    public EntityName inactivateName(String id) {
        return getInnerIdentityService().inactivateName(id);
    }

    @Override
    public EntityEmployment addEmploymentToEntity(EntityEmployment employment) {
        return getInnerIdentityService().addEmploymentToEntity(employment);
    }

    @Override
    public EntityEmployment updateEmployment(EntityEmployment employment) {
        return getInnerIdentityService().updateEmployment(employment);
    }

    @Override
    public EntityEmployment inactivateEmployment(String id) {
        return getInnerIdentityService().inactivateEmployment(id);
    }

    @Override
    public EntityBioDemographics addBioDemographicsToEntity(EntityBioDemographics bioDemographics) {
        return getInnerIdentityService().addBioDemographicsToEntity(bioDemographics);
    }

    @Override
    public EntityBioDemographics updateBioDemographics(EntityBioDemographics bioDemographics) {
        return getInnerIdentityService().updateBioDemographics(bioDemographics);
    }

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, EntityNamePrincipalName> getDefaultNamesForPrincipalIds(
			List<String> principalIds) {
		return getInnerIdentityService().getDefaultNamesForPrincipalIds(principalIds);
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEmailType(java.lang.String)
	 */
	public Type getEmailType(String code) {
		return getInnerIdentityService().getEmailType(code);
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEmploymentStatus(java.lang.String)
	 */
	public Type getEmploymentStatus(String code) {
		return getInnerIdentityService().getEmploymentStatus(code);
	}


    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEmploymentType(java.lang.String)
	 */
	public Type getEmploymentType(String code) {
		return getInnerIdentityService().getEmploymentType(code);
	}


    /**
	 * This method first tries the inner IdentityService impl, and resorts to
	 * the IdentityArchiveService if need be.
	 * 
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityDefault(java.lang.String)
	 */
	public EntityDefault getEntityDefault(String entityId) {
		EntityDefault entity = getInnerIdentityService().getEntityDefault(entityId);
    	if ( entity == null ) {
    		entity = getIdentityArchiveService().getEntityDefaultInfoFromArchive( entityId );
    	} else {
			getIdentityArchiveService().saveDefaultInfoToArchive(entity);
    	}
		return entity;
	}

	/**
	 * This method first tries the inner IdentityService impl, and resorts to
	 * the IdentityArchiveService if need be.
	 * 
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityDefaultByPrincipalId(java.lang.String)
	 */
	public EntityDefault getEntityDefaultByPrincipalId(String principalId) {
		EntityDefault entity = getInnerIdentityService().getEntityDefaultByPrincipalId(principalId);
    	if ( entity == null ) {
    		entity = getIdentityArchiveService().getEntityDefaultInfoFromArchiveByPrincipalId( principalId );
    	} else {
			getIdentityArchiveService().saveDefaultInfoToArchive(entity);
    	}
    	return entity;
	}

	/**
	 * This method first tries the inner IdentityService impl, and resorts to
	 * the IdentityArchiveService if need be.
	 * 
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityDefaultByPrincipalName(java.lang.String)
	 */
	public EntityDefault getEntityDefaultByPrincipalName(String principalName) {
		EntityDefault entity = getInnerIdentityService().getEntityDefaultByPrincipalName(principalName);
    	if ( entity == null ) {
    		entity = getIdentityArchiveService().getEntityDefaultInfoFromArchiveByPrincipalName( principalName );
    	} else {
			getIdentityArchiveService().saveDefaultInfoToArchive(entity);
    	}
    	return entity;
	}

    @Override
    public EntityDefaultQueryResults findEntityDefaults(@WebParam(name = "query") QueryByCriteria queryByCriteria) {
        return getInnerIdentityService().findEntityDefaults(queryByCriteria);
    }

    @Override
    public EntityQueryResults findEntities(@WebParam(name = "query") QueryByCriteria queryByCriteria) {
        return getInnerIdentityService().findEntities(queryByCriteria);
    }

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntity(java.lang.String)
	 */
	public Entity getEntity(String entityId) {
		return getInnerIdentityService().getEntity(entityId);
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	public Entity getEntityByPrincipalId(String principalId) {
		return getInnerIdentityService().getEntityByPrincipalId(principalId);
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityByPrincipalName(java.lang.String)
	 */
	public Entity getEntityByPrincipalName(String principalName) {
		return getInnerIdentityService().getEntityByPrincipalName(principalName);
	}


    @Override
    public Entity createEntity(Entity entity) {
        return getInnerIdentityService().createEntity(entity);
    }

    @Override
    public Entity updateEntity(Entity entity) {
        return getInnerIdentityService().updateEntity(entity);
    }

    @Override
    public Entity inactivateEntity(String entityId) {
        return getInnerIdentityService().inactivateEntity(entityId);
    }

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getNameType(java.lang.String)
	 */
	public Type getNameType(String code) {
		return getInnerIdentityService().getNameType(code);
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public EntityPrivacyPreferences getEntityPrivacyPreferences(
			String entityId) {
		return getInnerIdentityService().getEntityPrivacyPreferences(entityId);
	}

    @Override
    public EntityPrivacyPreferences addPrivacyPreferencesToEntity(EntityPrivacyPreferences privacyPreferences) {
        return getInnerIdentityService().addPrivacyPreferencesToEntity(privacyPreferences);
    }

    @Override
    public EntityPrivacyPreferences updatePrivacyPreferences(EntityPrivacyPreferences privacyPreferences) {
        return getInnerIdentityService().updatePrivacyPreferences(privacyPreferences);
    }

    @Override
    public EntityCitizenship addCitizenshipToEntity(EntityCitizenship citizenship) {
        return getInnerIdentityService().addCitizenshipToEntity(citizenship);
    }

    @Override
    public EntityCitizenship updateCitizenship(EntityCitizenship citizenship) {
        return getInnerIdentityService().updateCitizenship(citizenship);
    }

    @Override
    public EntityCitizenship inactivateCitizenship(String id) {
        return getInnerIdentityService().inactivateCitizenship(id);
    }

    @Override
    public EntityEthnicity addEthnicityToEntity(EntityEthnicity ethnicity) {
        return getInnerIdentityService().addEthnicityToEntity(ethnicity);
    }

    @Override
    public EntityEthnicity updateEthnicity(EntityEthnicity ethnicity) {
        return getInnerIdentityService().updateEthnicity(ethnicity);
    }

    @Override
    public EntityResidency addResidencyToEntity(EntityResidency residency) {
        return getInnerIdentityService().addResidencyToEntity(residency);
    }

    @Override
    public EntityResidency updateResidency(EntityResidency residency) {
        return getInnerIdentityService().updateResidency(residency);
    }

    @Override
    public EntityVisa addVisaToEntity(EntityVisa visa) {
        return getInnerIdentityService().addVisaToEntity(visa);
    }

    @Override
    public EntityVisa updateVisa(EntityVisa visa) {
        return getInnerIdentityService().updateVisa(visa);
    }

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getEntityType(java.lang.String)
	 */
	public Type getEntityType(String code) {
		return getInnerIdentityService().getEntityType(code);
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getExternalIdentifierType(java.lang.String)
	 */
	public EntityExternalIdentifierType getExternalIdentifierType(String code) {
		return getInnerIdentityService().getExternalIdentifierType(code);
	}


	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getPhoneType(java.lang.String)
	 */
	public Type getPhoneType(String code) {
		return getInnerIdentityService().getPhoneType(code);
	}

    /**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getPrincipal(java.lang.String)
	 */
	public Principal getPrincipal(String principalId) {
		return getInnerIdentityService().getPrincipal(principalId);
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getPrincipalByPrincipalName(java.lang.String)
	 */
	public Principal getPrincipalByPrincipalName(String principalName) {
		return getInnerIdentityService().getPrincipalByPrincipalName(principalName);
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
	 */
	public Principal getPrincipalByPrincipalNameAndPassword(
			String principalName, String password) {
		return getInnerIdentityService().getPrincipalByPrincipalNameAndPassword(
				principalName, password);
	}

    @Override
    public Principal addPrincipalToEntity(Principal principal) {
        return getInnerIdentityService().addPrincipalToEntity(principal);
    }

    @Override
    public Principal updatePrincipal(Principal principal) {
        return getInnerIdentityService().updatePrincipal(principal);
    }

    @Override
    public Principal inactivatePrincipal(String principalId) {
        return getInnerIdentityService().inactivatePrincipal(principalId);
    }

    @Override
    public Principal inactivatePrincipalByName(String principalName) {
        return getInnerIdentityService().inactivatePrincipalByName(principalName);
    }

    @Override
    public EntityTypeContactInfo addEntityTypeContactInfoToEntity(EntityTypeContactInfo entityTypeContactInfo) {
        return getInnerIdentityService().addEntityTypeContactInfoToEntity(entityTypeContactInfo);
    }

    @Override
    public EntityTypeContactInfo updateEntityTypeContactInfo(EntityTypeContactInfo entityTypeContactInfo) {
        return getInnerIdentityService().updateEntityTypeContactInfo(entityTypeContactInfo);
    }

    @Override
    public EntityTypeContactInfo inactivateEntityTypeContactInfo(String entityId, String entityTypeCode) {
        return getInnerIdentityService().inactivateEntityTypeContactInfo(entityId, entityTypeCode);
    }

    @Override
    public EntityAddress addAddressToEntity(EntityAddress address) {
        return getInnerIdentityService().addAddressToEntity(address);
    }

    @Override
    public EntityAddress updateAddress(EntityAddress address) {
        return getInnerIdentityService().updateAddress(address);
    }

    @Override
    public EntityAddress inactivateAddress(String addressId) {
        return getInnerIdentityService().inactivateAddress(addressId);
    }

    @Override
    public EntityEmail addEmailToEntity(EntityEmail email) {
        return getInnerIdentityService().addEmailToEntity(email);
    }

    @Override
    public EntityEmail updateEmail(EntityEmail email) {
        return getInnerIdentityService().updateEmail(email);
    }

    @Override
    public EntityEmail inactivateEmail(String emailId) {
        return getInnerIdentityService().inactivateEmail(emailId);
    }

    @Override
    public EntityPhone addPhoneToEntity(EntityPhone phone) {
        return getInnerIdentityService().addPhoneToEntity(phone);
    }

    @Override
    public EntityPhone updatePhone(EntityPhone phone) {
        return getInnerIdentityService().updatePhone(phone);
    }

    @Override
    public EntityPhone inactivatePhone(String phoneId) {
        return getInnerIdentityService().inactivatePhone(phoneId);
    }

    @Override
    public EntityExternalIdentifier addExternalIdentifierToEntity(EntityExternalIdentifier externalId) {
        return getInnerIdentityService().addExternalIdentifierToEntity(externalId);
    }

    @Override
    public EntityExternalIdentifier updateExternalIdentifier(EntityExternalIdentifier externalId) {
        return getInnerIdentityService().updateExternalIdentifier(externalId);
    }

    @Override
    public EntityAffiliation addAffiliationToEntity(EntityAffiliation affiliation) {
        return getInnerIdentityService().addAffiliationToEntity(affiliation);
    }

    @Override
    public EntityAffiliation updateAffiliation(EntityAffiliation affiliation) {
        return getInnerIdentityService().updateAffiliation(affiliation);
    }

    @Override
    public EntityAffiliation inactivateAffiliation(String id) {
        return getInnerIdentityService().inactivateAffiliation(id);
    }


	private IdentityService getInnerIdentityService() {
		return innerIdentityService;
	}
	
	private IdentityArchiveService getIdentityArchiveService() {
		return identityArchiveService;
	}

}
