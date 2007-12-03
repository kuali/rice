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
package org.kuali.notification.web.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.UserChannelSubscription;
import org.kuali.notification.bo.UserDelivererConfig;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.service.NotificationChannelService;
import org.kuali.notification.service.NotificationMessageDelivererRegistryService;
import org.kuali.notification.service.UserPreferenceService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * This class is the controller that handles management of various user preferences interfaces (deliver types, user subscriptions, etc).
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserPreferencesController extends MultiActionController {
    
   private static String view = "";
   
   /** Logger for this class and subclasses */
   private static final Logger LOG = Logger.getLogger(UserPreferencesController.class);
   
   protected NotificationChannelService notificationChannelService;
   protected UserPreferenceService userPreferenceService;
   protected NotificationMessageDelivererRegistryService notificationMessageDelivererRegistryService;

   
   /**
    * Set the NotificationChannelService
    * @param notificationChannelService
    */   
   public void setNotificationChannelService(NotificationChannelService notificationChannelService) {
      this.notificationChannelService = notificationChannelService;
   }
   
   /**
    * Set the UserPreferenceService
    * @param userPreferenceService
    */   
   public void setUserPreferenceService(UserPreferenceService userPreferenceService) {
      this.userPreferenceService = userPreferenceService;
   }
   
   /**
    * Set the NotificationMessageDelivererRegistryService
    * @param notificationMessageDelivererRegistryService
    */   
   public void setNotificationMessageDelivererRegistryService(NotificationMessageDelivererRegistryService notificationMessageDelivererRegistryService) {
      this.notificationMessageDelivererRegistryService = notificationMessageDelivererRegistryService;
   }
   
   /**
    * This method displays the actionList preference screen.
    * @param request
    * @param response
    * @return
    * @throws ServletException
    * @throws IOException
    */
   public ModelAndView displayActionListPreferences(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       view = "ActionListPreferences";
       LOG.debug("remoteUser: "+request.getRemoteUser());
       Map<String, Object> model = new HashMap<String, Object>(); 
       return new ModelAndView(view, model);
   }
    
   /**
    * This method handles displaying the user preferences UI.
    * @param request
    * @param response
    * @return
    * @throws ServletException
    * @throws IOException
    */
   public ModelAndView displayUserPreferences(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

      view = "UserPreferencesForm";
      String userid = request.getRemoteUser();
      LOG.debug("remoteUser: "+userid);
      // get subscribable channels
      Collection<NotificationChannel> channels = this.notificationChannelService.getSubscribableChannels();
      // get current subscriptions for this user
      Collection<UserChannelSubscription> subscriptions = this.userPreferenceService.getCurrentSubscriptions(userid);
      Map<String, Object> currentsubs = new HashMap<String, Object>();
      Iterator<UserChannelSubscription> i = subscriptions.iterator();
      while (i.hasNext()) {
	  UserChannelSubscription sub = i.next();
	  String subid = Long.toString(sub.getChannel().getId());
 	  currentsubs.put(subid, subid);
	  LOG.debug("currently subscribed to: "+sub.getChannel().getId());
      }
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("channels", channels);
      model.put("currentsubs", currentsubs);
      
      return new ModelAndView(view, model);
   }
   
   /**
    * displayDelivererConfigurationForm - obtain information necessary
    * for displaying all possible Deliverer types and forward to the form
    * @param request
    * @param response
    * @return
    * @throws ServletException
    * @throws IOException
   */
   public ModelAndView displayDelivererConfigurationForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       view = "DelivererConfigurationForm";
       String userid = request.getRemoteUser();
       LOG.debug("remoteUser: "+userid); 
              
       // Get DeliveryType classes
       Collection<NotificationMessageDeliverer> deliveryTypes = this.notificationMessageDelivererRegistryService.getAllDelivererTypes();
       
       
       // get current subscription channel ids
       Collection<UserChannelSubscription> subscriptions = this.userPreferenceService.getCurrentSubscriptions(userid);
       Map<String, Object> currentsubs = new HashMap<String, Object>();;
       Iterator<UserChannelSubscription> i = subscriptions.iterator();
       while (i.hasNext()) {
 	  UserChannelSubscription sub = i.next();
 	  String subid = Long.toString(sub.getChannel().getId());
 	  currentsubs.put(subid, subid);
 	  LOG.debug("currently subscribed to: "+sub.getChannel().getId());
       }
       
       // get all channels       
       Collection<NotificationChannel> channels = this.notificationChannelService.getAllNotificationChannels();
       
       //     get all user preferences in a HashMap
       HashMap<String, String> preferences  = this.userPreferenceService.getPreferencesForUser(userid);
       
       // get existing configured deliverers
       Collection<UserDelivererConfig> currentDeliverers = this.userPreferenceService.getMessageDelivererConfigurationsForUser(userid);
       Map<String, Object> currentDeliverersMap = new HashMap<String, Object>();
       for (UserDelivererConfig udc: currentDeliverers) {
	  String channelId = String.valueOf(udc.getChannel().getId());
	  currentDeliverersMap.put(udc.getDelivererName()+"."+channelId, udc.getDelivererName()+"."+channelId);
       }
       
       Map<String, Object> model = new HashMap<String, Object>();
       model.put("channels", channels);
       model.put("currentsubs", currentsubs);
       model.put("deliveryTypes", deliveryTypes);
       model.put("preferences", preferences);
       model.put("currentDeliverersMap", currentDeliverersMap);
       return new ModelAndView(view, model);
   }
   
   /**
    * saveDelivererConfiguration - save deliverer configuration data
    * @param request
    * @param response
    * @return
    * @throws ServletException
    * @throws IOException
   */
   public ModelAndView saveDelivererConfiguration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       view = "DelivererConfigurationForm";
       String userid = request.getRemoteUser();
       LOG.debug("remoteUser: "+userid);
       boolean error = false;
       
       Map<String, Object> model = new HashMap<String, Object>();
       
       // create preferences map here so that we can pass them all back to the view
       HashMap<String, String> preferences  = new HashMap<String, String>();
                            
       // Get DeliveryType classes.  loop through each deliverer type to 
       // to obtain preferenceKeys.  Check to see if a matching request
       // parameter was provided, then save a record for the userID, channelID, and 
       // preference setting
       Collection<NotificationMessageDeliverer> deliveryTypes = this.notificationMessageDelivererRegistryService.getAllDelivererTypes();
       
       // first remove all configured user delivers for this user
       
       this.userPreferenceService.removeUserDelivererConfig(userid);        
       
       for (NotificationMessageDeliverer dt: deliveryTypes) {
	   String deliveryTypeName = dt.getName();
	   HashMap<String,String> prefMap = dt.getPreferenceKeys();
	   LOG.debug("deliveryName: "+deliveryTypeName);
	   HashMap<String, Object> userprefs = new HashMap<String, Object>();
	   for (String prefKey:prefMap.keySet()) {
	       LOG.debug("   key: "+prefKey+", value: "+request.getParameter(deliveryTypeName+"."+prefKey));
	       userprefs.put(deliveryTypeName+"."+prefKey, request.getParameter(deliveryTypeName+"."+prefKey ));
	       preferences.put(deliveryTypeName+"."+prefKey, request.getParameter(deliveryTypeName+"."+prefKey ));
	   }
	   try {
	      this.userPreferenceService.saveUserRecipientPreferences(userid, userprefs, dt);
	   } catch (ErrorList errorlist) {
	      error = true;
	      model.put("errorList", errorlist.getErrors()) ;
	   }
	   
	   // get channelName.channels
	   String[] channels = request.getParameterValues(deliveryTypeName+".channels");
	   if (channels != null && channels.length > 0) {
	       for (int j=0; j < channels.length; j++) {
		   LOG.debug(deliveryTypeName+".channels["+j+"] "+channels[j]);   
	       }
	   }
           //	 now save the userid, channel selection
	   this.userPreferenceService.saveUserDelivererConfig(userid, deliveryTypeName, channels);
       }
       
       
       // get current subscription channel ids
       Collection<UserChannelSubscription> subscriptions = this.userPreferenceService.getCurrentSubscriptions(userid);
       Map<String, Object> currentsubs = new HashMap<String, Object>();;
       Iterator<UserChannelSubscription> i = subscriptions.iterator();
       while (i.hasNext()) {
 	  UserChannelSubscription sub = i.next();
 	  String subid = Long.toString(sub.getChannel().getId());
 	  currentsubs.put(subid, subid);
 	  LOG.debug("currently subscribed to: "+sub.getChannel().getId());
       }
       
       // get all channels       
       Collection<NotificationChannel> channels = this.notificationChannelService.getAllNotificationChannels();
               
       // get existing configured deliverers
       Collection<UserDelivererConfig> currentDeliverers = this.userPreferenceService.getMessageDelivererConfigurationsForUser(userid);
       Map<String, Object> currentDeliverersMap = new HashMap<String, Object>();
       for (UserDelivererConfig udc: currentDeliverers) {
	  String channelId = String.valueOf(udc.getChannel().getId());
	  currentDeliverersMap.put(udc.getDelivererName()+"."+channelId, udc.getDelivererName()+"."+channelId);
       }
       
       // use for debugging, uncomment for production
       //LOG.info("CurrentDeliverersMap");
       //Iterator iter = currentDeliverersMap.keySet().iterator();
       //while (iter.hasNext()) {
       //   Object o = iter.next();	   
       //   LOG.info("key: "+o.toString()+", value: "+ currentDeliverersMap.get(o) );
       //}
      
       model.put("channels", channels);
       model.put("currentsubs", currentsubs);
       model.put("deliveryTypes", deliveryTypes);
       model.put("preferences", preferences);
       model.put("currentDeliverersMap", currentDeliverersMap);
       model.put("message", "Update Successful");
        
       return new ModelAndView(view, model);
   }
   
   /**
    * Subscribe To a Channel
    * @param request
    * @param response
    * @return
    * @throws ServletException
    * @throws IOException
   */
   public ModelAndView subscribeToChannel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       view = "UserPreferencesForm";
       String userid = request.getRemoteUser();
       LOG.debug("remoteUser: "+userid);
       String channelid = request.getParameter("channelid");
       NotificationChannel newChannel = this.notificationChannelService.getNotificationChannel(channelid);
       LOG.debug("newChannel name:"+newChannel.getName());
       UserChannelSubscription newSub = new UserChannelSubscription();
       newSub.setUserId(userid);
       newSub.setChannel(newChannel);
       LOG.debug("Calling service to subscribe to channel: "+newChannel.getName());
       this.userPreferenceService.subscribeToChannel(newSub);
       
       // get current subscription channel ids
       Collection<UserChannelSubscription> subscriptions = this.userPreferenceService.getCurrentSubscriptions(userid);
       Map<String, Object> currentsubs = new HashMap<String, Object>();;
       Iterator<UserChannelSubscription> i = subscriptions.iterator();
       while (i.hasNext()) {
 	  UserChannelSubscription sub = i.next();
 	  String subid = Long.toString(sub.getChannel().getId());
 	  currentsubs.put(subid, subid);
 	  LOG.debug("currently subscribed to: "+sub.getChannel().getId());
       }
       
       // get all subscribable channels       
       Collection<NotificationChannel> channels = this.notificationChannelService.getSubscribableChannels();
       
       Map<String, Object> model = new HashMap<String, Object>();
       model.put("channels", channels);
       model.put("currentsubs", currentsubs);
       return new ModelAndView(view, model);       
   }
   
   /**
    * Unsubscribe from Channel
    * @param request
    * @param response
    * @return
    * @throws ServletException
    * @throws IOException
    */
   public ModelAndView unsubscribeFromChannel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       view = "UserPreferencesForm";
       String userid = request.getRemoteUser();
       LOG.debug("remoteUser: "+userid);
       String channelid = request.getParameter("channelid");
       
       NotificationChannel newChannel = this.notificationChannelService.getNotificationChannel(channelid);
       LOG.debug("getting channel (id, user): "+channelid+","+userid); 
       UserChannelSubscription oldsub = this.userPreferenceService.getSubscription(channelid, userid);
       oldsub.setChannel(newChannel);
       
       LOG.debug("Calling service to unsubscribe: "+newChannel.getName());
       this.userPreferenceService.unsubscribeFromChannel(oldsub);
       LOG.debug("Finished unsubscribe service: "+newChannel.getName());
       
       // get current subscription channel ids
       Collection<UserChannelSubscription> subscriptions = this.userPreferenceService.getCurrentSubscriptions(userid);
       Map<String, Object> currentsubs = new HashMap<String, Object>();
       Iterator<UserChannelSubscription> i = subscriptions.iterator();
       while (i.hasNext()) {
 	  UserChannelSubscription sub = i.next();
 	  String subid = Long.toString(sub.getChannel().getId());
	  currentsubs.put(subid, subid);
 	  LOG.debug("currently subscribed to: "+sub.getChannel().getId());
       }
       
       // get all subscribable channels       
       Collection<NotificationChannel> channels = this.notificationChannelService.getSubscribableChannels();
       
       Map<String, Object> model = new HashMap<String, Object>();
       model.put("channels", channels);
       model.put("currentsubs", currentsubs);
       return new ModelAndView(view, model);    
        
   }
}
