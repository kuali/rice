package org.kuali.rice.kim.v2.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Principal;
import org.kuali.rice.kim.v2.bo.entity.Entity;
import org.kuali.rice.kim.v2.service.IdentityService;

// TODO implement this class
public class IdentityServiceImpl implements IdentityService {
	public Entity getEntityByPrincipalName(String principalName) {
    	return null;
    }

	public Principal getPrincipal(String principalId) {
    	return null;
    }
	
	public Entity getEntity(String entityId) {
    	return null;
    }

	public List<Entity> lookupEntitys(Map<String,String> searchCriteria) {
    	return null;
    }

    public List<Entity> lookupEntitys(Map<String,String> searchCriteria, Map<String, String> entityAttributes) {
    	return null;
    }

    public void saveEntity(Entity entity) {
    }
}
