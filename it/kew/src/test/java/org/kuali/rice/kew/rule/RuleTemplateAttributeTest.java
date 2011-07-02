/*
 * Copyright 2007-2008 The Kuali Foundation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;

/**
 * Tests the RuleTemplateAttribute class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleTemplateAttributeTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("RuleTemplateAttributeTestConfig.xml");
    }

    @Test
    public void testGetWorkflowAttribute() throws Exception {
        RuleTemplate template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName("TemplateWithRuleValidationAttribute");
        List<RuleTemplateAttribute> ruleTemplateAttributes = (List<RuleTemplateAttribute>) template.getRuleTemplateAttributes();
        int index = 0;
        for (RuleTemplateAttribute ruleTemplateAttribute : ruleTemplateAttributes) {
            boolean runtimeThrown = false;
            WorkflowAttribute attribute = null;
            try {
                attribute = ruleTemplateAttribute.getWorkflowAttribute();
            } catch (RuntimeException e) {
                runtimeThrown = true;
            }
            if (index == 0) {
                // should be the TestRuleAttribute
                assertFalse("RuntimeException should not have been thrown.", runtimeThrown);
                assertNotNull("Attribute should exist.", attribute);
                attribute = (WorkflowAttribute) ClassLoaderUtils.unwrapFromProxy(attribute);
                assertEquals("Should be TestRuleAttribute", TestRuleAttribute.class, attribute.getClass());
            } else if (index == 1) {
                // should be the TestRuleDelegationAttribute so should be null
                assertTrue("RuntimeException should have been thrown.", runtimeThrown);
                assertNull("This should be the rule delegation attribute, not a WorkflowAttribute.", attribute);
            }
            index++;
        }
    }

    @Test
    public void testIsWorkflowAttribute() throws Exception {
        RuleTemplate template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName("TemplateWithRuleValidationAttribute");
        List<RuleTemplateAttribute> ruleTemplateAttributes = (List<RuleTemplateAttribute>) template.getRuleTemplateAttributes();
        int index = 0;
        for (RuleTemplateAttribute ruleTemplateAttribute : ruleTemplateAttributes) {
            boolean isWorkflowAttribute = ruleTemplateAttribute.isWorkflowAttribute();
            Object attribute = ruleTemplateAttribute.getAttribute();
            attribute = ClassLoaderUtils.unwrapFromProxy(attribute);
            if (index == 0) {
                // should be the TestRuleAttribute
                assertNotNull(attribute);
                assertEquals("Should be TestRuleAttribute", TestRuleAttribute.class, attribute.getClass());
                assertTrue("TestRuleAttribute is a workflow attribute.", isWorkflowAttribute);
            } else if (index == 1) {
                // should be the TestRuleValidationAttribute so should be null
                assertEquals("Should be TestRuleValidationAttribute", TestRuleValidationAttribute.class, attribute.getClass());
                assertFalse("TestRuleValidationAttribute is not a workflow attribute", isWorkflowAttribute);
            }
            index++;
        }
    }

}
