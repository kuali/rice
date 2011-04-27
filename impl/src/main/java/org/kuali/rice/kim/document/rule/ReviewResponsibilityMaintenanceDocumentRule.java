/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.document.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.impl.ReviewResponsibility;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ReviewResponsibilityMaintenanceDocumentRule extends
		MaintenanceDocumentRuleBase {

	protected static final String ERROR_MESSAGE_PREFIX = "error.document.kim.reviewresponsibility.";
	protected static final String ERROR_INVALID_ROUTE_NODE = ERROR_MESSAGE_PREFIX + "invalidroutenode";
	protected static final String ERROR_DUPLICATE_RESPONSIBILITY = ERROR_MESSAGE_PREFIX + "duplicateresponsibility";

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean rulesPassed = true;
		GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
		try {
			ReviewResponsibility resp = (ReviewResponsibility)document.getNewMaintainableObject().getBusinessObject();
			// check the route level exists on the document or a child
			HashSet<String> routeNodeNames = getAllPossibleRouteNodeNames( resp.getDocumentTypeName() );
			if ( !routeNodeNames.contains( resp.getRouteNodeName() ) ) {
				GlobalVariables.getMessageMap().putError( "routeNodeName", ERROR_INVALID_ROUTE_NODE, resp.getRouteNodeName() );
				rulesPassed = false;
			}
			// check for creation of a duplicate node
			if ( !checkForDuplicateResponsibility( resp ) ) {
				GlobalVariables.getMessageMap().putError( "documentTypeName", ERROR_DUPLICATE_RESPONSIBILITY );
				rulesPassed = false;
			}
		} catch ( RuntimeException ex ) {
			LOG.error( "Error in processCustomRouteDocumentBusinessRules()", ex );
			throw ex;
		} finally {
			GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
		}
		return rulesPassed;
	}
	
	protected HashSet<String> getAllPossibleRouteNodeNames( String documentTypeName ) {
		DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName( documentTypeName );
		HashSet<String> routeNodeNames = new HashSet<String>();
		if ( docType != null ) {
			addNodesForDocType( docType, routeNodeNames );
			addNodesForChildDocTypes( docType, routeNodeNames );
		}
		return routeNodeNames;
	}
	
	@SuppressWarnings("unchecked")
	protected void addNodesForDocType( DocumentType docType, HashSet<String> routeNodeNames ) {
		List<RouteNode> routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes( docType, true );
		for ( RouteNode node : routeNodes ) {
			// only add request nodes (not split or join nodes)
			if ( node.isRoleNode() ) {
				routeNodeNames.add( node.getRouteNodeName() );
			}
		}
	}
	@SuppressWarnings("unchecked")
	protected void addNodesForChildDocTypes( DocumentType docType, HashSet<String> routeNodeNames ) {
		for ( DocumentType childDocType : (Collection<DocumentType>)docType.getChildrenDocTypes() ) {
			addNodesForDocType( childDocType, routeNodeNames );
			addNodesForChildDocTypes( childDocType, routeNodeNames );
		}
	}
	protected boolean checkForDuplicateResponsibility( ReviewResponsibility resp ) {
		HashMap<String,String> criteria = new HashMap<String,String>();
		criteria.put( "template.namespaceCode", KEWConstants.KEW_NAMESPACE );
		criteria.put( "template.name", KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME );
		criteria.put( "detailCriteria", "documentTypeName="+resp.getDocumentTypeName()+",routeNodeName="+resp.getRouteNodeName() );
		List<? extends KimResponsibilityInfo> results = KIMServiceLocator.getResponsibilityService().lookupResponsibilityInfo( criteria, true );
		return results.isEmpty() || results.get(0).getResponsibilityId().equals( resp.getResponsibilityId() );
	}
}
