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
package org.kuali.notification.services.impl;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.RecipientPreference;
import org.kuali.notification.bo.UserChannelSubscription;
import org.kuali.notification.bo.UserDelivererConfig;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.deliverer.impl.EmailMessageDeliverer;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.service.NotificationMessageDelivererRegistryService;
import org.kuali.notification.service.UserPreferenceService;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;
import org.kuali.notification.util.NotificationConstants;

/**
 * This class tests the user preferences service impl.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserPreferenceServiceImplTest extends NotificationTestCaseBase {
    public static final String VALID_USER_ID = TestConstants.TEST_USER_ONE;
    public static final String VALID_CHANNEL_ID = TestConstants.VALID_CHANNEL_ONE_ID.toString();
    public static final Long VALID_CHANNEL_ID_LONG = TestConstants.VALID_CHANNEL_ONE_ID;
    public static final String VALID_DELIVERER_NAME = EmailMessageDeliverer.NAME;
    public static final String[] CHANNEL_SELECTED = { TestConstants.VALID_CHANNEL_ONE_ID.toString() };
    public static final String VALID_PROPERTY = EmailMessageDeliverer.NAME + "." + EmailMessageDeliverer.EMAIL_ADDR_PREF_KEY;
    public static final String VALID_VALUE = TestConstants.EMAIL_DELIVERER_PROPERTY_VALUE;

    public UserPreferenceServiceImplTest() {
    }

    @Test
    public void testSubscribeToChannel() {
	UserPreferenceService impl = services.getUserPreferenceService();
	HashMap primaryKeys = new HashMap();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, VALID_CHANNEL_ID_LONG);
	NotificationChannel channel = (NotificationChannel) services.getBusinesObjectDao().findByPrimaryKey(NotificationChannel.class, primaryKeys);
	
	UserChannelSubscription newSub = new UserChannelSubscription();
	newSub.setUserId(VALID_USER_ID);
	newSub.setChannel(channel);
	impl.subscribeToChannel(newSub);
	UserChannelSubscription sub = impl.getSubscription(VALID_CHANNEL_ID, VALID_USER_ID);
	assertEquals(VALID_USER_ID, sub.getUserId());
	assertEquals(VALID_CHANNEL_ID_LONG, sub.getChannel().getId());
	
    }
    @Test
    public void testGetCurrentSubscriptions() {
	UserPreferenceService impl = services.getUserPreferenceService();
	HashMap primaryKeys = new HashMap();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, VALID_CHANNEL_ID_LONG);
	NotificationChannel channel = (NotificationChannel) services.getBusinesObjectDao().findByPrimaryKey(NotificationChannel.class, primaryKeys);
	
	UserChannelSubscription newSub = new UserChannelSubscription();
	newSub.setUserId(VALID_USER_ID);
	newSub.setChannel(channel);
	impl.subscribeToChannel(newSub);
	Collection<UserChannelSubscription> subs = impl.getCurrentSubscriptions(VALID_USER_ID);
	assertEquals(1, subs.size());
    }

    @Test
    public void testUnsubscribeFromChannel() {
	UserPreferenceService impl = services.getUserPreferenceService();
	HashMap primaryKeys = new HashMap();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, VALID_CHANNEL_ID_LONG);
	NotificationChannel channel = (NotificationChannel) services.getBusinesObjectDao().findByPrimaryKey(NotificationChannel.class, primaryKeys);
	
	
	UserChannelSubscription newSub = new UserChannelSubscription();
	newSub.setUserId(VALID_USER_ID);
	newSub.setChannel(channel);
	impl.subscribeToChannel(newSub);
	
	UserChannelSubscription userChannelSubscription = impl.getSubscription(VALID_CHANNEL_ID, VALID_USER_ID);
	impl.unsubscribeFromChannel(userChannelSubscription);
	
	UserChannelSubscription sub = impl.getSubscription(VALID_CHANNEL_ID, VALID_USER_ID);
	assertNull(sub);
	
    }

    @Test
    public void testSaveUserRecipientPreferences() {
	UserPreferenceService impl = services.getUserPreferenceService();
	NotificationMessageDelivererRegistryService delivererService = services.getNotificationMessageDelivererRegistryService();
	NotificationMessageDeliverer deliverer = delivererService.getDelivererByName(VALID_DELIVERER_NAME);
	if (deliverer == null) {
	    throw new RuntimeException("Message deliverer could not be obtained");
	}
		
	HashMap<String, String> userprefs = new HashMap<String, String>();
	userprefs.put(VALID_PROPERTY, VALID_VALUE);
	userprefs.put("Email.email_delivery_format", "text");
	try {
	  impl.saveUserRecipientPreferences(VALID_USER_ID, userprefs, deliverer);
	} catch (ErrorList list) {
	    throw new RuntimeException(list);
	}
	RecipientPreference recipientPreference = new RecipientPreference();
	recipientPreference.setRecipientId(VALID_USER_ID);
	Collection<RecipientPreference> prefs = services.getBusinesObjectDao().findMatchingByExample(recipientPreference);
	assertEquals(2, prefs.size()); 
    }

    @Test
    public void testGetUserRecipientPreferences() {
	UserPreferenceService impl = services.getUserPreferenceService();
	NotificationMessageDelivererRegistryService delivererService = services.getNotificationMessageDelivererRegistryService();
	NotificationMessageDeliverer deliverer = delivererService.getDelivererByName(VALID_DELIVERER_NAME);
	if (deliverer == null) {
	    throw new RuntimeException("Message deliverer could not be obtained");
	}
	HashMap<String, String> userprefs = new HashMap<String, String>();
	userprefs.put(VALID_PROPERTY, VALID_VALUE);
	userprefs.put("Email.email_delivery_format", "text");
	try {
	   impl.saveUserRecipientPreferences(VALID_USER_ID, userprefs, deliverer);
	} catch (ErrorList list) {
	   throw new RuntimeException(list);
	}
	
	RecipientPreference recipientPreference = impl.getUserRecipientPreferences(VALID_USER_ID, VALID_PROPERTY);
	assertEquals(VALID_VALUE, recipientPreference.getValue()); 
    }

    @Test
    public void testSaveUserDelivererConfigs() {
	UserPreferenceService impl = services.getUserPreferenceService();
	impl.saveUserDelivererConfig(VALID_USER_ID, VALID_DELIVERER_NAME, CHANNEL_SELECTED);
	
	UserDelivererConfig tmpUserDelivererConfig = new UserDelivererConfig();
	tmpUserDelivererConfig.setUserId(VALID_USER_ID);
	Collection<UserDelivererConfig> userDelivererConfigList = services.getBusinesObjectDao().findMatchingByExample(tmpUserDelivererConfig);
	assertEquals(1, userDelivererConfigList.size()); 
    }   
    

}
