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

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.RecipientPreference;
import org.kuali.notification.bo.UserDelivererConfig;
import org.kuali.notification.service.KENAPIService;
import org.kuali.notification.service.NotificationChannelService;
import org.kuali.notification.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Required;

/**
 * KEN API service implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KENAPIServiceImpl implements KENAPIService {
    private NotificationChannelService channelService;
    private UserPreferenceService prefsService;

    /**
     * Sets the NotificationChannelService
     * @param ncs the NotificationChannelService
     */
    @Required
    public void setNotificationChannelService(NotificationChannelService ncs) {
        this.channelService = ncs;
    }
    
    /**
     * Sets the UserPreferenceService
     * @param ups the UserPreferenceService
     */
    @Required
    public void setUserPreferenceService(UserPreferenceService ups) {
        this.prefsService = ups;
    }

    /**
     * @see org.kuali.notification.service.KENAPIService#getAllChannels()
     */
    public Collection<String> getAllChannelNames() {
        Collection<NotificationChannel> chans = channelService.getAllNotificationChannels();
        Collection<String> chanNames = new ArrayList<String>(chans.size());
        for (NotificationChannel c: chans) {
            chanNames.add(c.getName());
        }
        return chanNames;
    }

    /**
     * @see org.kuali.notification.service.KENAPIService#getDeliverersForRecipientAndChannel(java.lang.String, java.lang.String)
     */
    public Collection<String> getDeliverersForRecipientAndChannel(String recipient, String channel) {
        NotificationChannel nc = channelService.getNotificationChannelByName(channel);
        if (nc == null) {
            throw new RuntimeException("Invalid channel: '" + channel + "'");
        }
        Collection<UserDelivererConfig> configs = prefsService.getMessageDelivererConfigurationsForUserAndChannel(recipient, nc);
        Collection<String> deliverers = new ArrayList<String>(configs.size());
        for (UserDelivererConfig cfg: configs) {
            deliverers.add(cfg.getDelivererName());
        }
        return deliverers;
    }

    /**
     * @see org.kuali.notification.service.KENAPIService#getRecipientPreference(java.lang.String, java.lang.String)
     */
    public String getRecipientPreference(String recipient, String prefKey) {
        RecipientPreference rp = prefsService.getUserRecipientPreferences(recipient, prefKey);
        if (rp == null) return null;
        return rp.getValue();
    }
}