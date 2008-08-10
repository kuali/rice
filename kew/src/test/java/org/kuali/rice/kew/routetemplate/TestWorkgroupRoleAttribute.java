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
package org.kuali.rice.kew.routetemplate;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.Id;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.AbstractRoleAttribute;
import org.kuali.rice.kew.rule.ResolvedQualifiedRole;
import org.kuali.rice.kew.rule.Role;
import org.kuali.rice.kew.workgroup.GroupNameId;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestWorkgroupRoleAttribute extends AbstractRoleAttribute {

	public List<Role> getRoleNames() {
		List<Role> roles = new ArrayList<Role>();
		roles.add(new Role(getClass(), "workgroup", "workgroup label"));
		return roles;
	}

	public List<String> getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws KEWUserNotFoundException {
		List<String> qualRoleNames = new ArrayList<String>();
		qualRoleNames.add("TestWorkgroup");
		return qualRoleNames;
	}


	public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws KEWUserNotFoundException {
		List<Id> recipients = new ArrayList<Id>();
		recipients.add(new GroupNameId(qualifiedRole));
		return new ResolvedQualifiedRole("workgroup role lable", recipients);
	}



}
