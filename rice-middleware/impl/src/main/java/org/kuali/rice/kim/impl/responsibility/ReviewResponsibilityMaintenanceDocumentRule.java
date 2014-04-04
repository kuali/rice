/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

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
    protected static final String ERROR_NAMESPACE_AND_NAME_VALIDATION = ERROR_MESSAGE_PREFIX + "namespaceandnamevalidation";
    protected static final String NAMESPACE_CODE_PROPERTY = "namespaceCode";

    /**
     * @see org.kuali.rice.krad.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.maintenance.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean rulesPassed = super.processCustomRouteDocumentBusinessRules(document);

        GlobalVariables.getMessageMap().addToErrorPath(MAINTAINABLE_ERROR_PATH);
        try {
            ReviewResponsibilityBo newResp =
                    (ReviewResponsibilityBo) document.getNewMaintainableObject().getDataObject();
            ReviewResponsibilityBo oldResp =
                    (ReviewResponsibilityBo) document.getOldMaintainableObject().getDataObject();

            // check for duplicates if the responsibility is being copied or created
            if (!newResp.getId().equals(oldResp.getId())) {
                if (newResp.getDocumentTypeName() != null
                        && newResp.getRouteNodeName() != null
                        && !checkForDuplicateResponsibility(newResp)) {
                    GlobalVariables.getMessageMap().putError("documentTypeName", ERROR_DUPLICATE_RESPONSIBILITY);
                    rulesPassed &= false;
                }

                if (StringUtils.isNotBlank(newResp.getNamespaceCode()) && StringUtils.isNotBlank(newResp.getName())) {
                    rulesPassed &= validateNamespaceCodeAndName(newResp.getNamespaceCode(), newResp.getName());
                }
            } else {
                // check for duplicates if particular fields of the responsibility are being edited
                if (newResp.getDocumentTypeName() != null && newResp.getRouteNodeName() != null
                        && (!StringUtils.equals(oldResp.getDocumentTypeName(), newResp.getDocumentTypeName())
                            || !StringUtils.equals(oldResp.getRouteNodeName(), newResp.getRouteNodeName()))
                        && !checkForDuplicateResponsibility(newResp)) {
                    GlobalVariables.getMessageMap().putError("documentTypeName", ERROR_DUPLICATE_RESPONSIBILITY);
                    rulesPassed &= false;
                }

                if (StringUtils.isNotBlank(newResp.getNamespaceCode()) && StringUtils.isNotBlank(newResp.getName()) && (
                        !StringUtils.equals(oldResp.getNamespaceCode(), newResp.getNamespaceCode())
                                || !StringUtils.equals(oldResp.getName(), newResp.getName()))) {
                    rulesPassed &= validateNamespaceCodeAndName(newResp.getNamespaceCode(), newResp.getName());
                }
            }

        } finally {
            GlobalVariables.getMessageMap().removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        }
        return rulesPassed;
    }


	protected boolean checkForDuplicateResponsibility( ReviewResponsibilityBo resp ) {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        Predicate p = and(
            equal("template.namespaceCode", KewApiConstants.KEW_NAMESPACE ),
            equal("template.name", KewApiConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME),
            equal("attributes[documentTypeName]", resp.getDocumentTypeName())
            // KULRICE-8538 -- Check the route node by looping through the results below.  If it is added
            // into the predicate, no rows are ever returned.
            //equal("attributes[routeNodeName]", resp.getRouteNodeName())
        );
        builder.setPredicates(p);
        ResponsibilityQueryResults results = KimApiServiceLocator.getResponsibilityService().findResponsibilities(builder.build());
        List<Responsibility> responsibilities = new ArrayList<Responsibility>();

        if ( !results.getResults().isEmpty() ) {
            for ( Responsibility responsibility : results.getResults() ) {
                String routeNodeName = responsibility.getAttributes().get( KimConstants.AttributeConstants.ROUTE_NODE_NAME);
                if (StringUtils.isNotEmpty(routeNodeName) && StringUtils.equals(routeNodeName, resp.getRouteNodeName())){
                    responsibilities.add(responsibility);
                }
            }
        }

		return responsibilities.isEmpty();
	}

    protected boolean validateNamespaceCodeAndName(String namespaceCode,String name){
        Responsibility responsibility = KimApiServiceLocator.getResponsibilityService().findRespByNamespaceCodeAndName(namespaceCode,name);

        if(null != responsibility){
           GlobalVariables.getMessageMap().putError(NAMESPACE_CODE_PROPERTY,ERROR_NAMESPACE_AND_NAME_VALIDATION,namespaceCode,name);
           return false;
        } else {
            return true;
        }

    }
}
