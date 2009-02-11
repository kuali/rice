package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.EntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.NamePrincipalName;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAddressInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAffiliationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmailInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityExternalIdentifierInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPhoneInfo;
import org.kuali.rice.kim.bo.entity.dto.NamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * Base implementation of the identity (entity) service.  This version assumes the KimEntity
 * and related data is located within the KIM database. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityServiceImpl implements IdentityService {

	private BusinessObjectService businessObjectService;

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfo(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfo(String entityId) {
		KimEntity entity = getEntity( entityId );
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo( entity );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(String principalId) {
		KimEntity entity = getEntityByPrincipalName(principalId);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(String principalName) {
		KimEntity entity = getEntityByPrincipalName(principalName);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public KimPrincipal getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
		 Map<String,Object> criteria = new HashMap<String,Object>(3);
         criteria.put("principalName", principalName);
         criteria.put("password", password);
         criteria.put("active", true);
         Collection<KimPrincipal> principals = (Collection<KimPrincipal>)getBusinessObjectService().findMatching(KimPrincipalImpl.class, criteria);
         if (!principals.isEmpty() && principals.size() == 1) {
             return principals.iterator().next();
         }
         return null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntityDefaultInfo(java.util.Map, int)
	 */
	@SuppressWarnings("unchecked")
	public List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection<KimEntityImpl> baseResults = null; 
		if ( unbounded ) {
			baseResults = KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria );
		} else {
			baseResults = KNSServiceLocator.getLookupService().findCollectionBySearch( KimEntityImpl.class, searchCriteria );
		}
		List<KimEntityDefaultInfo> results = new ArrayList<KimEntityDefaultInfo>( baseResults.size() );
		for ( KimEntityImpl entity : baseResults ) {
			results.add( convertEntityImplToDefaultInfo( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimEntityDefaultInfo>( results, ((CollectionIncomplete<KimEntityDefaultInfo>)results).getActualSizeIfTruncated() ); 
		}		
		return results;
	}
	
	protected KimEntityDefaultInfo convertEntityImplToDefaultInfo( KimEntity entity ) {
		KimEntityDefaultInfo info = new KimEntityDefaultInfo();
		info.setEntityId( entity.getEntityId() );
		info.setDefaultName( new KimEntityNameInfo( entity.getDefaultName() ) );
		ArrayList<KimEntityEntityTypeDefaultInfo> entityTypesInfo = new ArrayList<KimEntityEntityTypeDefaultInfo>( entity.getEntityTypes().size() );
		info.setEntityTypes( entityTypesInfo );
		for ( EntityEntityType entityEntityType : entity.getEntityTypes() ) {
			KimEntityEntityTypeDefaultInfo typeInfo = new KimEntityEntityTypeDefaultInfo();
			typeInfo.setDefaultAddress( new KimEntityAddressInfo( entityEntityType.getDefaultAddress() ) );
			typeInfo.setDefaultEmailAddress( new KimEntityEmailInfo( entityEntityType.getDefaultEmailAddress() ) );
			typeInfo.setDefaultPhoneNumber( new KimEntityPhoneInfo( entityEntityType.getDefaultPhoneNumber() ) );
			entityTypesInfo.add( typeInfo );
		}
		
		ArrayList<KimEntityAffiliationInfo> affInfo = new ArrayList<KimEntityAffiliationInfo>( entity.getAffiliations().size() );
		info.setAffiliations( affInfo );
		for ( EntityAffiliation aff : entity.getAffiliations() ) {
			affInfo.add( new KimEntityAffiliationInfo( aff ) );
			if ( aff.isActive() && aff.isDefault() ) {
				info.setDefaultAffiliation( affInfo.get( affInfo.size() - 1 ) );
			}
		}
		info.setPrimaryEmployment( new KimEntityEmploymentInformationInfo( entity.getPrimaryEmployment() ) );
		ArrayList<KimEntityExternalIdentifierInfo> idInfo = new ArrayList<KimEntityExternalIdentifierInfo>( entity.getExternalIdentifiers().size() );
		info.setExternalIdentifiers( idInfo );
		for ( EntityExternalIdentifier id : entity.getExternalIdentifiers() ) {
			idInfo.add( new KimEntityExternalIdentifierInfo( id ) );
		}
		return info;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipal(java.lang.String)
	 */
	public KimPrincipal getPrincipal(String principalId) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("principalId", principalId);
		return (KimPrincipal) getBusinessObjectService().findByPrimaryKey(KimPrincipalImpl.class, criteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntity(java.lang.String)
	 */
	public KimEntity getEntity(String entityId) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("entityId", entityId);
		return (KimEntity) getBusinessObjectService().findByPrimaryKey(KimEntityImpl.class, criteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntitys(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public List<KimEntity> lookupEntitys(Map<String, String> searchCriteria) {
		return new ArrayList(KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria ));
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntityIds(java.util.Map)
	 */
	public List<String> lookupEntityIds(Map<String,String> searchCriteria) {
		List<KimEntity> entities = lookupEntitys( searchCriteria );
		List<String> entityIds = new ArrayList<String>( entities.size() );
		for ( KimEntity entity : entities ) {
			entityIds.add( entity.getEntityId() );
		}
		return entityIds;
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipalByPrincipalName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public KimPrincipal getPrincipalByPrincipalName(String principalName) {
		 Map<String,Object> criteria = new HashMap<String,Object>(1);
         criteria.put("principalName", principalName);
         Collection<KimPrincipal> principals = (Collection<KimPrincipal>)getBusinessObjectService().findMatching(KimPrincipalImpl.class, criteria);
         if (!principals.isEmpty() && principals.size() == 1) {
             return principals.iterator().next();
         }
         return null;
    }

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityByPrincipalName(java.lang.String)
	 */
	public KimEntity getEntityByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
        return getEntityByKeyValue("principals.principalName", principalName);
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	public KimEntity getEntityByPrincipalId(String principalId) {
		if ( StringUtils.isBlank( principalId ) ) {
			return null;
		}
        return getEntityByKeyValue("principals.principalId", principalId);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityIdByPrincipalId(java.lang.String)
	 */
	public String getEntityIdByPrincipalId(String principalId) {
		if ( StringUtils.isBlank( principalId ) ) {
			return null;
		}
		KimPrincipal principal = getPrincipal(principalId);		
		return principal != null ? principal.getEntityId() : null;
    }

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityIdByPrincipalName(java.lang.String)
	 */
	public String getEntityIdByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
		KimPrincipal principal = getPrincipalByPrincipalName(principalName);		
		return principal != null ? principal.getEntityId() : null;
    }
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipalIdByPrincipalName(java.lang.String)
	 */
	public String getPrincipalIdByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
		KimPrincipal principal = getPrincipalByPrincipalName( principalName );
		return principal != null ? principal.getPrincipalId() : null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getDefaultNamesForEntityIds(java.util.List)
	 */
	public Map<String, EntityName> getDefaultNamesForEntityIds(List<String> entityIds) {
		// TODO - Use a DAO
		Map<String, EntityName> result = new HashMap<String, EntityName>(entityIds.size());
		
		for(String s : entityIds) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put("entityId", s);
			criteria.put("dflt", "Y");
			
			EntityName name = (EntityNameImpl) getBusinessObjectService().findByPrimaryKey(EntityNameImpl.class, criteria);
			
			result.put(s, name);
		}
		
		return result;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, NamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds) {
		// TODO - use a dao?
		// TODO - what if principal not found, NullPointerException
		Map<String, NamePrincipalName> result = new HashMap<String, NamePrincipalName>(principalIds.size());
		
		for(String s : principalIds) {
			NamePrincipalNameInfo namePrincipal = new NamePrincipalNameInfo();
			
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put("principalId", s);
			KimPrincipal principal = (KimPrincipalImpl) getBusinessObjectService().findByPrimaryKey(KimPrincipalImpl.class, criteria);
			
			namePrincipal.setPrincipalName(principal.getPrincipalName());
			
			criteria.clear();
			criteria.put("entityId", principal.getEntityId());
			criteria.put("dflt", "Y");
			EntityName name = (EntityNameImpl) getBusinessObjectService().findByPrimaryKey(EntityNameImpl.class, criteria);
			
			namePrincipal.setDefaultEntityName(name);
			
			result.put(s, namePrincipal);
		}
		
		return result;
	}
	
	/**
	 * Generic helper method for performing a lookup through the business object service.
	 */
	@SuppressWarnings("unchecked")
	protected KimEntity getEntityByKeyValue(String key, String value) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put(key, value);
        Collection<KimEntity> entities = (Collection<KimEntity>)getBusinessObjectService().findMatching(KimEntityImpl.class, criteria);
        if (entities.size() >= 1) {
        	return entities.iterator().next();
        }
		return null;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
}
