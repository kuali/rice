package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.KimEntityNamePrincipalName;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAddressInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAffiliationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmailInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityExternalIdentifierInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPhoneInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPrivacyPreferencesInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityPrivacyPreferencesImpl;
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
		KimEntity entity = getEntityImpl( entityId );
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
         if (!principals.isEmpty()) {
             return new KimPrincipalInfo( principals.iterator().next() );
         }
         return null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntityDefaultInfo(Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection baseResults = null; 
		if ( unbounded ) {
			baseResults = KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria );
		} else {
			baseResults = KNSServiceLocator.getLookupService().findCollectionBySearch( KimEntityImpl.class, searchCriteria );
		}
		List<KimEntityDefaultInfo> results = new ArrayList<KimEntityDefaultInfo>( baseResults.size() );
		for ( KimEntityImpl entity : (Collection<KimEntityImpl>)baseResults ) {
			results.add( convertEntityImplToDefaultInfo( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimEntityDefaultInfo>( results, ((CollectionIncomplete<KimEntityDefaultInfo>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getMatchingEntityCount(java.util.Map)
	 */
	public int getMatchingEntityCount(Map<String,String> searchCriteria) {
		return getBusinessObjectService().countMatching( KimEntityImpl.class, searchCriteria );
	}
	
	protected KimEntityDefaultInfo convertEntityImplToDefaultInfo( KimEntity entity ) {
		KimEntityDefaultInfo info = new KimEntityDefaultInfo();
		info.setEntityId( entity.getEntityId() );
		info.setActive( entity.isActive() );
		ArrayList<KimPrincipalInfo> principalInfo = new ArrayList<KimPrincipalInfo>( entity.getPrincipals().size() );
		info.setPrincipals( principalInfo );
		for ( KimPrincipal p : entity.getPrincipals() ) {
			principalInfo.add( new KimPrincipalInfo( p ) );
		}
		info.setDefaultName( new KimEntityNameInfo( entity.getDefaultName() ) );
		ArrayList<KimEntityEntityTypeDefaultInfo> entityTypesInfo = new ArrayList<KimEntityEntityTypeDefaultInfo>( entity.getEntityTypes().size() );
		info.setEntityTypes( entityTypesInfo );
		for ( KimEntityEntityType entityEntityType : entity.getEntityTypes() ) {
			KimEntityEntityTypeDefaultInfo typeInfo = new KimEntityEntityTypeDefaultInfo();
			typeInfo.setEntityTypeCode( entityEntityType.getEntityTypeCode() );
			typeInfo.setDefaultAddress( new KimEntityAddressInfo( entityEntityType.getDefaultAddress() ) );
			typeInfo.setDefaultEmailAddress( new KimEntityEmailInfo( entityEntityType.getDefaultEmailAddress() ) );
			typeInfo.setDefaultPhoneNumber( new KimEntityPhoneInfo( entityEntityType.getDefaultPhoneNumber() ) );
			entityTypesInfo.add( typeInfo );
		}
		
		ArrayList<KimEntityAffiliationInfo> affInfo = new ArrayList<KimEntityAffiliationInfo>( entity.getAffiliations().size() );
		info.setAffiliations( affInfo );
		for ( KimEntityAffiliation aff : entity.getAffiliations() ) {
			affInfo.add( new KimEntityAffiliationInfo( aff ) );
			if ( aff.isActive() && aff.isDefault() ) {
				info.setDefaultAffiliation( affInfo.get( affInfo.size() - 1 ) );
			}
		}
		info.setPrimaryEmployment( new KimEntityEmploymentInformationInfo( entity.getPrimaryEmployment() ) );
		ArrayList<KimEntityExternalIdentifierInfo> idInfo = new ArrayList<KimEntityExternalIdentifierInfo>( entity.getExternalIdentifiers().size() );
		info.setExternalIdentifiers( idInfo );
		for ( KimEntityExternalIdentifier id : entity.getExternalIdentifiers() ) {
			idInfo.add( new KimEntityExternalIdentifierInfo( id ) );
		}
		return info;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences(String entityId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put("entityId", entityId);
		return new KimEntityPrivacyPreferencesInfo( (KimEntityPrivacyPreferences)getBusinessObjectService().findByPrimaryKey(KimEntityPrivacyPreferencesImpl.class, criteria) );
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipal(java.lang.String)
	 */
	public KimPrincipal getPrincipal(String principalId) {
		return new KimPrincipalInfo( getPrincipalImpl( principalId ) );
	}
	
	public KimPrincipalImpl getPrincipalImpl(String principalId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put("principalId", principalId);
		return (KimPrincipalImpl)getBusinessObjectService().findByPrimaryKey(KimPrincipalImpl.class, criteria);
	}

	public KimEntityImpl getEntityImpl(String entityId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put("entityId", entityId);
		return (KimEntityImpl)getBusinessObjectService().findByPrimaryKey(KimEntityImpl.class, criteria);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntitys(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	protected List<KimEntity> lookupEntitys(Map<String, String> searchCriteria) {
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
             return new KimPrincipalInfo( principals.iterator().next() );
         }
         return null;
    }

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityByPrincipalName(java.lang.String)
	 */
	protected KimEntityImpl getEntityByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
        return getEntityByKeyValue("principals.principalName", principalName);
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	protected KimEntityImpl getEntityByPrincipalId(String principalId) {
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
		KimPrincipal principal = getPrincipalImpl(principalId);		
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
	public Map<String, KimEntityName> getDefaultNamesForEntityIds(List<String> entityIds) {
		// TODO - Use a DAO
		Map<String, KimEntityName> result = new HashMap<String, KimEntityName>(entityIds.size());
		
		for(String s : entityIds) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put("entityId", s);
			criteria.put("dflt", "Y");
			
			KimEntityName name = (KimEntityNameImpl) getBusinessObjectService().findByPrimaryKey(KimEntityNameImpl.class, criteria);
			
			result.put(s, new KimEntityNameInfo( name ) );
		}
		
		return result;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, KimEntityNamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds) {
		// TODO - use a dao?
		// TODO - what if principal not found, NullPointerException
		Map<String, KimEntityNamePrincipalName> result = new HashMap<String, KimEntityNamePrincipalName>(principalIds.size());
		
		for(String s : principalIds) {
			KimEntityNamePrincipalNameInfo namePrincipal = new KimEntityNamePrincipalNameInfo();
			
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put("principalId", s);
			KimPrincipal principal = (KimPrincipalImpl) getBusinessObjectService().findByPrimaryKey(KimPrincipalImpl.class, criteria);
			
			namePrincipal.setPrincipalName(principal.getPrincipalName());
			
			criteria.clear();
			criteria.put("entityId", principal.getEntityId());
			criteria.put("dflt", "Y");
			KimEntityName name = (KimEntityNameImpl) getBusinessObjectService().findByPrimaryKey(KimEntityNameImpl.class, criteria);
			
			namePrincipal.setDefaultEntityName(name);
			
			result.put(s, namePrincipal);
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
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
}
