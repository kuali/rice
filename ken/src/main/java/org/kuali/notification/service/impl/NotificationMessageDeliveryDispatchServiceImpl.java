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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.deliverer.BulkNotificationMessageDeliverer;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.NotificationMessageDeliveryException;
import org.kuali.notification.service.NotificationMessageDelivererRegistryService;
import org.kuali.notification.service.NotificationMessageDeliveryDispatchService;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.service.ProcessingResult;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.notification.util.PerformanceLog;
import org.springframework.transaction.PlatformTransactionManager;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 * This is the default out-of-the-box implementation that leverages KEW to process and deliver 
 * notification messages.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDeliveryDispatchServiceImpl extends ConcurrentJob<NotificationMessageDelivery> implements NotificationMessageDeliveryDispatchService {
    private BusinessObjectDao businessObjectDao;
    private NotificationMessageDelivererRegistryService messageDelivererRegistryService;
    private NotificationMessageDeliveryService messageDeliveryService;

    /**
     * Constructs a NotificationMessageDeliveryDispatchServiceImpl instance.
     * @param businessObjectDao
     * @param txManager
     * @param executor
     * @param messageDelivererRegistryService
     */
    public NotificationMessageDeliveryDispatchServiceImpl(BusinessObjectDao businessObjectDao, PlatformTransactionManager txManager, 
	    ExecutorService executor, NotificationMessageDeliveryService messageDeliveryService, NotificationMessageDelivererRegistryService messageDelivererRegistryService) {
        super(txManager, executor);
        this.businessObjectDao = businessObjectDao;
        this.messageDeliveryService = messageDeliveryService;
        this.messageDelivererRegistryService = messageDelivererRegistryService;
    }

    /**
     * This method is responsible for atomically finding all untaking messagedeliveries, marking them as taken
     * and returning them to the caller for processing.
     * @return a list of available message deliveries that have been marked as taken by the caller
     */
    @Override
    protected Collection<NotificationMessageDelivery> takeAvailableWorkItems() {
        return messageDeliveryService.takeMessageDeliveriesForDispatch();
    }

    /**
     * Group work items by deliverer and notification, so that deliveries to bulk deliverers are grouped
     * by notification
     * @see org.kuali.notification.service.impl.ConcurrentJob#groupWorkItems(java.util.Collection)
     */
    @Override
    protected Collection<Collection<NotificationMessageDelivery>> groupWorkItems(Collection<NotificationMessageDelivery> workItems, ProcessingResult result) {
        Collection<Collection<NotificationMessageDelivery>> groupedWorkItems = new ArrayList<Collection<NotificationMessageDelivery>>(workItems.size());

        Map<String, Collection<NotificationMessageDelivery>> bulkWorkUnits = new HashMap<String, Collection<NotificationMessageDelivery>>();
        for (NotificationMessageDelivery messageDelivery: workItems) {
            
            NotificationMessageDeliverer deliverer = messageDelivererRegistryService.getDeliverer(messageDelivery);
            if (deliverer == null) {
                LOG.error("Error obtaining message deliverer for message delivery: " + messageDelivery);
                result.addFailure("Error obtaining message deliverer for message delivery: " + messageDelivery);
                unlockWorkItemAtomically(messageDelivery);
                continue;
            }

            if (deliverer instanceof BulkNotificationMessageDeliverer) {
                // group by bulk-deliverer+notification combo
                String key = messageDelivery.getMessageDeliveryTypeName() + ":" + messageDelivery.getNotification().getId();
                Collection<NotificationMessageDelivery> workUnit = bulkWorkUnits.get(key);
                if (workUnit == null) {
                    workUnit = new LinkedList<NotificationMessageDelivery>();
                    bulkWorkUnits.put(key, workUnit);
                }
                workUnit.add(messageDelivery);
            } else {
                ArrayList<NotificationMessageDelivery> l = new ArrayList<NotificationMessageDelivery>(1);
                l.add(messageDelivery);
                groupedWorkItems.add(l);
            }
        }
        
        return groupedWorkItems;
    }

    /**
     * This method is responsible for atomically delivering a single message delivery, based on that message delivery's specified
     * delivery type.  If the delivery succeeds, then the message delivery is marked as delivered (and untaken) and the change is committed.  If the
     * delivery fails then an exception is thrown and the transaction is rolled back.  Note, the only database work that is actually done
     * is only done if the delivery succeeds in the first place, so there really isn't anything to "roll back" per se on the part of
     * the notification system, unless the delivery type implementation itself participates in the transaction (in which case presumably whatever
     * work it did would not get committed).
     * @param messageDelivery the NotificationMessageDelivery to deliver
     */
    @Override
    protected Collection<?> processWorkItems(Collection<NotificationMessageDelivery> messageDeliveries) {
	NotificationMessageDelivery firstMessageDelivery = messageDeliveries.iterator().next();
	// get our hands on the appropriate NotificationMessageDeliverer instance
	NotificationMessageDeliverer messageDeliverer = messageDelivererRegistryService.getDeliverer(firstMessageDelivery);
	if (messageDeliverer == null) {
	    throw new RuntimeException("Message deliverer could not be obtained");
	}

	if (messageDeliveries.size() > 1) {
	    // this is a bulk deliverer, so we need to batch the NotificationMessageDeliveries
	    if (messageDeliverer instanceof BulkNotificationMessageDeliverer) {
	        throw new RuntimeException("Discrepency in dispatch service: deliverer for list of message deliveries is not a BulkNotificationMessageDeliverer");
	    }
	    return bulkDeliver((BulkNotificationMessageDeliverer) messageDeliverer, messageDeliveries);
	} else {
	    return deliver(messageDeliverer, firstMessageDelivery);
	}
    }

    /**
     * Implements delivery of a single NotificationMessageDelivery
     * @param deliverer the deliverer
     * @param messageDelivery the delivery
     * @return collection of strings indicating successful deliveries
     */
    protected Collection<String> deliver(NotificationMessageDeliverer messageDeliverer, NotificationMessageDelivery messageDelivery) {
        // we have our message deliverer, so tell it to deliver the message
        try {
            messageDeliverer.deliverMessage(messageDelivery);
        } catch (NotificationMessageDeliveryException nmde) {
            LOG.error("Error delivering message " + messageDelivery, nmde);
            throw new RuntimeException(nmde);
        }

        // by definition we have succeeded at this point if no exception was thrown by the messageDeliverer
        // so update the status of the delivery message instance to DELIVERED (and unmark as taken)
        // and persist
        markDelivered(messageDelivery);

        LOG.debug("Message delivery '" + messageDelivery.getId() + "' for notification '" + messageDelivery.getNotification().getId() + "' was successfully delivered.");
        PerformanceLog.logDuration("Time to dispatch notification delivery for notification " + messageDelivery.getNotification().getId(), System.currentTimeMillis() - messageDelivery.getNotification().getSendDateTime().getTime());

        List<String> success = new ArrayList<String>(1);
        success.add("Successfully delivered " +  messageDelivery);
        return success;
    }

    /**
     * Implements bulk delivery of a collection of {@link NotificationMessageDelivery}s
     * @param deliverer the deliverer
     * @param messageDeliveries the deliveries
     * @return collection of strings indicating successful deliveries
     */
    protected Collection<String> bulkDeliver(BulkNotificationMessageDeliverer messageDeliverer, Collection<NotificationMessageDelivery> messageDeliveries) {
        // we have our message deliverer, so tell it to deliver the message
        try {
            messageDeliverer.deliverMessage(messageDeliveries);
        } catch (NotificationMessageDeliveryException nmde) {
            LOG.error("Error bulk-delivering messages " + messageDeliveries, nmde);
            throw new RuntimeException(nmde);
        }

        // by definition we have succeeded at this point if no exception was thrown by the messageDeliverer
        // so update the status of the delivery message instance to DELIVERED (and unmark as taken)
        // and persist
        List<String> successes = new ArrayList<String>(messageDeliveries.size());
        for (NotificationMessageDelivery nmd: messageDeliveries) {
            successes.add("Successfully delivered " + nmd);
            LOG.debug("Message delivery '" + nmd.getId() + "' for notification '" + nmd.getNotification().getId() + "' was successfully delivered.");
            PerformanceLog.logDuration("Time to dispatch notification delivery for notification " + nmd.getNotification().getId(), System.currentTimeMillis() - nmd.getNotification().getSendDateTime().getTime());
            markDelivered(nmd);
        }

        return successes;
    }

    /**
     * Marks a MessageDelivery as having been delivered, and unlocks it
     * @param messageDelivery the messageDelivery instance to mark
     */
    protected void markDelivered(NotificationMessageDelivery messageDelivery) {
        messageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.DELIVERED);
        // mark as unlocked
        messageDelivery.setLockedDate(null);
        businessObjectDao.save(messageDelivery);
    }

    /**
     * @see org.kuali.notification.service.impl.ConcurrentJob#unlockWorkItem(java.lang.Object)
     */
    protected void unlockWorkItem(NotificationMessageDelivery delivery) {
        messageDeliveryService.unlockMessageDelivery(delivery);
    }

    /**
     * This method is responsible for obtaining all undelivered message delivery
     * records and iterating over them, delivering each using the proper
     * delivery mechanism.
     * @see org.kuali.notification.service.NotificationService#processUndeliveredNotificationMessageDeliveries()
     */
    public ProcessingResult processUndeliveredNotificationMessageDeliveries() {
        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] STARTING NOTIFICATION MESSAGE DELIVERY PROCESSING");

        ProcessingResult result = run();

        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] FINISHED NOTIFICATION MESSAGE DELIVERY PROCESSING - Successes = " + 
                  result.getSuccesses().size() + ", Failures = " + result.getFailures().size());
        
        return result;
    }
}
