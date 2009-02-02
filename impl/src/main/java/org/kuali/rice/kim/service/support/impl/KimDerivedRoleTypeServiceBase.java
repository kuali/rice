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
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is the base class for all derived role type services 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDerivedRoleTypeServiceBase extends KimRoleTypeServiceBase {

	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getRoleMembersFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        return new ArrayList<RoleMembershipInfo>();
	}

	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#isApplicationRoleType()
	 */
	@Override
	public boolean isApplicationRoleType() {
		return true;
	}

}