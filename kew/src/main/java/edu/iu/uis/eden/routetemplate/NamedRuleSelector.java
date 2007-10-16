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
package edu.iu.uis.eden.routetemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * Rule selector that select a rule based on configured rule name 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class NamedRuleSelector implements RuleSelector {
    /**
     * The route node config param consulted to determine the rule name to select
     */
    public static final String RULE_NAME_CFG_KEY = "ruleName";

    public List<Rule> selectRules(RouteContext context, DocumentRouteHeaderValue routeHeader,
            RouteNodeInstance nodeInstance, String selectionCriterion, Timestamp effectiveDate) throws WorkflowException {
        RouteNode routeNodeDef = nodeInstance.getRouteNode();
        Map<String, String> routeNodeConfig = Utilities.getKeyValueCollectionAsMap(routeNodeDef.getConfigParams());
        String ruleName = routeNodeConfig.get(RULE_NAME_CFG_KEY);
        if (ruleName == null) {
            throw new WorkflowException("No 'rule.name' configuration parameter present on route node definition: " + routeNodeDef);
        }
        RuleBaseValues ruleDef = KEWServiceLocator.getRuleService().getRuleByName(ruleName);
        if (ruleDef == null) {
            throw new WorkflowException("No rule found with name '" + ruleName + "'");
        }
        List<Rule> rules = new ArrayList<Rule>(1);
        rules.add(new RuleImpl(ruleDef, context.getDocumentContent()));
        return rules;
    }
}