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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.PerformanceLogger;


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

    public List<Rule> selectRules(RouteContext context, DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, String selectionCriterion, Timestamp effectiveDate) throws WorkflowException {
	// for TemplateRuleSelector, the criterion is taken as a ruletemplate name
	final String ruleTemplateName = selectionCriterion;

	Set<MassRuleAttribute> massRules = new HashSet<MassRuleAttribute>();
	RuleTemplate template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
	if (template == null) {
	    throw new WorkflowRuntimeException("Could not locate the rule template with name " + ruleTemplateName + " on document " + routeHeader.getDocumentId());
	}
	for (Iterator iter = template.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {

	    RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iter.next();
	    if (!templateAttribute.isWorkflowAttribute()) {
		continue;
	    }
	    WorkflowAttribute attribute = templateAttribute.getWorkflowAttribute();
	    if (attribute instanceof MassRuleAttribute) {
		massRules.add((MassRuleAttribute) attribute);
	    }

	}

	List rules = null;
	if (effectiveDate != null) {
	    rules = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName(), effectiveDate);
	} else {
	    rules = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName());
	}
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
	for (RuleBaseValues ruleDefinition: (List<RuleBaseValues>) rules) {
	    ruleList.add(new RuleImpl(ruleDefinition));
	}
	return ruleList;
    }

}
