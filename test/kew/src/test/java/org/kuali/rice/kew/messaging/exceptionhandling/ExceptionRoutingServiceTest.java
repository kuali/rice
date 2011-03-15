/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.messaging.exceptionhandling;

import org.junit.Test;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This is a unit test for testing the functionality of the ExceptionRoutingService. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class ExceptionRoutingServiceTest extends KEWTestCase {

	/**
	 * Checks to make sure that the KIM routing is working.
	 * Based upon the test method org.kuali.rice.kew.doctype.DocumentTypeTest.testFinalApproverRouting()
	 */
	@Test public void testKimExceptionRouting() throws Exception {
		loadXmlFile("RouteExceptionTestDoc.xml");
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("admin"), "TestFinalApproverDocumentType");
        document.setTitle("");
        document.routeDocument("");
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        try {
            document.approve("");
            fail("document should have thrown routing exception");
        } catch (Exception e) {
            //deal with single transaction issue in test.
        	TestUtilities.getExceptionThreader().join();
        	document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
            assertTrue("Document should be in exception routing", document.stateIsException());
        }
	}

	/**
	 * Checks to make sure that the KIM routing is working with hierarchical documents.
	 * Based upon the test method org.kuali.rice.kew.doctype.DocumentTypeTest.testFinalApproverRouting()
	 */
	@Test public void testKimExceptionRoutingWithDocHierarchy() throws Exception {
		loadXmlFile("RouteExceptionTestDoc.xml");
		String[] docNames = {"TestFinalApproverDocumentType_Child", "TestFinalApproverDocumentType_GrandChild"};
		// Test the child doc and then the grandchild doc.
		for (int i = 0; i < docNames.length; i++) {
			// Perform the same steps as in the previous unit test.
			WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("admin"), docNames[i]);
	        document.setTitle("");
	        document.routeDocument("");
	        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
	        try {
	            document.approve("");
	            fail("document should have thrown routing exception");
	        } catch (Exception e) {
	            //deal with single transaction issue in test.
	        	TestUtilities.getExceptionThreader().join();
	        	document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
	            assertTrue("Document should be in exception routing", document.stateIsException());
	        }
		}
	}

	/**
	 * Checks to make sure that the KIM routing is working for a RiceDocument child.
	 * Based upon the test method org.kuali.rice.kew.doctype.DocumentTypeTest.testFinalApproverRouting()
	 */
	@Test public void testKimExceptionRoutingWithRiceDocumentChild() throws Exception {
		loadXmlFile("RouteExceptionTestDoc.xml");
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("admin"), "DocumentTypeDocument_New");
        document.setTitle("");
        document.routeDocument("");
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        try {
            document.approve("");
            fail("document should have thrown routing exception");
        } catch (Exception e) {
            //deal with single transaction issue in test.
        	TestUtilities.getExceptionThreader().join();
        	document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
            assertTrue("Document should be in exception routing", document.stateIsException());
        }
	}
}
