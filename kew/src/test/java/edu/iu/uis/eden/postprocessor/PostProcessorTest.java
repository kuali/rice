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
package edu.iu.uis.eden.postprocessor;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;

public class PostProcessorTest extends KEWTestCase {

	private static final String APPLICATION_CONTENT = "<some><application>content</application></some>";
	private static final String DOC_TITLE = "The Doc Title";
	
	protected void loadTestData() throws Exception {
        loadXmlFile("PostProcessorConfig.xml");
    }
	
	/**
	 * Tests that modifying a document in the post processor works.  This test will do a few things:
	 * 
	 * 1) Change the document content in the post processor
	 * 2) Send an app specific FYI request to the initiator of the document
	 * 3) Modify the document title.
	 * 
	 * This test is meant to expose the bug KULWF-668 where it appears an OptimisticLockException is
	 * being thrown after returning from the EPIC post processor.
	 */
	@Test public void testModifyDocumentInPostProcessor() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "testModifyDocumentInPostProcessor");
		document.saveDocument("");
		assertEquals("application content should be empty initially", "", document.getApplicationContent());
		assertNull("Doc title should be empty initially", document.getTitle());
		
		// now route the document, it should through a 2 nodes, then go PROCESSED then FINAL
		document.routeDocument("");
		assertEquals("Should have transitioned nodes twice", 2, DocumentModifyingPostProcessor.levelChanges);
		assertTrue("SHould have called the processed status change", DocumentModifyingPostProcessor.processedChange);
		assertTrue("Document should be final.", document.stateIsFinal());
		assertEquals("Application content should have been sucessfully modified.", APPLICATION_CONTENT, document.getApplicationContent());
				
		// check that the title was modified successfully
		assertEquals("Wrong doc title", DOC_TITLE, document.getTitle());
		
		// check that the document we routed from the post processor exists
		assertNotNull("SHould have routed a document from the post processor.", DocumentModifyingPostProcessor.routedDocumentId);
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), DocumentModifyingPostProcessor.routedDocumentId);
		assertTrue("document should be enroute", document.stateIsEnroute());
		assertEquals("Document should have 1 pending request.", 1, KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId()).size());
		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		document.approve("");
		assertTrue("Document should be final.", document.stateIsFinal());
	}
	
	public static class DocumentModifyingPostProcessor extends DefaultPostProcessor {

		public static boolean processedChange = false;
		public static int levelChanges = 0;
		public static Long routedDocumentId;
		
		public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
			if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
				WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), statusChangeEvent.getRouteHeaderId());
				document.setApplicationContent(APPLICATION_CONTENT);
				document.setTitle(DOC_TITLE);
				document.saveRoutingData();
				// now route another document from the post processor, sending it an adhoc request
				WorkflowDocument ppDocument = new WorkflowDocument(new NetworkIdVO("user1"), "testModifyDocumentInPostProcessor");
				routedDocumentId = ppDocument.getRouteHeaderId();
				ppDocument.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "", new NetworkIdVO("ewestfal"), "", true);
				ppDocument.routeDocument("");
				processedChange = true;
			}
			return new ProcessDocReport(true);
		}

		public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
			levelChanges++;
			WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), levelChangeEvent.getRouteHeaderId());
			document.setTitle("Current level change: " + levelChanges);
			document.saveRoutingData();
			return new ProcessDocReport(true);
		}
		
		
		
		
		
	}
	
}
