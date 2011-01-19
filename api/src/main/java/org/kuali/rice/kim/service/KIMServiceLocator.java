package org.kuali.rice.kim.service;

import org.apache.log4j.Logger;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

import javax.xml.namespace.QName;

public class KIMServiceLocator {
    private static final Logger LOG = Logger.getLogger(KIMServiceLocator.class);

    public static final String KIM_PERSON_SERVICE = "personService";
    public static final String KIM_IDENTITY_MANAGEMENT_SERVICE = "kimIdentityManagementService";
    public static final String KIM_ROLE_SERVICE = "kimRoleService";


    @SuppressWarnings("unchecked")
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
}
