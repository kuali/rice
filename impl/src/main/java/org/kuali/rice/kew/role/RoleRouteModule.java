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
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kim.bo.role.KimResponsibility;

/**
 * The RoleRouteModule is responsible for interfacing with the KIM
 * Role system to provide Role-based routing to KEW. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleRouteModule implements RouteModule {
	
	protected static final String QUALIFIER_RESOLVER_ELEMENT = "qualifierResolver";
	
	public List<ActionRequestValue> findActionRequests(RouteContext context)
			throws Exception {
		
		ActionRequestFactory arFactory = new ActionRequestFactory(context.getDocument(), context.getNodeInstance());
		List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
		QualifierResolver qualifierResolver = loadQualifierResolver(context);
		List<Map<String, String>> qualifiers = qualifierResolver.resolve(context);
		String documentTypeName = context.getDocument().getDocumentType().getName();
		String nodeName = context.getNodeInstance().getName();
		
		/**
		 * TODO Call responsibility service here
		 */
		List<KimResponsibility> responsibilities = thisIsWhereWeCallTheResponsibilityService(documentTypeName, nodeName, qualifiers);
		
		for (KimResponsibility responsibility : responsibilities) {
			arFactory.addResponsibilityRequest(responsibility);
		}
		actionRequests = new ArrayList<ActionRequestValue>(arFactory.getRequestGraphs());
		return actionRequests;
	}

	protected QualifierResolver loadQualifierResolver(RouteContext context) {
		String qualifierResolverName = RouteNodeUtils.getValueOfCustomProperty(context.getNodeInstance().getRouteNode(), QUALIFIER_RESOLVER_ELEMENT);
		if (StringUtils.isBlank(qualifierResolverName)) {
			// TODO alternatively, in future could provide a default implementation?
			throw new RiceRuntimeException("Could not determine qualiferResolver for this node.  Please define one in document type XML.");
		}
		RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(qualifierResolverName);
		if (ruleAttribute == null) {
			throw new RiceRuntimeException("Failed to locate QualifierResolver for attribute name: " + qualifierResolverName);
		}
		ObjectDefinition definition = getAttributeObjectDefinition(ruleAttribute);
		return (QualifierResolver)GlobalResourceLoader.getObject(definition);
	}
	
    protected ObjectDefinition getAttributeObjectDefinition(RuleAttribute ruleAttribute) {
    	return new ObjectDefinition(ruleAttribute.getClassName(), ruleAttribute.getMessageEntity());
    }
    
    protected List<KimResponsibility> thisIsWhereWeCallTheResponsibilityService(String documentTypeName, String nodeName, List<Map<String, String>> qualifiers) {
    	// TODO call the responsibility service
    	return new ArrayList<KimResponsibility>();
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
