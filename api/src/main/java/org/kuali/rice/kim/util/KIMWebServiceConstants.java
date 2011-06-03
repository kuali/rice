/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.util;

import org.kuali.rice.core.util.RiceConstants;


/**
 * This class is used to hold constants that are used when exposing services to the bus
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KIMWebServiceConstants {

	public static final String MODULE_TARGET_NAMESPACE = RiceConstants.RICE_JAXWS_TARGET_NAMESPACE_BASE + "/kim";

	public static final class PermissionService {
		public static final String WEB_SERVICE_NAME = "kimPermissionServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.service.PermissionService";
		public static final String WEB_SERVICE_PORT = "KimPermissionServicePort";
		
		private PermissionService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class PermissionUpdateService {
		public static final String WEB_SERVICE_NAME = "kimPermissionUpdateServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.service.PermissionUpdateService";
		public static final String WEB_SERVICE_PORT = "KimPermissionUpdateServicePort";
		
		private PermissionUpdateService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class RoleService {
		public static final String WEB_SERVICE_NAME = "kimRoleServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.service.RoleService";
		public static final String WEB_SERVICE_PORT = "RoleServicePort";
		
		private RoleService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class RoleUpdateService {
		public static final String WEB_SERVICE_NAME = "kimRoleUpdateServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.service.RoleUpdateService";
		public static final String WEB_SERVICE_PORT = "RoleUpdateServicePort";
		
		private RoleUpdateService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class GroupService {
		public static final String WEB_SERVICE_NAME = "kimGroupServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.api.group.GroupService";
		public static final String WEB_SERVICE_PORT = "GroupServicePort";
		
		private GroupService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class GroupUpdateService {
		public static final String WEB_SERVICE_NAME = "kimGroupUpdateServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.api.group.GroupUpdateService";
		public static final String WEB_SERVICE_PORT = "GroupUpdateServicePort";
		
		private GroupUpdateService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class ResponsibilityService {
		public static final String WEB_SERVICE_NAME = "kimResponsibilityServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.service.ResponsibilityService";
		public static final String WEB_SERVICE_PORT = "ResponsibilityServicePort";
		
		private ResponsibilityService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class ResponsibilityUpdateService {
		public static final String WEB_SERVICE_NAME = "kimResponsibilityUpdateServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.service.ResponsibilityUpdateService";
		public static final String WEB_SERVICE_PORT = "ResponsibilityUpdateServicePort";
		
		private ResponsibilityUpdateService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class IdentityService {
		public static final String WEB_SERVICE_NAME = "kimIdentityServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.api.entity.services.IdentityService";
		public static final String WEB_SERVICE_PORT = "IdentityServicePort";
		
		private IdentityService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public static final class IdentityManagementService {
		public static final String WEB_SERVICE_NAME = "kimIdentityManagementServiceSOAP";
		public static final String INTERFACE_CLASS = "org.kuali.rice.kim.api.services.IdentityManagementService";
		public static final String WEB_SERVICE_PORT = "IdentityManagementServicePort";
		
		private IdentityManagementService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	public final static class IdentityManagementNotificationService {
		public static final String WEB_SERVICE_NAME = "kimIdentityManagementNotificationServiceSOAP";
		
		private IdentityManagementNotificationService() {
			throw new UnsupportedOperationException("do not call");
		}
	}

	private KIMWebServiceConstants() {
		throw new UnsupportedOperationException("do not call");
	}
}
