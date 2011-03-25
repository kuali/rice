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

package org.kuali.rice.ken.service.impl;

import org.kuali.rice.core.framework.dao.GenericDao;
import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.bo.NotificationRecipient;
import org.kuali.rice.ken.bo.NotificationRecipientList;
import org.kuali.rice.ken.bo.UserChannelSubscription;
import org.kuali.rice.ken.deliverer.impl.KEWActionListMessageDeliverer;
import org.kuali.rice.ken.exception.NotificationMessageDeliveryException;
import org.kuali.rice.ken.service.NotificationMessageDeliveryResolverService;
import org.kuali.rice.ken.service.NotificationRecipientService;
import org.kuali.rice.ken.service.NotificationService;
import org.kuali.rice.ken.service.ProcessingResult;
import org.kuali.rice.ken.service.UserPreferenceService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * This is the default out-of-the-box implementation that leverages the status flag on a notification (RESOLVED versus UNRESOLVED) to determine whether
 * the notification's message deliveries need to be resolved or not.  This also looks at the start and auto remove
 * dates and times.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationMessageDeliveryResolverServiceImpl extends ConcurrentJob<Notification> implements NotificationMessageDeliveryResolverService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(NotificationMessageDeliveryResolverServiceImpl.class);

    private NotificationRecipientService notificationRecipientService;
    private GenericDao businessObjectDao;
    private UserPreferenceService userPreferenceService;
    private NotificationService notificationService;

    /**
     * Constructs a NotificationMessageDeliveryDispatchServiceImpl instance.
     * @param notificationRecipientService
     * @param businessObjectDao
     * @param txManager
     * @param executor
     * @param userPreferenceService
     */
    public NotificationMessageDeliveryResolverServiceImpl(NotificationService notificationService, NotificationRecipientService notificationRecipientService,
            GenericDao businessObjectDao, PlatformTransactionManager txManager, ExecutorService executor,
	    UserPreferenceService userPreferenceService) {
        super(txManager, executor);
        this.notificationService = notificationService;
        this.notificationRecipientService = notificationRecipientService;
        this.businessObjectDao = businessObjectDao;
        this.userPreferenceService = userPreferenceService;
    }

    /**
     * Obtains and marks as taken all unresolved (and untaken) notifications
     * @return a collection of available Notifications to process
     */
    @Override
    protected Collection<Notification> takeAvailableWorkItems() {
        Collection<Notification> nots = notificationService.takeNotificationsForResolution();
        //LOG.debug("Took " + nots.size() + " notifications");
        
        //for (Notification not: nots) {
        //   LOG.debug("Took notification: " + not.getId() + " " + not.getTitle());
        //}
        return nots;
    }


    /**
     * This method is responsible for building out the complete recipient list, which will resolve all members for groups, and add
     * them to the official list only if they are not already in the list.
     * @param notification
     * @return HashSet<String>
     */
    private HashSet<String> buildCompleteRecipientList(Notification notification) {
        HashSet<String> completeRecipientList = new HashSet<String>(notification.getRecipients().size());

        // process the list that came in with the notification request
           for (int i = 0; i < notification.getRecipients().size(); i++) {
               NotificationRecipient recipient = notification.getRecipient(i);
               if (KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(recipient.getRecipientType())) {
                   // resolve group's users
                   String[] groupMembers = notificationRecipientService.getGroupMembers(recipient.getRecipientId());
                   for(int j = 0; j < groupMembers.length; j++) {
                       completeRecipientList.add(groupMembers[j]);
                   }
               } else {  // just a user, so add to the list
                   completeRecipientList.add(recipient.getRecipientId());
               }
           }

           // now process the default recipient lists that are associated with the channel
           Iterator<NotificationRecipientList> i = notification.getChannel().getRecipientLists().iterator();
           while (i.hasNext()) {
               NotificationRecipientList listRecipient  = i.next();
               if (KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(listRecipient.getRecipientType())) {
                   // resolve group's users
                   String[] groupMembers = notificationRecipientService.getGroupMembers(listRecipient.getRecipientId());
                   for (int j = 0; j < groupMembers.length; j++) {
                       completeRecipientList.add(groupMembers[j]);
                   }
               } else {  // just a user, so add to the list
                   completeRecipientList.add(listRecipient.getRecipientId());
               }
           }

           // now process the subscribers that are associated with the channel
           List<UserChannelSubscription> subscriptions = notification.getChannel().getSubscriptions();
           for (UserChannelSubscription subscription: subscriptions) {
               // NOTE: at this time channel subscriptions are USER-only - GROUP is not supported
               // this could be implemented by adding a recipientType/userType column as we do in
               // other recipient/user-related tables/BOs
               completeRecipientList.add(subscription.getUserId());
           }

           return completeRecipientList;
    }

    /**
     * Generates all message deliveries for a given notification and save thems to the database.
     * Updates each Notification record to indicate it has been resolved.
     * Should be performed within a separate transaction
     * @param notification the Notification for which to generate message deliveries
     * @return a count of the number of message deliveries generated
     */
    /* Perform within transaction */
    @Override
    protected Collection<Object> processWorkItems(Collection<Notification> notifications) {
        List<Object> successes = new ArrayList<Object>();

        // because this concurrent job does not performed grouping of work items, there should only
        // ever be one notification object per work unit anyway...
        for (Notification notification: notifications) {
            // now figure out each unique recipient for this notification
            HashSet<String> uniqueRecipients = buildCompleteRecipientList(notification);

            // now for each unique recipient, figure out each delivery end point and create a NotificationMessageDelivery record
            Iterator<String> j = uniqueRecipients.iterator();
            while(j.hasNext()) {
                String userRecipientId = j.next();

                NotificationMessageDelivery defaultMessageDelivery = new NotificationMessageDelivery();
                defaultMessageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED);
                defaultMessageDelivery.setNotification(notification);
                defaultMessageDelivery.setUserRecipientId(userRecipientId);

                //now save that delivery end point; this record will be later processed by the dispatch service which will actually deliver it
                businessObjectDao.save(defaultMessageDelivery);

                try {
                    new KEWActionListMessageDeliverer().deliverMessage(defaultMessageDelivery);
                } catch (NotificationMessageDeliveryException e) {
                    throw new RuntimeException(e);
                }

                // we have no delivery stage any more, anything we send to KCB needs to be considered "delivered" from
                // the perspective of KEN
                defaultMessageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.DELIVERED);
                businessObjectDao.save(defaultMessageDelivery);

                successes.add(defaultMessageDelivery);

                // also, update the status of the notification so that it's message deliveries are not resolved again
                notification.setProcessingFlag(NotificationConstants.PROCESSING_FLAGS.RESOLVED);
                // unlock the record now
                notification.setLockedDate(null);
                businessObjectDao.save(notification);
            }

        }

        return successes;
    }

    /**
     * @see org.kuali.rice.ken.service.impl.ConcurrentJob#unlockWorkItem(java.lang.Object)
     */
    @Override
    protected void unlockWorkItem(Notification notification) {
        LOG.debug("Unlocking notification: " + notification.getId() + " " + notification.getTitle());
        notificationService.unlockNotification(notification);
    }

    /**
     * This method is responsible for resolving the list of NotificationMessageDelivery records for a given notification.  This service will look
     * at all notifications that are ready to be delivered and will "explode" out specific message delivery records for given delivery end points.
     * @see org.kuali.rice.ken.service.NotificationMessageDeliveryResolverService#resolveNotificationMessageDeliveries()
     */
    public ProcessingResult resolveNotificationMessageDeliveries() {
        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] STARTING RESOLUTION OF NOTIFICATION MESSAGE DELIVERIES");

        ProcessingResult result = run();

        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] FINISHED RESOLUTION OF NOTIFICATION MESSAGE DELIVERIES - " +
                  "Message Delivery End Points Resolved = " + result.getSuccesses().size());

        return result;
    }
}
