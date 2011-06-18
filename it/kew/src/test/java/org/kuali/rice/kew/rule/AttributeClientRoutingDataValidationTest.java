/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;

import static org.junit.Assert.*;

/**
 * Tests that an attribute implementing WorkflowAttributeXmlValidator interface can be validated from the 
 * client application, including and especially edl.
 * 
 * An attribute that doesn't implement the interface should record no errors when validated.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeClientRoutingDataValidationTest extends KEWTestCase {


    protected void loadTestData() throws Exception {
        loadXmlFile("AttributeClientRoutingDataValidationTest.xml");
    }    
	
	@Test public void testClientApplicationValidationImplementsWorkflowAttributeXmlValidator() throws Exception {
		WorkflowAttributeDefinitionDTO attDef = new WorkflowAttributeDefinitionDTO(TestRuleAttributeThree.class.getName());
		WorkflowAttributeValidationErrorDTO[] validationErrors = new WorkflowInfo().validAttributeDefinition(attDef);
		assertTrue("Validation errors should not be empty", validationErrors.length != 0);
		assertEquals("Should be 2 validation errors", 2, validationErrors.length);
		boolean foundKey1 = false;
		boolean foundKey2 = false;
		for (int i = 0; i < validationErrors.length; i++) {
			WorkflowAttributeValidationErrorDTO error = validationErrors[i];
			if (error.getKey().equals("key1")) {
				assertEquals("key1 key should have message of value1", "value1", error.getMessage());
				foundKey1 = true;
			} else if (error.getKey().equals("key2")) {
				assertEquals("key2 key should have message of value2", "value2", error.getMessage());
				foundKey2 = true;
			}
		}
		
		assertTrue("should have found a key1 error", foundKey1);
		assertTrue("should have found a key2 error", foundKey2);
	}
	
	@Test public void testClientApplicationValidationNoImplementsWorkflowAttributeXmlValidator() throws Exception {
		WorkflowAttributeDefinitionDTO attDef = new WorkflowAttributeDefinitionDTO(TestRuleAttributeDuex.class.getName());
		WorkflowAttributeValidationErrorDTO[] validationErrors = new WorkflowInfo().validAttributeDefinition(attDef);
		assertTrue("Validation errors should be empty because WorkflowAttributeXmlValidator interface is not implemented", validationErrors.length == 0);
	}
	
	@Test public void testThrowWorkflowExceptionNoneExistentAttribute() throws Exception {
		WorkflowAttributeDefinitionDTO attDef = new WorkflowAttributeDefinitionDTO("FakeyMcAttribute");
		try {
			new WorkflowInfo().validAttributeDefinition(attDef);
			fail("Should have thrown WorkflowException attempting to lookup non-existent attribute");
		} catch (WorkflowRuntimeException e) {
			assertTrue("This is the correct exception to throw", true);
		}
	}
}
