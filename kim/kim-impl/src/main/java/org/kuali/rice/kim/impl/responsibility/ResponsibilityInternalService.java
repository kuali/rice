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
package org.kuali.rice.kim.impl.responsibility;

import java.util.Set;

import org.kuali.rice.kim.impl.role.RoleMemberBo;

/**
 * This is an internal service that was created as a proxy for kew
 * updates
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ResponsibilityInternalService {

	void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds);
	void saveRoleMember(RoleMemberBo roleMember);
	void removeRoleMember(RoleMemberBo roleMember);
	void updateActionRequestsForRoleChange(String roleId);
}
