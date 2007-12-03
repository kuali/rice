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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.RecipientPreference;
import org.kuali.notification.bo.UserChannelSubscription;
import org.kuali.notification.bo.UserDelivererConfig;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.service.NotificationChannelService;
import org.kuali.notification.service.UserPreferenceService;
import org.kuali.notification.util.NotificationConstants;

/**
 * UserPreferenceService implementation - uses the businessObjectDao to get at data in the underlying database.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private BusinessObjectDao businessObjectDao;
    private NotificationChannelService notificationChannelService;
    
    private static final Logger LOG = Logger.getLogger(UserPreferenceServiceImpl.class);

    /**
     * Constructs a UserPreferenceServiceImpl 
     * @param businessObjectDao
     * @param notificationChannelService
     */
    public UserPreferenceServiceImpl(BusinessObjectDao businessObjectDao, NotificationChannelService notificationChannelService) {
	this.businessObjectDao = businessObjectDao;
	this.notificationChannelService = notificationChannelService;
    }

    /**
     * @see org.kuali.notification.service.UserPreferenceService#getCurrentSubscriptions(java.lang.String)
     */
    public Collection getCurrentSubscriptions(String userid) {
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
    
    /**
     * This method retrieve user a recipient preference for a specific property
     * @see org.kuali.notification.service.UserPreferenceService#getUserRecipientPreferences(java.lang.String, java.lang.String, java.lang.String)
     */
    public RecipientPreference getUserRecipientPreferences(String userid, String prop ) {
        HashMap<String, String> uniqueKeys = new HashMap<String,String>();	

        uniqueKeys.put(NotificationConstants.BO_PROPERTY_NAMES.RECIPIENT_ID, userid);
        uniqueKeys.put(NotificationConstants.BO_PROPERTY_NAMES.RECIPIENT_TYPE, NotificationConstants.RECIPIENT_TYPES.USER);
	uniqueKeys.put(NotificationConstants.BO_PROPERTY_NAMES.PROPERTY, prop);
	
	RecipientPreference recipientPreference = (RecipientPreference) businessObjectDao.findByUniqueKey(RecipientPreference.class, uniqueKeys);
	return recipientPreference;
    }
    
    /**
     * This method retrieve user a recipient preference.
     * @see org.kuali.notification.service.UserPreferenceService#getPreferencesForUser(java.lang.String)
     */
    public HashMap getPreferencesForUser(String userid) {
	RecipientPreference recipientPreference = new RecipientPreference();
        recipientPreference.setRecipientId(userid);	
        HashMap<String, String> prefs = new HashMap<String,String>();
        Collection<RecipientPreference> userPrefs =  businessObjectDao.findMatchingByExample(recipientPreference);
        for (RecipientPreference p: userPrefs) {
            prefs.put(p.getProperty(), p.getValue());
        }
        
	return prefs;
    }
    
    /**
     * This method saves recipient preference property and values.
     * @see org.kuali.notification.service.UserPreferenceService#saveUserRecipientPreferences(java.lang.String, java.lang.String, java.lang.String)
     */
    public void saveUserRecipientPreferences(String userid, HashMap prefs, NotificationMessageDeliverer deliverer) throws ErrorList {
	LOG.info("Saving user recipient preferences");
			
	try {
	   deliverer.validatePreferenceValues(prefs);         
	} catch (ErrorList list) {
	   LOG.error("user preferences failed validation");
	   throw list;    
	}
	
	Iterator iter = prefs.keySet().iterator();
	while (iter.hasNext()) {
	   Object o = iter.next();
	   String prop = o.toString();
	   String value = (String) prefs.get(prop);
	   	   
           // We need to check if this property is already set
	   // for the user by checking doing a unique key query...if
	   // it already exists, update, otherwise add it 
	   RecipientPreference currentPreference = getUserRecipientPreferences(userid, prop);
	   if (currentPreference != null) {
	      currentPreference.setRecipientType(NotificationConstants.RECIPIENT_TYPES.USER);
	      currentPreference.setRecipientId(userid);
	      currentPreference.setProperty(prop);
	      currentPreference.setValue(value);
	      try {
	          businessObjectDao.save(currentPreference);
	      } catch(Exception e) {
	          LOG.error("Exception when saving recipientPreference");		    
	      }   
	   } else {
	      RecipientPreference recipientPreference = new RecipientPreference();
	      recipientPreference.setRecipientType(NotificationConstants.RECIPIENT_TYPES.USER);
	      recipientPreference.setRecipientId(userid);
	      recipientPreference.setProperty(prop);
	      recipientPreference.setValue(value);
	      try {
	          businessObjectDao.save(recipientPreference);
	      } catch(Exception e) {
	          LOG.error("Exception when adding recipient");		    
	      }
	   }
	}
    }
    
    /**
     * @see org.kuali.notification.service.UserPreferenceService#removeUserDelivererConfig(java.lang.String, java.lang.String, java.lang.String[])
     */
    public void removeUserDelivererConfig(String userid) {
 	
	UserDelivererConfig tmpUserDelivererConfig = new UserDelivererConfig();
	tmpUserDelivererConfig.setUserId(userid);
	try {
	   Collection<UserDelivererConfig> deleteList = getMessageDelivererConfigurationsForUser(userid);
	   for (UserDelivererConfig deliverer: deleteList) {
	       businessObjectDao.delete(deliverer);    
	   }
	    
	} catch(Exception e) {
	    LOG.error("Exception when deleting userDelivererConfig",e);
	}
    }
    
    /**
     * Dynamically saves each UserDelivererConfig object by iterating over the returned list and deriving the appropriate keys to save by.
     * @param userid id of user for whom to configure deliverer
     * @param delivererName name of deliverer to associate with user
     * @param selected array of channel ID strings for which the deliverer will be enabled for the specified user 
     * @see org.kuali.notification.service.UserPreferenceService#saveUserDelivererConfig(java.lang.String, java.lang.String, java.lang.String[])
     */
    public void saveUserDelivererConfig(String userid, String delivererName, String[] selected) {
	LOG.info("Saving user Deliverer config");
	
	// now add the channels selected
	LOG.info("enabling deliverer ("+delivererName+") for channels");
	if (selected != null && selected.length > 0) {
	   // if selected[0] is 0 we want to remove this deliverer
	   // for all channels.  We already did that above.
	   if (selected[0].equals("0")) {
	       LOG.info("no channels selected for this deliverer.");
	   } else {
    	      for (int i=0; i < selected.length ; i++) {
    	         UserDelivererConfig userDelivererConfig = new UserDelivererConfig();
    	
    	         userDelivererConfig.setUserId(userid);
    	         userDelivererConfig.setDelivererName(delivererName);
    	         NotificationChannel channel = new NotificationChannel();
    	         channel = notificationChannelService.getNotificationChannel(selected[i]);
    	         LOG.info("Enabled for: "+channel.getName());
    	         userDelivererConfig.setChannel(channel);
    	         try {
    	            businessObjectDao.save(userDelivererConfig);
    	         } catch(Exception e) {
    	            LOG.error("Exception when saving userDelivererConfig",e);		    
    	         }
    	      }
	   }
	}
    }
    
    /**
     * @see org.kuali.notification.service.UserPreferenceService#getMessageDelivererConfigurationsForUserAndChannel(java.lang.String, org.kuali.notification.bo.NotificationChannel)
     */
    public Collection getMessageDelivererConfigurationsForUserAndChannel(String userId, NotificationChannel channel) {
	Collection<UserDelivererConfig> configuredMessageDeliverers = null;
	    
	HashMap<String, Object> criteria = new HashMap<String,Object>();
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.USER_ID, userId);
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.CHANNEL_ID, channel.getId());
	
	configuredMessageDeliverers = businessObjectDao.findMatching(UserDelivererConfig.class, criteria);
	
	return configuredMessageDeliverers; 
    }
    
    /**
     * @see org.kuali.notification.service.UserPreferenceService#getMessageDelivererConfigurationsForUser(java.lang.String, org.kuali.notification.bo.NotificationChannel)
     */
    public Collection getMessageDelivererConfigurationsForUser(String userId) {
	Collection<UserDelivererConfig> configuredMessageDeliverers = null;
	    
	HashMap<String, Object> criteria = new HashMap<String,Object>();
	criteria.put(NotificationConstants.BO_PROPERTY_NAMES.USER_ID, userId);
	
	configuredMessageDeliverers = businessObjectDao.findMatching(UserDelivererConfig.class, criteria);
	
	return configuredMessageDeliverers; 
    }    
}