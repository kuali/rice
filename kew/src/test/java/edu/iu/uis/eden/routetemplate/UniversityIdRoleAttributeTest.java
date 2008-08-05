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
package edu.iu.uis.eden.routetemplate;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.junit.Test;
import org.kuali.rice.kew.dto.EmplIdDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.workflow.test.KEWTestCase;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UniversityIdRoleAttributeTest extends KEWTestCase {
    
    private static final String ATTRIBUTE_NAME = "UniversityIdRoleAttribute";
    private static final String UNIVERSITY_ID_PROP = "universityId";
    
    @Test
    public void testUniversityIdAttribute() throws Exception {
	loadXmlFile("UniversityIdRoleAttributeTestConfig.xml");
	
	// network id to university id mapping
	// -----------------------------------
	// ewestfal     ->     1
	// rkirkend     ->     2
	// bmcgough     ->     4
	
	WorkflowDocument document = new WorkflowDocument(new EmplIdDTO("1"), "UniversityIdRoleAttributeTest");
	
	WorkflowAttributeDefinitionDTO universityIdDef1 = new WorkflowAttributeDefinitionDTO("UniversityIdRoleAttribute");
	PropertyDefinitionDTO universityIdProp1 = new PropertyDefinitionDTO(UNIVERSITY_ID_PROP, "2");
	universityIdDef1.addProperty(universityIdProp1);
	
	WorkflowAttributeDefinitionDTO universityIdDef2 = new WorkflowAttributeDefinitionDTO("UniversityIdRoleAttribute");
	PropertyDefinitionDTO universityIdProp2 = new PropertyDefinitionDTO(UNIVERSITY_ID_PROP, "4");
	universityIdDef2.addProperty(universityIdProp2);
	
	document.addAttributeDefinition(universityIdDef1);
	document.addAttributeDefinition(universityIdDef2);
	
	document.routeDocument("Routing!");
	
	// load the document as rkirkend
	
	document = new WorkflowDocument(new EmplIdDTO("2"), document.getRouteHeaderId());
	assertTrue("Document should be ENROUTE", document.stateIsEnroute());
	assertTrue("rkirkend should have an approve request.", document.isApprovalRequested());
	
	// load the document as bmcgough
	document = new WorkflowDocument(new EmplIdDTO("4"), document.getRouteHeaderId());
	assertTrue("bmcgough should have an approve request.", document.isApprovalRequested());
	
	// submit an approve as bmcgough
	document.approve("i approve");
	
	// reload as rkirkend, verify still enroute
	document = new WorkflowDocument(new EmplIdDTO("2"), document.getRouteHeaderId());
	assertTrue("Document should be ENROUTE", document.stateIsEnroute());
	assertTrue("rkirkend should have an approve request.", document.isApprovalRequested());
	document.approve("i also approve");
	
	// now the document should be FINAL
	assertTrue("Document should be FINAL", document.stateIsFinal());
	
    }
    
    @Test
    public void testParameterizedUniversityIdAttribute() throws Exception {
	loadXmlFile("ParameterizedUniversityIdRoleAttributeTestConfig.xml");
	
	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "UniversityIdRoleAttributeTest");
	
	WorkflowAttributeDefinitionDTO univIdDef1 = new WorkflowAttributeDefinitionDTO("UniversityIdRoleAttribute");
	PropertyDefinitionDTO univIdProp1 = new PropertyDefinitionDTO(UNIVERSITY_ID_PROP, "2");
	univIdDef1.addProperty(univIdProp1);
		
	document.addAttributeDefinition(univIdDef1);
	
	document.routeDocument("Routing!");
	
	// load the document as rkirkend
	
	document = new WorkflowDocument(new EmplIdDTO("2"), document.getRouteHeaderId());
	assertTrue("Document should be ENROUTE", document.stateIsEnroute());
	assertTrue("rkirkend should have an approve request.", document.isApprovalRequested());
	
	// now let's verify the XML
	
	XPath xPath = XPathHelper.newXPath();
	assertTrue("Should have found the ID.", (Boolean)xPath.evaluate("//UniversityIdRoleAttribute/thisIdRocks", new InputSource(new StringReader(document.getDocumentContent().getFullContent())), XPathConstants.BOOLEAN));
	
    }

}
