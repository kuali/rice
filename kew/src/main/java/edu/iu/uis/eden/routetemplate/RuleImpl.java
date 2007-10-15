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

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * {@link Rule} implementation 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
class RuleImpl implements Rule {
    /**
     * The BO of the rule definition in the system
     */
    private final RuleBaseValues ruleDefinition;
    /**
     * The content of the document being processed
     */
    private final DocumentContent docContent;

    RuleImpl(RuleBaseValues ruleDefinition, DocumentContent docContent) {
        this.ruleDefinition = ruleDefinition;
        this.docContent = docContent;
    }

    public RuleBaseValues getDefinition() {
        return ruleDefinition;
    }

    public RuleExpressionResult evaluate() throws EdenUserNotFoundException, WorkflowException {
        RuleExpression expression = null;
        boolean hasCustomExpression = false;
        if (hasCustomExpression) {
            // determine "type" of expression, load up correct implementation
            // expose extension values (and possibly other rule config) to evaluation expression (e.g. XPath variables)
            // expression = ...
        } else {
            expression = new WorkflowAttributeRuleExpression(ruleDefinition, docContent); 
        }

        return expression.evaluate();
    }
}