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
package org.kuali.rice.kim.api.services;

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
import org.kuali.rice.kim.api.identity.type.EntityTypeData;
import org.kuali.rice.kim.api.identity.visa.EntityVisa;
import org.kuali.rice.kim.api.jaxb.StringToKimEntityNameInfoMapAdapter;
import org.kuali.rice.kim.api.jaxb.StringToKimEntityNamePrincipalInfoMapAdapter;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * This service provides operations to query for principal and identity data.
 * 
 * <p>A principal represents an identity that can authenticate.  In essence, a principal can be
 * thought of as an "account" or as an identity's authentication credentials.  A principal has
 * an id which is used to uniquely identify it.  It also has a name which represents the
 * principal's username and is typically what is entered when authenticating.  All principals
 * are associated with one and only one identity.
 * 
 * <p>An identity represents a person or system.  Additionally, other "types" of entities can
 * be defined in KIM.  Information like name, phone number, etc. is associated with an identity.
 * It is the representation of a concrete person or system.  While an identity will typically
 * have a single principal associated with it, it is possible for an identity to have more than
 * one principal or even no principals at all (in the case where the identity does not actually
 * authenticate).
 * 
 * <p>This service also provides operations for querying various pieces of reference data, such as 
 * address types, affiliation types, phone types, etc.
 * 
 * <p>This service provides read-only operations.  For write operations, see
 * {@link org.kuali.rice.kim.service.IdentityUpdateService}.
 * 
 * @see org.kuali.rice.kim.service.IdentityUpdateService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = KIMWebServiceConstants.IdentityService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface IdentityService {

    //TODO: refine contract, add javadoc, add annotations
    //TODO:


	/**
	 * Gets a List of identity default info for entities based on the given search criteria.
	 * 
	 * <p>If unbounded is set to false, then this method will return all results.  If unbounded is set to
	 * true then the number of search results will be bounded based on default configuration for number
	 * of search results returned in a a bounded search.
	 * 
	 * <p>The searchCriteria Map is a map of identity field names to search values.
	 */
	EntityDefaultQueryResults findEntityDefaults(@WebParam(name = "query") QueryByCriteria queryByCriteria);

	EntityQueryResults findEntities(@WebParam(name = "query") QueryByCriteria queryByCriteria);

	
    /**
	 * Get the identity info for the identity with the given id.
	 */
	Entity getEntity( @WebParam(name="entityId") String entityId );

	/**
	 * Get the identity info for the identity of the principal with the given principal id.
	 */
	Entity getEntityByPrincipalId(@WebParam(name = "principalId") String principalId);

	/**
	 * Get the identity info for the identity of the principal with the given principal name.
	 */
	Entity getEntityByPrincipalName(@WebParam(name = "principalName") String principalName);
    /**
     * Returns a count of the number of entities that match the given search criteria.
     */
    //int findMatchingEntityCount(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name = "searchCriteria") Map<String,String> searchCriteria );
    Entity createEntity(Entity entity);
    Entity updateEntity(Entity entity);
    Entity inactivateEntity(String entityId);
    
    
    /**
	 * Get the identity default info for the identity with the given id.
	 */
	EntityDefault getEntityDefault(@WebParam(name = "entityId") String entityId);

	/**
	 * Get the identity default info for the identity of the principal with the given principal id.
	 */
	EntityDefault getEntityDefaultByPrincipalId(@WebParam(name = "principalId") String principalId);

	/**
	 * Get the identity default info for the identity of the principal with the given principal name.
	 */
	EntityDefault getEntityDefaultByPrincipalName(@WebParam(name = "principalName") String principalName);
    
    

    /** 
     * Get the principal with the given unique principal ID. Returns null if not found. 
     */
    Principal getPrincipal( @WebParam(name="principalId") String principalId );

    /**
     * Get the principal with the given principalName.
     */
    Principal getPrincipalByPrincipalName( @WebParam(name="principalName") String principalName );

    /**
     * Get the principal with the given name and password.
     */
    Principal getPrincipalByPrincipalNameAndPassword( @WebParam(name="principalName") String principalName,  @WebParam(name="password") String password );

    Principal addPrincipalToEntity(Principal principal);
    Principal updatePrincipal(Principal principal);
    Principal inactivatePrincipal(String principalId);
    Principal inactivatePrincipalByName(String principalName);

    EntityTypeData addEntityTypeDataToEntity(EntityTypeData entityTypeData);
    EntityTypeData updateEntityTypeData(EntityTypeData entityTypeData);
    EntityTypeData inactivateEntityTypeData(String entityId, String entityTypeCode);

    EntityAddress addAddressToEntity(EntityAddress address);
    EntityAddress updateAddress(EntityAddress address);
    EntityAddress inactivateAddress(String addressId);

    EntityEmail addEmailToEntity(EntityEmail email);
    EntityEmail updateEmail(EntityEmail email);
    EntityEmail inactivateEmail(String emailId);

    
    EntityPhone addPhoneToEntity(EntityPhone phone);
    EntityPhone updatePhone(EntityPhone phone);
    EntityPhone inactivatePhone(String phoneId);

    EntityExternalIdentifier addExternalIdentifierToEntity(EntityExternalIdentifier externalId);
    EntityExternalIdentifier updateExternalIdentifier(EntityExternalIdentifier externalId);

    EntityAffiliation addAffiliationToEntity(EntityAffiliation affiliation);
    EntityAffiliation updateAffiliation(EntityAffiliation affiliation);
    EntityAffiliation inactivateAffiliation(String entityId, String affiliationTypeCode);

    /**
	 * Gets the name for the principals with ids in the given List.
	 * 
	 * <p>The resulting Map contains the principalId as the key and the name information as the value.
	 * When fetching names by principal id, the resulting name info contains the identity's name info
	 * as well as the principal's name info.
	 */
	@XmlJavaTypeAdapter(value = StringToKimEntityNamePrincipalInfoMapAdapter.class) 
    Map<String, EntityNamePrincipalName> getDefaultNamesForPrincipalIds(@WebParam(name="principalIds") List<String> principalIds);
    
    /**
     * Gets the names for the entities with ids in the given list.
     */
	@XmlJavaTypeAdapter(value = StringToKimEntityNameInfoMapAdapter.class) 
    Map<String, EntityName> getDefaultNamesForEntityIds(@WebParam(name="entityIds") List<String> entityIds);
    EntityName addNameToEntity(EntityName name);
    EntityName updateName(EntityName name);
    EntityName inactivateName(String entityId, String nameTypeCode);

    EntityEmployment addEmploymentToEntity(EntityEmployment employment);
    EntityEmployment updateEmployment(EntityEmployment employment);
    EntityEmployment inactivateEmployment(String entityId, String employmentTypeCode, String employmentStatusCode, String affiliationId);

    EntityBioDemographics addBioDemographicsToEntity(EntityBioDemographics bioDemographics);
    EntityBioDemographics updateBioDemographics(EntityBioDemographics bioDemographics);
    
    /**
	 * Gets the privacy preferences for the identity with the given identity id.
	 */
	EntityPrivacyPreferences getEntityPrivacyPreferences( @WebParam(name="entityId") String entityId );
    EntityPrivacyPreferences addPrivacyPreferencesToEntity(EntityPrivacyPreferences privacyPreferences);
    EntityPrivacyPreferences updatePrivacyPreferences(EntityPrivacyPreferences privacyPreferences);

    EntityCitizenship addCitizenshipToEntity(EntityCitizenship citizenship);
    EntityCitizenship updateCitizenship(EntityCitizenship citizenship);
    EntityCitizenship inactivateCitizenship(String entityId, String citizenshipTypeCode);

    EntityEthnicity addEthnicityToEntity(EntityEthnicity ethnicity);
    EntityEthnicity updateEthnicity(EntityEthnicity ethnicity);
    
    EntityResidency addResidencyToEntity(EntityResidency residency);
    EntityResidency updateResidency(EntityResidency residency);

    EntityVisa addVisaToEntity(EntityVisa visa);
    EntityVisa updateVisa(EntityVisa visa);

    /**
     * Gets the identity type for the given identity type code.
     */
	Type getEntityType( @WebParam(name="code") String code );
    Type createEntityType(Type type);
    Type updateEntityType(Type type);
    Type inactivateEntityType(String typeCode);

    /**
     * Gets the address type for the given address type code.
     */
	Type getAddressType( @WebParam(name="code") String code );
    Type createAddressType(Type type);
    Type updateAddressType(Type type);
    Type inactivateAddressType(String typeCode);

    /**
     * Gets the affiliation type for the given affiliation type code.
     */
	EntityAffiliationType getAffiliationType( @WebParam(name="code") String code );
    EntityAffiliationType createAffilationType(EntityAffiliationType type);
    EntityAffiliationType updateAffilationType(EntityAffiliationType type);
    EntityAffiliationType inactivateAffilationType(String typeCode);

    /**
	 * Gets the citizenship status for the given citizenship status code.
	 */
	Type getCitizenshipStatus( @WebParam(name="code") String code );
    Type createCitizenshipStatus(Type status);
    Type updateCitizenshipStatus(Type status);
    Type inactivateCitizenshipStatus(String statusCode);

    /**
     * Gets the employment type for the given employment type code.
     */
	Type getEmploymentType( @WebParam(name="code") String code );
    Type createEmploymentType(Type type);
    Type updateEmploymentType(Type type);
    Type inactivateEmploymentType(String typeCode);
    
    /**
     * Gets the employment status for the given employment status code.
     */
	Type getEmploymentStatus( @WebParam(name="code") String code );
    Type createEmploymentStatus(Type type);
    Type updateEmploymentStatus(Type type);
    Type inactivateEmploymentStatus(String statusCode);
    
    /**
     * Gets the external identifier type for the given external identifier type code.
     */
	EntityExternalIdentifierType getExternalIdentifierType( @WebParam(name="code") String code );
    EntityExternalIdentifierType createExternalIdentifierType(EntityExternalIdentifierType type);
    EntityExternalIdentifierType updateExternalIdentifierType(EntityExternalIdentifierType type);
    EntityExternalIdentifierType inactivateExternalIdentifierType(String typeCode);
    
    /**
     * Gets the identity name type for the given identity name type code.
     */
	Type getNameType(@WebParam(name = "code") String code);
    Type createNameType(Type type);
    Type updateNameType(Type type);
    Type inactivateNameType(String typeCode);
    
    /**
     * Gets the phone type for the given phone type code.
     */
	Type getPhoneType( @WebParam(name="code") String code );
    Type createPhoneType(Type type);
    Type updatePhoneType(Type type);
    Type inactivatePhoneType(String typeCode);
    
    /**
     * Gets the email type for the given email type code.
     */
	Type getEmailType( @WebParam(name="code") String code );
    Type createEmailType(Type type);
    Type updateEmailType(Type type);
    Type inactivateEmailType(String typeCode);
}
