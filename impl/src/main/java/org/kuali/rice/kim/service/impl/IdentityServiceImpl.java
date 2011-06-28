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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.services.IdentityService;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.impl.identity.EntityTypeBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo;
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo;
import org.kuali.rice.kim.impl.identity.entity.EntityBo;
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierTypeBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo;
import org.kuali.rice.kim.impl.identity.type.EntityTypeDataBo;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of the identity (identity) service.  This version assumes the KimEntity
 * and related data is located within the KIM database. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@WebService(endpointInterface = KIMWebServiceConstants.IdentityService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.IdentityService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.IdentityService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class IdentityServiceImpl implements IdentityService, IdentityUpdateService {

	private BusinessObjectService businessObjectService;

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfo(java.lang.String)
	 */
	public Entity getEntity(String entityId) {
		EntityBo entity = getEntityBo( entityId );
		if ( entity == null ) {
			return null;
		}
		return EntityBo.to( entity );
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfoByPrincipalId(java.lang.String)
	 */
	public Entity getEntityInfoByPrincipalId(String principalId) {
		EntityBo entity = getEntityByPrincipalId(principalId);
		if ( entity == null ) {
			return null;
		}
		return EntityBo.to(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfoByPrincipalName(java.lang.String)
	 */
	public Entity getEntityInfoByPrincipalName(String principalName) {
		EntityBo entity = getEntityByPrincipalName(principalName);
		if ( entity == null ) {
			return null;
		}
		return EntityBo.to(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfo(java.lang.String)
	 */
	public EntityDefault getEntityDefaultInfo(String entityId) {
		EntityBo entity = getEntityBo( entityId );
		if ( entity == null ) {
			return null;
		}
		return EntityBo.toDefault( entity );
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfoByPrincipalId(java.lang.String)
	 */
	public EntityDefault getEntityDefaultInfoByPrincipalId(String principalId) {
		EntityBo entity = getEntityByPrincipalId(principalId);
		if ( entity == null ) {
			return null;
		}
		return EntityBo.toDefault(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfoByPrincipalName(java.lang.String)
	 */
	public EntityDefault getEntityDefaultInfoByPrincipalName(String principalName) {
		EntityBo entity = getEntityByPrincipalName(principalName);
		if ( entity == null ) {
			return null;
		}
		return EntityBo.toDefault(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Principal getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
		 Map<String,Object> criteria = new HashMap<String,Object>(3);
         criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName);
         criteria.put(KIMPropertyConstants.Principal.PASSWORD, password);
         criteria.put(KIMPropertyConstants.Principal.ACTIVE, true);
         Collection<PrincipalBo> principals = getBusinessObjectService().findMatching(PrincipalBo.class, criteria);

         if (!principals.isEmpty()) {
             return PrincipalBo.to(principals.iterator().next());
         }
         return null;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityInfo(Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> lookupEntityInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection baseResults = lookupEntityImpls(searchCriteria, unbounded);
		List<Entity> results = new ArrayList<Entity>( baseResults.size() );
		for ( EntityBo entity : (Collection<EntityBo>)baseResults ) {
			results.add( EntityBo.to( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<Entity>( results, ((CollectionIncomplete<Entity>)baseResults).getActualSizeIfTruncated() );
		}		
		return results;
	}
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityDefaultInfo(Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<EntityDefault> lookupEntityDefaultInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection baseResults = lookupEntityImpls(searchCriteria, unbounded);
		List<EntityDefault> results = new ArrayList<EntityDefault>( baseResults.size() );
		for ( EntityBo entity : (Collection<EntityBo>)baseResults ) {
			results.add( EntityBo.toDefault( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<EntityDefault>( results, ((CollectionIncomplete<EntityDefault>)baseResults).getActualSizeIfTruncated() );
		}		
		return results;
	}

	protected Collection lookupEntityImpls(Map<String,String> searchCriteria, boolean unbounded) {
		if ( unbounded ) {
			return KRADServiceLocatorWeb.getLookupService().findCollectionBySearchUnbounded( EntityBo.class, searchCriteria );
		} else {
			return KRADServiceLocatorWeb.getLookupService().findCollectionBySearch( EntityBo.class, searchCriteria );
		}
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getMatchingEntityCount(java.util.Map)
	 */
	public int getMatchingEntityCount(Map<String,String> searchCriteria) {
		return getBusinessObjectService().countMatching( EntityBo.class, searchCriteria );
	}

/*	protected EntityDefault EntityBo.toDefault( EntityBo entity ) {
		EntityDefault.Builder info = EntityDefault.Builder.create();
		info.setEntityId( entity.getEntityId() );
		info.setActive( entity.isActive() );
		ArrayList<Principal.Builder> principalInfo = new ArrayList<Principal.Builder>( entity.getPrincipals().size() );
		for ( PrincipalBo p : entity.getPrincipals() ) {
			principalInfo.add( Principal.Builder.create(p) );
		}
        info.setPrincipals( principalInfo );

        if ( ObjectUtils.isNotNull( entity.getPrivacyPreferences() ) ) {
            info.setPrivacyPreferences(EntityPrivacyPreferences.Builder.create(entity.getPrivacyPreferences()));
        }

		info.setName(EntityName.Builder.create(entity.getDefaultName()));
		ArrayList<EntityTypeDataDefault.Builder> entityTypesInfo = new ArrayList<EntityTypeDataDefault.Builder>( entity.getEntityTypes().size() );
		for ( EntityTypeDataBo entityTypeDataBo : entity.getEntityTypes() ) {
			entityTypesInfo.add(EntityTypeDataDefault.Builder.create(EntityTypeDataBo.toDefault(entityTypeDataBo)));
		}
        info.setEntityTypes( entityTypesInfo );
		
		ArrayList<EntityAffiliation.Builder> affInfo = new ArrayList<EntityAffiliation.Builder>( entity.getAffiliations().size() );

		for ( EntityAffiliationBo aff : entity.getAffiliations() ) {
			affInfo.add( EntityAffiliation.Builder.create(aff) );
			if ( aff.isActive() && aff.isDefaultValue() ) {
				info.setDefaultAffiliation( affInfo.get( affInfo.size() - 1 ) );
			}
		}
        info.setAffiliations( affInfo );

		info.setEmployment(EntityEmployment.Builder.create(entity.getPrimaryEmployment()));
		ArrayList<EntityExternalIdentifier.Builder> idInfo = new ArrayList<EntityExternalIdentifier.Builder>( entity.getExternalIdentifiers().size() );
		for ( EntityExternalIdentifierContract id : entity.getExternalIdentifiers() ) {
			idInfo.add( EntityExternalIdentifier.Builder.create(id) );
		}
        info.setExternalIdentifiers( idInfo );
		return info.build();
	}*/
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public EntityPrivacyPreferences getEntityPrivacyPreferences(String entityId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, entityId);
		return EntityPrivacyPreferencesBo.to(getBusinessObjectService().findByPrimaryKey(EntityPrivacyPreferencesBo.class, criteria));
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipal(java.lang.String)
	 */
	public Principal getPrincipal(String principalId) {
		PrincipalBo principal = getPrincipalImpl( principalId );
		if ( principal == null ) {
			return null;
		}
		return PrincipalBo.to(principal);
	}
	
	private PrincipalBo getPrincipalImpl(String principalId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_ID, principalId);
		return getBusinessObjectService().findByPrimaryKey(PrincipalBo.class, criteria);
	}

	private EntityBo getEntityBo(String entityId) {
		EntityBo entityImpl = getBusinessObjectService().findByPrimaryKey(EntityBo.class, Collections.singletonMap("id", entityId));
        if(entityImpl!=null) {
        	entityImpl.refresh();
            /*TODO: We need to try and remove this.  Currently, without it, some integration tests fail because of some
             * sort of OJB caching and not filling in the type values.  We need to figure out why this is happening and fix it.
             * Yes, this is a hack :P
             */
            for (EntityTypeDataBo type : entityImpl.getEntityTypes()) {
                type.refresh();
                for (EntityAddressBo addressBo : type.getAddresses()) {
                    addressBo.refreshReferenceObject("addressType");
                }
                for (EntityEmailBo emailBo : type.getEmailAddresses()) {
                    emailBo.refreshReferenceObject("emailType");
                }
                for (EntityPhoneBo phoneBo : type.getPhoneNumbers()) {
                    phoneBo.refreshReferenceObject("phoneType");
                }
            }
            for (EntityNameBo name : entityImpl.getNames()) {
                name.refreshReferenceObject("nameType");
            }
        }
        return entityImpl;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntitys(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	protected List<EntityBo> lookupEntitys(Map<String, String> searchCriteria) {
		return new ArrayList(KRADServiceLocatorWeb.getLookupService().findCollectionBySearchUnbounded( EntityBo.class, searchCriteria ));
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityIds(java.util.Map)
	 */
	public List<String> lookupEntityIds(Map<String,String> searchCriteria) {
		List<EntityBo> entities = lookupEntitys( searchCriteria );
		List<String> entityIds = new ArrayList<String>( entities.size() );
		for ( EntityBo entity : entities ) {
			entityIds.add( entity.getId() );
		}
		return entityIds;
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipalByPrincipalName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Principal getPrincipalByPrincipalName(String principalName) {
		if ( StringUtils.isBlank(principalName) ) {
			return null;
		}
		Map<String,Object> criteria = new HashMap<String,Object>(1);
        criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName.toLowerCase());
        Collection<PrincipalBo> principals = getBusinessObjectService().findMatching(PrincipalBo.class, criteria);
        if (!principals.isEmpty() && principals.size() == 1) {
            return PrincipalBo.to(principals.iterator().next());
        }
        return null;
    }

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityByPrincipalName(java.lang.String)
	 */
	protected EntityBo getEntityByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
        return getEntityByKeyValue("principals." + KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName.toLowerCase());
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	protected EntityBo getEntityByPrincipalId(String principalId) {
		if ( StringUtils.isBlank( principalId ) ) {
			return null;
		}
        return getEntityByKeyValue("principals." + KIMPropertyConstants.Principal.PRINCIPAL_ID, principalId);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityIdByPrincipalId(java.lang.String)
	 */
	public String getEntityIdByPrincipalId(String principalId) {
		if ( StringUtils.isBlank( principalId ) ) {
			return null;
		}
		PrincipalBo principal = getPrincipalImpl(principalId);
		return principal != null ? principal.getEntityId() : null;
    }

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityIdByPrincipalName(java.lang.String)
	 */
	public String getEntityIdByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
		Principal principal = getPrincipalByPrincipalName(principalName);
		return principal != null ? principal.getEntityId() : null;
    }
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getPrincipalIdByPrincipalName(java.lang.String)
	 */
	public String getPrincipalIdByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
		Principal principal = getPrincipalByPrincipalName( principalName );
		return principal != null ? principal.getPrincipalId() : null;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getDefaultNamesForEntityIds(java.util.List)
	 */
	public Map<String, EntityName> getDefaultNamesForEntityIds(List<String> entityIds) {
		// TODO - Use a DAO
		Map<String, EntityName> result = new HashMap<String, EntityName>(entityIds.size());
		
		for(String s : entityIds) {
			Map<String,Object> criteria = new HashMap<String,Object>();
			criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, s);
			criteria.put("defaultValue", true);
			
			EntityNameBo name = getBusinessObjectService().findByPrimaryKey(EntityNameBo.class, criteria);
			
			result.put(s, EntityNameBo.to(name) );
		}
		
		return result;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, KimEntityNamePrincipalNameInfo> getDefaultNamesForPrincipalIds(List<String> principalIds) {
		// TODO - use a dao?
		// TODO - what if principal not found, NullPointerException
		Map<String, KimEntityNamePrincipalNameInfo> result = new HashMap<String, KimEntityNamePrincipalNameInfo>(principalIds.size());
		
		for(String s : principalIds) {
			KimEntityNamePrincipalNameInfo namePrincipal = new KimEntityNamePrincipalNameInfo();
			
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_ID, s);
			PrincipalBo principal = (PrincipalBo) getBusinessObjectService().findByPrimaryKey(PrincipalBo.class, criteria);
			
			if (null != principal) {
				namePrincipal.setPrincipalName(principal.getPrincipalName());
				
				criteria.clear();
				criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, principal.getEntityId());
				criteria.put("defaultValue", "Y");
				EntityNameBo name = getBusinessObjectService().findByPrimaryKey(EntityNameBo.class, criteria);
				namePrincipal.setDefaultEntityName( EntityNameBo.to( name ) );
				
				result.put(s, namePrincipal);
			}
		}
		
		return result;
	}
	
	/**
	 * Generic helper method for performing a lookup through the business object service.
	 */
	@SuppressWarnings("unchecked")
	protected EntityBo getEntityByKeyValue(String key, String value) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(key, value);
        Collection<EntityBo> entities = getBusinessObjectService().findMatching(EntityBo.class, criteria);
        if (entities.size() >= 1) {
        	return entities.iterator().next();
        }
		return null;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	public Type getAddressType( String code ) {
		EntityAddressTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityAddressTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityAddressTypeBo.to(impl);
	}
	
	public EntityAffiliationType getAffiliationType( String code ) {
		EntityAffiliationTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityAffiliationTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityAffiliationTypeBo.to(impl);
	}

	public Type getCitizenshipStatus( String code ) {
		EntityCitizenshipStatusBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityCitizenshipStatusBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityCitizenshipStatusBo.to(impl);
	}

	public Type getEmailType( String code ) {
		EntityEmailTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityEmailTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityEmailTypeBo.to(impl);
	}

	public Type getEmploymentStatus( String code ) {
		EntityEmploymentStatusBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityEmploymentStatusBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityEmploymentStatusBo.to(impl);
	}

	public Type getEmploymentType( String code ) {
		EntityEmploymentTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityEmploymentTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityEmploymentTypeBo.to(impl);
	}

	public Type getEntityNameType( String code ) {
		EntityNameTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityNameTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityNameTypeBo.to(impl);
	}

	public Type getEntityType( String code ) {
		EntityTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityTypeBo.to(impl);
	}

	public EntityExternalIdentifierType getExternalIdentifierType( String code ) {
		EntityExternalIdentifierTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityExternalIdentifierTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityExternalIdentifierTypeBo.to(impl);
	}
	
	public Type getPhoneType( String code ) {
		EntityPhoneTypeBo impl = getBusinessObjectService().findBySinglePrimaryKey(EntityPhoneTypeBo.class, code);
		if ( impl == null ) {
			return null;
		}
		return EntityPhoneTypeBo.to(impl);
	}
	
//	protected static Map<String,ExternalIdentifierTypeInfo> externalIdentifierTypeInfoCache = new HashMap<String, ExternalIdentifierTypeInfo>();
//	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code ) {
//		if ( !externalIdentifierTypeInfoCache.containsKey(code) ) {
//			Map<String,String> pk = new HashMap<String, String>(1);
//			pk.put("code", code);
//			ExternalIdentifierTypeImpl impl = (ExternalIdentifierTypeImpl)getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, pk);
//			if ( impl != null ) {
//				externalIdentifierTypeInfoCache.put(code, impl.toInfo());
//			}
//		}
//		return externalIdentifierTypeInfoCache.get(code);
//	}
	
}
