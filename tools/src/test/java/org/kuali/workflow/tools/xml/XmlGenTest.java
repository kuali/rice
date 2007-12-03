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
package org.kuali.workflow.tools.xml;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class XmlGenTest extends Assert {

	@Test
	public void testIndexing() throws Exception {
		String outputPath = System.getProperty("java.io.tmpdir")+"/xmlgentest";
		File outputFile = new File(outputPath);
		if (outputFile.exists()) {
			FileUtils.deleteDirectory(outputFile);
		}

		XmlGen xmlGen = new XmlGen();
		xmlGen.setInputDirectoryPath("tools/test/input");
		xmlGen.setOutputDirectoryPath(outputPath);
		xmlGen.setXmlGenHelper(new TestXmlGenHelper());
		xmlGen.run();

		// verify everything was indexed correctly

		assertEquals("Should be 2 document types.", 2, xmlGen.getDocumentTypes().size());
		assertEquals("Should be 2 attributes.", 2, xmlGen.getAttributes().size());
		assertEquals("Should be 5 templates.", 5, xmlGen.getTemplates().size());

		boolean foundInitiatorAttribute = false;
		boolean foundSchoolAttribute = false;
		for (Attribute attribute : xmlGen.getAttributes().values()) {
			if (attribute.getName().equals("InitiatorAttribute")) {
				foundInitiatorAttribute = true;
				List<String> fieldNames = attribute.getFieldNames();
				assertEquals("Field names for the initiator attribute should be empty.", 0, fieldNames.size());
			} else if (attribute.getName().equals("EDLSchoolAttribute")) {
				foundSchoolAttribute = true;
				List<String> fieldNames = attribute.getFieldNames();
				assertEquals("School attribute should have 1 field.", 1, fieldNames.size());
				assertEquals("school", fieldNames.get(0));
			}
		}
		assertTrue(foundInitiatorAttribute);
		assertTrue(foundSchoolAttribute);

		// check the SpreadsheetOutput
		SpreadsheetOutput output = xmlGen.getOutput();

		// verify the output
		assertEquals("Should be 3 rules.", 3, output.getRules().size());

		// rule1
		Rule rule1 = output.getRules().get(0);
		assertEquals("OfferRequest", rule1.getDocumentType().getName());
		assertEquals("OfficeOfAffirmativeAction-SchoolRouting", rule1.getTemplate().getName());
		assertFalse(rule1.getIgnorePrevious());
		assertEquals("School/RC of Bl-BUS for OAA eDoc Lite", rule1.getDescription());
		assertEquals(1, rule1.getRuleExtensions().size());
		RuleExtension rule1Ext1 = rule1.getRuleExtensions().get(0);
		assertEquals(1, rule1Ext1.getExtensionValues().size());
		RuleExtensionValue rule1ExtVal1 = rule1Ext1.getExtensionValues().get(0);
		assertEquals("school", rule1ExtVal1.getKey());
		assertEquals("BUS", rule1ExtVal1.getValue());
		assertEquals(1, rule1.getResponsibilities().size());
		Responsibility rule1Resp1 = rule1.getResponsibilities().get(0);
		assertEquals("A", rule1Resp1.getActionRequested());
		assertNotNull(rule1Resp1.getWorkgroup());
		assertEquals("OAA_BL-BUS", rule1Resp1.getWorkgroup().getName());
		Workgroup rule1Wg1 = rule1Resp1.getWorkgroup();
		assertEquals(3, rule1Wg1.getMembers().size());
		assertTrue(rule1Wg1.getMembers().contains("user1"));
		assertTrue(rule1Wg1.getMembers().contains("user2"));
		assertTrue(rule1Wg1.getMembers().contains("user3"));

		// rule2
		Rule rule2 = output.getRules().get(1);
		assertEquals("OfferRequest", rule2.getDocumentType().getName());
		assertEquals("OfficeOfAffirmativeAction-SchoolRouting", rule2.getTemplate().getName());
		assertFalse(rule2.getIgnorePrevious());
		assertEquals("EDUC Description", rule2.getDescription());
		assertEquals(1, rule2.getRuleExtensions().size());
		RuleExtension rule2Ext1 = rule2.getRuleExtensions().get(0);
		assertEquals(1, rule2Ext1.getExtensionValues().size());
		RuleExtensionValue rule2ExtVal1 = rule2Ext1.getExtensionValues().get(0);
		assertEquals("school", rule2ExtVal1.getKey());
		assertEquals("EDUC", rule2ExtVal1.getValue());
		assertEquals(1, rule2.getResponsibilities().size());
		Responsibility rule2Resp1 = rule2.getResponsibilities().get(0);
		assertEquals("K", rule2Resp1.getActionRequested());
		assertNotNull(rule2Resp1.getWorkgroup());
		assertEquals("MyEducWG", rule2Resp1.getWorkgroup().getName());
		assertEquals("user5", rule2Resp1.getWorkgroup().getMembers().get(0));

		// rule3
		Rule rule3 = output.getRules().get(2);
		assertEquals("OfferRequest", rule3.getDocumentType().getName());
		assertEquals("OfficeOfAffirmativeAction-SchoolRouting", rule3.getTemplate().getName());
		assertTrue(rule3.getIgnorePrevious());
		assertTrue(rule3.getDescription().contains("MUS"));
		assertTrue(rule3.getDescription().contains("OfferRequest"));
		assertTrue(rule3.getDescription().contains("school"));
		assertEquals(1, rule3.getRuleExtensions().size());
		RuleExtension rule3Ext1 = rule3.getRuleExtensions().get(0);
		assertEquals(1, rule3Ext1.getExtensionValues().size());
		RuleExtensionValue rule3ExtVal1 = rule3Ext1.getExtensionValues().get(0);
		assertEquals("school", rule3ExtVal1.getKey());
		assertEquals("MUS", rule3ExtVal1.getValue());
		assertEquals(2, rule3.getResponsibilities().size());
		Responsibility rule3Resp1 = rule3.getResponsibilities().get(0);
		assertEquals("F", rule3Resp1.getActionRequested());
		assertNull(rule3Resp1.getWorkgroup());
		assertEquals("user3", rule3Resp1.getUser());
		Responsibility rule3Resp2 = rule3.getResponsibilities().get(1);
		assertEquals("F", rule3Resp2.getActionRequested());
		assertNull(rule3Resp2.getWorkgroup());
		assertEquals("user2", rule3Resp2.getUser());

		if (outputFile.exists()) {
			FileUtils.deleteDirectory(outputFile);
		}
	}

	private class TestXmlGenHelper extends XmlGenHelper {

		@Override
		public String generateRuleDescription(Rule rule) {
			if (rule.getResponsibilities().size() > 0 &&
					rule.getResponsibilities().get(0).getWorkgroup() != null &&
					rule.getResponsibilities().get(0).getWorkgroup().getName().equals("MyEducWG")) {
				return "EDUC Description";
			}
			return null;
		}

		@Override
		public List<String> resolveFieldNames(Attribute attribute) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
