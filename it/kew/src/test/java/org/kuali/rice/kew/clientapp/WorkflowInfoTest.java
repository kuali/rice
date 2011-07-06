/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.clientapp;

import org.junit.Test;
import org.kuali.rice.kew.actions.BlanketApproveTest;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import static org.junit.Assert.*;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class WorkflowInfoTest extends KEWTestCase {

	@Override
	protected void loadTestData() {
		// need this configuration to create a BlanketApproveParallelTest
		loadXmlFile(BlanketApproveTest.class, "ActionsConfig.xml");
	}
	
    /**
     * Tests the loading of a RouteHeaderVO using the WorkflowInfo.
     *
     * Verifies that an NPE no longer occurrs as mentioned in KULRICE-765.
     */
    @Test
    public void testGetRouteHeader() throws Exception {
     // ensure the UserSession is cleared out (could have been set up by other tests)
    GlobalVariables.setUserSession(null);
    String ewestfalPrincipalId = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName("ewestfal").getPrincipalId();
    GlobalVariables.setUserSession(new UserSession("ewestfal"));
    WorkflowDocument document = WorkflowDocumentFactory.createDocument(ewestfalPrincipalId, "TestDocumentType");
	String documentId = document.getDocumentId();
	assertNotNull(documentId);

	RouteHeaderDTO routeHeaderVO = new WorkflowInfo().getRouteHeader(documentId);
	assertNotNull(routeHeaderVO);

	assertEquals(documentId, routeHeaderVO.getDocumentId());
	assertEquals(KEWConstants.ROUTE_HEADER_INITIATED_CD, routeHeaderVO.getDocRouteStatus());
    }

    @Test
    public void testGetDocumentStatus() throws Exception {
	WorkflowInfo info = new WorkflowInfo();
	// verify that a null document id throws an exception
	try {
	    String status = info.getDocumentStatus(null);
	    fail("A WorkflowException should have been thrown, instead returned status: " + status);
	} catch (WorkflowException e) {
    } catch (IllegalArgumentException e) {
    }
	// verify that a bad document id throws an exception
	try {
	    String status = info.getDocumentStatus("-1");
	    fail("A WorkflowException Should have been thrown, instead returned status: " + status);
	} catch (WorkflowException e) {}

	// now create a doc and load it's status
	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
	String documentId = document.getDocumentId();
	assertNotNull(documentId);

	String status = info.getDocumentStatus(documentId);
	assertEquals("Document should be INITIATED.", KEWConstants.ROUTE_HEADER_INITIATED_CD, status);

	// cancel the doc, it's status should be updated
	document.cancel("");
	status = info.getDocumentStatus(documentId);
	assertEquals("Document should be CANCELED.", KEWConstants.ROUTE_HEADER_CANCEL_CD, status);
    }
    
    /**
     * test for issue KFSMI-2979
     * This method verifies that workflowInfo.getDocumentRoutedByPrincipalId returns the blanket approver 
     * for a document that was put onroute by that person (the blanket approver)
     */
    @Test
    public void testBlanketApproverSubmitted() throws WorkflowException {
    	Person blanketApprover = KimApiServiceLocator.getPersonService().getPersonByPrincipalName("ewestfal");

        WorkflowDocument document = WorkflowDocumentFactory.createDocument(blanketApprover.getPrincipalId(), "BlanketApproveParallelTest");
        document.blanketApprove("");

        String routedByPrincipalId = new WorkflowInfo().getDocumentRoutedByPrincipalId(document.getDocumentId());
        assertEquals("the blanket approver should be the routed by", blanketApprover.getPrincipalId(), routedByPrincipalId);
    }
    
    @Test
    public void testGetAppDocId() throws Exception {
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
    	document.saveDocumentData();
    	
    	String appDocId = new WorkflowInfo().getAppDocId(document.getDocumentId());
    	assertNull("appDocId should be null", appDocId);
    	
    	String appDocIdValue = "1234";
    	document.setApplicationDocumentId(appDocIdValue);
    	document.saveDocumentData();
    	
    	appDocId = new WorkflowInfo().getAppDocId(document.getDocumentId());
    	assertEquals("Incorrect appDocId", appDocIdValue, appDocId);
    }

}
