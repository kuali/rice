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
package edu.iu.uis.eden.clientapp;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowInfoTest extends KEWTestCase {

    /**
     * Tests the loading of a RouteHeaderVO using the WorkflowInfo.
     *
     * Verifies that an NPE no longer occurrs as mentioned in KULRICE-765.
     */
    @Test
    public void testGetRouteHeader() throws Exception {
	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "TestDocumentType");
	Long documentId = document.getRouteHeaderId();
	assertNotNull(documentId);

	RouteHeaderVO routeHeaderVO = new WorkflowInfo().getRouteHeader(documentId);
	assertNotNull(routeHeaderVO);

	assertEquals(documentId, routeHeaderVO.getRouteHeaderId());
	assertEquals(EdenConstants.ROUTE_HEADER_INITIATED_CD, routeHeaderVO.getDocRouteStatus());
    }

    @Test
    public void testGetDocumentStatus() throws Exception {
	WorkflowInfo info = new WorkflowInfo();
	// verify that a null document id throws an exception
	try {
	    String status = info.getDocumentStatus(null);
	    fail("A WorkflowException should have been thrown, instead returned status: " + status);
	} catch (WorkflowException e) {}
	// verify that a bad document id throws an exception
	try {
	    String status = info.getDocumentStatus(new Long(-1));
	    fail("A WorkflowException Should have been thrown, instead returned status: " + status);
	} catch (WorkflowException e) {}

	// now create a doc and load it's status
	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "TestDocumentType");
	Long documentId = document.getRouteHeaderId();
	assertNotNull(documentId);

	String status = info.getDocumentStatus(documentId);
	assertEquals("Document should be INITIATED.", EdenConstants.ROUTE_HEADER_INITIATED_CD, status);

	// cancel the doc, it's status should be updated
	document.cancel("");
	status = info.getDocumentStatus(documentId);
	assertEquals("Document should be CANCELED.", EdenConstants.ROUTE_HEADER_CANCEL_CD, status);
    }

}
