/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.rule;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;

import java.util.ArrayList;
import java.util.List;


/**
 * RolePoker implementation which handles initiating refresh of the "poked" role.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RolePokerProcessor implements RolePoker {

	public void reResolveRole(Long documentId, String roleName, String qualifiedRoleNameLabel) {
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
		if (org.apache.commons.lang.StringUtils.isEmpty(roleName)) {
			throw new IllegalArgumentException("Can't poke a role without a role name!");
		}
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		try {
		if (qualifiedRoleNameLabel == null) {
			KEWServiceLocator.getRoleService().reResolveRole(document, roleName);
		} else {
			KEWServiceLocator.getRoleService().reResolveQualifiedRole(document, roleName, qualifiedRoleNameLabel);
		}
		} catch (WorkflowException e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	public void reResolveRole(Long documentId, String roleName) {
		reResolveRole(documentId, roleName, null);
	}
		
	private String[] parseParameters(String parameters) {
		List strings = new ArrayList();
		boolean isEscaped = false;
		StringBuffer buffer = new StringBuffer();
		for (int index = 0; index < parameters.length(); index++) {
			char character = parameters.charAt(index);
			if (isEscaped) {
				isEscaped = false;
				buffer.append(character);
			} else {
				if (character == '\\') {
					isEscaped = true;
				} else if (character == ',') {
					strings.add(buffer.toString());
					buffer = new StringBuffer();
				} else {
					buffer.append(character);
				}
			}
		}
		return (String[])strings.toArray(new String[0]);
	}



}
