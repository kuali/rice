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

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * A rule expression implementation that uses Bean Scripting Framework.
 * The language is given by the type qualifier, e.g.:
 * &lt;expression type="BSF:groovy"&gt;...
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BSFRuleExpression implements RuleExpression {
    static {
        BSFManager.registerScriptingEngine(
                "groovy", 
                "org.codehaus.groovy.bsf.GroovyEngine", 
                new String[] { "groovy", "gy" }
        );
    }
    public RuleExpressionResult evaluate(RuleBaseValues ruleDefinition, RouteContext context) throws EdenUserNotFoundException, WorkflowException {
        String type = ruleDefinition.getRuleExpressionDef().getType();
        String lang = "groovy";
        int colon = type.indexOf(':');
        if (colon > -1) {
            lang = type.substring(colon + 1);
        }
        String expression = ruleDefinition.getRuleExpressionDef().getExpression();
        RuleExpressionResult result;
        BSFManager manager = new BSFManager();
        try {
            manager.declareBean("rule", ruleDefinition, RuleBaseValues.class);
            result = (RuleExpressionResult) manager.eval(lang, ruleDefinition.toString(), 0, 0, expression);
        } catch (BSFException e) {
            throw new WorkflowException("Error evaluating " + type + " expression: '" + expression + "'", e);
        }
        if (result == null) {
            return new RuleExpressionResult(false);
        } else {
            return result;
        }
    }
}