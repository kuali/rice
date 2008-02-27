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
package org.kuali.rice.kcb.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.bo.RecipientDelivererConfig;
import org.kuali.rice.kcb.exception.MessageDeliveryException;
import org.kuali.rice.kcb.exception.MessageDismissalException;
import org.kuali.rice.kcb.quartz.MessageProcessingJob;
import org.kuali.rice.kcb.service.MessageDelivererRegistryService;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.kcb.service.MessagingService;
import org.kuali.rice.kcb.service.RecipientPreferenceService;
import org.kuali.rice.kcb.vo.MessageVO;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * MessagingService implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MessagingServiceImpl implements MessagingService {
    private static final Logger LOG = Logger.getLogger(MessagingServiceImpl.class);

    private MessageService messageService;
    private MessageDeliveryService messageDeliveryService;
    private MessageDelivererRegistryService delivererRegistry;
    private RecipientPreferenceService recipientPrefs;
    /**
     * Whether to perform the processing  synchronously
     */
    private boolean synchronous;
    
    /**
     * Sets the MessageService
     * @param messageService the MessageService
     */
    @Required
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Sets the MessageDeliveryService
     * @param messageDeliveryService the MessageDeliveryService
     */
    @Required
    public void setMessageDeliveryService(MessageDeliveryService messageDeliveryService) {
        this.messageDeliveryService = messageDeliveryService;
    }

    /**
     * Sets whether to perform the processing synchronously
     * @param sync whether to perform the processing synchronously
     */
    public void setSynchronous(boolean sync) {
        LOG.info("Setting synchronous messaging to: " + sync);
        this.synchronous = sync;
    }
    /**
     * Sets the MessageDelivererRegistryService
     * @param registry the MessageDelivererRegistryService
     */
    @Required
    public void setMessageDelivererRegistryService(MessageDelivererRegistryService registry) {
        this.delivererRegistry = registry;
    }

    /**
     * Sets the RecipientPreferencesService
     * @param prefs the RecipientPreferenceService
     */
    public void setRecipientPreferenceService(RecipientPreferenceService prefs) {
        this.recipientPrefs = prefs;
    }

    /**
     * @see org.kuali.rice.kcb.service.MessagingService#deliver(org.kuali.rice.kcb.vo.MessageVO)
     */
    public long deliver(MessageVO message) throws MessageDeliveryException {
        final Message m = new Message();
        m.setTitle(message.getTitle());
        m.setDeliveryType(message.getDeliveryType());
        m.setChannel(message.getChannel());
        m.setRecipient(message.getRecipient());
        m.setContentType(message.getContentType());
        m.setContent(message.getContent());

        LOG.error("saving message: " +m);
        messageService.saveMessage(m);

        Set<String> delivererTypes = getDelivererTypesForUserAndChannel(m.getRecipient(), m.getChannel());
        LOG.error("Deliverer types for " + m.getRecipient() + "/" + m.getChannel() + ": " + delivererTypes.size());
        for (String type: delivererTypes) {
            
            MessageDelivery delivery = new MessageDelivery();
            delivery.setDelivererTypeName(type);
            delivery.setMessage(m);

//            MessageDeliverer deliverer = delivererRegistry.getDeliverer(delivery);
//            if (deliverer != null) {
//                deliverer.deliverMessage(delivery);
//            }
        
            LOG.error("saving messagedelivery: " +delivery);
            messageDeliveryService.saveMessageDelivery(delivery);
        }

        LOG.error("queuing job");
        queueJob(MessageProcessingJob.Mode.DELIVER, m.getId(), null, null);

        LOG.error("returning");
        return m.getId();
    }

    /**
     * @see org.kuali.rice.kcb.service.MessagingService#remove(int)
     */
    public void remove(long messageId) throws MessageDismissalException {
        Message m = messageService.getMessage(Long.valueOf(messageId));
        if (m == null) {
            throw new MessageDismissalException("No such message: " + messageId);
        }

        queueJob(MessageProcessingJob.Mode.REMOVE, messageId, null, null);
        
        /*Collection<MessageDelivery> deliveries = messageDeliveryService.getMessageDeliveries(m);
        for (MessageDelivery delivery: deliveries) {
            delivery.setDeliveryStatus(MessageDeliveryStatus.REMOVED.name());

            MessageDeliverer deliverer = delivererRegistry.getDeliverer(delivery);
            if (deliverer != null) {
                deliverer.dismiss(delivery, "nobody", "no reason");
            }

            messageDeliveryService.deleteMessageDelivery(delivery);
        }*/
        
        //messageService.deleteMessage(m);
    }

    /**
     * Determines what delivery endpoints the user has configured
     * @param userRecipientId the user
     * @return a Set of NotificationConstants.MESSAGE_DELIVERY_TYPES
     */
    private Set<String> getDelivererTypesForUserAndChannel(String userRecipientId, String channel) {
        Set<String> deliveryTypes = new HashSet<String>(1);
        
        // manually add the default one since they don't have an option on this one
        //deliveryTypes.add(NotificationConstants.MESSAGE_DELIVERY_TYPES.DEFAULT_MESSAGE_DELIVERY_TYPE);
        
        //now look for what they've configured for themselves
        Collection<RecipientDelivererConfig> deliverers = recipientPrefs.getDeliverersForRecipientAndChannel(userRecipientId, channel);
        
        for (RecipientDelivererConfig cfg: deliverers) {
            deliveryTypes.add(cfg.getDelivererName());
        }

        return deliveryTypes;
    }

    private void queueJob(MessageProcessingJob.Mode mode, long messageId, String user, String cause) {
        // queue up the processing job after the transaction has committed
        LOG.error("registering synchronization");
        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
        TransactionSynchronizationManager.registerSynchronization(new QueueProcessingJobSynchronization(
            mode,
            messageId,
            user,
            cause,
            synchronous
        ));
    }
    
    public static class QueueProcessingJobSynchronization extends TransactionSynchronizationAdapter {
        private static final Logger LOG = Logger.getLogger(QueueProcessingJobSynchronization.class);
        private final MessageProcessingJob.Mode mode;
        private final long messageId;
        private final String user;
        private final String cause;
        private final boolean synchronous;

        private QueueProcessingJobSynchronization(MessageProcessingJob.Mode mode, long messageId, String user, String cause, boolean synchronous) {
            this.mode = mode;
            this.messageId = messageId;
            this.user = user;
            this.cause = cause;
            this.synchronous = synchronous;
        }

        @Override
        public void afterCommit() {
            scheduleJob();
        }
        /*@Override
        public void afterCompletion(int status) {
            if (STATUS_COMMITTED == status) {
                scheduleJob();
            } else {
                LOG.error("Status is not committed.  Not scheduling message processing job.");
            }
        }*/

        public void scheduleJob() {
            LOG.info("Queueing processing job");
            try {
                Scheduler scheduler = KSBServiceLocator.getScheduler();
                JobDetail jobDetail = GlobalKCBServiceLocator.getInstance().getMessageProcessingJobDetail();
                // make sure the job detail is registered
                if (scheduler.getJobDetail(jobDetail.getName(), jobDetail.getGroup()) == null) {
                    scheduler.addJob(jobDetail, true);
                }
    
                /*if (scheduler.getJobListener(MessageDeletionListener.NAME) == null) {
                    scheduler.addJobListener(new MessageDeletionListener());
                }*/
    
                JobDataMap data = new JobDataMap();
                data.put("mode", mode.name());
                data.put("user", user);
                data.put("cause", cause);
                data.put("messageId", messageId);
    
                if (synchronous) {
                    LOG.info("Invoking job synchronously in Thread " + Thread.currentThread());
                    new MessageProcessingJob(messageId, mode, user, cause).run();
                    /*final Object lock = new Object();
                    scheduler.addGlobalJobListener(new JobListenerSupport() {
                        @Override
                        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
                            LOG.debug("Job was executed: " + context);
                            if (MessageProcessingJob.NAME.equals(context.getJobDetail().getName())) {
                                LOG.debug("Obtaining lock");
                                synchronized (lock) {
                                    LOG.debug("Notifying on lock");    
                                    lock.notifyAll();
                                }
                            }
                            
                        }
                        public String getName() {
                            return System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(10);
                        }
                        
                    });*/
                    //scheduler.triggerJob(jobDetail.getName(), jobDetail.getGroup(), data);
                
                    /*LOG.debug("Waiting for job to complete...");
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ie) {
                            throw new RuntimeException(ie);
                        }
                    }
                    LOG.debug("Job completed...");*/
                } else {
                    String uniqueTriggerName = jobDetail.getName() + "-Trigger-" + System.currentTimeMillis() + Math.random();
                    SimpleTrigger trigger = new SimpleTrigger(uniqueTriggerName, jobDetail.getGroup() + "-Trigger");
                    LOG.info("Scheduling trigger: " + trigger);
                    trigger.setJobName(jobDetail.getName());
                    trigger.setJobGroup(jobDetail.getGroup());
                    trigger.setJobDataMap(data);
                    scheduler.scheduleJob(trigger);
                }
            } catch (SchedulerException se) {
                throw new RuntimeException(se);
            }
        }
    }
}