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

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.kuali.notification.bo.UserChannelSubscription;
import org.kuali.notification.service.NotificationChannelService;
import org.kuali.notification.service.UserPreferenceService;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.rice.dao.GenericDao;

/**
 * UserPreferenceService implementation - uses the businessObjectDao to get at data in the underlying database.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private GenericDao businessObjectDao;
    private NotificationChannelService notificationChannelService;

    private static final Logger LOG = Logger.getLogger(UserPreferenceServiceImpl.class);

    /**
     * Constructs a UserPreferenceServiceImpl 
     * @param businessObjectDao
     * @param notificationChannelService
     */
    public UserPreferenceServiceImpl(GenericDao businessObjectDao, NotificationChannelService notificationChannelService) {
        this.businessObjectDao = businessObjectDao;
        this.notificationChannelService = notificationChannelService;
    }

    /**
     * @see org.kuali.notification.service.UserPreferenceService#getCurrentSubscriptions(java.lang.String)
     */
    public Collection<UserChannelSubscription> getCurrentSubscriptions(String userid) {
        UserChannelSubscription userChannelSubscription = new UserChannelSubscription();
        userChannelSubscription.setUserId(userid);

        return businessObjectDao.findMatchingByExample(userChannelSubscription);
    }

    /**
     * @see org.kuali.notification.service.UserPreferenceService#getSubscription(java.lang.String, java.lang.String)
     */
    public UserChannelSubscription getSubscription(String channelid, String userid) {
        HashMap<String, String> uniqueKeys = new HashMap<String,String>();

        uniqueKeys.put(NotificationConstants.BO_PROPERTY_NAMES.CHANNEL_ID, channelid);
        uniqueKeys.put(NotificationConstants.BO_PROPERTY_NAMES.USER_ID, userid);

        UserChannelSubscription subscription = (UserChannelSubscription) businessObjectDao.findByUniqueKey(UserChannelSubscription.class, uniqueKeys);

        return subscription; 
    }

    /**
     * @see org.kuali.notification.service.UserPreferenceService#subscribeToChannel(org.kuali.notification.bo.UserChannelSubscription)
     */
    public void subscribeToChannel(UserChannelSubscription userChannelSubscription) {
        LOG.info("Saving channel subscription");
        try {
            businessObjectDao.save(userChannelSubscription);
        } catch(Exception e) {
            LOG.error("Exception when saving userChannelSubscription");		    
        }
        LOG.debug("Channel subscription saved");
    }

    /**
     * @see org.kuali.notification.service.UserPreferenceService#unsubscribeFromChannel(org.kuali.notification.bo.UserChannelSubscription)
     */
    public void unsubscribeFromChannel(UserChannelSubscription userChannelSubscription) {
        LOG.info("unsubscribing from channel"); 
        try {
            businessObjectDao.delete(userChannelSubscription);
        } catch(Exception e) {
            LOG.error("Exception when deleting userChannelSubscription");		    
        }

    }
}