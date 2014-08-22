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
package org.kuali.rice.kew.rule;

import org.junit.Test;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.kew.rule.bo.RuleBaseValuesLookupableHelperServiceImpl;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttributeBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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
        RuleTemplateBo template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName("TemplateWithRuleValidationAttribute");
        List<RuleTemplateAttributeBo> ruleTemplateAttributes = (List<RuleTemplateAttributeBo>) template.getRuleTemplateAttributes();
        int index = 0;
        for (RuleTemplateAttributeBo ruleTemplateAttribute : ruleTemplateAttributes) {
            boolean runtimeThrown = false;
            WorkflowRuleAttribute attribute = null;
            try {
                attribute = ruleTemplateAttribute.getWorkflowAttribute();
            } catch (RuntimeException e) {
                runtimeThrown = true;
            }
            if (index == 0) {
                // should be the TestRuleAttribute
                assertFalse("RuntimeException should not have been thrown.", runtimeThrown);
                assertNotNull("Attribute should exist.", attribute);
                attribute = (WorkflowRuleAttribute) ClassLoaderUtils.unwrapFromProxy(attribute);
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
        RuleTemplateBo template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName("TemplateWithRuleValidationAttribute");
        List<RuleTemplateAttributeBo> ruleTemplateAttributes = (List<RuleTemplateAttributeBo>) template.getRuleTemplateAttributes();
        int index = 0;
        for (RuleTemplateAttributeBo ruleTemplateAttribute : ruleTemplateAttributes) {
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
                assertEquals("Should be TestRuleValidationAttribute", TestRuleValidationAttribute.class,
                        attribute.getClass());
                assertFalse("TestRuleValidationAttribute is not a workflow attribute", isWorkflowAttribute);
            }
            index++;
        }
    }

    /**
     * Ensure that Rule Template Attributes that should generate a field on the lookup form do so.
     */
    @Test
    public void testAdditionalAttributes() {
        RuleTemplateBo template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName("Testing.Template");
        RuleBaseValuesLookupableHelperServiceImpl lookupableHelperService = new RuleBaseValuesLookupableHelperServiceImpl();
        lookupableHelperService.setBusinessObjectClass(template.getClass());
        // Get the default rows before processing the template's attributes through the lookupable helper service.
        int rowCountBefore = lookupableHelperService.getRows().size();
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("ruleTemplate.name", template.getName());
        lookupableHelperService.checkForAdditionalFields(fields);
        int rowCountAfter = lookupableHelperService.getRows().size();
        assertNotEquals(rowCountBefore, rowCountAfter);
    }
}
