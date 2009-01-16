package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.NamePrincipalName;
import org.kuali.rice.kim.bo.entity.dto.NamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * Base implementation of the identity (entity) service.  This version assumes the KimEntity
 * and related data is located within the KIM database. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityServiceImpl implements IdentityService {

	protected BusinessObjectService businessObjectService;

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
		 Map<String,Object> criteria = new HashMap<String,Object>();
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
        return getEntityByKeyValue("principalName", principalName);
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
        if (!entities.isEmpty() && entities.size() == 1) {
        	return entities.iterator().next();
        }
		return null;
	}

	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
}
