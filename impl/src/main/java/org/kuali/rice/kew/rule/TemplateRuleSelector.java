/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttributeBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.PerformanceLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * Rule selector that selects rules based on configured template name 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class TemplateRuleSelector implements RuleSelector {
    /**
     * Records the number of selected rules, prior to MassRuleAttribute filtering
     */
    private int numberOfSelectedRules;

    /**
     * @return the number of selected rules, prior to MassRuleAttribute filtering
     */
    int getNumberOfSelectedRules() {
	return numberOfSelectedRules;
    }

    public List<Rule> selectRules(RouteContext context, DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, String selectionCriterion, Timestamp effectiveDate) {
        // for TemplateRuleSelector, the criterion is taken as a ruletemplate name
        final String ruleTemplateName = selectionCriterion;

        Set<MassRuleAttribute> massRules = new HashSet<MassRuleAttribute>();
        RuleTemplateBo template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
        //RuleTemplate template = KewApiServiceLocator.getRuleService().getRuleTemplateByName(ruleTemplateName);
        if (template == null) {
            throw new WorkflowRuntimeException("Could not locate the rule template with name " + ruleTemplateName + " on document " + routeHeader.getDocumentId());
        }
        for (RuleTemplateAttributeBo templateAttribute : template.getActiveRuleTemplateAttributes()) {
            if (!templateAttribute.isWorkflowAttribute()) {
            continue;
            }
            WorkflowRuleAttribute attribute = templateAttribute.getWorkflowAttribute();
            if (attribute instanceof MassRuleAttribute) {
            massRules.add((MassRuleAttribute) attribute);
            }

        }

        List<org.kuali.rice.kew.api.rule.Rule> rules = null;
        QueryByCriteria.Builder query = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(equal("ruleTemplate.name", ruleTemplateName));

        // Check all document types in ancestry
        DocumentType dt = routeHeader.getDocumentType();
        List<Predicate> documentTypeAncestry = new ArrayList<Predicate>();
        while (dt != null) {
            documentTypeAncestry.add(equal("docTypeName", dt.getName()));
            dt = dt.getParentDocType();
        }
        predicates.add(and(or(documentTypeAncestry.toArray(new Predicate[documentTypeAncestry.size()]))));
        Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        predicates.add(and(
                           or(isNull("fromDateValue"), greaterThanOrEqual("fromDateValue", currentTime)),
                           or(isNull("toDateValue"), lessThan("toDateValue", currentTime))
                      ));
        predicates.add(equal("active", new Integer(1))); //true
        predicates.add(equal("delegateRule", new Integer(0)));  //false
        predicates.add(equal("templateRuleInd", new Integer(0))); //false
        if (effectiveDate != null) {
            predicates.add(
                    and(
                        lessThanOrEqual("activationDate", effectiveDate),
                        greaterThan("deactivationDate", effectiveDate)));
            //rules = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName(), effectiveDate);
        } else {
            predicates.add(equal("currentInd", new Integer(1))); //true
            //rules = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName());
        }
        Predicate p = and(predicates.toArray(new Predicate[]{}));
        query.setPredicates(p);
        rules = KewApiServiceLocator.getRuleService().findRules(query.build()).getResults();
        numberOfSelectedRules = rules.size();

        // TODO really the route context just needs to be able to support nested create and clears
        // (i.e. a Stack model similar to transaction intercepting in Spring) and we wouldn't have to do this
        if (context.getDocument() == null) {
            context.setDocument(routeHeader);
        }
        if (context.getNodeInstance() == null) {
            context.setNodeInstance(nodeInstance);
        }
        DocumentContent documentContent = context.getDocumentContent();
        PerformanceLogger performanceLogger = new PerformanceLogger();
        // have all mass rule attributes filter the list of non applicable rules
        for (Iterator iter = massRules.iterator(); iter.hasNext();) {
            MassRuleAttribute massRuleAttribute = (MassRuleAttribute) iter.next();
            rules = massRuleAttribute.filterNonMatchingRules(context, rules);
        }
        performanceLogger.log("Time to filter massRules for template " + template.getName());

        List<Rule> ruleList = new ArrayList<Rule>(rules.size());
        for (org.kuali.rice.kew.api.rule.Rule ruleDefinition: rules) {
            ruleList.add(new RuleImpl(ruleDefinition));
        }
        return ruleList;
    }

}
