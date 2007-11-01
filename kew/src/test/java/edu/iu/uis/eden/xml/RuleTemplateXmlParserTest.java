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
package edu.iu.uis.eden.xml;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RuleTemplateXmlParserTest extends KEWTestCase {

    private static final String RULE_ATTRIBUTE_ONE = "TemplateTestRuleAttribute1";
    private static final String RULE_ATTRIBUTE_TWO = "TemplateTestRuleAttribute2";
    private static final String RULE_ATTRIBUTE_THREE = "TemplateTestRuleAttribute3";
    private static final String RULE_ATTRIBUTE_FOUR = "TemplateTestRuleAttribute4";
    private static final String RULE_ATTRIBUTE_FIVE = "TemplateTestRuleAttribute5";
    
    private enum TemplateParserGeneralFixture {
	VALID_TEMPLATE_MIN_XML("ValidRuleTemplate", "RuleTemplate_Valid", new String[]{RULE_ATTRIBUTE_ONE}, new String[0], new String[]{RULE_ATTRIBUTE_ONE}, new String[0]),
	VALID_TEMPLATE_FULL_XML("ValidRuleTemplate_Full", "RuleTemplate_Valid_Full", new String[]{RULE_ATTRIBUTE_ONE}, new String[0], new String[]{RULE_ATTRIBUTE_ONE}, new String[0]),
	// below has allowOverwrite=true
	VALID_TEMPLATE_OVERWRITE("ValidRuleTemplateOverwrite", "RuleTemplate_Valid", new String[]{RULE_ATTRIBUTE_ONE}, new String[]{RULE_ATTRIBUTE_FOUR}, new String[]{RULE_ATTRIBUTE_FOUR}, new String[]{RULE_ATTRIBUTE_ONE}),
	INVALID_TEMPLATE_OVERWRITE("InvalidRuleTemplateOverwrite", "RuleTemplate_Valid", new String[]{RULE_ATTRIBUTE_ONE}, new String[0], new String[]{RULE_ATTRIBUTE_ONE}, new String[0]),
	VALID_TEMPLATE_WITH_FULL_DEFAULTS("ValidRuleTemplateWithDefaults", "RuleTemplate_Valid_Defaults", new String[]{RULE_ATTRIBUTE_TWO}, new String[0], new String[]{RULE_ATTRIBUTE_TWO}, new String[0]),
	VALID_TEMPLATE_WITH_LIMITED_DEFAULTS("ValidRuleTemplateWithDefaults2", "RuleTemplate_Valid_Some_Defaults", new String[]{RULE_ATTRIBUTE_THREE}, new String[0], new String[0], new String[]{RULE_ATTRIBUTE_THREE}),
	VALID_TEMPLATE_WITH_INSTRUCTIONS_DEFAULT("ValidRuleTemplateWithDefaults3", "RuleTemplate_Valid_Instructions_Default", new String[]{RULE_ATTRIBUTE_TWO}, new String[0], new String[]{RULE_ATTRIBUTE_TWO}, new String[0]),
	;
	
	public String fileNameParameter;
	public String ruleTemplateName;
	public String[] activeAttributeNames;
	public String[] inactiveAttributeNames;
	public String[] requiredAttributeNames;
	public String[] nonRequiredAttributeNames;
	
	private TemplateParserGeneralFixture(String fileNameParameter, String ruleTemplateName, String[] activeAttributeNames, String[] inactiveAttributeNames, String[] requiredAttributeNames, String[] nonRequiredAttributeNames) {
	    this.fileNameParameter = fileNameParameter;
	    this.ruleTemplateName = ruleTemplateName;
	    this.activeAttributeNames = activeAttributeNames;
	    this.inactiveAttributeNames = inactiveAttributeNames;
	    this.requiredAttributeNames = requiredAttributeNames;
	    this.nonRequiredAttributeNames = nonRequiredAttributeNames;
	}
    }

    protected void loadTestData() throws Exception {
        loadXmlFile("RuleTemplateConfig.xml");
    }

    private void testTemplate(String docName, Class expectedException) throws Exception {
        RuleTemplateXmlParser parser = new RuleTemplateXmlParser();
        String filename = "RT_" +  docName + ".xml";
        try {
            parser.parseRuleTemplates(getClass().getResourceAsStream(filename));
            if (expectedException != null) {
                fail(filename + " successfully loaded.  Expected exception of class '" + expectedException + "'");
            }
        } catch (Exception e) {
            if (expectedException == null || !(expectedException.isAssignableFrom(e.getClass()))) {
                throw e;
            } else {
                log.error(filename + " exception: " + e);
            }
        }
    }
    
    private void testListOfTemplateAttributes(List ruleTemplateAttributes, String[] activeRuleTemplateAttributeNames, String[] requiredRuleTemplateAttributeNames) {
	for (Iterator iterator = ruleTemplateAttributes.iterator(); iterator.hasNext();) {
	    RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iterator.next();
	    String ruleTemplateName = templateAttribute.getRuleAttribute().getName();
	    if (requiredRuleTemplateAttributeNames != null) {
		runTestsOnTemplateAttributeField(ruleTemplateName, templateAttribute.isRequired(), requiredRuleTemplateAttributeNames, "required");
	    }
	    if (activeRuleTemplateAttributeNames != null) {
		runTestsOnTemplateAttributeField(ruleTemplateName, templateAttribute.isActive(), activeRuleTemplateAttributeNames, "active");
	    }
	}
    }
    
    private void runTestsOnTemplateAttributeField(String ruleTemplateName, boolean valueToCheck, String[] attributeNamesShouldBeTrue, String errorMessageIdentifier) {
	boolean foundAttribute = false;
	boolean attributeIsRequired = false;
	for (String attributeNameThatShouldPass : attributeNamesShouldBeTrue) {
	    if (ruleTemplateName.equals(attributeNameThatShouldPass)) {
		foundAttribute = true;
		if (!valueToCheck) {
		    fail("Attribute with name '" + ruleTemplateName + "' should have been " + errorMessageIdentifier + " but is not");
		}
		else {
		    attributeIsRequired = true;
		}
	    }
	}
	if ( (!foundAttribute) && (valueToCheck) ) {
	    fail("Attribute with name '" + ruleTemplateName + "' should not be " + errorMessageIdentifier + " but is");
	}
    }
    
    @Test public void testLoadValidTemplate() throws Exception {
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_FULL_XML.fileNameParameter, null);
        
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_MIN_XML.fileNameParameter, null);
        RuleTemplate template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(TemplateParserGeneralFixture.VALID_TEMPLATE_MIN_XML.ruleTemplateName);
        testListOfTemplateAttributes(template.getRuleTemplateAttributes(), TemplateParserGeneralFixture.VALID_TEMPLATE_MIN_XML.activeAttributeNames, TemplateParserGeneralFixture.VALID_TEMPLATE_MIN_XML.requiredAttributeNames);
    }

    @Test public void testLoadValidTemplateWithOverwrite() throws Exception {
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_FULL_XML.fileNameParameter, null);
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_OVERWRITE.fileNameParameter, null); // allowOverwrite=true
        
        RuleTemplate template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(TemplateParserGeneralFixture.VALID_TEMPLATE_OVERWRITE.ruleTemplateName);
        testListOfTemplateAttributes(template.getRuleTemplateAttributes(), TemplateParserGeneralFixture.VALID_TEMPLATE_OVERWRITE.activeAttributeNames, TemplateParserGeneralFixture.VALID_TEMPLATE_OVERWRITE.requiredAttributeNames);
    }

    @Test public void testLoadInvalidTemplateWithOverwrite() throws Exception {
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_MIN_XML.fileNameParameter, null);
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_MIN_XML.fileNameParameter, RuntimeException.class);  // no allowOverwrite specified
        testTemplate(TemplateParserGeneralFixture.INVALID_TEMPLATE_OVERWRITE.fileNameParameter, RuntimeException.class);  // allowOverwrite=false
    }

    @Test public void testLoadValidTemplateWithDefaults() throws Exception {
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_WITH_FULL_DEFAULTS.fileNameParameter, null);
    }

    @Test public void testLoadValidTemplateWithSomeDefaults() throws Exception {
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_WITH_LIMITED_DEFAULTS.fileNameParameter, null);
    }
    
    @Test public void testLoadValidTemplateWithInstructionsDefault() throws Exception {
        testTemplate(TemplateParserGeneralFixture.VALID_TEMPLATE_WITH_INSTRUCTIONS_DEFAULT.fileNameParameter, null);
    }
    
    private enum TemplateParserAttributeActivationFixture {
	ATTRIBUTE_1(new String[]{RULE_ATTRIBUTE_ONE}, new String[]{RULE_ATTRIBUTE_TWO}),
	ATTRIBUTE_2(new String[]{}, new String[]{RULE_ATTRIBUTE_ONE,RULE_ATTRIBUTE_TWO}),
	ATTRIBUTE_3(new String[]{RULE_ATTRIBUTE_ONE,RULE_ATTRIBUTE_THREE}, new String[]{RULE_ATTRIBUTE_TWO}),
	ATTRIBUTE_4(new String[]{RULE_ATTRIBUTE_TWO,RULE_ATTRIBUTE_FIVE}, new String[]{RULE_ATTRIBUTE_ONE,RULE_ATTRIBUTE_THREE,RULE_ATTRIBUTE_FOUR,}),
	;
	
	public static final String RULE_TEMPLATE_XML_FILENAME_PARM = "ActivationAttributesTest_";
	public static final String RULE_TEMPLATE_NAME = "RuleTemplate_Activation_Test";
	
	public String[] activeAttributeNames;
	public String[] inactiveAttributeNames;
	
	private TemplateParserAttributeActivationFixture(String[] activeAttributeNames, String[] inactiveAttributeNames) {
	    this.activeAttributeNames = activeAttributeNames;
	    this.inactiveAttributeNames = inactiveAttributeNames;
	}
    }

    @Test public void testAttributeActivationAndRemoval() throws Exception {
	RuleTemplate template = null;
	int totalAttributes = -1;
        for (TemplateParserAttributeActivationFixture currentEnum : TemplateParserAttributeActivationFixture.values()) {
            String fileNameParameter = TemplateParserAttributeActivationFixture.RULE_TEMPLATE_XML_FILENAME_PARM + (currentEnum.ordinal() + 1);
            testTemplate(fileNameParameter, null);
            template = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(TemplateParserAttributeActivationFixture.RULE_TEMPLATE_NAME);
            totalAttributes = currentEnum.activeAttributeNames.length + currentEnum.inactiveAttributeNames.length; 
            assertEquals("Total Number of Attributes from Rule Template is wrong",totalAttributes,template.getRuleTemplateAttributes().size());
            testListOfTemplateAttributes(template.getRuleTemplateAttributes(), currentEnum.activeAttributeNames, null);
	}
    }
    
    // test for ingesting active attribute
    
    // test for ingesting inactive attribute
    
    // test for ingesting active attribute and then reingest to test manual inactivation
    
    // test for ingesting active attribute and then reingest to test automatic inactivation
    
}
