package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPrivacyPreferencesInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.reference.dto.AddressTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.CitizenshipStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmailTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityNameTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.PhoneTypeInfo;
import org.kuali.rice.kim.bo.reference.impl.AddressTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.AffiliationTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.CitizenshipStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmailTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.EntityNameTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.EntityTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;

public interface IdentityService {
	
	/** Get an entity object from the main entity ID */ 
//	KimEntity getEntity( String entityId );
	
	/** Get an entity object based on the principal name */
//	KimEntity getEntityByPrincipalName(String principalName);
	
//	KimEntity getEntityByPrincipalId(String principalId);

	// EXTENDED CLIENT API
    
	// KIM INTERNAL METHODS
	
	/** Get a KimPrincipal object based on it's unique principal ID */
	KimPrincipalInfo getPrincipal(String principalId);
	
	/** Get a KimPrincipal object based on the principalName. */
	KimPrincipalInfo getPrincipalByPrincipalName(String principalName);

	KimPrincipalInfo getPrincipalByPrincipalNameAndPassword(String principalName, String password);
	
	/** Find entity objects based on the given criteria. */
//	List<KimEntity> lookupEntitys(Map<String,String> searchCriteria);
    
	KimEntityDefaultInfo getEntityDefaultInfo( String entityId );
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId( String principalId );
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName( String principalName );
	List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo( Map<String,String> searchCriteria, boolean unbounded );
	int getMatchingEntityCount( Map<String,String> searchCriteria );
	
	KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences( String entityId );
	
    Map<String, KimEntityNamePrincipalNameInfo> getDefaultNamesForPrincipalIds(List<String> principalIds);
    
    Map<String, KimEntityNameInfo> getDefaultNamesForEntityIds(List<String> entityIds);

//	/** Return the entity ID for the given principal */
//	String getEntityIdByPrincipalId( String principalId );
//	
//	/** Return the entity ID for a given principal name */
//	String getEntityIdByPrincipalName( String principalName );
//
//	/** Return the principal ID for a given principal name */
//	String getPrincipalIdByPrincipalName( String principalName );
//
//	/** Find entity IDs based on the given criteria. */
//	List<String> lookupEntityIds(Map<String,String> searchCriteria);

	public AddressTypeInfo getAddressType( String code );
	public AffiliationTypeInfo getAffiliationType( String code );
	public CitizenshipStatusInfo getCitizenshipStatus( String code );
	public EmailTypeInfo getEmailType( String code );
	public EmploymentStatusInfo getEmploymentStatus( String code );
	public EmploymentTypeInfo getEmploymentType( String code );
	public EntityNameTypeInfo getEntityNameType( String code );
	public EntityTypeInfo getEntityType( String code );
	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code );
	public PhoneTypeInfo getPhoneType( String code );
    
}
