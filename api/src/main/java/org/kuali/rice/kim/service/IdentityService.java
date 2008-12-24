package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.entity.EntityName;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.NamePrincipalName;

public interface IdentityService {
	
	/** Get an entity object from the main entity ID */ 
	KimEntity getEntity( String entityId );
	
	/** Get an entity object based on the principal name */
	KimEntity getEntityByPrincipalName(String principalName);

	// EXTENDED CLIENT API
    
	// KIM INTERNAL METHODS
	
	/** Get a KimPrincipal object based on it's unique principal ID */
	public KimPrincipal getPrincipal(String principalId);
	
	/** Get a KimPrincipal object based on the principalName. */
	public KimPrincipal getPrincipalByPrincipalName(String principalName);

	/** Find entity objects based on the given criteria. */
	public List<KimEntity> lookupEntitys(Map<String,String> searchCriteria);
    
    public Map<String, NamePrincipalName> getDefaultNamesForPrincipalIds(List<String> principalIds);
    
    public Map<String, EntityName> getDefaultNamesForEntityIds(List<String> entityIds);

	/** Return the entity ID for the given principal */
	String getEntityIdByPrincipalId( String principalId );
	
	/** Return the entity ID for a given principal name */
	String getEntityIdByPrincipalName( String principalName );

	/** Return the principal ID for a given principal name */
	String getPrincipalIdByPrincipalName( String principalName );

	/** Find entity IDs based on the given criteria. */
	public List<String> lookupEntityIds(Map<String,String> searchCriteria);
    
    // Do we need APIs here to pull the extended Entity information that we don't need to load always?
    // like addresses, external identifiers, citizenship?

}
