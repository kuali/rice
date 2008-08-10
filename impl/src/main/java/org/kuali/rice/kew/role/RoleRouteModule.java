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
package org.kuali.rice.kew.role;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.role.service.RoleService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routemodule.BaseRouteModule;
import org.kuali.rice.kew.routemodule.RouteModuleException;
import org.kuali.rice.kew.routetemplate.FlexRM;
import org.kuali.rice.kew.util.PerformanceLogger;


/**
 * The RouteModule which is responsible for generating Action Requests from Roles configured in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleRouteModule extends BaseRouteModule {

private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlexRM.class);

	public static String EFFECTIVE_DATE_PARAM = "RoleRouteModule.EffectiveDate";

	public List findActionRequests(RouteContext context) throws Exception {
		DocumentRouteHeaderValue document = context.getDocument();
		Role role = getRole(context);
		LOG.debug("Generating Action Requests for role '" + role.getName() + "' on document " + document.getRouteHeaderId());
		List<QualifiedRole> roleEvaluationSet = loadRoleEvaluationSet(context, role);
		List<RoleResolver> roleResolvers = findRoleResolvers(role, context);
		if (roleResolvers == null || roleResolvers.isEmpty()) {
			throw new RouteModuleException("Could not find a RoleResolver for this role '" + role.getName() + "'");
		}
		List<QualifiedRole> matchingQualifiedRoles = applyRoleResolvers(context, role, roleEvaluationSet, roleResolvers);
		return generateActionRequests(context, matchingQualifiedRoles, role);
	}

	protected Role getRole(RouteContext context) {
		String roleName = getRoleName(context);
		Role role = KEWServiceLocator.getRoleService().findRoleByName(roleName);
		if (role == null) {
			throw new RouteModuleException("Could not locate the role with the given name '" + roleName + "'");
		}
		return role;
	}

	protected List<QualifiedRole> loadRoleEvaluationSet(RouteContext context, Role role) {
		return getRoleService().findQualifiedRolesForRole(role.getName(), getEffectiveDate(context));
	}

	protected Timestamp getEffectiveDate(RouteContext context) {
		return (Timestamp)context.getParameters().get(EFFECTIVE_DATE_PARAM);
	}

	protected List<QualifiedRole> applyRoleResolvers(RouteContext context, Role role, List<QualifiedRole> roleEvaluationSet, List<RoleResolver> resolvers) {
		List<List<QualifiedRole>> resolvedQualifiedRoles = new ArrayList<List<QualifiedRole>>();
		for (RoleResolver resolver : resolvers) {
			List<QualifiedRole> resolved = resolver.resolve(context, role, roleEvaluationSet);
			if (resolved != null && !resolved.isEmpty()) {
				resolvedQualifiedRoles.add(resolved);
			}
		}
		return mergeResolvedQualifiedRoles(context, role, resolvedQualifiedRoles);
	}

	protected List<QualifiedRole> mergeResolvedQualifiedRoles(RouteContext context, Role role, List<List<QualifiedRole>> resolvedQualifiedRoles) {
		Set<Long> mergedIds = new HashSet<Long>();
		List<QualifiedRole> qualifiedRoles = new ArrayList<QualifiedRole>();
		for (List<QualifiedRole> qualifiedRoleSet : resolvedQualifiedRoles) {
			for (QualifiedRole qualifiedRole : qualifiedRoleSet) {
				Long qRoleId = qualifiedRole.getQualifiedRoleId();
				if (!mergedIds.contains(qRoleId)) {
					qualifiedRoles.add(qualifiedRole);
					if (qRoleId != null) {
						mergedIds.add(qRoleId);
					}
				}
			}
		}
		return qualifiedRoles;
	}

	protected String getRoleName(RouteContext context) {
		RouteNode node = context.getNodeInstance().getRouteNode();
		return node.getRouteMethodName();
	}

	protected List<RoleResolver> findRoleResolvers(Role role, RouteContext context) {
		// TODO
		return null;
	}

	protected List<ActionRequestValue> generateActionRequests(RouteContext context, List<QualifiedRole> qualifiedRoles, Role role) {
		PerformanceLogger performanceLogger = new PerformanceLogger();
		ActionRequestFactory factory = new ActionRequestFactory(context.getDocument(), context.getNodeInstance());
		for (QualifiedRole qualifiedRole : qualifiedRoles) {
			generateActionRequests(context, qualifiedRole, factory);
		}
		List<ActionRequestValue> actionRequests = factory.getRequestGraphs();
		performanceLogger.log("Time to make action requests for role '" + role.getName() + "'");
		return actionRequests;
	}

	protected void generateActionRequests(RouteContext context, QualifiedRole qualifiedRole, ActionRequestFactory factory) {
		// TODO
	}

	private RoleService getRoleService() {
		return KEWServiceLocator.getRoleService();
	}

	private ActionRequestService getActionRequestService() {
		return KEWServiceLocator.getActionRequestService();
	}

}
