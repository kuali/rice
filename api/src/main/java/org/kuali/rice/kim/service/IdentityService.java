package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.NamePrincipalName;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;

public interface IdentityService {
	
	/** Get an entity object from the main entity ID */ 
	KimEntity getEntity( String entityId );
	
	/** Get an entity object based on the principal name */
	KimEntity getEntityByPrincipalName(String principalName);
	
	KimEntity getEntityByPrincipalId(String principalId);

	// EXTENDED CLIENT API
    
	// KIM INTERNAL METHODS
	
	/** Get a KimPrincipal object based on it's unique principal ID */
	KimPrincipal getPrincipal(String principalId);
	
	/** Get a KimPrincipal object based on the principalName. */
	KimPrincipal getPrincipalByPrincipalName(String principalName);

	KimPrincipal getPrincipalByPrincipalNameAndPassword(String principalName, String password);
	
	/** Find entity objects based on the given criteria. */
	List<KimEntity> lookupEntitys(Map<String,String> searchCriteria);
    
	KimEntityDefaultInfo getEntityDefaultInfo( String entityId );
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId( String principalId );
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName( String principalName );
	List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo( Map<String,String> searchCriteria, boolean unbounded );
	
    Map<String, NamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds);
    
    Map<String, EntityName> getDefaultNamesForEntityIds(List<String> entityIds);

	/** Return the entity ID for the given principal */
	String getEntityIdByPrincipalId( String principalId );
	
	/** Return the entity ID for a given principal name */
	String getEntityIdByPrincipalName( String principalName );

	/** Return the principal ID for a given principal name */
	String getPrincipalIdByPrincipalName( String principalName );

	/** Find entity IDs based on the given criteria. */
	List<String> lookupEntityIds(Map<String,String> searchCriteria);
    
}
