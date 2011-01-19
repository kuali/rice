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
package org.kuali.rice.kew.actions;

import org.junit.Test;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;

import static org.junit.Assert.fail;

public class CreateDocumentTest extends KEWTestCase {
    
    @Override
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    /**
	 * Tests the attempt to create a document from a non-existent document type.
	 */
	@Test public void testCreateNonExistentDocumentType() throws Exception {
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "flim-flam-flooey");
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
		// create documents of this type should return an error
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "BlanketApproveTest");
		try {
			document.getRouteHeaderId();
			fail("A workflow exception should have been thrown.");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Tests the attempt to create a document from a document type with no routing path.
     */
    @Test public void testCreateInactiveDocumentType() throws Exception {
        // the CreatedDocumentInactive document type is inactive and should not be able to 
        // be initiated for a new document
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "CreatedDocumentInactive");
        try {
            document.getRouteHeaderId();
            fail("A workflow exception should have been thrown.");
        } catch (WorkflowException e) {
            e.printStackTrace();
        }
    }
    
    protected String getPrincipalIdForName(String principalName) {
        return KEWServiceLocator.getIdentityHelperService()
                .getIdForPrincipalName(principalName);
    }
}
