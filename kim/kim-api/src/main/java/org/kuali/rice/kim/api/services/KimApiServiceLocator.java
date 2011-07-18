package org.kuali.rice.kim.api.services;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.role.RoleUpdateService;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.PermissionUpdateService;
import org.kuali.rice.kim.service.PersonService;

public class KimApiServiceLocator {
    private static final Logger LOG = Logger.getLogger(KimApiServiceLocator.class);

    public static final String KIM_GROUP_SERVICE = "kimGroupService";
    public static final String KIM_IDENTITY_SERVICE = "kimIdentityService";
    public static final String KIM_PERMISSION_SERVICE = "kimPermissionService";
    public static final String KIM_RESPONSIBILITY_SERVICE = "kimResponsibilityService";
    public static final String KIM_ROLE_SERVICE = "kimRoleService";
    public static final String KIM_PERSON_SERVICE = "personService";

    public static final String KIM_IDENTITY_UPDATE_SERVICE = "kimIdentityUpdateService";
    public static final String KIM_ROLE_UPDATE_SERVICE = "kimRoleUpdateService";
	public static final String KIM_PERMISSION_UPDATE_SERVICE = "kimPermissionUpdateService";
    public static final String KIM_TYPE_INFO_SERVICE = "kimTypeInfoService";

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

    public static KimTypeInfoService getKimTypeInfoService() {
        return getService(KIM_TYPE_INFO_SERVICE);
    }

    public static PersonService getPersonService() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetching service " + KIM_PERSON_SERVICE);
        }
        return getService(KIM_PERSON_SERVICE);
    }

    public static RoleService getRoleService() {
        return getService(KIM_ROLE_SERVICE);
    }
    
    public static org.kuali.rice.kim.api.group.GroupService getGroupService() {
    	return getService(KIM_GROUP_SERVICE);
    }
    
    public static IdentityService getIdentityService() {
    	return getService(KIM_IDENTITY_SERVICE);
    }
    public static PermissionService getPermissionService() {
    	return getService(KIM_PERMISSION_SERVICE);
    }
    public static ResponsibilityService getResponsibilityService() {
    	return getService(KIM_RESPONSIBILITY_SERVICE);
    }
    
    public static IdentityUpdateService getIdentityUpdateService() {
    	return getService(KIM_IDENTITY_UPDATE_SERVICE);
    }

    public static RoleUpdateService getRoleUpdateService() {
    	return getService(KIM_ROLE_UPDATE_SERVICE);
    }


    public static PermissionUpdateService getPermissionUpdateService() {
    	return getService(KIM_PERMISSION_UPDATE_SERVICE);
    }
}
