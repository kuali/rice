/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kns.bo.Inactivateable;


/**
 * Represents a single permissions within the system.
 * 
 * Permissions are attached to roles.  All authorization checks should be done against permissions,
 * never against roles or groups.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimPermission extends Inactivateable {

	String getPermissionId();
	
	/** List of details for this role/permission relationship.  See the class comment for more information. */
	AttributeSet getDetails();
	
	public boolean hasDetails();
	
	String getNamespaceCode();
	String getName();
	String getTemplateId();
	
	/** Verbose description of the Permission and functionally what permissions it implies. */
	String getDescription();
	
}
