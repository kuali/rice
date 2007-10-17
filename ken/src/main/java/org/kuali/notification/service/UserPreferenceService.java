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
package org.kuali.notification.service;

import java.util.Collection;
import java.util.HashMap;

import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.RecipientPreference;
import org.kuali.notification.bo.UserChannelSubscription;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.ErrorList;

/**
 * Service for accessing user preferences in the KEN system.{@link UserPreference}
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface UserPreferenceService {
    /**
     * This method retrieves all of the current channel subscriptions for a user.
     * @param userid
     * @return Collection
     */
    public Collection getCurrentSubscriptions(String userid);
    
    /**
     * This method retrieves the UserChannelSubscription instance given the two unique keys that are 
     * passed in.
     * @param channelid
     * @param userid
     * @return UserChannelSubscription
     */
    public UserChannelSubscription getSubscription(String channelid, String userid);

    /**
     * This method will add a channel subscription into the system.
     * @param userChannelSubscription
     */
    public void subscribeToChannel(UserChannelSubscription userChannelSubscription);

    /**
     * This method will remove a channel subscription from the system.
     * @param userChannelSubscription
     */
    public void unsubscribeFromChannel(UserChannelSubscription userChannelSubscription);
    
    /**
     * This method will save a user recipient preference in the system.
     * @param userid
     * @param prefs a hashmap of key/values
     * @param deliveryTypeName name of deliverer
     */
    public void saveUserRecipientPreferences(String userid, HashMap prefs, NotificationMessageDeliverer deliverer) throws ErrorList;
    
    /**
     * This method will get a specific user recipient preference from the system.
     * @param userid
     * @param key
     */
    public RecipientPreference getUserRecipientPreferences(String userid, String key);
    
    /**
     * This method will get all  user recipient preference from the system.
     * @param userid
     */
    public HashMap getPreferencesForUser(String userid);
    
    /**
     * This method will remove all user deliverer configuration preference in the system.
     * @param userid
     */
    public void removeUserDelivererConfig(String userid);
    
    /**
     * This method will save a user deliverer configuration preference in the system.
     * @param userid
     * @param delivererName
     * @param selected
     */
    public void saveUserDelivererConfig(String userid, String delivererName, String[] selected);
    
    /**
     * This method will retrieve all of the message deliverer configurations for a given user, associated with a 
     * particular channel.
     * @param userId
     * @param channel
     */
    public Collection getMessageDelivererConfigurationsForUserAndChannel(String userId, NotificationChannel channel);
    
    /**
     * This method will retrieve all of the message deliverer configurations for a given user 
     * @param userId
     */
    public Collection getMessageDelivererConfigurationsForUser(String userId);
}