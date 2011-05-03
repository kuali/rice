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
package org.kuali.rice.kew.routemanager;

import org.junit.Test;
import org.kuali.rice.kew.dto.ActionRequestDTO;

import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.test.BaselineTestCase;

import javax.xml.namespace.QName;
import java.util.Collection;

import static org.junit.Assert.*;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class ExceptionRoutingTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("RouteManagerConfig.xml");
    }

    protected void setUpAfterDataLoad() throws Exception {
		super.setUpAfterDataLoad();
		// reset these static constants, otherwise they will cause problems between test runs
		ExceptionRoutingTestPostProcessor.THROW_DO_ACTION_TAKEN_EXCEPTION = false;
		ExceptionRoutingTestPostProcessor.THROW_ROUTE_DELETE_ROUTE_HEADER_EXCEPTION = false;
		ExceptionRoutingTestPostProcessor.THROW_ROUTE_STATUS_CHANGE_EXCEPTION = false;
		ExceptionRoutingTestPostProcessor.THROW_ROUTE_STATUS_LEVEL_EXCEPTION = false;
		ExceptionRoutingTestPostProcessor.TRANSITIONED_OUT_OF_EXCEPTION_ROUTING = false;
		ExceptionRoutingTestPostProcessor.BLOW_UP_ON_TRANSITION_INTO_EXCEPTION = false;
	}

    @Test public void testSequentialExceptionRouting() throws Exception {
        WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "ExceptionRoutingSequentialDoc");
        try {
            doc.routeDocument("");
            fail("should have thrown routing exception");
        } catch (Exception e) {
        }

        TestUtilities.getExceptionThreader().join();//this is necessary to ensure that the exception request will be generated.

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), doc.getDocumentId());
        assertTrue("Document should be in exception status", doc.stateIsException());
        
        WorkflowInfo info = new WorkflowInfo();
        ActionRequestDTO[] actionRequests = info.getActionRequests(doc.getDocumentId());

        assertEquals("Should be a single exception request", 1, actionRequests.length);
        for (int i = 0; i < actionRequests.length; i++) {
            ActionRequestDTO actionRequest = actionRequests[i];
            Group group = KimApiServiceLocator.getIdentityManagementService().getGroup(actionRequest.getGroupId());
            assertTrue("Request should be an exception request.", actionRequest.isExceptionRequest());
            assertTrue("Complete should be requested", actionRequest.isCompleteRequest());
            assertTrue("Request should be a workgroup request", actionRequest.isGroupRequest());
            assertEquals("Request should be to 'ExceptionRoutingGroup'", "ExceptionRoutingGroup", group.getName());
            assertNotNull("annotation cannot be null", actionRequest.getAnnotation());
            assertFalse("annotation cannot be empty", "".equals(actionRequest.getAnnotation()));
        }

    }
    
    /**
     * This tests the solution for KULRICE-4493.  Essentially, the problem was that when the workflow engine
     * would transition the document to exception status it would invoke the post processor.  If invoking
     * the post processor raised an exception, that would cause the transaction to get rolled back and the
     * document would get "stuck" in the ENROUTE state with no pending requests.
     */
//    @Test public void testExceptionRouting_BlowUpOnStatusChangeToException() throws Exception {
//    	
//    	// first, configure the post processor so that it throws an exception when we call doRouteStatusChange on transition into exception status
//    	ExceptionRoutingTestPostProcessor.BLOW_UP_ON_TRANSITION_INTO_EXCEPTION = true;
//    	    	
//    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "AlwaysExplodeTestDocument");
//    	try {
//    		doc.routeDocument("");
//    		fail("We should be in exception routing");
//    	} catch (Exception e) {
//    	}
//
//    	TestUtilities.getExceptionThreader().join();//this is necessary to ensure that the exception request will be generated.
//    	
//    	doc = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), doc.getDocumentId());
//    	assertTrue("document should be in exception routing", doc.stateIsException());
//
//    }

	@Test public void testInvalidActionsInExceptionRouting() throws Exception {
        WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "ExceptionRoutingSequentialDoc");
        try {
            doc.routeDocument("");
            fail("should have thrown routing exception");
        } catch (Exception e) {
            log.info("Expected exception occurred: " + e);
        }

        TestUtilities.getExceptionThreader().join();//this is necessary to ensure that the exception request will be generated.

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), doc.getDocumentId());
        assertTrue("Document should be in exception status", doc.stateIsException());

        try {
            doc.routeDocument("routing a document that is in exception routing");
            fail("Succeeded in routing document that is in exception routing");
        } catch (InvalidActionTakenException iate) {
            log.info("Expected exception occurred: " + iate);
        } catch (WorkflowException we) {
            fail("Attempt at routing document in exception routing succeeded, when it should have been an InvalidActionTakenException");
        }
    }

	@Test public void testParallelExceptionRouting() throws Exception {
        WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), "ExceptionRoutingParallelDoc");
        doc.routeDocument("");
        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), doc.getDocumentId());
        assertTrue("User should have an approve request", doc.isApprovalRequested());
        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("bmcgough"), doc.getDocumentId());
        assertTrue("User should have an approve request", doc.isApprovalRequested());
        RouteNodeInstanceDTO[] nodes = new WorkflowInfo().getActiveNodeInstances(doc.getDocumentId());

        // at this point we should be at RouteNode1 and RouteNode3
        assertEquals("There should be two active nodes", 2, nodes.length);
        TestUtilities.assertAtNode(doc, "RouteNode1");
        TestUtilities.assertAtNode(doc, "RouteNode3");

        try {
            doc.approve("");
            fail("should have generated routing exception");
        } catch (Exception e) {
        }

        TestUtilities.getExceptionThreader().join();//this is necessary to ensure that the exception request will be generated.
        WorkflowInfo info = new WorkflowInfo();
        ActionRequestDTO[] actionRequests = info.getActionRequests(doc.getDocumentId());
        RouteNodeInstanceDTO routeNode1 = null;
        for (RouteNodeInstanceDTO nodeInstanceVO : nodes) {
        	if (nodeInstanceVO.getName().equals("RouteNode1")) {
        		routeNode1 = nodeInstanceVO;
        	}
        }
        assertNotNull("Could not locate the routeNode1 node instance.", routeNode1);

        boolean hasCompleteRequest = false;
        for (int i = 0; i < actionRequests.length; i++) {
            ActionRequestDTO actionRequest = actionRequests[i];
            if (actionRequest.isCompleteRequest()) {
            	Group group = KimApiServiceLocator.getIdentityManagementService().getGroup(actionRequest.getGroupId());
                assertTrue("Complete should be requested", actionRequest.isCompleteRequest());
                assertTrue("Request should be a workgroup request", actionRequest.isGroupRequest());
                assertNull("For exception routing, node instance should have a null id.", actionRequest.getNodeInstanceId());
                //assertEquals("Node instance id should be id of routeNode1", routeNode1.getRouteNodeInstanceId(), actionRequest.getNodeInstanceId());
                // routeMethod name should be null as well
                assertNull("Exception request routeMethodName wrong", actionRequest.getRouteMethodName());
                assertEquals("Request should be to 'ExceptionRoutingGroup'", "ExceptionRoutingGroup", group.getName());
                hasCompleteRequest = true;
            }
        }
        assertTrue("Document should have had a complete request", hasCompleteRequest);
        ExplodingRuleAttribute.dontExplode=true;

        //there should be a single action item to our member of the exception workgroup
        Collection actionItems = KEWServiceLocator.getActionListService().findByDocumentId(doc.getDocumentId());
        assertEquals("There should only be action items for the member of our exception workgroup", 1, actionItems.size());

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("user3"), doc.getDocumentId());
        assertTrue("Document should be routing for completion to member of exception workgroup", doc.isCompletionRequested());
        assertTrue("Document should be in exception status", doc.stateIsException());
        doc.complete("");

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("bmcgough"), doc.getDocumentId());
        doc.approve("");

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), doc.getDocumentId());
        doc.approve("");

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), doc.getDocumentId());
        doc.approve("");

        doc = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), doc.getDocumentId());
        doc.approve("");

        assertTrue("Document should be final", doc.stateIsFinal());
    }

    /**
     * this tests that the document appropriately gets to exception routing if there is a
     * problem when transitioning out of first node
     *
     * @throws Exception
     */
    @Test public void testExceptionInTransitionFromStart() throws Exception {

    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "AdhocTransitionTestDocument");
    	//blow chunks transitioning out of adhoc to the first route node
    	ExceptionRoutingTestPostProcessor.THROW_ROUTE_STATUS_LEVEL_EXCEPTION = true;

    	try {
    		doc.routeDocument("");
    		fail("We should be in exception routing");
    	} catch (Exception e) {
    	}

    	TestUtilities.getExceptionThreader().join();//this is necessary to ensure that the exception request will be generated.
    	doc = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), doc.getDocumentId());
    	assertTrue("document should be in exception routing", doc.stateIsException());
    }

    /**
     * Test to verify the fix for KULWF-669.
     *
     * This tests that if we requeue an exception document (through the RouteQueueService) that it doesn't transition
     * out of exception routing.  Then check that, if we complete it, it properly transitions out of exception routing.
     */
    @Test public void testRequeueOfExceptionDocument() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "AdhocTransitionTestDocument");
    	document.routeDocument("");
        assertFalse("Document should not be in exception routing.", document.stateIsException());

        // in fact, at this point it should be routed to jhopf
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), document.getDocumentId());
        assertTrue("Jhopf should have an approve.", document.isApprovalRequested());

        // let's tell it to blow up on level change
        ExceptionRoutingTestPostProcessor.THROW_ROUTE_STATUS_CHANGE_EXCEPTION = true;
        try {
        	document.approve("");
        	fail("We should be in exception routing");
    	} catch (Exception e) {
    	}

    	TestUtilities.waitForExceptionRouting();
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
    	assertTrue("document should be in exception routing", document.stateIsException());

    	// now requeue the document it should stay at exception routing and the status change callback should not
    	// indicate a transition out of exception routing (this is to make sure it's not going out of exception
    	// routing and then right back in)
    	ExceptionRoutingTestPostProcessor.THROW_ROUTE_STATUS_CHANGE_EXCEPTION = false;
    	assertFalse("Should not have transitioned out of exception routing yet.", ExceptionRoutingTestPostProcessor.TRANSITIONED_OUT_OF_EXCEPTION_ROUTING);
    	// the requeue here should happen synchronously because we are using the SynchronousRouteQueue
    	DocumentRouteHeaderValue routeHeaderValue = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentId());
    	QName documentServiceName = new QName(routeHeaderValue.getDocumentType().getServiceNamespace(), MessageServiceNames.DOCUMENT_ROUTING_SERVICE);
    	KSBXMLService routeDocumentMessageService = (KSBXMLService)MessageServiceNames.getServiceAsynchronously(documentServiceName, routeHeaderValue);
    	routeDocumentMessageService.invoke(String.valueOf(document.getDocumentId()));

//    	SpringServiceLocator.getMessageHelper().sendMessage(MessageServiceNames.DOCUMENT_ROUTING_SERVICE, String.valueOf(document.getDocumentId()), routeHeaderValue);

    	// the document should still be in exception routing
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
    	assertTrue("document should be in exception routing", document.stateIsException());
        assertFalse("document shouldn't have transitioned out of exception routing.", ExceptionRoutingTestPostProcessor.TRANSITIONED_OUT_OF_EXCEPTION_ROUTING);

        // now turn status change exceptions off and complete the exception request
        ExceptionRoutingTestPostProcessor.THROW_ROUTE_STATUS_CHANGE_EXCEPTION = false;
        assertTrue("rkirkend should be in the exception workgroup.", document.isCompletionRequested());
        document.complete("Completing out of exception routing.");

        // Note: The behavior here will be a bit different then in a real setting because in these tests the route queue is synchronous so jhopf's original
        // Approve never actually took place because the transaction was rolled back (because of the exception in the post process).  Therefore, we still
        // need to take action as him again to push the document to FINAL
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), document.getDocumentId());
        assertTrue(document.isApprovalRequested());
        document.approve("");

        // document should now be FINAL
        assertTrue("Document should be FINAL.", document.stateIsFinal());

        // the status change out of exception routing should have happened
        assertTrue("Document should have transitioned out of exception routing.", ExceptionRoutingTestPostProcessor.TRANSITIONED_OUT_OF_EXCEPTION_ROUTING);
    }

}
