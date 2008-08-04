package org.kuali.rice.kim.v2.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Entity;
import org.kuali.rice.kim.v2.bo.Principal;

public interface IdentityService extends IdentityServiceBase {
	// CLIENT API	
	public Entity getEntityByPrincipalName(String principalName);

	// EXTENDED CLIENT API
    
	// KIM INTERNAL METHODS
	
	public Principal getPrincipal(String principalId);
	
	public Entity getEntity(String entityId);

	public List<Entity> lookupEntitys(Map<String,String> searchCriteria);

    public List<Entity> lookupEntitys(Map<String,String> searchCriteria, Map<String, String> entityAttributes);

    public void saveEntity(Entity entity);
}
