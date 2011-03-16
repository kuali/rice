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
package org.kuali.rice.kew.postprocessor;

import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.test.BaselineTestCase;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
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
        XMLUnit.setIgnoreWhitespace(true);
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "testModifyDocumentInPostProcessor");
		document.saveDocument("");
		assertEquals("application content should be empty initially", "", document.getApplicationContent());
		assertNull("Doc title should be empty initially", document.getTitle());
		
		// now route the document, it should through a 2 nodes, then go PROCESSED then FINAL
		document.routeDocument("");
		assertEquals("Should have transitioned nodes twice", 2, DocumentModifyingPostProcessor.levelChanges);
		assertTrue("SHould have called the processed status change", DocumentModifyingPostProcessor.processedChange);
		assertTrue("Document should be final.", document.stateIsFinal());
		XMLAssert.assertXMLEqual("Application content should have been sucessfully modified.", APPLICATION_CONTENT, document.getApplicationContent());
				
		// check that the title was modified successfully
		assertEquals("Wrong doc title", DOC_TITLE, document.getTitle());
		
		// check that the document we routed from the post processor exists
		assertNotNull("SHould have routed a document from the post processor.", DocumentModifyingPostProcessor.routedDocumentId);
		document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), DocumentModifyingPostProcessor.routedDocumentId);
		assertTrue("document should be enroute", document.stateIsEnroute());
		assertEquals("Document should have 1 pending request.", 1, KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId()).size());
		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		document.approve("");
		assertTrue("Document should be final.", document.stateIsFinal());
	}
	/**
     * Tests that modifying a document in the post processor works.  This test will do a few things:
     * 
     * 1) Change the document content in the post processor
     * 2) Send an app specific FYI request to the initiator of the document
     * 3) Modify the document title.
     * 
     * This test is meant to test that an empty post processor works.  The empty post processor should be handled as a DefaultPostProcessor.
     */
    
	@Test public void testEmptyPostProcessor() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "testEmptyPostProcessor");
        document.saveDocument("");
        assertEquals("application content should be empty initially", "", document.getApplicationContent());
        assertNull("Doc title should be empty initially", document.getTitle());
        
        assertTrue("Document should be final.", document.stateIsFinal());
               
        DocumentType testEmptyDocType = KEWServiceLocator.getDocumentTypeService().findByName("testEmptyPostProcessor");
        assertTrue("Post Processor should be set to 'none'",  StringUtils.equals("none", testEmptyDocType.getPostProcessorName()));
        assertTrue("Post Processor should be of type DefaultPostProcessor", testEmptyDocType.getPostProcessor() instanceof org.kuali.rice.kew.postprocessor.DefaultPostProcessor);
    }
    
	private static boolean shouldReturnDocumentIdsToLock = false;
	private static Long documentAId = null;
	private static Long documentBId = null;
	private static UpdateDocumentThread updateDocumentThread = null;
	
	protected String getPrincipalIdForName(String principalName) {
        return KEWServiceLocator.getIdentityHelperService()
                .getIdForPrincipalName(principalName);
    }
	/**
	 * Tests the locking of additional documents from the Post Processor.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	@Test public void testGetDocumentIdsToLock() throws Exception {
		
		/**
		 * Let's recreate the original optimistic lock scenario that caused this issue to crop up, essentially:
		 * 
		 * 1) Thread one locks and processes document A in the workflow engine
		 * 2) Thread one loads document B
		 * 3) Thread two locks and processes document B from document A's post processor, doing an update which increments the version number of document B
		 * 4) Thread A attempts to update document B and gets an optimistic lock exception 
		 */
		
		WorkflowDocument documentB = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
		documentB.saveDocument("");
		documentBId = documentB.getRouteHeaderId();
		updateDocumentThread = new UpdateDocumentThread(documentBId);
		
		// this is the document with the post processor
		WorkflowDocument documentA = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "testGetDocumentIdsToLock");
		documentA.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "", getPrincipalIdForName("rkirkend"), "", true);
		
		try {
			documentA.routeDocument(""); // this should trigger our post processor and our optimistic lock exception
			fail("An exception should have been thrown as the result of an optimistic lock!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * Now let's try the same thing again, this time returning document B's id as a document to lock, the error should not happen this time
		 */
		
		shouldReturnDocumentIdsToLock = true;
		
		documentB = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
		documentB.saveDocument("");
		documentBId = documentB.getRouteHeaderId();
		updateDocumentThread = new UpdateDocumentThread(documentBId);
		
		// this is the document with the post processor
		documentA = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "testGetDocumentIdsToLock");
		documentA.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "", getPrincipalIdForName("rkirkend"), "", true);
		
		documentA.routeDocument(""); // this should trigger our post processor and our optimistic lock exception
		documentA = new WorkflowDocument(getPrincipalIdForName("rkirkend"), documentA.getRouteHeaderId());
		assertTrue("rkirkend should have approve request", documentA.isApprovalRequested());
		
	}
	
	public static class DocumentModifyingPostProcessor extends DefaultPostProcessor {

		public static boolean processedChange = false;
		public static int levelChanges = 0;
		public static Long routedDocumentId;
		
		protected String getPrincipalIdForName(String principalName) {
	        return KEWServiceLocator.getIdentityHelperService()
	                .getIdForPrincipalName(principalName);
	    }
		
		public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
			if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
				WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), statusChangeEvent.getRouteHeaderId());
				document.setApplicationContent(APPLICATION_CONTENT);
				document.setTitle(DOC_TITLE);
				document.saveRoutingData();
				// now route another document from the post processor, sending it an adhoc request
				WorkflowDocument ppDocument = new WorkflowDocument(getPrincipalIdForName("user1"), "testModifyDocumentInPostProcessor");
				routedDocumentId = ppDocument.getRouteHeaderId();
				// principal id 1 = ewestfal
				ppDocument.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "", "2001", "", true);
				ppDocument.routeDocument("");
				processedChange = true;
			}
			return new ProcessDocReport(true);
		}

		public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
			levelChanges++;
			WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), levelChangeEvent.getRouteHeaderId());
			document.setTitle("Current level change: " + levelChanges);
			document.saveRoutingData();
			return new ProcessDocReport(true);
		}
		
	}
	
	public static class GetDocumentIdsToLockPostProcessor extends DefaultPostProcessor {

	    protected String getPrincipalIdForName(String principalName) {
	        return KEWServiceLocator.getIdentityHelperService()
	                .getIdForPrincipalName(principalName);
	    }
	    
		@Override
		public List<Long> getDocumentIdsToLock(DocumentLockingEvent lockingEvent) throws Exception {
			WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), lockingEvent.getRouteHeaderId());
			if (shouldReturnDocumentIdsToLock) {
				List<Long> docIds = new ArrayList<Long>();
				docIds.add(documentBId);
				return docIds;
			}
			return null;
		}

		@Override
		public ProcessDocReport afterProcess(AfterProcessEvent event) throws Exception {
			WorkflowDocument wfDocument = new WorkflowDocument(getPrincipalIdForName("ewestfal"), event.getRouteHeaderId());
			if (wfDocument.stateIsEnroute()) {
				// first, let's load document B in this thread
				DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentBId);
				// now let's execute the thread
				new Thread(updateDocumentThread).start();
				// let's wait for a few seconds to either let the thread process or let it aquire the lock
				Thread.sleep(5000);
				// now update document B
				KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
			}
			return super.afterProcess(event);
		}
		
		
		
	}
	
	/**
	 * A Thread which simply locks and updates the document
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	private class UpdateDocumentThread implements Runnable {
		private Long documentId;
		public UpdateDocumentThread(Long documentId) {
			this.documentId = documentId;
		}
		public void run() {
			TransactionTemplate template = new TransactionTemplate(KEWServiceLocator.getPlatformTransactionManager());
			template.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
					DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
					KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
					return null;
				}
			});
		}
	}
	
}
