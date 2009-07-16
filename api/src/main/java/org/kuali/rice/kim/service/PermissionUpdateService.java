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
package org.kuali.rice.kim.service;

import javax.jws.WebParam;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;


/**
 * This service provides operations for creating and updating permissions. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface PermissionUpdateService {

	void savePermission( @WebParam(name="permissionId") String permissionId, 
			 @WebParam(name="permissionTemplateId") String permissionTemplateId, 
			 @WebParam(name="namespaceCode") String namespaceCode,
			 @WebParam(name="name") String name,
			 @WebParam(name="description") String description,
			 @WebParam(name="active") boolean active,
			 @WebParam(name="permissionDetails") AttributeSet permissionDetails );
	
}
