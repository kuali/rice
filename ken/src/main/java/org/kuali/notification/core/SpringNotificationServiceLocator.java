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
package org.kuali.notification.core;

import java.util.Properties;

import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.service.NotificationAuthorizationService;
import org.kuali.notification.service.NotificationChannelService;
import org.kuali.notification.service.NotificationContentTypeService;
import org.kuali.notification.service.NotificationEmailService;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.service.NotificationMessageDelivererRegistryService;
import org.kuali.notification.service.NotificationMessageDeliveryAutoRemovalService;
import org.kuali.notification.service.NotificationMessageDeliveryDispatchService;
import org.kuali.notification.service.NotificationMessageDeliveryResolverService;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.service.NotificationRecipientService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.service.NotificationWorkflowDocumentService;
import org.kuali.notification.service.UserPreferenceService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.BeanFactory;

/**
 * NotificationServiceLocator backed by a Spring Bean Factory - responsible for returning instances of services instantiated by the Spring context loader.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SpringNotificationServiceLocator implements NotificationServiceLocator {
    // Spring bean names
    private static final String NOTIFICATION_SERVICE = "notificationService";
    private static final String NOTIFICATION_CONTENT_TYPE_SERVICE = "notificationContentTypeService";
    private static final String MESSAGE_CONTENT_SERVICE = "messageContentService";
    private static final String BUSINESS_OBJECT_DAO = "businessObjectDao";
    private static final String NOTIFICATION_AUTHORIZATION_SERVICE = "notificationAuthorizationService";
    private static final String NOTIFICATION_WORKFLOW_DOCUMENT_SERVICE = "notificationWorkflowDocumentService";
    private static final String NOTIFICATION_MESSAGE_DELIVERY_DISPATCH_SERVICE = "notificationMessageDeliveryDispatchService";
    private static final String NOTIFICATION_MESSAGE_DELIVERY_RESOLVER_SERVICE = "notificationMessageDeliveryResolverService";
    private static final String NOTIFICATION_MESSAGE_DELIVERY_AUTOREMOVAL_SERVICE = "notificationMessageDeliveryAutoRemovalService";
    private static final String NOTIFICATION_RECIPIENT_SERVICE = "notificationRecipientService";
    private static final String NOTIFICATION_MESSAGE_DELIVERY_SERVICE = "notificationMessageDeliveryService";
    private static final String NOTIFICATION_MESSAGE_DELIVERER_REGISTRY_SERVICE = "notificationMessageDelivererRegistryService";
    private static final String USER_PREFERENCE_SERVICE = "userPreferenceService";
    private static final String NOTIFICATION_CHANNEL_SERVICE = "notificationChannelService";
    private static final String NOTIFICATION_EMAIL_SERVICE = "notificationEmailService";
    private static final String NOTIFICATION_CONFIG = "notificationConfig";
    private static final String NOTIFICATION_SCHEDULER = "notificationScheduler";

    private BeanFactory beanFactory;

    /**
     * Constructs a SpringNotificationServiceLocator.java.
     * @param beanFactory
     */
    public SpringNotificationServiceLocator(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationService()
     */
    public NotificationService getNotificationService() {
        return (NotificationService) beanFactory.getBean(NOTIFICATION_SERVICE);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationContentTypeService()
     */
    public NotificationContentTypeService getNotificationContentTypeService() {
        return (NotificationContentTypeService) beanFactory.getBean(NOTIFICATION_CONTENT_TYPE_SERVICE);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationMessageContentService()
     */
    public NotificationMessageContentService getNotificationMessageContentService() {
        return (NotificationMessageContentService) beanFactory.getBean(MESSAGE_CONTENT_SERVICE);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getBusinesObjectDao()
     */
    public BusinessObjectDao getBusinesObjectDao() {
        return (BusinessObjectDao) beanFactory.getBean(BUSINESS_OBJECT_DAO);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationAuthorizationService()
     */
    public NotificationAuthorizationService getNotificationAuthorizationService() {
        return (NotificationAuthorizationService) beanFactory.getBean(NOTIFICATION_AUTHORIZATION_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationWorkflowDocumentService()
     */
    public NotificationWorkflowDocumentService getNotificationWorkflowDocumentService() {
        return (NotificationWorkflowDocumentService) beanFactory.getBean(NOTIFICATION_WORKFLOW_DOCUMENT_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationMessageDeliveryDispatchService()
     */
    public NotificationMessageDeliveryDispatchService getNotificationMessageDeliveryDispatchService() {
        return (NotificationMessageDeliveryDispatchService) beanFactory.getBean(NOTIFICATION_MESSAGE_DELIVERY_DISPATCH_SERVICE);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationMessageDeliveryAutoRemovalService()
     */
    public NotificationMessageDeliveryAutoRemovalService getNotificationMessageDeliveryAutoRemovalService() {
        return (NotificationMessageDeliveryAutoRemovalService) beanFactory.getBean(NOTIFICATION_MESSAGE_DELIVERY_AUTOREMOVAL_SERVICE);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationMessageDeliveryResolverService()
     */
    public NotificationMessageDeliveryResolverService getNotificationMessageDeliveryResolverService() {
        return (NotificationMessageDeliveryResolverService) beanFactory.getBean(NOTIFICATION_MESSAGE_DELIVERY_RESOLVER_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationRecipientService()
     */
    public NotificationRecipientService getNotificationRecipientService() {
        return (NotificationRecipientService) beanFactory.getBean(NOTIFICATION_RECIPIENT_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationMessageDeliveryService()
     */
    public NotificationMessageDeliveryService getNotificationMessageDeliveryService() {
        return (NotificationMessageDeliveryService) beanFactory.getBean(NOTIFICATION_MESSAGE_DELIVERY_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationMessageDelivererRegistryService()
     */
    public NotificationMessageDelivererRegistryService getNotificationMessageDelivererRegistryService() {
        return (NotificationMessageDelivererRegistryService) beanFactory.getBean(NOTIFICATION_MESSAGE_DELIVERER_REGISTRY_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getUserPreferenceService()
     */
    public UserPreferenceService getUserPreferenceService() {
        return (UserPreferenceService) beanFactory.getBean(USER_PREFERENCE_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationChannelService()
     */
    public NotificationChannelService getNotificationChannelService() {
        return (NotificationChannelService) beanFactory.getBean(NOTIFICATION_CHANNEL_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationEmailService()
     */
    public NotificationEmailService getNotificationEmailService() {
        return (NotificationEmailService) beanFactory.getBean(NOTIFICATION_EMAIL_SERVICE);
    }
    
    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getNotificationConfiguration()
     */
    public Properties getNotificationConfiguration() {
        return (Properties) beanFactory.getBean(NOTIFICATION_CONFIG);
    }

    /**
     * @see org.kuali.notification.core.NotificationServiceLocator#getScheduler()
     */
    public Scheduler getScheduler() {
        return (Scheduler) beanFactory.getBean(NOTIFICATION_SCHEDULER);
    }
}