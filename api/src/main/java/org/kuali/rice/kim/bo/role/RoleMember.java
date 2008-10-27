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
package org.kuali.rice.kim.bo.role;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.bo.Inactivateable;


/**
 * Base class for the common methods between objects which may be members of a Role.
 * 
 *  This provides a common API for accessing group member information by code 
 *  that may want to iterate over multiple types (like groups and principals)
 *  
 *  It also contains the API for qualifier which may be attached to a member's
 *  "membership" in this role.  That is, they are a member of this role only when
 *  the listed qualifier are matched.  The corresponding KimTypeService implementation
 *  determines what a matching qualification is.  The AuthorizationService will make the
 *  appropriate service calls to match these up.
 * 
 * @author Kuali Rice Team (kuali-rice@googleRoles.com)
 *
 */
public interface RoleMember extends Inactivateable {

	/** Unique identifier for a this join record. */
	String getRoleMemberId();
	
	/** ID of the role of which this person/group is a member. */
	String getRoleId();
	
	/** List of qualifier for this role/member relationship.  See the class comment for more information. */
	AttributeSet getQualifier();
	boolean hasQualifier();
}
