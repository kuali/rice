package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

/**
 * Base interface for the IdentityService which does not use any complex objects.
 * 
 *  This interface may be implemented by a remote (web-service) implementation.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface IdentityServiceBase {

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
