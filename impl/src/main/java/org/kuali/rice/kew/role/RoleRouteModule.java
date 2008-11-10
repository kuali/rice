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
import org.kuali.rice.kew.rule.XmlConfiguredAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
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
	protected static final String NAMESPACE_ELEMENT = "namespace";
	
	protected static final String DOCUMENT_TYPE_DETAIL = "documentType";
	protected static final String NODE_NAME_DETAIL = "nodeName";
	
	public List<ActionRequestValue> findActionRequests(RouteContext context)
			throws Exception {
		
		ActionRequestFactory arFactory = new ActionRequestFactory(context.getDocument(), context.getNodeInstance());
		List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
		QualifierResolver qualifierResolver = loadQualifierResolver(context);
		List<AttributeSet> qualifiers = qualifierResolver.resolve(context);
		String responsibilityName = loadResponsibilityName(context);
		String namespaceCode = loadNamespace(context);
		AttributeSet responsibilityDetails = loadResponsibilityDetails(context);
		for (AttributeSet qualifier : qualifiers) {
			List<ResponsibilityActionInfo> responsibilities = KIMServiceLocator.getResponsibilityService().getResponsibilityActions(namespaceCode, responsibilityName, qualifier, responsibilityDetails);
			List<ResponsibilitySet> responsibilitySets = partitionResponsibilities(responsibilities);
			for (ResponsibilitySet responsibilitySet : responsibilitySets) {
				String approvePolicy = responsibilitySet.getApprovePolicy();
				if (KEWConstants.APPROVE_POLICY_ALL_APPROVE.equals(approvePolicy)) {
					for (ResponsibilityActionInfo responsibility : responsibilities) {
						arFactory.addRoleResponsibilityRequest(Collections.singletonList(responsibility), approvePolicy);
					}
				} else {
					arFactory.addRoleResponsibilityRequest(responsibilities, approvePolicy);
				}
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
			if (resolver instanceof XmlConfiguredAttribute) {
				((XmlConfiguredAttribute)resolver).setRuleAttribute(ruleAttribute);
			}
		}
		if (resolver == null) {
			// TODO alternatively, in future could provide a default implementation?
			throw new RiceRuntimeException("Could not determine qualiferResolver for this node.  QualifierResolver name was " + qualifierResolverName + ".  Please define one in document type XML.");
		}
		return resolver;
	}
	
	protected AttributeSet loadResponsibilityDetails(RouteContext context) {
		String documentTypeName = context.getDocument().getDocumentType().getName();
		String nodeName = context.getNodeInstance().getName();
		AttributeSet responsibilityDetails = new AttributeSet();
		responsibilityDetails.put(DOCUMENT_TYPE_DETAIL, documentTypeName);
		responsibilityDetails.put(NODE_NAME_DETAIL, nodeName);
		return responsibilityDetails;
	}
	
	protected String loadResponsibilityName(RouteContext context) {
		String responsibilityName = RouteNodeUtils.getValueOfCustomProperty(context.getNodeInstance().getRouteNode(), RESPONSIBILITY_NAME_ELEMENT);
		if (StringUtils.isBlank(responsibilityName)) {
			throw new RiceRuntimeException("Failed to determine the responsibility name.  Please ensure that <responsibilityName> is configured in your Document Type XML.");
		}
		return responsibilityName;
	}
	
	protected String loadNamespace(RouteContext context) {
		String namespace = RouteNodeUtils.getValueOfCustomProperty(context.getNodeInstance().getRouteNode(), NAMESPACE_ELEMENT);
		if (StringUtils.isBlank(namespace)) {
			namespace = "";
		}
		return namespace;
	}
	
    protected ObjectDefinition getAttributeObjectDefinition(RuleAttribute ruleAttribute) {
    	return new ObjectDefinition(ruleAttribute.getClassName(), ruleAttribute.getServiceNamespace());
    }
    
    protected List<ResponsibilitySet> partitionResponsibilities(List<ResponsibilityActionInfo> responsibilities) {
    	List<ResponsibilitySet> responsibilitySets = new ArrayList<ResponsibilitySet>();
    	for (ResponsibilityActionInfo responsibility : responsibilities) {
    		ResponsibilitySet targetResponsibilitySet = null;
    		for (ResponsibilitySet responsibiliySet : responsibilitySets) {
    			if (responsibiliySet.matches(responsibility)) {
    				targetResponsibilitySet = responsibiliySet;
    			}
    		}
    		if (targetResponsibilitySet == null) {
    			targetResponsibilitySet = new ResponsibilitySet(responsibility);
    			responsibilitySets.add(targetResponsibilitySet);
    		}
    		targetResponsibilitySet.getResponsibilities().add(responsibility);
    	}
    	return responsibilitySets;
    }
    
//    protected List<ResponsibilityActionInfo> thisIsWhereWeCallTheResponsibilityService(String responsibilityName, String documentTypeName, String nodeName, AttributeSet qualification) {
//    	//KIMServiceLocator.getResponsibilityService().getResponsibilityActions("", responsibilityName, qualification, responsibilityDetails)
//    	// for now, let's stub in something dumb
//    	//KIMServiceLocator.getResponsibilityService().getResponsibilityInfoByName(responsibilityName, qualification, responsibilityDetails);
//        List<ResponsibilityActionInfo> responsibilityInfos = new ArrayList<ResponsibilityActionInfo>();
//        String chart = qualification.get("chart");
//        String org = qualification.get("org");
//        if (chart.equals("BL")) {
//        	ResponsibilityActionInfo info = new ResponsibilityActionInfo();
//        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
//        	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("admin");
//        	info.setPrincipalId(principal.getPrincipalId());
//        	info.setPriorityNumber(1);
//        	info.setResponsibilityName("123");
//        	info.setResponsibilityName(responsibilityName);
//        	info.setRoleId("1234");
//        	responsibilityInfos.add(info);
//        	// add a second one
//        	info = new ResponsibilityActionInfo();
//        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
//        	principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user2");
//        	info.setPrincipalId(principal.getPrincipalId());
//        	info.setPriorityNumber(1);
//        	info.setResponsibilityName("1235");
//        	info.setResponsibilityName(responsibilityName);
//        	info.setRoleId("1234");
//        	responsibilityInfos.add(info);
//        } else if (chart.equals("IN")) {
//        	ResponsibilityActionInfo info = new ResponsibilityActionInfo();
//        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
//        	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user1");
//        	info.setPrincipalId(principal.getPrincipalId());
//        	info.setPriorityNumber(1);
//        	info.setResponsibilityName("321");
//        	info.setResponsibilityName(responsibilityName);
//        	info.setRoleId("4321");
//        	responsibilityInfos.add(info);
//        
//        	// not quite ready for groups yet
//        	/*
//        	ResponsibilityActionInfo info = new ResponsibilityActionInfo();
//        	info.setActionTypeCode(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
//        	KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE, "WorkflowAdmin");
//        	info.setGroupId(group.getGroupId());
//        	info.setPriorityNumber(1);
//        	info.setResponsibilityId("321456");
//        	info.setResponsibilityName(responsibilityName);
//        	info.setRoleId("4321");
//        	responsibilityInfos.add(info);
//        	*/
//        } 
//        return responsibilityInfos;
//    }
	
	/**
	 * Return null so that the responsibility ID will remain the same.
	 *
	 * @see org.kuali.rice.kew.routemodule.RouteModule#resolveResponsibilityId(java.lang.Long)
	 */
	public ResponsibleParty resolveResponsibilityId(Long responsibilityId)
			throws WorkflowException {
		return null;
	}
	
	class ResponsibilitySet {
		private String actionRequestCode;
		private String approvePolicy;
		private List<ResponsibilityActionInfo> responsibilities = new ArrayList<ResponsibilityActionInfo>();

		public ResponsibilitySet(ResponsibilityActionInfo responsibility) {
			this.actionRequestCode = responsibility.getActionTypeCode();
			this.approvePolicy = responsibility.getActionPolicyCode();
		}
		
		public boolean matches(ResponsibilityActionInfo responsibility) {
			return responsibility.getActionTypeCode().equals(actionRequestCode) &&
				responsibility.getActionPolicyCode().equals(approvePolicy);
		}

		public String getActionRequestCode() {
			return this.actionRequestCode;
		}

		public String getApprovePolicy() {
			return this.approvePolicy;
		}

		public List<ResponsibilityActionInfo> getResponsibilities() {
			return this.responsibilities;
		}		
		
	}

}
