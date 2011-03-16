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
package org.kuali.rice.kew.rule;

import org.junit.Test;

import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.StringReader;

import static org.junit.Assert.assertTrue;

/**
 * This is a description of what this class does - ewestfal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class PrincipalIdRoleAttributeTest extends KEWTestCase {

	private static final String ATTRIBUTE_NAME = "PrincipalIdRoleAttribute";
	private static final String PRINCIPAL_ID_PROP = "principalId";

	@Test
	public void testPrincipalIdAttribute() throws Exception {
		loadXmlFile("PrincipalIdRoleAttributeTestConfig.xml");

		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName(
				"ewestfal"), "PrincipalIdRoleAttributeTest");

		WorkflowAttributeDefinitionDTO principalIdDef1 = new WorkflowAttributeDefinitionDTO(
				"PrincipalIdRoleAttribute");
		PropertyDefinitionDTO principalIdProp1 = new PropertyDefinitionDTO(
				PRINCIPAL_ID_PROP, KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("rkirkend").getPrincipalId());
		principalIdDef1.addProperty(principalIdProp1);

		WorkflowAttributeDefinitionDTO principalIdDef2 = new WorkflowAttributeDefinitionDTO(
				"PrincipalIdRoleAttribute");
		PropertyDefinitionDTO principalIdProp2 = new PropertyDefinitionDTO(
				PRINCIPAL_ID_PROP, KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("bmcgough").getPrincipalId());
		principalIdDef2.addProperty(principalIdProp2);

		document.addAttributeDefinition(principalIdDef1);
		document.addAttributeDefinition(principalIdDef2);

		document.routeDocument("Routing!");

		// load the document as rkirkend

		document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document
				.getRouteHeaderId());
		assertTrue("Document should be ENROUTE", document.stateIsEnroute());
		assertTrue("rkirkend should have an approve request.", document
				.isApprovalRequested());

		// load the document as bmcgough
		document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document
				.getRouteHeaderId());
		assertTrue("bmcgough should have an approve request.", document
				.isApprovalRequested());

		// submit an approve as bmcgough
		document.approve("i approve");

		// reload as rkirkend, verify still enroute
		document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document
				.getRouteHeaderId());
		assertTrue("Document should be ENROUTE", document.stateIsEnroute());
		assertTrue("rkirkend should have an approve request.", document
				.isApprovalRequested());
		document.approve("i also approve");

		// now the document should be FINAL
		assertTrue("Document should be FINAL", document.stateIsFinal());

	}

	@Test
	public void testParameterizedPrincipalIdAttribute() throws Exception {
		loadXmlFile("ParameterizedPrincipalIdRoleAttributeTestConfig.xml");

		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName(
				"ewestfal"), "PrincipalIdRoleAttributeTest");

		WorkflowAttributeDefinitionDTO principalIdDef1 = new WorkflowAttributeDefinitionDTO(
				"PrincipalIdRoleAttribute");
		PropertyDefinitionDTO principalIdProp1 = new PropertyDefinitionDTO(
				PRINCIPAL_ID_PROP, KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("rkirkend").getPrincipalId());
		principalIdDef1.addProperty(principalIdProp1);

		document.addAttributeDefinition(principalIdDef1);

		document.routeDocument("Routing!");

		// load the document as rkirkend

		document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document
				.getRouteHeaderId());
		assertTrue("Document should be ENROUTE", document.stateIsEnroute());
		assertTrue("rkirkend should have an approve request.", document
				.isApprovalRequested());

		// now let's verify the XML

		XPath xPath = XPathHelper.newXPath();
		assertTrue("Should have found the ID.", (Boolean) xPath.evaluate(
				"//" + ATTRIBUTE_NAME + "/thisIdRocks", new InputSource(
						new StringReader(document.getDocumentContent()
								.getFullContent())), XPathConstants.BOOLEAN));

	}

}
