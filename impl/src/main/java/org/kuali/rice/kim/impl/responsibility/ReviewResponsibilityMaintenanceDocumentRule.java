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
package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ReviewResponsibilityMaintenanceDocumentRule extends MaintenanceDocumentRuleBase {

	protected static final String ERROR_MESSAGE_PREFIX = "error.document.kim.reviewresponsibility.";
	protected static final String ERROR_DUPLICATE_RESPONSIBILITY = ERROR_MESSAGE_PREFIX + "duplicateresponsibility";

	/**
	 * @see org.kuali.rice.krad.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean rulesPassed = true;
		GlobalVariables.getMessageMap().addToErrorPath( MAINTAINABLE_ERROR_PATH );
		try {
			ReviewResponsibilityBo resp = (ReviewResponsibilityBo)document.getNewMaintainableObject().getBusinessObject();
			// check for creation of a duplicate node
			if ( !checkForDuplicateResponsibility( resp ) ) {
				GlobalVariables.getMessageMap().putError( "documentTypeName", ERROR_DUPLICATE_RESPONSIBILITY );
				rulesPassed = false;
			}
        } finally {
			GlobalVariables.getMessageMap().removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
		}
		return rulesPassed;
	}

	protected boolean checkForDuplicateResponsibility( ReviewResponsibilityBo resp ) {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate p = and(
            equal("template.namespaceCode", KEWConstants.KEW_NAMESPACE ),
            equal("template.name", KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME),
            equal("attributes[documentTypeName]", resp.getDocumentTypeName()),
            equal("attributes[routeNodeName]", resp.getRouteNodeName())
        );
        builder.setPredicates(p);
        ResponsibilityQueryResults results = KimApiServiceLocator.getResponsibilityService().findResponsibilities(builder.build());
		return results.getResults().isEmpty();
	}
}
