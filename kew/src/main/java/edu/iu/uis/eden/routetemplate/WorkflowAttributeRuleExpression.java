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
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.xmlrouting.GenericXMLRuleAttribute;

/**
 * Standard rule expression implementation that evaluates the attributes associated with the rule definition 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
class WorkflowAttributeRuleExpression implements RuleExpression {
    public RuleExpressionResult evaluate(RuleBaseValues ruleDefinition, RouteContext context) throws EdenUserNotFoundException, WorkflowException {
        boolean match = isMatch(ruleDefinition, context.getDocumentContent());
        if (match) {
            return new RuleExpressionResult(match, ruleDefinition.getResponsibilities());
        } else {
            return new RuleExpressionResult(match);
        }
    }

    public boolean isMatch(RuleBaseValues ruleDefinition, DocumentContent docContent) {
        for (Iterator iter = ruleDefinition.getRuleTemplate().getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
            RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
            if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                continue;
            }
            WorkflowAttribute routingAttribute = (WorkflowAttribute) ruleTemplateAttribute.getWorkflowAttribute();

            RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
            if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                ((GenericXMLRuleAttribute) routingAttribute).setRuleAttribute(ruleAttribute);
            }
            String className = ruleAttribute.getClassName();
            List editedRuleExtensions = new ArrayList();
            for (Iterator iter2 = ruleDefinition.getRuleExtensions().iterator(); iter2.hasNext();) {
                RuleExtension extension = (RuleExtension) iter2.next();
                if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(className)) {
                    editedRuleExtensions.add(extension);
                }
            }
            if (!routingAttribute.isMatch(docContent, editedRuleExtensions)) {
                return false;
            }
        }
        return true;
    }

}