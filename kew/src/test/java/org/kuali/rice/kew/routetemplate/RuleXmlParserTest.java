/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package org.kuali.rice.kew.routetemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.Core;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowServiceErrorException;
import org.kuali.rice.kew.batch.KEWXmlDataLoader;
import org.kuali.rice.kew.exception.EdenUserNotFoundException;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.routetemplate.RuleBaseValues;
import org.kuali.rice.kew.routetemplate.RuleExtension;
import org.kuali.rice.kew.routetemplate.RuleExtensionValue;
import org.kuali.rice.kew.routetemplate.RuleResponsibility;
import org.kuali.rice.kew.routetemplate.RuleService;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.xml.RuleXmlParser;
import org.kuali.workflow.test.KEWTestCase;
import org.springframework.test.AssertThrows;


public class RuleXmlParserTest extends KEWTestCase {
    private static final Logger LOG = Logger.getLogger(RuleXmlParserTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("RouteTemplateConfig.xml");
        loadXmlFile("DuplicateRuleToImport.xml");
    }

    @Test public void testRuleXmlParserCacheUpdate() throws Exception {
        //Lifecycle cache = new CacheLifecycle();
        //cache.start();
        RuleService ruleService = KEWServiceLocator.getRuleService();
        int ruleSize = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType").size();
        
        List collections = new ArrayList();
        //ultimately it is the content of RulesToImport that determines whether or not we're 
        //going to hit the rules xml parser
        InputStream xmlFile = TestUtilities.loadResource(this.getClass(), "RulesToImport.xml");
        collections.add(KEWXmlDataLoader.getFileXmlDocCollection(xmlFile, "WorkflowUnitTestTemp"));
        KEWServiceLocator.getXmlIngesterService().ingest(collections, null);
        
        Thread.sleep(5000);//give cache time to reload;
        int newRuleSize = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType").size();
        assertEquals("Three more rules should have been returned from the cached service", ruleSize + 3, newRuleSize);
        //cache.stop();
    }

    @Test public void testDuplicateRule() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("DuplicateRuleToImport.xml");
        assertNotNull(stream);
        log.info("Importing duplicate again");
        try {
            KEWServiceLocator.getRuleService().loadXml(stream, null);
        } catch (WorkflowServiceErrorException wsee) {
            assertNotNull(TestUtilities.findExceptionInStack(wsee, InvalidXmlException.class));
        }
    }
    
    @Test public void testDuplicateRuleWithExpression() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("DuplicateRuleToImportWithExpression.xml");
        assertNotNull(stream);
        log.info("Importing duplicate again");
        try {
            KEWServiceLocator.getRuleService().loadXml(stream, null);
        } catch (WorkflowServiceErrorException wsee) {
            assertNotNull(TestUtilities.findExceptionInStack(wsee, InvalidXmlException.class));
        }
    }
    
    @Test public void testNotDuplicateRule() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("NotADuplicateRuleToImport.xml");
        assertNotNull(stream);
        log.info("Importing a unique rule");
        // load the unique template first
        KEWServiceLocator.getRuleTemplateService().loadXml(stream, null);
        stream = getClass().getResourceAsStream("NotADuplicateRuleToImport.xml");
        // then the rule
        KEWServiceLocator.getRuleService().loadXml(stream, null);
    }
    
    @Test public void testNotDuplicateRuleWithExpression() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("NotADuplicateRuleToImportWithExpression.xml");
        assertNotNull(stream);
        log.info("Importing a unique rule");
        // load the unique template first
        KEWServiceLocator.getRuleTemplateService().loadXml(stream, null);
        stream = getClass().getResourceAsStream("NotADuplicateRuleToImportWithExpression.xml");
        // then the rule
        KEWServiceLocator.getRuleService().loadXml(stream, null);
    }

    private static RuleExtensionValue getExtensionValue(List<RuleExtensionValue> list, String name) {
        for (RuleExtensionValue extensionValue: list) {
            if (name.equals(extensionValue.getKey())) return extensionValue;
        }
        return null;
    }

    @Test public void testNamedRule() throws EdenUserNotFoundException {
        loadXmlFile("NamedRule.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "color");
        assertEquals("color", extensionValue.getKey());
        assertEquals("green", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "shape");
        assertEquals("shape", extensionValue.getKey());
        assertEquals("square", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user1", responsibility.getWorkflowUser().getAuthenticationUserId().getId());
        assertEquals("A", responsibility.getActionRequestedCd());
    }
    
    @Test public void testNamedRuleWithExpression() throws EdenUserNotFoundException {
        loadXmlFile("NamedRuleWithExpression.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "color");
        assertEquals("color", extensionValue.getKey());
        assertEquals("green", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "shape");
        assertEquals("shape", extensionValue.getKey());
        assertEquals("square", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user1", responsibility.getWorkflowUser().getAuthenticationUserId().getId());
        assertEquals("A", responsibility.getActionRequestedCd());
        assertNotNull(rule.getRuleExpressionDef());
        assertEquals("someType", rule.getRuleExpressionDef().getType());
        assertEquals("some expression", rule.getRuleExpressionDef().getExpression());
    }

    @Test public void testUpdatedRule() throws EdenUserNotFoundException {
        testNamedRule();
        loadXmlFile("UpdatedNamedRule.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule with an updated description, rule extension values, and responsibilities", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "flavor");
        assertEquals("flavor", extensionValue.getKey());
        assertEquals("vanilla", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "value");
        assertEquals("value", extensionValue.getKey());
        assertEquals("10", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user2", responsibility.getWorkflowUser().getAuthenticationUserId().getId());
        assertEquals("F", responsibility.getActionRequestedCd());
    }
    
    @Test public void testUpdatedRuleWithExpression() throws EdenUserNotFoundException {
        testNamedRule();
        loadXmlFile("UpdatedNamedRuleWithExpression.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule with an updated description, rule extension values, and responsibilities", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "flavor");
        assertEquals("flavor", extensionValue.getKey());
        assertEquals("vanilla", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "value");
        assertEquals("value", extensionValue.getKey());
        assertEquals("10", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user2", responsibility.getWorkflowUser().getAuthenticationUserId().getId());
        assertEquals("F", responsibility.getActionRequestedCd());
    }
    
    /**
     * This test tests that an anonymous rule will still be checked against named rules for duplication.
     * @throws EdenUserNotFoundException
     */
    @Test public void testAnonymousDuplicatesNamed() throws EdenUserNotFoundException {
        testNamedRule();

        final InputStream stream = getClass().getResourceAsStream("DuplicateAnonymousRule.xml");
        assertNotNull(stream);
        log.info("Importing anonymous duplicate rule");
        AssertThrows at = new AssertThrows(WorkflowServiceErrorException.class, "Expected exception was not thrown") {
            @Override
            public void test() throws Exception {
                KEWServiceLocator.getRuleService().loadXml(stream, null);           
            }
        };
        at.runTest();
        assertNotNull("Expected exception was not thrown", TestUtilities.findExceptionInStack(at.getActualException(), InvalidXmlException.class));
    }
    
    /**
     * This test tests that an anonymous rule will still be checked against named rules for duplication.
     * @throws EdenUserNotFoundException
     */
    @Test public void testAnonymousWithExpressionDuplicatesNamed() throws EdenUserNotFoundException {
        testNamedRuleWithExpression();

        final InputStream stream = getClass().getResourceAsStream("DuplicateAnonymousRuleWithExpression.xml");
        assertNotNull(stream);
        log.info("Importing anonymous duplicate rule");
        AssertThrows at = new AssertThrows(WorkflowServiceErrorException.class, "Expected exception was not thrown") {
            @Override
            public void test() throws Exception {
                KEWServiceLocator.getRuleService().loadXml(stream, null);           
            }
        };
        at.runTest();
        assertNotNull("Expected exception was not thrown", TestUtilities.findExceptionInStack(at.getActualException(), InvalidXmlException.class));
    }

    @Test public void testParameterReplacement() throws IOException, InvalidXmlException, EdenUserNotFoundException {
        Core.getCurrentContextConfig().overrideProperty("test.replacement.user", "user3");
        Core.getCurrentContextConfig().overrideProperty("test.replacement.workgroup", "WorkflowAdmin");
        List<RuleBaseValues> rules = new RuleXmlParser().parseRules(getClass().getResourceAsStream("ParameterizedRule.xml"));
        assertEquals(1, rules.size());
        RuleBaseValues rule = rules.get(0);
        assertEquals(2, rule.getResponsibilities().size());
        RuleResponsibility resp = (RuleResponsibility) rule.getResponsibilities().get(0);

        if (resp.isUsingWorkflowUser()) {
            assertEquals("user3", resp.getWorkflowUser().getAuthenticationUserId().getId());
        } else {
            assertEquals("WorkflowAdmin", resp.getWorkgroup().getGroupNameId().getNameId());
        }
        
        Core.getCurrentContextConfig().overrideProperty("test.replacement.user", "user1");
        Core.getCurrentContextConfig().overrideProperty("test.replacement.workgroup", "TestWorkgroup");
        rules = new RuleXmlParser().parseRules(getClass().getResourceAsStream("ParameterizedRule.xml"));
        assertEquals(1, rules.size());
        rule = rules.get(0);
        assertEquals(2, rule.getResponsibilities().size());
        resp = (RuleResponsibility) rule.getResponsibilities().get(0);

        if (resp.isUsingWorkflowUser()) {
            assertEquals("user1", resp.getWorkflowUser().getAuthenticationUserId().getId());    
        } else {
            assertEquals("TestWorkgroup", resp.getWorkgroup().getGroupNameId().getNameId());
        }
    }
    
    @Test public void removeTemplateFromNamedRule() throws IOException, InvalidXmlException, EdenUserNotFoundException {
        RuleService ruleService = KEWServiceLocator.getRuleService();
        int originalRuleCount = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType").size();

        testNamedRule();
        
        LOG.debug("Rules for doctype/template combo:");
        int ruleCount = 0;
        List<RuleBaseValues> list = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType");
        if (list != null) {
            ruleCount = list.size();
            for (RuleBaseValues rbv: list) {
                LOG.info(rbv);
            }
        }        
        
        loadXmlFile("NamedRuleWithoutTemplate.xml");
        
        LOG.debug("Rules for doctype/template combo after import of named rule:");
        int ruleCountAfter = 0;
        list = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType");
        if (list != null) {
            ruleCountAfter = list.size();
            for (RuleBaseValues rbv: list) {
                LOG.info(rbv);
            }
        }

        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");

        assertNotNull(rule);
        LOG.info("Rule id of latest version: " + rule.getRuleBaseValuesId());
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule with previously defined template removed", rule.getDescription());

        assertEquals("The rules for template/doctype combo should have been decreased by one after reimport of named rule without template", ruleCount - 1, ruleCountAfter);
        assertEquals("Rule count should be original template/doctype combo rule count after removing template from named rule", originalRuleCount, ruleCountAfter);
        
        assertNull(rule.getRuleTemplate());
        
        // templateless rules cannot have extensions, so these should be removed
        List extensions = rule.getRuleExtensions();
        assertEquals(0, extensions.size());
 
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user2", responsibility.getWorkflowUser().getAuthenticationUserId().getId());
        assertEquals("F", responsibility.getActionRequestedCd());
    }
    
    @Test public void testInvalidTemplatelessNamedRule() throws EdenUserNotFoundException {
        testNamedRule();
        loadXmlFile("InvalidTemplatelessNamedRule.xml");
    }
}