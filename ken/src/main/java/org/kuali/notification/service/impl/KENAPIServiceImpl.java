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
import org.kuali.notification.service.KENAPIService;
import org.kuali.notification.service.NotificationChannelService;
import org.springframework.beans.factory.annotation.Required;

/**
 * KEN API service implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KENAPIServiceImpl implements KENAPIService {
    private NotificationChannelService channelService;

    /**
     * Sets the NotificationChannelService
     * @param ncs the NotificationChannelService
     */
    @Required
    public void setNotificationChannelService(NotificationChannelService ncs) {
        this.channelService = ncs;
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

}
