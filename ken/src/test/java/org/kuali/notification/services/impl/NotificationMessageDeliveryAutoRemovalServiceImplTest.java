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
package org.kuali.notification.services.impl;

import java.util.Collection;
import java.util.HashMap;

import org.apache.ojb.broker.query.Criteria;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.service.NotificationMessageDeliveryAutoRemovalService;
import org.kuali.notification.service.ProcessingResult;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.util.NotificationConstants;

/**
 * Tests NotificationMessageDeliveryAutoRemovalServiceImpl
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore // deadlocks are detected during clear database lifecycle (even when select for update is commented out...)
public class NotificationMessageDeliveryAutoRemovalServiceImplTest extends NotificationTestCaseBase {
    // NOTE: this value is highly dependent on test data 
    private static final int EXPECTED_SUCCESSES = 8;
    
    protected void assertProcessResults() {
        // one error should have occurred and the delivery should have been marked unlocked again
        Criteria criteria = new Criteria();
        criteria.addNotNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE);
        Collection<NotificationMessageDelivery> lockedDeliveries = services.getBusinesObjectDao().findMatching(NotificationMessageDelivery.class, criteria);
        assertEquals(0, lockedDeliveries.size());

        // should be 1 autoremoved delivery
        HashMap<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(NotificationConstants.BO_PROPERTY_NAMES.MESSAGE_DELIVERY_STATUS, NotificationConstants.MESSAGE_DELIVERY_STATUS.AUTO_REMOVED);
        Collection<NotificationMessageDelivery> unprocessedDeliveries = services.getBusinesObjectDao().findMatching(NotificationMessageDelivery.class, queryCriteria);
        assertEquals(EXPECTED_SUCCESSES, unprocessedDeliveries.size());
    }

    /**
     * Test auto-removal message deliveries
     */
    @Test
    @Ignore
    public void testAutoRemovedNotificationMessageDeliveries() {
        NotificationMessageDeliveryAutoRemovalService nSvc = services.getNotificationMessageDeliveryAutoRemovalService();


        services.getNotificationMessageDeliveryResolverService().resolveNotificationMessageDeliveries();
        services.getNotificationMessageDeliveryDispatchService().processUndeliveredNotificationMessageDeliveries();
        ProcessingResult result = nSvc.processAutoRemovalOfDeliveredNotificationMessageDeliveries();
        
        assertEquals(0, result.getFailures().size());
        assertEquals(EXPECTED_SUCCESSES, result.getSuccesses().size());
        
        assertProcessResults();
    }

    /**
     * Test concurrent auto-removal of message deliveries
     */
    @Ignore
    @Test
    public void testAutoRemovalConcurrency() throws InterruptedException {
        final NotificationMessageDeliveryAutoRemovalService nSvc = services.getNotificationMessageDeliveryAutoRemovalService();

        services.getNotificationMessageDeliveryResolverService().resolveNotificationMessageDeliveries();
        services.getNotificationMessageDeliveryDispatchService().processUndeliveredNotificationMessageDeliveries();

        final ProcessingResult[] results = new ProcessingResult[2];
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                results[0] = nSvc.processAutoRemovalOfDeliveredNotificationMessageDeliveries();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                results[1] = nSvc.processAutoRemovalOfDeliveredNotificationMessageDeliveries();
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();

        // assert that ONE of the autoremovers got all the items, and the other got NONE of the items
        LOG.info("Results of thread #1: " + results[0]);
        LOG.info("Results of thread #2: " + results[1]);
        assertTrue((results[0].getSuccesses().size() == EXPECTED_SUCCESSES && results[0].getFailures().size() == 0 && results[1].getSuccesses().size() == 0 && results[1].getFailures().size() == 0) ||
                   (results[1].getSuccesses().size() == EXPECTED_SUCCESSES && results[1].getFailures().size() == 0 && results[0].getSuccesses().size() == 0 && results[0].getFailures().size() == 0));
        
        assertProcessResults();
    }
}