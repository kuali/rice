package org.kuali.rice.kim.service;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

import javax.xml.namespace.QName;

public class KIMServiceLocator {
    private static final Logger LOG = Logger.getLogger(KIMServiceLocator.class);

    public static final String KIM_GROUP_SERVICE = "kimGroupService";
    public static final String KIM_IDENTITY_MANAGEMENT_SERVICE = "kimIdentityManagementService";
    public static final String KIM_IDENTITY_SERVICE = "kimIdentityService";
    public static final String KIM_PERMISSION_SERVICE = "kimPermissionService";
    public static final String KIM_RESPONSIBILITY_SERVICE = "kimResponsibilityService";
    public static final String KIM_ROLE_SERVICE = "kimRoleService";
    public static final String KIM_ROLE_MANAGEMENT_SERVICE = "kimRoleManagementService";
    public static final String KIM_PERSON_SERVICE = "personService";

    public static PersonService getPersonService() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetching service " + KIM_PERSON_SERVICE);
        }
        return (PersonService) GlobalResourceLoader.getResourceLoader().getService(new QName(KIM_PERSON_SERVICE));
    }

    public static IdentityManagementService getIdentityManagementService() {
    	if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + KIM_IDENTITY_MANAGEMENT_SERVICE);
		}
    	return (IdentityManagementService) GlobalResourceLoader.getService(new QName(KIM_IDENTITY_MANAGEMENT_SERVICE));
    }

    public static RoleService getRoleService() {
        return (RoleService) GlobalResourceLoader.getService(new QName(KIM_ROLE_SERVICE));
    }
    
    public static GroupService getGroupService() {
    	return (GroupService) GlobalResourceLoader.getService(new QName(KIM_GROUP_SERVICE));
    }
    
    public static IdentityService getIdentityService() {
    	return (IdentityService) GlobalResourceLoader.getService(new QName(KIM_IDENTITY_SERVICE));
    }
    public static PermissionService getPermissionService() {
    	return (PermissionService) GlobalResourceLoader.getService(new QName(KIM_PERMISSION_SERVICE));
    }
    public static ResponsibilityService getResponsibilityService() {
    	return (ResponsibilityService) GlobalResourceLoader.getService(new QName(KIM_RESPONSIBILITY_SERVICE));
    }
    
    public static RoleManagementService getRoleManagementService() {
    	if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + KIM_ROLE_MANAGEMENT_SERVICE);
		}
    	return (RoleManagementService) GlobalResourceLoader.getResourceLoader().getService(new QName(KIM_ROLE_MANAGEMENT_SERVICE));
    }
    
}
