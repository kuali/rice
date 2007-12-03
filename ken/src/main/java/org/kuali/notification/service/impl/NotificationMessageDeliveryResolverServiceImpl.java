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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.bo.NotificationRecipient;
import org.kuali.notification.bo.NotificationRecipientList;
import org.kuali.notification.bo.UserChannelSubscription;
import org.kuali.notification.bo.UserDelivererConfig;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.service.NotificationMessageDeliveryResolverService;
import org.kuali.notification.service.NotificationRecipientService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.service.ProcessingResult;
import org.kuali.notification.service.UserPreferenceService;
import org.kuali.notification.util.NotificationConstants;
import org.springframework.transaction.PlatformTransactionManager;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 * This is the default out-of-the-box implementation that leverages the status flag on a notification (RESOLVED versus UNRESOLVED) to determine whether 
 * the notification's message deliveries need to be resolved or not.  This also looks at the start and auto remove 
 * dates and times.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDeliveryResolverServiceImpl extends ConcurrentJob<Notification> implements NotificationMessageDeliveryResolverService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(NotificationMessageDeliveryResolverServiceImpl.class);
    
    private NotificationRecipientService notificationRecipientService;
    private BusinessObjectDao businessObjectDao;
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
	    BusinessObjectDao businessObjectDao, PlatformTransactionManager txManager, ExecutorService executor, 
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
        return notificationService.takeNotificationsForResolution();
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
               if (NotificationConstants.RECIPIENT_TYPES.GROUP.equals(recipient.getRecipientType())) {
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
               if (NotificationConstants.RECIPIENT_TYPES.GROUP.equals(listRecipient.getRecipientType())) {
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
     * Determines what delivery endpoints the user has configured
     * @param userRecipientId the user
     * @return a Set of NotificationConstants.MESSAGE_DELIVERY_TYPES
     */
    private Set<String> getDelivererTypesForUserAndChannel(String userRecipientId, NotificationChannel channel) {
        Set<String> deliveryTypes = new HashSet<String>(1);
        // manually add the default one since they don't have an option on this one
        deliveryTypes.add(NotificationConstants.MESSAGE_DELIVERY_TYPES.DEFAULT_MESSAGE_DELIVERY_TYPE);
        
        //now look for what they've configured for themselves
        Iterator<UserDelivererConfig> userDelivererConfigs = userPreferenceService.getMessageDelivererConfigurationsForUserAndChannel(userRecipientId, channel).iterator();
        
        // and add each config's name to the list that gets passed out, by which messages will be sent to
        while(userDelivererConfigs.hasNext()) {
            UserDelivererConfig config = userDelivererConfigs.next();
            
            deliveryTypes.add(config.getDelivererName());
        }
        
        return deliveryTypes;
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
            
                Set<String> deliveryTypes = getDelivererTypesForUserAndChannel(userRecipientId, notification.getChannel());
    
                for (String type: deliveryTypes) {
                    NotificationMessageDelivery defaultMessageDelivery = new NotificationMessageDelivery();
                    defaultMessageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.UNDELIVERED);
                    defaultMessageDelivery.setMessageDeliveryTypeName(type);
                    defaultMessageDelivery.setNotification(notification);
                    defaultMessageDelivery.setUserRecipientId(userRecipientId);
    
                    //now save that delivery end point; this record will be later processed by the dispatch service which will actually deliver it
                    businessObjectDao.save(defaultMessageDelivery);
    
                    successes.add("Successfully generated message delivery " + defaultMessageDelivery + " for notification " + notification);
                }
    
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
     * @see org.kuali.notification.service.impl.ConcurrentJob#unlockWorkItem(java.lang.Object)
     */
    @Override
    protected void unlockWorkItem(Notification notification) {
        notificationService.unlockNotification(notification);
    }

    /**
     * This method is responsible for resolving the list of NotificationMessageDelivery records for a given notification.  This service will look 
     * at all notifications that are ready to be delivered and will "explode" out specific message delivery records for given delivery end points.
     * @see org.kuali.notification.service.NotificationMessageDeliveryResolverService#resolveNotificationMessageDeliveries()
     */
    public ProcessingResult resolveNotificationMessageDeliveries() {
        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] STARTING RESOLUTION OF NOTIFICATION MESSAGE DELIVERIES");

        ProcessingResult result = run();
        
        LOG.debug("[" + new Timestamp(System.currentTimeMillis()).toString() + "] FINISHED RESOLUTION OF NOTIFICATION MESSAGE DELIVERIES - " + 
                  "Message Delivery End Points Resolved = " + result.getSuccesses().size());

        return result;
    }
}