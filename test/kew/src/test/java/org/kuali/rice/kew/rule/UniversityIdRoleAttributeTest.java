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
import org.kuali.rice.kew.dto.EmplIdDTO;

import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.StringReader;

import static org.junit.Assert.assertTrue;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UniversityIdRoleAttributeTest extends KEWTestCase {
    
    private static final String ATTRIBUTE_NAME = "UniversityIdRoleAttribute";
    private static final String UNIVERSITY_ID_PROP = "universityId";
    
    @Test
    public void testUniversityIdAttribute() throws Exception {
	loadXmlFile("UniversityIdRoleAttributeTestConfig.xml");
	
	// network id to university id mapping as defined in DefaultSuiteTestData.xml
	// -----------------------------------
	// ewestfal     ->     2001
	// rkirkend     ->     2002
	// bmcgough     ->     2004
	
	WorkflowDocument document = new WorkflowDocument(new EmplIdDTO("2001"), "UniversityIdRoleAttributeTest");
	
	WorkflowAttributeDefinitionDTO universityIdDef1 = new WorkflowAttributeDefinitionDTO("UniversityIdRoleAttribute");
	PropertyDefinitionDTO universityIdProp1 = new PropertyDefinitionDTO(UNIVERSITY_ID_PROP, "2002");
	universityIdDef1.addProperty(universityIdProp1);
	
	WorkflowAttributeDefinitionDTO universityIdDef2 = new WorkflowAttributeDefinitionDTO("UniversityIdRoleAttribute");
	PropertyDefinitionDTO universityIdProp2 = new PropertyDefinitionDTO(UNIVERSITY_ID_PROP, "2004");
	universityIdDef2.addProperty(universityIdProp2);
	
	document.addAttributeDefinition(universityIdDef1);
	document.addAttributeDefinition(universityIdDef2);
	
	document.routeDocument("Routing!");
	
	// load the document as rkirkend
	
	document = new WorkflowDocument(new EmplIdDTO("2002"), document.getRouteHeaderId());
	assertTrue("Document should be ENROUTE", document.stateIsEnroute());
	assertTrue("rkirkend should have an approve request.", document.isApprovalRequested());
	
	// load the document as bmcgough
	document = new WorkflowDocument(new EmplIdDTO("2004"), document.getRouteHeaderId());
	assertTrue("bmcgough should have an approve request.", document.isApprovalRequested());
	
	// submit an approve as bmcgough
	document.approve("i approve");
	
	// reload as rkirkend, verify still enroute
	document = new WorkflowDocument(new EmplIdDTO("2002"), document.getRouteHeaderId());
	assertTrue("Document should be ENROUTE", document.stateIsEnroute());
	assertTrue("rkirkend should have an approve request.", document.isApprovalRequested());
	document.approve("i also approve");
	
	// now the document should be FINAL
	assertTrue("Document should be FINAL", document.stateIsFinal());
	
    }
    
    @Test
    public void testParameterizedUniversityIdAttribute() throws Exception {
	loadXmlFile("ParameterizedUniversityIdRoleAttributeTestConfig.xml");
	
	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "UniversityIdRoleAttributeTest");
	
	WorkflowAttributeDefinitionDTO univIdDef1 = new WorkflowAttributeDefinitionDTO("UniversityIdRoleAttribute");
	PropertyDefinitionDTO univIdProp1 = new PropertyDefinitionDTO(UNIVERSITY_ID_PROP, "2002");
	univIdDef1.addProperty(univIdProp1);
		
	document.addAttributeDefinition(univIdDef1);
	
	document.routeDocument("Routing!");
	
	// load the document as rkirkend
	
	document = new WorkflowDocument(new EmplIdDTO("2002"), document.getRouteHeaderId());
	assertTrue("Document should be ENROUTE", document.stateIsEnroute());
	assertTrue("rkirkend should have an approve request.", document.isApprovalRequested());
	
	// now let's verify the XML
	
	XPath xPath = XPathHelper.newXPath();
	assertTrue("Should have found the ID.", (Boolean)xPath.evaluate("//UniversityIdRoleAttribute/thisIdRocks", new InputSource(new StringReader(document.getDocumentContent().getFullContent())), XPathConstants.BOOLEAN));
	
    }

}
