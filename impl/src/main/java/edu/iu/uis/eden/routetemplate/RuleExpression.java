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

import org.kuali.rice.kew.exception.EdenUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;

import edu.iu.uis.eden.engine.RouteContext;

/**
 * A rule is an entity that can be evaluated at runtime to generate a list of {@link RuleResponsibility}s
 * to which to dispatch action requests.
 * @see RuleBaseValues
 * @see RuleExpressionResult
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RuleExpression {
    /**
     * Performs an evaluation and returns a list of 0 or more {@link RuleResponsibility}s to which to dispatch action requests
     * @param rule the rule that this rule expression applies to
     * @param context the RouteContext under which the expression is being evaluated
     * @return the result of the rule evaluation
     * @throws EdenUserNotFoundException
     * @throws WorkflowException
     */
    public RuleExpressionResult evaluate(Rule rule, RouteContext context) throws EdenUserNotFoundException, WorkflowException;
}
