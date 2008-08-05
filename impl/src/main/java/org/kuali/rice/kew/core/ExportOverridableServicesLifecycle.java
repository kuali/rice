/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.core;

import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.ksb.messaging.JavaServiceDefinition;
import org.kuali.rice.ksb.messaging.PropertyConditionalKSBExporter;

import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * This class is used to export the UserService and WorkgroupService to the service bus.  We originally did this in
 * Spring.  However, the problem here was that if the user and/or workgroup services were being overridden in the 
 * institutional plugin, we would be proxying the non-overridden version of the service.  So we need to do it here
 * manually after the plugin registry has started and pull the user/group services from the resource loading so
 * that we get the proper overridden versions.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ExportOverridableServicesLifecycle extends BaseLifecycle {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.core.lifecycle.BaseLifecycle#start()
     */
    @Override
    public void start() throws Exception {
	PropertyConditionalKSBExporter userServiceExporter = new PropertyConditionalKSBExporter();
	JavaServiceDefinition userServiceDef = new JavaServiceDefinition();
	userServiceDef.setService(KEWServiceLocator.getUserService());
	userServiceDef.setServiceInterface(UserService.class.getName());
	userServiceDef.setServiceNameSpaceURI("");
	userServiceDef.setLocalServiceName(KEWServiceLocator.USER_SERVICE);
	userServiceExporter.setServiceDefinition(userServiceDef);
	userServiceExporter.setPropertyName(KEWConstants.USE_REMOTE_IDENTITY_SERVICES);
	userServiceExporter.afterPropertiesSet();
	
	PropertyConditionalKSBExporter workgroupServiceExporter = new PropertyConditionalKSBExporter();
	JavaServiceDefinition workgroupServiceDef = new JavaServiceDefinition();
	workgroupServiceDef.setService(KEWServiceLocator.getWorkgroupService());
	workgroupServiceDef.setServiceInterface(WorkgroupService.class.getName());
	workgroupServiceDef.setServiceNameSpaceURI("");
	workgroupServiceDef.setLocalServiceName(KEWServiceLocator.WORKGROUP_SRV);
	workgroupServiceExporter.setServiceDefinition(workgroupServiceDef);
	workgroupServiceExporter.setPropertyName(KEWConstants.USE_REMOTE_IDENTITY_SERVICES);
	workgroupServiceExporter.afterPropertiesSet();
	super.start();
    }
    
}
