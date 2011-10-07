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

import org.junit.Test;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.test.BaseRiceTestCase;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
	public void testResolve_simpleMap() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(SIMPLE_CONFIG_1);
		resolver.setExtensionDefinition(RuleAttribute.to(ruleAttribute));
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(SIMPLE_DOC_XML_1);
		context.setDocumentContent(docContent);
		
		List<Map<String, String>> maps = resolver.resolve(context);
		verifyAccountmaps(maps);
	}
	
	@Test
	public void testResolve_simpleMap_noBaseXPath() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(SIMPLE_CONFIG_2);
		resolver.setExtensionDefinition(RuleAttribute.to(ruleAttribute));
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(SIMPLE_DOC_XML_1);
		context.setDocumentContent(docContent);
		
		List<Map<String, String>> maps = resolver.resolve(context);
		verifyAccountmaps(maps);
		
	}
	
	private void verifyAccountmaps(List<Map<String, String>> maps) {
		assertEquals("Incorrect number of attribute sets.", 4, maps.size());
		
		String acctNumKey = "accountNumber";
		Map<String, String> map1 = maps.get(0);
		assertEquals(1, map1.size());
		assertEquals("12345", map1.get(acctNumKey));
		Map<String, String> map2 = maps.get(1);
		assertEquals(1, map2.size());
		assertEquals("54321", map2.get(acctNumKey));
		Map<String, String> map3 = maps.get(2);
		assertEquals(1, map3.size());
		assertEquals("102030", map3.get(acctNumKey));
		Map<String, String> map4 = maps.get(3);
		assertEquals(1, map4.size());
		assertEquals("302010", map4.get(acctNumKey));
	}
	
	@Test
	public void testResolve_compoundMap1() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(COMPOUND_CONFIG_1);
		resolver.setExtensionDefinition(RuleAttribute.to(ruleAttribute));
		
		RouteContext context = new RouteContext();
		DocumentContent docContent = new StandardDocumentContent(COMPOUND_DOC_XML_1);
		context.setDocumentContent(docContent);
		
		List<Map<String, String>> maps = resolver.resolve(context);
		assertEquals("Incorrect number of attribute sets", 2, maps.size());
		
		String chartKey = "chart";
		String orgKey = "org";
		
		Map<String, String> map1 = maps.get(0);
		assertEquals(2, map1.size());
		assertEquals("BL", map1.get(chartKey));
		assertEquals("BUS", map1.get(orgKey));
		
		Map<String, String> map2 = maps.get(1);
		assertEquals(2, map2.size());
		assertEquals("IN", map2.get(chartKey));
		assertEquals("MED", map2.get(orgKey));
	}
	
	@Test
	public void testResolve_compoundMap() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(COMPOUND_CONFIG_1);
		resolver.setExtensionDefinition(RuleAttribute.to(ruleAttribute));
		
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
		
		List<Map<String, String>> maps = resolver.resolve(context);
		assertEquals(1, maps.size());
		assertEquals("BL", maps.get(0).get("chart"));
		assertEquals("BUS", maps.get(0).get("org"));
	}
	
	@Test
	public void testResolve_compoundMap_noBaseXPath() throws Exception {
		XPathQualifierResolver resolver = new XPathQualifierResolver();
		
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setXmlConfigData(COMPOUND_CONFIG_2);
		resolver.setExtensionDefinition(RuleAttribute.to(ruleAttribute));
		
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
		
		List<Map<String, String>> maps = resolver.resolve(context);
		assertEquals(1, maps.size());
		assertEquals("BL", maps.get(0).get("chart"));
		assertEquals("BUS", maps.get(0).get("org"));
		
	}
	
}
