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

import java.util.List;
import java.util.Map;


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
public interface RoleMember {

	/** Unique identifier for a this join record. */
	String getRoleMemberId();
	
	/** ID of the role of which this person/group is a member. */
	String getRoleId();
	
	/** Returns a string identifying the type of member.  Something like "GROUP" or "PRINCIPAL".
	 * (up to the implementation)
	 */
	String getRoleMemberTypeCode();
	
	/** Returns the actual implementation class for the role member instance. */
	Class<? extends RoleMember> getRoleMemberClass();
	
	/**
	 * The unique identifier of the member from its respective table.
	 * In the default implementation this will either be a principalId
	 * or a RoleId.
	 */
	String getMemberId();

	/** List of qualifier for this role/member relationship.  See the class comment for more information. */
	List<RoleMemberAttributeData> getQualifier();
	Map<String,String> getQualifierAsMap();
	boolean hasQualifier();
}
