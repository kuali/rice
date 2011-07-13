/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kim.service;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.IdentityArchiveService;
import org.kuali.rice.kim.api.group.GroupUpdateService;
import org.kuali.rice.kim.api.role.RoleUpdateService;
import org.kuali.rice.kim.util.KimConstants;

import javax.xml.namespace.QName;

/**
 * Service locator for KIM.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class KIMServiceLocatorInternal {

	private static final Logger LOG = Logger.getLogger(KIMServiceLocatorInternal.class);

	public static final String KIM_RUN_MODE_PROPERTY = "kim.mode";

    public static final String KIM_IDENTITY_UPDATE_SERVICE = "kimIdentityUpdateService";
	public static final String KIM_IDENTITY_ARCHIVE_SERVICE = "kimIdentityArchiveService";
	public static final String KIM_GROUP_UPDATE_SERVICE = "kimGroupUpdateService";
    public static final String KIM_ROLE_UPDATE_SERVICE = "kimRoleUpdateService";
	public static final String KIM_PERMISSION_UPDATE_SERVICE = "kimPermissionUpdateService";
	public static final String KIM_AUTHENTICATION_SERVICE = "kimAuthenticationService";
    public static final String KIM_UI_DOCUMENT_SERVICE = "kimUiDocumentService";
	public static final String GROUP_INTERNAL_SERVICE = "groupInternalService";

    public static Object getService(String serviceName) {
		return getBean(serviceName);
	}

	public static Object getBean(String serviceName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + serviceName);
		}
		return GlobalResourceLoader.getResourceLoader().getService(
                (RunMode.REMOTE.equals(RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KIM_RUN_MODE_PROPERTY)))) ?
                        new QName(KimConstants.KIM_MODULE_NAMESPACE, serviceName) : new QName(serviceName));
	}

    public static IdentityUpdateService getIdentityUpdateService() {
    	return (IdentityUpdateService)getService(KIM_IDENTITY_UPDATE_SERVICE);
    }

    public static IdentityArchiveService getIdentityArchiveService() {
    	return (IdentityArchiveService)getService(KIM_IDENTITY_ARCHIVE_SERVICE);
    }

    

    public static GroupUpdateService getGroupUpdateService() {
    	return (GroupUpdateService)getService(KIM_GROUP_UPDATE_SERVICE);
    }

    public static RoleUpdateService getRoleUpdateService() {
    	return (RoleUpdateService)getService(KIM_ROLE_UPDATE_SERVICE);
    }

    
    public static PermissionUpdateService getPermissionUpdateService() {
    	return (PermissionUpdateService)getService(KIM_PERMISSION_UPDATE_SERVICE);
    }

    public static AuthenticationService getAuthenticationService() {
    	if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + KIM_AUTHENTICATION_SERVICE);
		}
    	return (AuthenticationService) GlobalResourceLoader.getResourceLoader().getService(new QName(KIM_AUTHENTICATION_SERVICE));
    }

    public static UiDocumentService getUiDocumentService() {
    	return (UiDocumentService)getService(KIM_UI_DOCUMENT_SERVICE);
    }

    public static GroupInternalService getGroupInternalService() {
        return (GroupInternalService)getService(GROUP_INTERNAL_SERVICE);
    }

}
