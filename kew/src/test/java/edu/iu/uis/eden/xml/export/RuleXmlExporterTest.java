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
package edu.iu.uis.eden.xml.export;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.test.OldClearDatabaseLifecycle;

/**
 * Tests the RuleXmlExporter by importing XML, exporting it, and then re-importing the xml.<br><br>
 *
 * NOTE: It's important to note that the success of this test depends on all of the Rules in any
 * XML having unique descriptions as this is the only way for the test to identify
 * the rules from the original imported XML and the XML imported from the export.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleXmlExporterTest extends XmlExporterTestCase {

	@Test public void testExport() throws Exception {
        loadXmlFile("edu/iu/uis/eden/actions/ActionsConfig.xml");
        loadXmlStream(new FileInputStream(getBaseDir() + "/src/test/resources/edu/iu/uis/eden/batch/data/RuleAttributeContent.xml"));
        loadXmlStream(new FileInputStream(getBaseDir() + "/src/test/resources/edu/iu/uis/eden/batch/data/RuleTemplateContent.xml"));
        loadXmlStream(new FileInputStream(getBaseDir() + "/src/test/resources/edu/iu/uis/eden/batch/data/DocumentTypeContent.xml"));
        loadXmlStream(new FileInputStream(getBaseDir() + "/src/test/resources/edu/iu/uis/eden/batch/data/RuleContent.xml"));
        assertExport();
    }

    /**
     * Note that the assertion here will fail if you have multiple rules with the same description.
     */
    protected void assertExport() throws Exception {
        // export all existing rules and their dependencies (document types, rule templates, rule attributes)
        List oldRules = KEWServiceLocator.getRuleService().fetchAllRules(true);
        assertAllRulesHaveUniqueNames(oldRules);

        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        dataSet.getRules().addAll(oldRules);
        dataSet.getDocumentTypes().addAll(KEWServiceLocator.getDocumentTypeService().findAllCurrent());
        dataSet.getRuleTemplates().addAll(KEWServiceLocator.getRuleTemplateService().findAll());
        dataSet.getRuleAttributes().addAll(KEWServiceLocator.getRuleAttributeService().findAll());
        byte[] xmlBytes = KEWServiceLocator.getXmlExporterService().export(ExportFormat.XML, dataSet);
        assertTrue("XML should be non empty.", xmlBytes != null && xmlBytes.length > 0);

        // now clear the tables
        new OldClearDatabaseLifecycle().start();

        // import the exported xml
        loadXmlStream(new BufferedInputStream(new ByteArrayInputStream(xmlBytes)));

        List newRules = KEWServiceLocator.getRuleService().fetchAllRules(true);
        assertEquals("Should have same number of old and new Rules.", oldRules.size(), newRules.size());
        for (Iterator iterator = oldRules.iterator(); iterator.hasNext();) {
            RuleBaseValues oldRule = (RuleBaseValues) iterator.next();
            boolean foundRule = false;
            for (Iterator iterator2 = newRules.iterator(); iterator2.hasNext();) {
                RuleBaseValues newRule = (RuleBaseValues) iterator2.next();
                if (oldRule.getDescription().equals(newRule.getDescription())) {
                    assertRuleExport(oldRule, newRule);
                    foundRule = true;
                }
            }
            assertTrue("Could not locate the new rule for description " + oldRule.getDescription(), foundRule);
        }
    }

    private void assertRuleExport(RuleBaseValues oldRule, RuleBaseValues newRule) {
        assertFalse("Ids should be different.", oldRule.getRuleBaseValuesId().equals(newRule.getRuleBaseValuesId()));
        assertEquals(oldRule.getActiveInd(), newRule.getActiveInd());
        assertEquals(DateUtils.round(oldRule.getActivationDate(), Calendar.DATE), DateUtils.round(newRule.getActivationDate(), Calendar.DATE));
        assertEquals(oldRule.getCurrentInd(), newRule.getCurrentInd());
        assertEquals(oldRule.getDeactivationDate(), newRule.getDeactivationDate());
        assertEquals(oldRule.getDelegateRule(), newRule.getDelegateRule());
        assertEquals(oldRule.getDescription(), newRule.getDescription());
        assertEquals(oldRule.getDocTypeName(), newRule.getDocTypeName());
        assertEquals(DateUtils.round(oldRule.getFromDate(), Calendar.DATE), DateUtils.round(newRule.getFromDate(), Calendar.DATE));
        assertEquals(oldRule.getIgnorePrevious(), newRule.getIgnorePrevious());
        assertEquals(oldRule.getPreviousVersionId(), newRule.getPreviousVersionId());
        assertEquals(oldRule.getRouteHeaderId(), newRule.getRouteHeaderId());
        assertEquals(oldRule.getRuleTemplate().getName(), newRule.getRuleTemplate().getName());
        assertEquals(DateUtils.round(oldRule.getToDate(), Calendar.DATE), DateUtils.round(newRule.getToDate(), Calendar.DATE));
        assertEquals(oldRule.getVersionNbr(), newRule.getVersionNbr());

        assertRuleExtensions(oldRule.getRuleExtensions(), newRule.getRuleExtensions());
        assertResponsibilities(oldRule.getResponsibilities(), newRule.getResponsibilities());


    }

    private void assertRuleExtensions(List oldRuleExtensions, List newRuleExtensions) {
        assertEquals(oldRuleExtensions.size(), newRuleExtensions.size());
        for (Iterator iterator = oldRuleExtensions.iterator(); iterator.hasNext();) {
            RuleExtension oldExtension = (RuleExtension) iterator.next();
            boolean foundExtension = false;
            for (Iterator iterator2 = newRuleExtensions.iterator(); iterator2.hasNext();) {
                RuleExtension newExtension = (RuleExtension) iterator2.next();
                if (oldExtension.getRuleTemplateAttribute().getRuleAttribute().getName().equals(newExtension.getRuleTemplateAttribute().getRuleAttribute().getName()) &&
                        oldExtension.getRuleTemplateAttribute().getRuleTemplate().getName().equals(newExtension.getRuleTemplateAttribute().getRuleTemplate().getName())) {
                        assertExtensionValues(oldExtension.getExtensionValues(), newExtension.getExtensionValues());
                        foundExtension = true;
                        break;
                }
            }
            assertTrue("Could not locate rule extension.", foundExtension);
        }
    }

    private void assertExtensionValues(List oldExtensionValues, List newExtensionValues) {
        assertEquals(oldExtensionValues.size(), newExtensionValues.size());
        for (Iterator iterator = oldExtensionValues.iterator(); iterator.hasNext();) {
            RuleExtensionValue oldValue = (RuleExtensionValue) iterator.next();
            boolean foundValue = false;
            for (Iterator iterator2 = oldExtensionValues.iterator(); iterator2.hasNext();) {
                RuleExtensionValue newValue = (RuleExtensionValue) iterator2.next();
                if (oldValue.getKey().equals(newValue.getKey())) {
                    assertEquals(oldValue.getValue(), newValue.getValue());
                    foundValue = true;
                    break;
                }
            }
            assertTrue("Could not locate extension value.", foundValue);
        }
    }

    private void assertResponsibilities(List oldResps, List newResps) {
        assertEquals(oldResps.size(), newResps.size());
        for (Iterator iterator = oldResps.iterator(); iterator.hasNext();) {
            RuleResponsibility oldResp = (RuleResponsibility) iterator.next();
            boolean foundResp = false;
            for (Iterator iterator2 = newResps.iterator(); iterator2.hasNext();) {
                RuleResponsibility newResp = (RuleResponsibility) iterator2.next();
                if (oldResp.getRuleResponsibilityName().equals(newResp.getRuleResponsibilityName())) {
                    assertEquals(oldResp.getActionRequestedCd(), newResp.getActionRequestedCd());
                    assertEquals(oldResp.getApprovePolicy(), newResp.getApprovePolicy());
                    assertEquals(oldResp.getResolvedRoleName(), newResp.getResolvedRoleName());
                    assertEquals(oldResp.getRole(), newResp.getRole());
                    assertEquals(oldResp.getRuleResponsibilityType(), newResp.getRuleResponsibilityType());
                    assertEquals(oldResp.getPriority(), newResp.getPriority());
                    assertDelegations(oldResp.getDelegationRules(), newResp.getDelegationRules());
                    foundResp = true;
                    break;
                }
            }
            assertTrue("Could not locate responsibility "+oldResp.getRuleResponsibilityName()+" on rule "+oldResp.getRuleBaseValues().getDescription(), foundResp);
        }
    }

    private void assertDelegations(List oldDelegations, List newDelegations) {
        assertEquals(oldDelegations.size(), newDelegations.size());
        for (Iterator iterator = oldDelegations.iterator(); iterator.hasNext();) {
            RuleDelegation oldDelegation = (RuleDelegation) iterator.next();
            boolean foundDelegation = false;
            for (Iterator iterator2 = newDelegations.iterator(); iterator2.hasNext();) {
                RuleDelegation newDelegation = (RuleDelegation) iterator2.next();
                if (oldDelegation.getDelegationRuleBaseValues().getDescription().equals(newDelegation.getDelegationRuleBaseValues().getDescription())) {
                    assertEquals(oldDelegation.getDelegationType(), newDelegation.getDelegationType());
                    assertRuleExport(oldDelegation.getDelegationRuleBaseValues(), newDelegation.getDelegationRuleBaseValues());
                    foundDelegation = true;
                    break;
                }
            }
            assertTrue("Could not locate delegation.", foundDelegation);
        }
    }

    private void assertAllRulesHaveUniqueNames(List rules) throws Exception {
    	Set<String> ruleDescriptions = new HashSet<String>();
    	for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
			RuleBaseValues rule = (RuleBaseValues) iterator.next();
			assertFalse("Found 2 rules with the same description '" + rule.getDescription() + "'.  " +
					"In order for this test to work, all rules in the configuration files must have unique descriptions.",
					ruleDescriptions.contains(rule.getDescription()));
			ruleDescriptions.add(rule.getDescription());
		}
    }

}
