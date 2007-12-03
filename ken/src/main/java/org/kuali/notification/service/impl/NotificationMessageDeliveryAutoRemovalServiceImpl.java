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
package org.kuali.notification.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.deliverer.BulkNotificationMessageDeliverer;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.NotificationAutoRemoveException;
import org.kuali.notification.service.NotificationMessageDelivererRegistryService;
import org.kuali.notification.service.NotificationMessageDeliveryAutoRemovalService;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.service.ProcessingResult;
import org.kuali.notification.util.NotificationConstants;
import org.springframework.transaction.PlatformTransactionManager;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 * Auto removes expired message deliveries.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDeliveryAutoRemovalServiceImpl extends ConcurrentJob<NotificationMessageDelivery> implements NotificationMessageDeliveryAutoRemovalService {
    private BusinessObjectDao businessObjectDao;
    private NotificationMessageDelivererRegistryService messageDeliveryRegistryService;
    private NotificationMessageDeliveryService messageDeliveryService;

    /**
     * Constructs a NotificationMessageDeliveryDispatchServiceImpl instance.
     * @param businessObjectDao
     * @param txManager
     * @param executor
     * @param messageDeliveryRegistryService
     */
    public NotificationMessageDeliveryAutoRemovalServiceImpl(BusinessObjectDao businessObjectDao, PlatformTransactionManager txManager, 
	    ExecutorService executor, NotificationMessageDeliveryService messageDeliveryService, NotificationMessageDelivererRegistryService messageDeliveryRegistryService) {
        super(txManager, executor);
        this.messageDeliveryService = messageDeliveryService;
        this.businessObjectDao = businessObjectDao;
        this.messageDeliveryRegistryService = messageDeliveryRegistryService;
    }

    /**
     * @see org.kuali.notification.service.impl.ConcurrentJob#takeAvailableWorkItems()
     */
    @Override
    protected Collection<NotificationMessageDelivery> takeAvailableWorkItems() {
        return messageDeliveryService.takeMessageDeliveriesForAutoRemoval();
    }

    /**
     * @see org.kuali.notification.service.impl.ConcurrentJob#processWorkItem(java.lang.Object)
     */
    @Override
    protected Collection<String> processWorkItems(Collection<NotificationMessageDelivery> messageDeliveries) {
        NotificationMessageDeliverer messageDeliverer = null;
        NotificationMessageDelivery firstMessageDelivery = messageDeliveries.iterator().next();

	// get our hands on the appropriate NotificationMessageDeliverer instance
	messageDeliverer = messageDeliveryRegistryService.getDeliverer(firstMessageDelivery);
	if (messageDeliverer == null) {
	    throw new RuntimeException("Message deliverer could not be obtained");
	}
        if (messageDeliveries.size() > 1) {
            // this is a bulk deliverer, so we need to batch the NotificationMessageDeliveries
            if (messageDeliverer instanceof BulkNotificationMessageDeliverer) {
                throw new RuntimeException("Discrepency in autoremove service: deliverer for list of message deliveries is not a BulkNotificationMessageDeliverer");
            }
            return bulkAutoRemove((BulkNotificationMessageDeliverer) messageDeliverer, messageDeliveries);
        } else {
            return autoRemove(messageDeliverer, firstMessageDelivery);
        }
    }

    /**
     * Auto-removes a single message delivery
     * @param messageDeliverer the message deliverer
     * @param messageDelivery the message delivery to auto-remove
     * @return collection of strings indicating successful auto-removals
     */
    protected Collection<String> autoRemove(NotificationMessageDeliverer messageDeliverer, NotificationMessageDelivery messageDelivery) {
        List<String> successes = new ArrayList<String>(1);

        // we have our message deliverer, so tell it to auto remove the message
        try {
            messageDeliverer.autoRemoveMessageDelivery(messageDelivery);
            LOG.debug("Auto-removal of message delivery '" + messageDelivery.getId() + "' for notification '" + messageDelivery.getNotification().getId() + "' was successful.");
            successes.add("Auto-removal of message delivery '" + messageDelivery.getId() + "' for notification '" + messageDelivery.getNotification().getId() + "' was successful.");
        } catch (NotificationAutoRemoveException nmde) {
            LOG.error("Error auto-removing message " + messageDelivery);
            throw new RuntimeException(nmde);
        }
        
        // unlock item
        // now update the status of the delivery message instance to AUTO_REMOVED and persist
        markAutoRemoved(messageDelivery);

        return successes;
    }

    /**
     * Bulk-auto-removes a collection of message deliveries
     * @param messageDeliverer the message deliverer
     * @param messageDeliveries the message deliveries to bulk auto-remove
     * @return collection of strings indicating successful auto-removals
     */
    protected Collection<String> bulkAutoRemove(BulkNotificationMessageDeliverer messageDeliverer, Collection<NotificationMessageDelivery> messageDeliveries) {
     // we have our message deliverer, so tell it to deliver the message
        try {
            messageDeliverer.autoRemoveMessageDelivery(messageDeliveries);
        } catch (NotificationAutoRemoveException nmare) {
            LOG.error("Error bulk-auto-removing messages " + messageDeliveries, nmare);
            throw new RuntimeException(nmare);
        }

        // by definition we have succeeded at this point if no exception was thrown by the messageDeliverer
        // so update the status of the delivery message instance to DELIVERED (and unmark as taken)
        // and persist
        List<String> successes = new ArrayList<String>(messageDeliveries.size());
        for (NotificationMessageDelivery nmd: messageDeliveries) {
            LOG.debug("Auto-removal of message delivery '" + nmd.getId() + "' for notification '" + nmd.getNotification().getId() + "' was successful.");
            successes.add("Auto-removal of message delivery '" + nmd.getId() + "' for notification '" + nmd.getNotification().getId() + "' was successful.");
            markAutoRemoved(nmd);
        }

        return successes;
    }

    /**
     * Marks a MessageDelivery as having been auto-removed, and unlocks it
     * @param messageDelivery the messageDelivery instance to mark
     */
    protected void markAutoRemoved(NotificationMessageDelivery messageDelivery) {
        messageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.AUTO_REMOVED);
        // mark as unlocked
        messageDelivery.setLockedDate(null);
        businessObjectDao.save(messageDelivery);
    }

    /**
     * @see org.kuali.notification.service.impl.ConcurrentJob#unlockWorkItem(java.lang.Object)
     */
    @Override
    protected void unlockWorkItem(NotificationMessageDelivery delivery) {
        messageDeliveryService.unlockMessageDelivery(delivery);
    }

    /**
     * This implementation looks up all UNDELIVERED/DELIVERED message deliveries with an autoRemoveDateTime <= current date time and then iterates 
     * over each to call the appropriate functions to do the "auto-removal" by "canceling" each associated notification 
     * workflow document.
     * @see org.kuali.notification.service.NotificationMessageDeliveryDispatchService#processAutoRemovalOfDeliveredNotificationMessageDeliveries()
     */
    public ProcessingResult processAutoRemovalOfDeliveredNotificationMessageDeliveries() {
        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] STARTING NOTIFICATION AUTO-REMOVAL PROCESSING");

        ProcessingResult result = run();
        
        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] FINISHED NOTIFICATION AUTO-REMOVAL PROCESSING - Successes = " + result.getSuccesses().size() + ", Failures = " + result.getFailures().size());

        return result;
    }
}
