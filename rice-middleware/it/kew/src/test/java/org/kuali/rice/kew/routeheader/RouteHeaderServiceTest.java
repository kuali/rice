/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader;

import org.junit.Test;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.test.BaselineTestCase;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class RouteHeaderServiceTest extends KEWTestCase {

    private RouteHeaderService routeHeaderService;

    protected void setUpAfterDataLoad() throws Exception {
        super.setUpAfterDataLoad();
        routeHeaderService = KEWServiceLocator.getRouteHeaderService();
    }
    
    /**
     * Tests the saving of a document with large XML content.  This verifies that large CLOBs (> 4000 bytes)
     * can be saved by OJB.  This can cause paticular issues with Oracle and OJB has to unwrap the native jdbc
     * Connections and Statements from the pooled connection.  We need to make sure this is working for our
     * pooling software of choice.
     */
    @Test
    public void testLargeDocumentContent() throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<content>");
        for (int index = 0; index < 10000; index++) {
            buffer.append("abcdefghijklmnopqrstuvwxyz");
        }
        buffer.append("</content>");
        DocumentRouteHeaderValue document = new DocumentRouteHeaderValue();
        document.setDocContent(buffer.toString());
        document.setDocRouteStatus(KewApiConstants.ROUTE_HEADER_INITIATED_CD);
        document.setDocRouteLevel(0);
        document.setDateModified(new Timestamp(System.currentTimeMillis()));
        document.setCreateDate(new Timestamp(System.currentTimeMillis()));
        document.setInitiatorWorkflowId("1");
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName("TestDocumentType");
        assertNotNull(documentType);
        document.setDocumentTypeId(documentType.getDocumentTypeId());
        document = routeHeaderService.saveRouteHeader(document);
        assertNotNull("Document was saved, it should have an ID now.", document.getDocumentId());
        
        // now reload from database and verify it's the right size
        document = routeHeaderService.getRouteHeader(document.getDocumentId());
        String docContent = document.getDocContent();
        assertEquals("Doc content should be the same size as original string buffer.", buffer.length(), docContent.length());
        assertTrue("Should be greater than about 5000 bytes.", docContent.getBytes().length > 5000);
    }

    @Test public void testGetApplicationIdByDocumentId() throws Exception {
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType2");
    	String documentId = document.getDocumentId();
    	String applicationId = routeHeaderService.getApplicationIdByDocumentId(documentId);
    	assertEquals("applicationId should be KEWNEW", "KEWNEW", applicationId);

    	// now check TestDocumentType
    	document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestDocumentType");
    	documentId = document.getDocumentId();
    	applicationId = routeHeaderService.getApplicationIdByDocumentId(documentId);
    	assertEquals("applicationId should be KUALI", "KUALI", applicationId);
    }

    @Test public void testLockRouteHeader() throws Exception {

        long timeout = 60 * 1000;

    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("rkirkend"), "TestDocumentType");
    	document.saveDocumentData();
    	String documentId = document.getDocumentId();

        final Locker locker1 = new Locker(documentId);
        locker1.start();
        locker1.latch1.await(timeout, TimeUnit.MILLISECONDS);

        // the locker show now be waiting on the second latch
        assertTrue(locker1.waiting);
        assertFalse(locker1.completed);

        // now start a second locker thread to attempt to lock the document as well, it should end up getting blocked
        // from locking
        final Locker locker2 = new Locker(documentId);
        locker2.start();

        // the thread has been started, let's give it a little bit of time to attempt to lock the doc, it should get
        // blocked in the select ... for update
        Thread.sleep(2000);

        // this locker should essentially be blocked on the call to lock the route header
        assertTrue(locker2.prelock);
        assertFalse(locker2.waiting);

        // now, release the first lock
        locker1.latch2.countDown();
        locker1.join(timeout);

        // at this point locker1 should have completed
        assertTrue(locker1.completed);

        // give locker2 a little bit of time to finish it's lock on the route header and proceed to it's wait
        Thread.sleep(2000);
        locker2.latch2.countDown();
        locker2.join(timeout);

        // locker 2 should be completed as well
        assertTrue(locker2.completed);

    }

    private class Locker extends Thread {

        private static final long TIMEOUT = 60 * 1000;

        String documentId;
        CountDownLatch latch1;
        CountDownLatch latch2;

        volatile boolean prelock;
        volatile boolean waiting;
        volatile boolean completed;

        Locker(String documentId) {
            this.documentId = documentId;
            this.latch1 = new CountDownLatch(1);
            this.latch2 = new CountDownLatch(1);
        }

        public void run() {
            getTransactionTemplate().execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    prelock = true;
                    routeHeaderService.lockRouteHeader(documentId);
                    try {
                        waiting = true;
                        latch1.countDown();
                        latch2.await(TIMEOUT, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                         throw new RuntimeException("Shouldn't have been interrupted but was.", e);
                    }
                    return null;
                }
            });
            completed = true;
        }
    }

}
