package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityEmail;
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.EntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.EntityPhone;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.NamePrincipalName;
import org.kuali.rice.kim.bo.entity.dto.EntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.NamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KualiDecimal;

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
		throw new UnsupportedOperationException();
		// return null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(String principalName) {
		throw new UnsupportedOperationException();
		// return null;
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
	public List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
			Map<String,String> searchCriteria, int maxResults) {
		throw new UnsupportedOperationException();
		// return null;
	}
	
	protected KimEntityDefaultInfo convertEntityImplToDefaultInfo( KimEntity entity ) {
		KimEntityDefaultInfo info = new KimEntityDefaultInfo();
		info.setEntityId( entity.getEntityId() );
		info.setEntityTypes( new ArrayList<KimEntityEntityTypeDefaultInfo>( entity.getEntityTypes().size() ) );
		for ( EntityEntityType entityEntityType : entity.getEntityTypes() ) {
			KimEntityEntityTypeDefaultInfo typeInfo = new KimEntityEntityTypeDefaultInfo();
//			populateAddressInfo( typeInfo, entityEntityType );
//			populateEmailInfo( typeInfo, entityEntityType );
//			populatePhoneInfo( typeInfo, entityEntityType );
			info.getEntityTypes().add( typeInfo );
		}
		populateNameInfo( info, entity );
//		populateAffiliationInfo( info, entity );
//		populateEmploymentInfo( info, entity );
//		populateExternalIdentifiers( info, entity );
		return info;
	}
	
	protected void populateNameInfo( KimEntityDefaultInfo info, KimEntity entity ) {
		EntityName entityName = entity.getDefaultName();
		EntityNameInfo nameInfo = new EntityNameInfo( entityName );
		info.setDefaultName( nameInfo );
	}
	
//	protected void populateAddressInfo( EntityEntityType entityEntityType ) {
//		EntityAddress defaultAddress = entityEntityType.getDefaultAddress();
//		if ( defaultAddress != null ) {			
//			addressLine1 = unNullify( defaultAddress.getLine1() );
//			addressLine2 = unNullify( defaultAddress.getLine2() );
//			addressLine3 = unNullify( defaultAddress.getLine3() );
//			addressCityName = unNullify( defaultAddress.getCityName() );
//			addressStateCode = unNullify( defaultAddress.getStateCode() );
//			addressPostalCode = unNullify( defaultAddress.getPostalCode() );
//			addressCountryCode = unNullify( defaultAddress.getCountryCode() );
//		} else {
//			addressLine1 = "";
//			addressLine2 = "";
//			addressLine3 = "";
//			addressCityName = "";
//			addressStateCode = "";
//			addressPostalCode = "";
//			addressCountryCode = "";
//		}
//	}
//	
//	protected void populateEmailInfo( EntityEntityType entityEntityType ) {
//		EntityEmail entityEmail = entityEntityType.getDefaultEmailAddress();
//		if ( entityEmail != null ) {
//			emailAddress = unNullify( entityEmail.getEmailAddress() );
//		} else {
//			emailAddress = "";
//		}
//	}
//	
//	protected void populatePhoneInfo( EntityEntityType entityEntityType ) {
//		EntityPhone entityPhone = entityEntityType.getDefaultPhoneNumber();
//		if ( entityPhone != null ) {
//			phoneNumber = unNullify( entityPhone.getFormattedPhoneNumber() );
//		} else {
//			phoneNumber = "";
//		}
//	}
//	
//	protected void populateAffiliationInfo( KimEntity entity ) {
//		affiliations = entity.getAffiliations();
//		EntityAffiliation defaultAffiliation = entity.getDefaultAffiliation();
//		if ( defaultAffiliation != null  ) {
//			campusCode = unNullify( defaultAffiliation.getCampusCode() );
//		} else {
//			campusCode = "";
//		}
//	}
//	
//	protected void populateEmploymentInfo( KimEntity entity ) {
//		EntityEmploymentInformation employmentInformation = entity.getPrimaryEmployment();
//		if ( employmentInformation != null ) {
//			employeeStatusCode = unNullify( employmentInformation.getEmployeeStatusCode() );
//			employeeTypeCode = unNullify( employmentInformation.getEmployeeTypeCode() );
//			primaryDepartmentCode = unNullify( employmentInformation.getPrimaryDepartmentCode() );
//			employeeId = unNullify( employmentInformation.getEmployeeId() );
//			if ( employmentInformation.getBaseSalaryAmount() != null ) {
//				baseSalaryAmount = employmentInformation.getBaseSalaryAmount();
//			} else {
//				baseSalaryAmount = KualiDecimal.ZERO;
//			}
//		} else {
//			employeeStatusCode = "";
//			employeeTypeCode = "";
//			primaryDepartmentCode = "";
//			employeeId = "";
//			baseSalaryAmount = KualiDecimal.ZERO;
//		}
//	}
//	
//	protected void populateExternalIdentifiers( KimEntity entity ) {
//		List<? extends EntityExternalIdentifier> externalIds = entity.getExternalIdentifiers();
//		externalIdentifiers = new HashMap<String,String>( externalIds.size() );
//		for ( EntityExternalIdentifier eei : externalIds ) {
//			externalIdentifiers.put( eei.getExternalIdentifierTypeCode(), eei.getExternalId() );
//		}
//	}
//	
//	/** So users of this class don't need to program around nulls. */
//	private String unNullify( String str ) {
//		if ( str == null ) {
//			return "";
//		}
//		return str;
//	}
	
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

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
}
