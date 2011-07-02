/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.role;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.test.BaseRiceTestCase;

/**
 * Tests the XPathQualifierResolver.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class XPathQualifierResolverTest extends BaseRiceTestCase {

	private static final String SIMPLE_CONFIG_1 = "<resolverConfig>" +
		"<baseXPathExpression>/xmlData/accountNumbers</baseXPathExpression>" +
		"<qualifier name=\"accountNumber\">" +
		"<xPathExpression>./accountNumber</xPathExpression>" +
		"</qualifier>" +
		"</resolverConfig>";
	
	private static final String SIMPLE_DOC_XML_1 = "<xmlData>" +
		"<accountNumbers>" +
		"<accountNumber>12345</accountNumber>" +
		"<accountNumber>54321</accountNumber>" +
		"<accountNumber>102030</accountNumber>" +
		"<accountNumber>302010</accountNumber>" +
		"</accountNumbers>" +
		"</xmlData>";
	
	private static final String SIMPLE_CONFIG_2 = "<resolverConfig>" +
	"<qualifier name=\"accountNumber\">" +
	"<xPathExpression>//accountNumber</xPathExpression>" +
	"</qualifier>" +
	"</resolverConfig>";
	
	private static final String COMPOUND_CONFIG_1 = "<resolverConfig>" +
		"<baseXPathExpression>/xmlData/chartOrg</baseXPathExpression>" +
		"<qualifier name=\"chart\">" +
		"<xPathExpression>./chart</xPathExpression>" +
		"</qualifier>" +
		"<qualifier name=\"org\">" +
		"<xPathExpression>./org</xPathExpression>" +
		"</qualifier>" +
		"</resolverConfig>";

	private static final String COMPOUND_CONFIG_2 = "<resolverConfig>" +
		"<qualifier name=\"chart\">" +
		"<xPathExpression>//chart</xPathExpression>" +
		"</qualifier>" +
		"<qualifier name=\"org\">" +
		"<xPathExpression>//org</xPathExpression>" +
		"</qualifier>" +
		"</resolverConfig>";

	
	private static final String COMPOUND_DOC_XML_1 =  "<xmlData>" +
	 	"<chartOrg>" +
	 	"<chart>BL</chart>" +
	 	"<org>BUS</org>" +
	 	"</chartOrg>" +
	 	"<chartOrg>" +
	 	"<chart>IN</chart>" +
	 	"<org>MED</org>" +
	 	"</chartOrg>" +
	 	"</xmlData>";
	
	private static final String COMPOUND_DOC_XML_2 =  "<xmlData>" +
	 	"<chartOrg>" +
	 	"<chart>BL</chart>" +
	 	"<org>BUS</org>" +
	 	"<chart>IN</chart>" +
	 	"<org>MED</org>" +
	 	"</chartOrg>" +
	 	"</xmlData>";
	
	private static final String COMPOUND_DOC_XML_3 =  "<xmlData>" +
 		"<chartOrg>" +
 		"<chart>BL</chart>" +
 		"<org>BUS</org>" +
 		"</chartOrg>" +
 		"</xmlData>";
	
	@Test
	public void testResolve_simpleAttributeSet() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(SIMPLE_CONFIG_1);
		resolver.setRuleAttribute(ruleAttribute);
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(SIMPLE_DOC_XML_1);
		context.setDocumentContent(docContent);
		
		List<AttributeSet> attributeSets = resolver.resolve(context);
		verifyAccountAttributeSets(attributeSets);
	}
	
	@Test
	public void testResolve_simpleAttributeSet_noBaseXPath() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(SIMPLE_CONFIG_2);
		resolver.setRuleAttribute(ruleAttribute);
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(SIMPLE_DOC_XML_1);
		context.setDocumentContent(docContent);
		
		List<AttributeSet> attributeSets = resolver.resolve(context);
		verifyAccountAttributeSets(attributeSets);
		
	}
	
	private void verifyAccountAttributeSets(List<AttributeSet> attributeSets) {
		assertEquals("Incorrect number of attribute sets.", 4, attributeSets.size());
		
		String acctNumKey = "accountNumber";
		AttributeSet attributeSet1 = attributeSets.get(0);
		assertEquals(1, attributeSet1.size());
		assertEquals("12345", attributeSet1.get(acctNumKey));
		AttributeSet attributeSet2 = attributeSets.get(1);
		assertEquals(1, attributeSet2.size());
		assertEquals("54321", attributeSet2.get(acctNumKey));
		AttributeSet attributeSet3 = attributeSets.get(2);
		assertEquals(1, attributeSet3.size());
		assertEquals("102030", attributeSet3.get(acctNumKey));
		AttributeSet attributeSet4 = attributeSets.get(3);
		assertEquals(1, attributeSet4.size());
		assertEquals("302010", attributeSet4.get(acctNumKey));
	}
	
	@Test
	public void testResolve_compoundAttributeSet() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(COMPOUND_CONFIG_1);
		resolver.setRuleAttribute(ruleAttribute);
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(COMPOUND_DOC_XML_1);
		context.setDocumentContent(docContent);
		
		List<AttributeSet> attributeSets = resolver.resolve(context);
		assertEquals("Incorrect number of attribute sets", 2, attributeSets.size());
		
		String chartKey = "chart";
		String orgKey = "org";
		
		AttributeSet attributeSet1 = attributeSets.get(0);
		assertEquals(2, attributeSet1.size());
		assertEquals("BL", attributeSet1.get(chartKey));
		assertEquals("BUS", attributeSet1.get(orgKey));
		
		AttributeSet attributeSet2 = attributeSets.get(1);
		assertEquals(2, attributeSet2.size());
		assertEquals("IN", attributeSet2.get(chartKey));
		assertEquals("MED", attributeSet2.get(orgKey));
	}
	
	@Test
	public void testResolve_compoundAttributeSet_badXml() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(COMPOUND_CONFIG_1);
		resolver.setRuleAttribute(ruleAttribute);
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(COMPOUND_DOC_XML_2);
		context.setDocumentContent(docContent);
		
		// should fail with this document content
		
		try {
			resolver.resolve(context);
			fail("Invalid XML was encountered, resolver should have thrown an exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// however, should succeed with this
		
		context = new RouteContext();
		docContent = new StandardDocumentContent(COMPOUND_DOC_XML_3);
		context.setDocumentContent(docContent);
		
		List<AttributeSet> attributeSets = resolver.resolve(context);
		assertEquals(1, attributeSets.size());
		assertEquals("BL", attributeSets.get(0).get("chart"));
		assertEquals("BUS", attributeSets.get(0).get("org"));
	}
	
	@Test
	public void testResolve_compoundAttributeSet_noBaseXPath() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(COMPOUND_CONFIG_2);
		resolver.setRuleAttribute(ruleAttribute);
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(COMPOUND_DOC_XML_2);
		context.setDocumentContent(docContent);
		
		// should fail with this document content
		
		try {
			resolver.resolve(context);
			fail("Invalid XML was encountered, resolver should have thrown an exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// however, should succeed with this
		
		context = new RouteContext();
		docContent = new StandardDocumentContent(COMPOUND_DOC_XML_3);
		context.setDocumentContent(docContent);
		
		List<AttributeSet> attributeSets = resolver.resolve(context);
		assertEquals(1, attributeSets.size());
		assertEquals("BL", attributeSets.get(0).get("chart"));
		assertEquals("BUS", attributeSets.get(0).get("org"));
		
	}
	
}
