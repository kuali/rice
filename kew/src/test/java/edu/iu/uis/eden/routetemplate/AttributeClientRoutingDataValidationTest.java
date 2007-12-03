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
package edu.iu.uis.eden.routetemplate;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Tests that an attribute implementing WorkflowAttributeXmlValidator interface can be validated from the 
 * client application, including and especially edl.
 * 
 * An attribute that doesn't implement the interface should record no errors when validated.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AttributeClientRoutingDataValidationTest extends KEWTestCase {


    protected void loadTestData() throws Exception {
        loadXmlFile("AttributeClientRoutingDataValidationTest.xml");
    }    
	
	@Test public void testClientApplicationValidationImplementsWorkflowAttributeXmlValidator() throws Exception {
		WorkflowAttributeDefinitionVO attDef = new WorkflowAttributeDefinitionVO(TestRuleAttributeThree.class.getName());
		WorkflowAttributeValidationErrorVO[] validationErrors = new WorkflowInfo().validAttributeDefinition(attDef);
		assertTrue("Validation errors should not be empty", validationErrors.length != 0);
		assertEquals("Should be 2 validation errors", 2, validationErrors.length);
		boolean foundKey1 = false;
		boolean foundKey2 = false;
		for (int i = 0; i < validationErrors.length; i++) {
			WorkflowAttributeValidationErrorVO error = validationErrors[i];
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
		WorkflowAttributeDefinitionVO attDef = new WorkflowAttributeDefinitionVO(TestRuleAttributeDuex.class.getName());
		WorkflowAttributeValidationErrorVO[] validationErrors = new WorkflowInfo().validAttributeDefinition(attDef);
		assertTrue("Validation errors should be empty because WorkflowAttributeXmlValidator interface is not implemented", validationErrors.length == 0);
	}
	
	@Test public void testThrowWorkflowExceptionNoneExistentAttribute() throws Exception {
		WorkflowAttributeDefinitionVO attDef = new WorkflowAttributeDefinitionVO("FakeyMcAttribute");
		try {
			new WorkflowInfo().validAttributeDefinition(attDef);
			fail("Should have thrown WorkflowException attempting to lookup non-existent attribute");
		} catch (WorkflowException e) {
			assertTrue("This is the correct exception to throw", true);
		}
	}
}
