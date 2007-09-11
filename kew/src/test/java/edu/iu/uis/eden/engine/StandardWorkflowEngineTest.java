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
package edu.iu.uis.eden.engine;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.JavaServiceDefinition;
import edu.iu.uis.eden.messaging.KEWJavaService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.messaging.exceptionhandling.DocumentMessageExceptionHandler;
import edu.iu.uis.eden.postprocessor.DefaultPostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.test.TestUtilities;

public class StandardWorkflowEngineTest extends KEWTestCase {

	protected void loadTestData() throws Exception {
		loadXmlFile("EngineConfig.xml");
	}

	/**
	 * Tests that the proper state is set up on the root branch in the document
	 * to indicate that both PROCESSED and FINAL callbacks have been made into
	 * the post processor.
	 */
	@Test public void testSystemBranchState() throws Exception {
		// route the document to final
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "SimpleDocType");
		document.routeDocument("");
		assertTrue("Document should be final.", document.stateIsFinal());

		// now look at the branch state
		DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
		RouteNodeInstance nodeInstance = (RouteNodeInstance) routeHeader.getInitialRouteNodeInstance(0);
		BranchState processedBranchState = nodeInstance.getBranch().getBranchState(EdenConstants.POST_PROCESSOR_PROCESSED_KEY);
		BranchState finalBranchState = nodeInstance.getBranch().getBranchState(EdenConstants.POST_PROCESSOR_FINAL_KEY);
		assertNotNull(processedBranchState);
		assertNotNull(finalBranchState);
		assertEquals("true", processedBranchState.getValue());
		assertEquals("true", finalBranchState.getValue());
		assertEquals(1, TestPostProcessor.processedCount);
		assertEquals(1, TestPostProcessor.finalCount);
	}

	/**
	 * Tests that a FINAL document can go into exception routing and recover
	 * properly while only calling the PROCESSED and FINAL callbacks once.
	 */
	@Test public void testFinalDocumentExceptionRoutingRecovery() throws Exception {

		// route the document to final
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "SimpleDocType");
		document.routeDocument("");
		assertTrue("Document should be final.", document.stateIsFinal());
		assertEquals(1, TestPostProcessor.processedCount);
		assertEquals(1, TestPostProcessor.finalCount);

		// now queue up an exploder which should push the document into
		// exception routing
		JavaServiceDefinition serviceDef = new JavaServiceDefinition();
		serviceDef.setPriority(new Integer(1));
		serviceDef.setQueue(true);
		serviceDef.setRetryAttempts(0);
		serviceDef.setServiceInterface(KEWJavaService.class.getName());
		serviceDef.setServiceName(new QName("KEW", "exploader"));
		serviceDef.setService(new ImTheExploderProcessor());

		serviceDef.setMessageExceptionHandler(DocumentMessageExceptionHandler.class.getName());
		serviceDef.validate();
		KSBServiceLocator.getServiceDeployer().registerService(serviceDef, true);

		KEWJavaService exploderAsService = (KEWJavaService) MessageServiceNames.getServiceAsynchronously(new QName("KEW", "exploader"), KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId()));
		exploderAsService.invoke("");
		// we need to make the exploder a service to get this going again...
		// SpringServiceLocator.getRouteQueueService().requeueDocument(document.getRouteHeaderId(),
		// ImTheExploderProcessor.class.getName());
		// fail("Should have exploded!!!");
		TestUtilities.waitForExceptionRouting();

		// the document should be in exception routing now
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertTrue("Document should be in exception routing.", document.stateIsException());
		assertEquals(1, TestPostProcessor.processedCount);
		assertEquals(1, TestPostProcessor.finalCount);

		assertTrue("ewestfal should have a complete request.", document.isCompletionRequested());
		document.complete("");

		// the document should be final once again
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertTrue("Document should be final.", document.stateIsFinal());
		assertEquals(1, TestPostProcessor.processedCount);
		assertEquals(1, TestPostProcessor.finalCount);
	}

	public void tearDown() throws Exception {
	    try {
		TestPostProcessor.resetProcessedCount();
		TestPostProcessor.resetFinalCount();
	    } finally {
		super.tearDown();
	    }
	}

	public static class TestPostProcessor extends DefaultPostProcessor {

		public static int finalCount = 0;

		public static int processedCount = 0;

		public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
			if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
				processedCount++;
			} else if (EdenConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus())) {
				finalCount++;
			}
			return new ProcessDocReport(true);
		}

		public static void resetProcessedCount() {
			processedCount = 0;
		}

		public static void resetFinalCount() {
			finalCount = 0;
		}
	}

	public static class ImTheExploderProcessor implements KEWJavaService {

		public void invoke(Serializable payLoad) {
			throw new WorkflowRuntimeException("I'm the Exploder!!!");
		}

	}

}
