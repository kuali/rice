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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Expression implementation for KRA "meta rules".  A KRA "meta rule" consists of a sequence of
 * subordinate rule evaluations each processed according to an associated modifier:
 * <dl>
 *   <dt>next<dt>
 *   <dd>proceed with rule evaluation</dd>
 *   <dt>true</dt>
 *   <dd>if this rule evaluates to true, then return the "map" (responsibilities) associated with the rule</dd>
 *   <dt>false</dt>
 *   <dd>if this rule evaluates to false, then return the "map" (responsibilities) associated with the rule</dd>
 * </dl>
 * E.g.
 * <div><tt>bizRule1: next; bizRule2: true; bizRule3: false</tt></div>
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class KRAMetaRuleExpression implements RuleExpression {
    private static final Logger LOG = Logger.getLogger(KRAMetaRuleExpression.class);

    private static enum KRA_RULE_FLAG {
        NEXT, TRUE, FALSE
    }

    public RuleExpressionResult evaluate(RuleBaseValues ruleDefinition, RouteContext context) throws EdenUserNotFoundException, WorkflowException {
        RuleExpressionDef exprDef = ruleDefinition.getRuleExpressionDef();
        if (exprDef == null) {
            throw new WorkflowException("No expression defined in rule definition: " + ruleDefinition);
        }
        String expression = exprDef.getExpression();
        if (StringUtils.isEmpty(expression)) {
            throw new WorkflowException("Empty expression in rule definition: " + ruleDefinition);
        }

        String[] statements = expression.split("[;\r\n]");
        
        if (statements.length == 0) {
            throw new WorkflowException("No statements parsed in expression: " + expression);
        }

        for (int i = 0; i < statements.length; i++) {
            int stmtNum = i + 1;
            String statement = statements[i];
            String[] words = statement.split(":");
            if (words.length < 2) {
                throw new WorkflowException("Invalid statement (#" + stmtNum + "): " + statement);
            }
            String ruleName = words[0];
            if (StringUtils.isEmpty(ruleName)) {
                throw new WorkflowException("Invalid rule in statement (#" + stmtNum + "): " + statement);
            }
            String flag = words[1];
            KRA_RULE_FLAG flagCode = KRA_RULE_FLAG.valueOf(flag.toUpperCase());
            if (flagCode == null) {
                throw new WorkflowException("Invalid flag in statement (#" + stmtNum + "): " + statement);
            }
            RuleBaseValues nestedRule = KEWServiceLocator.getRuleService().getRuleByName(ruleName);
            if (nestedRule == null) {
                throw new WorkflowException("Rule '" + ruleName + "' in statement (#" + stmtNum + ") not found: " + statement);
            }
            switch (flagCode) {
                case NEXT: {
                    RuleImpl ruleImpl = new RuleImpl(nestedRule);
                    RuleExpressionResult result = ruleImpl.evaluate(nestedRule, context);
                    return result;
                }
                case TRUE:{
                    RuleImpl ruleImpl = new RuleImpl(nestedRule);
                    RuleExpressionResult result = ruleImpl.evaluate(nestedRule, context);
                    if (!result.isSuccess()) {
                        // failed...
                        return result;
                    }
                }
                case FALSE:{
                    RuleImpl ruleImpl = new RuleImpl(nestedRule);
                    RuleExpressionResult result = ruleImpl.evaluate(nestedRule, context);
                    if (result.isSuccess()) {
                        // failed...
                        return new RuleExpressionResult(false, result.getResponsibilities());
                    }
                }
                default:
                    throw new WorkflowException("Unhandled statement flag: " + flagCode);
            }
        }
        return null;
    }
}