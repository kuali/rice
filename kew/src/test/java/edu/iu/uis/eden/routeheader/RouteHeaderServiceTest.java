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
package edu.iu.uis.eden.routeheader;


import org.junit.Test;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.workflow.test.KEWTestCase;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.LockingException;

public class RouteHeaderServiceTest extends KEWTestCase {

    private Object lock = new Object();
    private RouteHeaderService routeHeaderService;

    protected void setUpTransaction() throws Exception {
        super.setUpTransaction();
        routeHeaderService = KEWServiceLocator.getRouteHeaderService();
    }

    @Test public void testGetMessageEntityByDocumentId() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "TestDocumentType2");
    	Long documentId = document.getRouteHeaderId();
    	String messageEntity = routeHeaderService.getMessageEntityByDocumentId(documentId);
    	assertEquals("Message entity should be KEWNEW", "KEWNEW", messageEntity);

    	// now check TestDocumentType
    	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "TestDocumentType");
    	documentId = document.getRouteHeaderId();
    	messageEntity = routeHeaderService.getMessageEntityByDocumentId(documentId);
    	assertEquals("Message entity should be KEW", "KEW", messageEntity);
    }

    @Test public void testLockRouteHeader() throws Exception {
	//fail("TestLockRouteHeader needs to be fixed.  It is currently deadlocking the tests!!!!");
    	if (Core.getRootConfig().getProperty("datasource.ojb.platform").equals("Mckoi")) {
    		return;
    	}

    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
    	document.saveRoutingData();
    	final Long documentId = document.getRouteHeaderId();
        Locker locker = null;
        synchronized (lock) {
            locker = new Locker(documentId);
            locker.start();
            lock.wait();
        }
        // document should be locked by the other thread at this point
        try {
            routeHeaderService.lockRouteHeader(documentId, false);
            fail("The route header should be locked.");
        } catch (LockingException e) {
            // should have been thrown!
        }
        synchronized (lock) {
            lock.notify();
        }
        locker.join();
        // document should be unlocked now
        routeHeaderService.lockRouteHeader(documentId, false);
        assertTrue("Locker thread should have completed.", locker.isCompleted());

        // now configure a lock timeout for 2 seconds
        Core.getCurrentContextConfig().overrideProperty(Config.DOCUMENT_LOCK_TIMEOUT, "2");
        synchronized (lock) {
            locker = new Locker(documentId);
            locker.start();
            lock.wait();
        }
        // document should be locked by the other thread at this point
        long millisStart = System.currentTimeMillis();
        try {
        	routeHeaderService.lockRouteHeader(documentId, true);
        	fail("The route header should be locked.");
        }  catch (LockingException e) {
        	// should have been thrown!
        }
        long millisEnd = System.currentTimeMillis();
        long timeLocked = (millisEnd - millisStart);
        // assert that the time locked was close to 2 seconds += .25 of a second
        assertTrue("Time locked should have been around 2 seconds but was " + timeLocked, timeLocked > (2000-250) && timeLocked < (2000+250));

        synchronized(lock) {
        	lock.notify();
        }
        locker.join();

        // document should be unlocked again
        routeHeaderService.lockRouteHeader(document.getRouteHeaderId(), false);
        assertTrue("Locker thread should have completed.", locker.isCompleted());
    }

    private class Locker extends Thread {
        private Long documentId;
        private boolean isCompleted = false;
        public Locker(Long documentId) {
            this.documentId = documentId;
        }
        public void run() {
            getTransactionTemplate().execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                	synchronized (lock) {
                        routeHeaderService.lockRouteHeader(documentId, true);
                        try {
                            lock.notify();
                            lock.wait();
                        } catch (InterruptedException e) {
                            fail("Shouldn't have been interrupted");
                        }
                    }
                    return null;
                }
            });
            isCompleted = true;
        }
        public boolean isCompleted() {
            return isCompleted;
        }
    }

}
