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
package edu.iu.uis.eden.actions;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class CreateDocumentTest extends KEWTestCase {
    
    @Override
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    /**
	 * Tests the attempt to create a document from a non-existent document type.
	 */
	@Test public void testCreateNonExistentDocumentType() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "flim-flam-flooey");
		try {
			document.getRouteHeaderId();
			fail("A workflow exception should have been thrown.");
		} catch (WorkflowException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests the attempt to create a document from a document type with no routing path.
	 */
	@Test public void testCreateNonRoutableDocumentType() throws Exception {
		// the BlanketApproveTest is a parent document type that has no routing path defined.  Attempts to
		// create documents of this type should throw a WorkflowException
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BlanketApproveTest");
		try {
			document.getRouteHeaderId();
			fail("A workflow exception should have been thrown.");
		} catch (WorkflowException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Tests the attempt to create a document from a document type with no routing path.
     */
    @Test public void testCreateInactiveDocumentType() throws Exception {
        // the CreatedDocumentInactive document type is inactive and should not be able to 
        // be initiated for a new document
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "CreatedDocumentInactive");
        try {
            document.getRouteHeaderId();
            fail("A workflow exception should have been thrown.");
        } catch (WorkflowException e) {
            e.printStackTrace();
        }
    }
}
