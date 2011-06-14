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
package org.kuali.rice.kim.bo;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface Role extends ExternalizableBusinessObject {

	public static final String GROUP_MEMBER_TYPE = "G";
	public static final String PRINCIPAL_MEMBER_TYPE = "P";
	public static final String ROLE_MEMBER_TYPE = "R";
	
	/** Unique identifier for this role. */
	String getRoleId();
	
	/** Namespace for this role - identifies the system/module to which this role applies */
	String getNamespaceCode();
	
	/** Name for this role.  This value will be seen by the users. */
	String getRoleName();
	
	/** Verbose description of the role and functionally what permissions it implies. */
	String getRoleDescription();
	
	/** Type identifier for this role.  This will control what additional attributes are available */
	String getKimTypeId();
	
}
