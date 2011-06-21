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
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.services.IdentityService;
import org.kuali.rice.kim.api.identity.type.EntityTypeDataDefault;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityExternalIdentifierInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;
import org.kuali.rice.kim.impl.identity.EntityTypeBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo;
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo;
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
import org.kuali.rice.krad.util.ObjectUtils;

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
	public KimEntityInfo getEntityInfo(String entityId) {
		KimEntityImpl entity = getEntityImpl( entityId );
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToInfo( entity );
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityInfo getEntityInfoByPrincipalId(String principalId) {
		KimEntityImpl entity = getEntityByPrincipalId(principalId);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityInfo getEntityInfoByPrincipalName(String principalName) {
		KimEntityImpl entity = getEntityByPrincipalName(principalName);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfo(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfo(String entityId) {
		KimEntityImpl entity = getEntityImpl( entityId );
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo( entity );
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(String principalId) {
		KimEntityImpl entity = getEntityByPrincipalId(principalId);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityDefaultInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(String principalName) {
		KimEntityImpl entity = getEntityByPrincipalName(principalName);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo(entity);
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
	public List<KimEntityInfo> lookupEntityInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection baseResults = lookupEntityImpls(searchCriteria, unbounded);
		List<KimEntityInfo> results = new ArrayList<KimEntityInfo>( baseResults.size() );
		for ( KimEntityImpl entity : (Collection<KimEntityImpl>)baseResults ) {
			results.add( convertEntityImplToInfo( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimEntityInfo>( results, ((CollectionIncomplete<KimEntityInfo>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
	}
	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityDefaultInfo(Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<KimEntityDefaultInfo> lookupEntityDefaultInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection baseResults = lookupEntityImpls(searchCriteria, unbounded);
		List<KimEntityDefaultInfo> results = new ArrayList<KimEntityDefaultInfo>( baseResults.size() );
		for ( KimEntityImpl entity : (Collection<KimEntityImpl>)baseResults ) {
			results.add( convertEntityImplToDefaultInfo( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimEntityDefaultInfo>( results, ((CollectionIncomplete<KimEntityDefaultInfo>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
	}

	protected Collection lookupEntityImpls(Map<String,String> searchCriteria, boolean unbounded) {
		if ( unbounded ) {
			return KRADServiceLocatorWeb.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria );
		} else {
			return KRADServiceLocatorWeb.getLookupService().findCollectionBySearch( KimEntityImpl.class, searchCriteria );
		}
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getMatchingEntityCount(java.util.Map)
	 */
	public int getMatchingEntityCount(Map<String,String> searchCriteria) {
		return getBusinessObjectService().countMatching( KimEntityImpl.class, searchCriteria );
	}
	
	protected KimEntityInfo convertEntityImplToInfo( KimEntityImpl entity ) {
		return new KimEntityInfo(entity);
	}

	protected KimEntityDefaultInfo convertEntityImplToDefaultInfo( KimEntityImpl entity ) {
		KimEntityDefaultInfo info = new KimEntityDefaultInfo();
		info.setEntityId( entity.getEntityId() );
		info.setActive( entity.isActive() );
		ArrayList<Principal> principalInfo = new ArrayList<Principal>( entity.getPrincipals().size() );
		info.setPrincipals( principalInfo );
		for ( PrincipalBo p : entity.getPrincipals() ) {
			principalInfo.add( PrincipalBo.to(p) );
		}
		EntityPrivacyPreferences privacy = null;
		if ( ObjectUtils.isNotNull( entity.getPrivacyPreferences() ) ) {
            privacy = EntityPrivacyPreferencesBo.to(entity.getPrivacyPreferences());
        }

		info.setPrivacyPreferences(privacy);
		info.setDefaultName( EntityNameBo.to(entity.getDefaultName()) );
		ArrayList<EntityTypeDataDefault> entityTypesInfo = new ArrayList<EntityTypeDataDefault>( entity.getEntityTypes().size() );
		info.setEntityTypes( entityTypesInfo );
		for ( EntityTypeDataBo entityTypeDataBo : entity.getEntityTypes() ) {
			entityTypesInfo.add( EntityTypeDataBo.toDefault(entityTypeDataBo) );
		}
		
		ArrayList<EntityAffiliation> affInfo = new ArrayList<EntityAffiliation>( entity.getAffiliations().size() );
		info.setAffiliations( affInfo );
		for ( EntityAffiliationBo aff : entity.getAffiliations() ) {
			affInfo.add( EntityAffiliationBo.to(aff) );
			if ( aff.isActive() && aff.isDefaultValue() ) {
				info.setDefaultAffiliation( affInfo.get( affInfo.size() - 1 ) );
			}
		}
		info.setPrimaryEmployment( EntityEmploymentBo.to(entity.getPrimaryEmployment()) );
		ArrayList<KimEntityExternalIdentifierInfo> idInfo = new ArrayList<KimEntityExternalIdentifierInfo>( entity.getExternalIdentifiers().size() );
		info.setExternalIdentifiers( idInfo );
		for ( KimEntityExternalIdentifier id : entity.getExternalIdentifiers() ) {
			idInfo.add( new KimEntityExternalIdentifierInfo( id ) );
		}
		return info;
	}
	
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

	public KimEntityImpl getEntityImpl(String entityId) {
		KimEntityImpl entityImpl = getBusinessObjectService().findByPrimaryKey(KimEntityImpl.class, Collections.singletonMap("entityId", entityId));
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
	protected List<KimEntityImpl> lookupEntitys(Map<String, String> searchCriteria) {
		return new ArrayList(KRADServiceLocatorWeb.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria ));
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#lookupEntityIds(java.util.Map)
	 */
	public List<String> lookupEntityIds(Map<String,String> searchCriteria) {
		List<KimEntityImpl> entities = lookupEntitys( searchCriteria );
		List<String> entityIds = new ArrayList<String>( entities.size() );
		for ( KimEntityImpl entity : entities ) {
			entityIds.add( entity.getEntityId() );
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
	protected KimEntityImpl getEntityByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
        return getEntityByKeyValue("principals." + KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName.toLowerCase());
	}

	/**
	 * @see org.kuali.rice.kim.api.identity.services.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	protected KimEntityImpl getEntityByPrincipalId(String principalId) {
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
	protected KimEntityImpl getEntityByKeyValue(String key, String value) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(key, value);
        Collection<KimEntityImpl> entities = (Collection<KimEntityImpl>)getBusinessObjectService().findMatching(KimEntityImpl.class, criteria);
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

	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code ) {
		ExternalIdentifierTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(ExternalIdentifierTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
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
