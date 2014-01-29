/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.ken.bo.UserChannelSubscriptionBo;
import org.kuali.rice.ken.service.NotificationChannelService;
import org.kuali.rice.ken.service.UserPreferenceService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.krad.data.DataObjectService;

import java.util.Collection;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * UserPreferenceService implementation - uses the businessObjectDao to get at data in the underlying database.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private NotificationChannelService notificationChannelService;
    private DataObjectService dataObjectService;

    private static final Logger LOG = Logger.getLogger(UserPreferenceServiceImpl.class);

    /**
     * Constructs a UserPreferenceServiceImpl 
     * @param dataObjectService
     * @param notificationChannelService
     */
    public UserPreferenceServiceImpl(DataObjectService dataObjectService, NotificationChannelService notificationChannelService) {
        this.dataObjectService = dataObjectService;
        this.notificationChannelService = notificationChannelService;
    }

    /**
     * @see org.kuali.rice.ken.service.UserPreferenceService#getCurrentSubscriptions(java.lang.String)
     */
    @Override
    public Collection<UserChannelSubscriptionBo> getCurrentSubscriptions(String userid) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("userId", userid));

        return dataObjectService.findMatching(UserChannelSubscriptionBo.class, criteria.build()).getResults();
    }

    /**
     * @see org.kuali.rice.ken.service.UserPreferenceService#getSubscription(java.lang.String, java.lang.String)
     */
    @Override
    public UserChannelSubscriptionBo getSubscription(String channelid, String userid) {
        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal(NotificationConstants.BO_PROPERTY_NAMES.CHANNEL_ID, channelid),
                equal(NotificationConstants.BO_PROPERTY_NAMES.USER_ID, userid));

        List<UserChannelSubscriptionBo> subscriptions = dataObjectService.findMatching(UserChannelSubscriptionBo.class,criteria.build()).getResults();

        if (!subscriptions.isEmpty() && subscriptions.size() == 1) {
            return subscriptions.get(0);
        } else {
            return null;
        }
    }

    /**
     * @see org.kuali.rice.ken.service.UserPreferenceService#subscribeToChannel(org.kuali.rice.ken.bo.UserChannelSubscriptionBo)
     */
    @Override
    public void subscribeToChannel(UserChannelSubscriptionBo userChannelSubscription) {
        LOG.info("Saving channel subscription");
        try {
            dataObjectService.save(userChannelSubscription);
        } catch(Exception e) {
            LOG.error("Exception when saving userChannelSubscription");		    
        }
        LOG.debug("Channel subscription saved");
    }

    /**
     * @see org.kuali.rice.ken.service.UserPreferenceService#unsubscribeFromChannel(org.kuali.rice.ken.bo.UserChannelSubscriptionBo)
     */
    @Override
    public void unsubscribeFromChannel(UserChannelSubscriptionBo userChannelSubscription) {
        LOG.info("unsubscribing from channel"); 
        try {
            dataObjectService.delete(userChannelSubscription);
        } catch(Exception e) {
            LOG.error("Exception when deleting userChannelSubscription");		    
        }

    }
}
