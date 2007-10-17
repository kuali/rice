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

/**
 * Interface for obtaining Notification System services
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface NotificationServiceLocator {
    /**
     * This method returns an instance of the Spring configured impl for the NotificationService.
     * @return NotificationService
     */
    public NotificationService getNotificationService();

    /**
     * This method returns an instance of the Spring configured impl for the NotificationContentTypeService.
     * @return NotificationContentTypeService
     */
    public NotificationContentTypeService getNotificationContentTypeService();

    /**
     * This method returns an instance of the Spring configured impl for the NotificationMessageContentService.
     * @return NotificationMessageContentService
     */
    public NotificationMessageContentService getNotificationMessageContentService();
    
    /**
     * This method returns an instance of the Spring configured impl for the BusinessObjectDao.
     * @return BusinessObjectDao
     */
    public BusinessObjectDao getBusinesObjectDao();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationAuthorizationService.
     * @return NotificationAuthorizationService
     */
    public NotificationAuthorizationService getNotificationAuthorizationService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationWorkflowDocumentService.
     * @return NotificationWorkflowDocumentService
     */
    public NotificationWorkflowDocumentService getNotificationWorkflowDocumentService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationMessageDeliveryDispatchService.
     * @return NotificationMessageDeliveryDispatchService
     */
    public NotificationMessageDeliveryDispatchService getNotificationMessageDeliveryDispatchService();

    /**
     * This method returns an instance of the Spring configured impl for the NotificationMessageDeliveryAutoRemovalService.
     * @return NotificationMessageDeliveryAutoRemovalService
     */
    public NotificationMessageDeliveryAutoRemovalService getNotificationMessageDeliveryAutoRemovalService();

    /**
     * This method returns an instance of the Spring configured impl for the getNotificationMessageDeliveryResolverService.
     * @return NotificationMessageDeliveryResolverService
     */
    public NotificationMessageDeliveryResolverService getNotificationMessageDeliveryResolverService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationRecipientService.
     * @return NotificationRecipientService
     */
    public NotificationRecipientService getNotificationRecipientService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationMessageDeliveryService.
     * @return NotificationMessageDeliveryService
     */
    public NotificationMessageDeliveryService getNotificationMessageDeliveryService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationMessageDelivererRegistryService.
     * @return NotificationMessageDelivererRegistryService
     */
    public NotificationMessageDelivererRegistryService getNotificationMessageDelivererRegistryService();
    
    /**
     * This method returns an instance of the Spring configured impl for the UserPreferenceService.
     * @return UserPreferenceService
     */
    public UserPreferenceService getUserPreferenceService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationChannelService.
     * @return NotificationChannelService
     */
    public NotificationChannelService getNotificationChannelService();
    
    /**
     * This method returns an instance of the Spring configured impl for the NotificationChannelService.
     * @return NotificationEmailService
     */
    public NotificationEmailService getNotificationEmailService();

    /**
     * Returns the Notification system configuration properties
     * @return the Notification system configuration properties
     */
    public Properties getNotificationConfiguration();
    
    /**
     * Returns the Quartz scheduler used by the Notification system
     * @return the Quartz scheduler used by the Notification system
     */
    public Scheduler getScheduler();
}