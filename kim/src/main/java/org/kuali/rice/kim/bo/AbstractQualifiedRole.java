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
package org.kuali.rice.kim.bo;

import java.io.Serializable;


/**
 * Abstract class from which GroupQualifiedRole and PrincipalQualifiedRole extend.
 * For simplicity, KIM interfaces may return objects of this class, which then can be cast
 * to the appropriate qualified role sub-class.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class AbstractQualifiedRole implements Serializable {
	private static final long serialVersionUID = -5155126090045426553L;

	private Long roleId;

	private Role role;

	/**
     * @return the roleId
     */
    public Long getRoleId() {
        return this.roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * This method ...
     *
     * @return Role
     */
    public Role getRole() {
	    return this.role;
	}

    /**
     * This method ...
     *
     * @param role
     */
	public void setRole(Role role) {
	    this.role = role;
	}
}
