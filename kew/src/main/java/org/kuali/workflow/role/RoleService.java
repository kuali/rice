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

import java.sql.Timestamp;
import java.util.List;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RoleService {

	public Role findRoleById(Long roleId);

	public Role findRoleByName(String roleName);

	public QualifiedRole findQualifiedRoleById(Long qualifiedRoleId);

	public List<QualifiedRole> findQualifiedRolesForRole(String roleName, Timestamp effectiveDate);

	public void save(Role role);

	public void save(QualifiedRole qualifiedRole);

	/**
	 * Re-resolves the given qualified role for all documents for the given document type (including children).
	 * This methods executes asynchronously.
	 */
	public void reResolveRole(DocumentType documentType, String roleName) throws WorkflowException;

	/**
	 * Re-resolves the given qualified role for all documents for the given document type (including children).
	 * This methods executes asynchronously.
	 */
	public void reResolveQualifiedRole(DocumentType documentType, String roleName, String qualifiedRoleNameLabel) throws WorkflowException;

	/**
	 * Re-resolves the given qualified role on the given document.  This method executes synchronously.
	 */
	public void reResolveQualifiedRole(DocumentRouteHeaderValue routeHeader, String roleName, String qualifiedRoleNameLabel) throws WorkflowException;

	/**
	 * Re-resolves the given role on the given document.  This method executes synchronously.
	 */
	public void reResolveRole(DocumentRouteHeaderValue routeHeader, String roleName) throws WorkflowException;

}
