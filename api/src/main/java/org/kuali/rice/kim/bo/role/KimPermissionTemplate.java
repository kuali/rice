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

import org.kuali.rice.krad.bo.Inactivatable;


/**
 * Represents a single permission template within the system.
 * 
 * Permissions templates define the type (and corresponding set of attributes) for the permission as well as a default name and description.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimPermissionTemplate extends Inactivatable {

	String getPermissionTemplateId();
	
	String getNamespaceCode();
	String getName();

	/** Verbose description of the Permission and functionally what permissions it implies. */
	String getDescription();

	/** Type identifier for this role.  This will control what additional attributes are available */
	String getKimTypeId();
	
}
