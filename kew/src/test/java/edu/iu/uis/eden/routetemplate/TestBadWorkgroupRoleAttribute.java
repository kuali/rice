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
package edu.iu.uis.eden.routetemplate;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * A workgroup role attribute which resolves to workgroups with empty ids.  This
 * should result in an error being thrown from the engine.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestBadWorkgroupRoleAttribute extends AbstractRoleAttribute {

	public List<Role> getRoleNames() {
		List<Role> roles = new ArrayList<Role>();
		roles.add(new Role(getClass(), "workgroup", "workgroup label"));
		return roles;
	}

	public List<String> getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
		List<String> qualRoleNames = new ArrayList<String>();
		qualRoleNames.add("TestWorkgroup");
		return qualRoleNames;
	}


	public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
		List<Id> recipients = new ArrayList<Id>();
		recipients.add(new GroupNameId(null));
		recipients.add(new GroupNameId(""));
		return new ResolvedQualifiedRole("workgroup role label", recipients);
	}



}
