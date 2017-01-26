/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;

import java.util.List;
import java.util.Set;

/**
 * This is an internal service that was created as a proxy for kew
 * updates
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ResponsibilityInternalService {

	void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds);
	RoleMemberBo saveRoleMember(RoleMemberBo roleMember);
    DelegateMemberBo saveDelegateMember(DelegateMemberBo delegateMember);
	void removeRoleMember(RoleMemberBo roleMember);
	void updateActionRequestsForRoleChange(String roleId);

        /**
     * Lets the system know (mainly for UI purposes) whether this responsibility expects RoleResponsibilityAction
     * records to be given at the assignment level or are global to the responsibility.  (I.e., they apply
     * to any member assigned to the responsibility.)
     */
   	boolean areActionsAtAssignmentLevelById(String responsibilityId );

    /**
     * Lets the system know (mainly for UI purposes) whether this responsibility expects RoleResponsibilityAction
     * records to be given at the assignment level or are global to the responsibility.  (I.e., they apply
     * to any member assigned to the responsibility.)
     */
   	boolean areActionsAtAssignmentLevel(Responsibility responsibility );
   	
   	/**
   	 * Get all the role-responsibility records attached to the given role.
   	 */
   	public List<RoleResponsibility> getRoleResponsibilities(String roleId);
}
