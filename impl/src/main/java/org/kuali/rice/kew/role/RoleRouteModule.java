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
package org.kuali.rice.kew.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNodeUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * The RoleRouteModule is responsible for interfacing with the KIM
 * Role system to provide Role-based routing to KEW. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleRouteModule implements RouteModule {
	
	protected static final String QUALIFIER_RESOLVER_ELEMENT = "qualifierResolver";
	protected static final String RESPONSIBILITY_NAME_ELEMENT = "responsibilityName";
	protected static final String APPROVE_POLICY_ELEMENT = "approvePolicy";
	
	public List<ActionRequestValue> findActionRequests(RouteContext context)
			throws Exception {
		
		ActionRequestFactory arFactory = new ActionRequestFactory(context.getDocument(), context.getNodeInstance());
		List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
		QualifierResolver qualifierResolver = loadQualifierResolver(context);
		List<Map<String, String>> qualifiers = qualifierResolver.resolve(context);
		String documentTypeName = context.getDocument().getDocumentType().getName();
		String nodeName = context.getNodeInstance().getName();
		String approvePolicy = loadApprovePolicy(context);
		for (Map<String, String> qualifier : qualifiers) {
			List<ResponsibilityActionInfo> responsibilities = thisIsWhereWeCallTheResponsibilityService(loadResponsibilityName(context), documentTypeName, nodeName, qualifier);
			// in the case of "all approve" from KIM roles, we really want this to be treated as separate and distinct requests, let's
			// handle the demultiplexing manually
			if (KEWConstants.APPROVE_POLICY_ALL_APPROVE.equals(approvePolicy)) {
				for (ResponsibilityActionInfo responsibility : responsibilities) {
					arFactory.addRoleResponsibilityRequest(Collections.singletonList(responsibility), approvePolicy);
				}
			} else {
				arFactory.addRoleResponsibilityRequest(responsibilities, approvePolicy);
			}
		}		
		actionRequests = new ArrayList<ActionRequestValue>(arFactory.getRequestGraphs());
		return actionRequests;
	}

	protected QualifierResolver loadQualifierResolver(RouteContext context) {
		String qualifierResolverName = RouteNodeUtils.getValueOfCustomProperty(context.getNodeInstance().getRouteNode(), QUALIFIER_RESOLVER_ELEMENT);
		QualifierResolver resolver = null;
		if (!StringUtils.isBlank(qualifierResolverName)) {
			RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(qualifierResolverName);
			if (ruleAttribute == null) {
				throw new RiceRuntimeException("Failed to locate QualifierResolver for attribute name: " + qualifierResolverName);
			}
			ObjectDefinition definition = getAttributeObjectDefinition(ruleAttribute);
			resolver = (QualifierResolver)GlobalResourceLoader.getObject(definition);
		}
		if (resolver == null) {
			// TODO alternatively, in future could provide a default implementation?
			throw new RiceRuntimeException("Could not determine qualiferResolver for this node.  QualifierResolver name was " + qualifierResolverName + ".  Please define one in document type XML.");
		}
		return resolver;
	}
	
	protected String loadResponsibilityName(RouteContext context) {
		String responsibilityName = RouteNodeUtils.getValueOfCustomProperty(context.getNodeInstance().getRouteNode(), RESPONSIBILITY_NAME_ELEMENT);
		if (StringUtils.isBlank(responsibilityName)) {
			throw new RiceRuntimeException("Failed to determine the responsibility name.  Please ensure that <responsibilityName> is configured in your Document Type XML.");
		}
		return responsibilityName;
	}
	
	protected String loadApprovePolicy(RouteContext context) {
		String approvePolicy = RouteNodeUtils.getValueOfCustomProperty(context.getNodeInstance().getRouteNode(), APPROVE_POLICY_ELEMENT);
		if (StringUtils.isBlank(approvePolicy)) {
			approvePolicy = KEWConstants.APPROVE_POLICY_FIRST_APPROVE;
		}
		return approvePolicy;
	}
	
    protected ObjectDefinition getAttributeObjectDefinition(RuleAttribute ruleAttribute) {
    	return new ObjectDefinition(ruleAttribute.getClassName(), ruleAttribute.getMessageEntity());
    }
    
    protected List<ResponsibilityActionInfo> thisIsWhereWeCallTheResponsibilityService(String responsibilityName, String documentTypeName, String nodeName, Map<String, String> qualification) {
    	// for now, let's stub in something dumb
    	//KIMServiceLocator.getResponsibilityService().getResponsibilityInfoByName(responsibilityName, qualification, responsibilityDetails);
        List<ResponsibilityActionInfo> responsibilityInfos = new ArrayList<ResponsibilityActionInfo>();
        String chart = qualification.get("chart");
        String org = qualification.get("org");
        if (chart.equals("BL")) {
        	ResponsibilityActionInfo info = new ResponsibilityActionInfo();
        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("admin");
        	info.setPrincipalId(principal.getPrincipalId());
        	info.setPriorityNumber(1);
        	info.setResponsibilityId("123");
        	info.setResponsibilityName(responsibilityName);
        	info.setRoleId("1234");
        	responsibilityInfos.add(info);
        	// add a second one
        	info = new ResponsibilityActionInfo();
        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        	principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user2");
        	info.setPrincipalId(principal.getPrincipalId());
        	info.setPriorityNumber(1);
        	info.setResponsibilityId("1235");
        	info.setResponsibilityName(responsibilityName);
        	info.setRoleId("1234");
        	responsibilityInfos.add(info);
        } else if (chart.equals("IN")) {
        	ResponsibilityActionInfo info = new ResponsibilityActionInfo();
        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user1");
        	info.setPrincipalId(principal.getPrincipalId());
        	info.setPriorityNumber(1);
        	info.setResponsibilityId("321");
        	info.setResponsibilityName(responsibilityName);
        	info.setRoleId("4321");
        	responsibilityInfos.add(info);
        
        	// not quite ready for groups yet
        	/*
        	ResponsibilityActionInfo info = new ResponsibilityActionInfo();
        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        	KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName("KFS", "WorkflowAdmin");
        	info.setGroupId(group.getGroupId());
        	info.setPriorityNumber(1);
        	info.setResponsibilityId("321456");
        	info.setResponsibilityName(responsibilityName);
        	info.setRoleId("4321");
        	responsibilityInfos.add(info);
        	*/
        } 
        return responsibilityInfos;
    }
	
	/**
	 * Return null so that the responsibility ID will remain the same.
	 *
	 * @see org.kuali.rice.kew.routemodule.RouteModule#resolveResponsibilityId(java.lang.Long)
	 */
	public ResponsibleParty resolveResponsibilityId(Long responsibilityId)
			throws WorkflowException {
		return null;
	}

}
