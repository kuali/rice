/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.role;

import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleAttribute implements java.io.Serializable {

	private Long roleAttributeId;

	private Long roleId;
	private Long ruleAttributeId;
	private Integer lockVerNbr;

	private Role role;
	private RuleAttribute ruleAttribute;

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Long getRoleAttributeId() {
		return roleAttributeId;
	}
	public void setRoleAttributeId(Long roleAttributeId) {
		this.roleAttributeId = roleAttributeId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public RuleAttribute getRuleAttribute() {
		return ruleAttribute;
	}
	public void setRuleAttribute(RuleAttribute ruleAttribute) {
		this.ruleAttribute = ruleAttribute;
	}
	public Long getRuleAttributeId() {
		return ruleAttributeId;
	}
	public void setRuleAttributeId(Long ruleAttributeId) {
		this.ruleAttributeId = ruleAttributeId;
	}

}
