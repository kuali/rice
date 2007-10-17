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
package org.kuali.notification.deliverer.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.validator.EmailValidator;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.core.GlobalNotificationServiceLocator;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.exception.NotificationAutoRemoveException;
import org.kuali.notification.exception.NotificationMessageDeliveryException;
import org.kuali.notification.service.NotificationEmailService;
import org.kuali.notification.service.UserPreferenceService;

/**
 * This class is responsible for describing the email delivery mechanism for
 * the system.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailMessageDeliverer implements NotificationMessageDeliverer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(EmailMessageDeliverer.class);

    private NotificationEmailService notificationEmailService;
    private UserPreferenceService userPreferenceService;
    
    public static final String NAME = "Email";
    public static final String EMAIL_ADDR_PREF_KEY = "email_address";
    public static final String EMAIL_DELIV_FRMT_PREF_KEY = "email_delivery_format";
    
    /**
     * Constructs a EmailMessageDeliverer.java.
     */
    public EmailMessageDeliverer() {
	this.notificationEmailService = GlobalNotificationServiceLocator.getInstance().getNotificationEmailService();
	this.userPreferenceService = GlobalNotificationServiceLocator.getInstance().getUserPreferenceService();
    }

    /**
     * This implementation uses the email service to deliver a notification.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#deliverMessage(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public void deliverMessage(NotificationMessageDelivery messageDelivery)
	    throws NotificationMessageDeliveryException {
	try {
	    // figure out the proper recipient email address
	    String recipientEmailAddressPrefKey = getName()+"."+EMAIL_ADDR_PREF_KEY;
	    String recipientEmailFormatPrefKey = getName()+"."+EMAIL_DELIV_FRMT_PREF_KEY;
	    
	    String recipientEmailAddress = userPreferenceService.getUserRecipientPreferences(messageDelivery.getUserRecipientId(), recipientEmailAddressPrefKey).getValue();
	    String recipientEmailFormat = userPreferenceService.getUserRecipientPreferences(messageDelivery.getUserRecipientId(), recipientEmailFormatPrefKey).getValue();
	    
	    Long emailMessageId = notificationEmailService.sendNotificationEmail(messageDelivery, recipientEmailAddress, recipientEmailFormat);
	    
	    String deliverySystemId = null;
	    if (emailMessageId != null) {
		deliverySystemId = emailMessageId.toString();
	    }
	    messageDelivery.setDeliverySystemId(deliverySystemId);
	} catch (Exception we) {
	    LOG.error("Error delivering email notification", we);
	    throw new NotificationMessageDeliveryException("Error delivering email notification", we);
	}
    }
    
    /**
     * This implementation does an auto-remove by "canceling" the workflow email with the message delivery record. 
     * In the case of email, it's a noop 
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#autoRemoveMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public void autoRemoveMessageDelivery(NotificationMessageDelivery messageDelivery) throws NotificationAutoRemoveException {
	// we can't remove an email message once it has been sent
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#dismissMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery, java.lang.String, java.lang.String)
     */
    public void dismissMessageDelivery(NotificationMessageDelivery messageDelivery, String user, String cause) {
        // we can't remove an email message once it has been sent
    }
    
    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getDescription()
     */
    public String getDescription() {
	return "Enter an Email Address and Email Delivery Format below and select the channels for which you would like email delivery " +
			"notifications. Select \"None\" in the channel list to remove a delivery type for all channels.  " +
			"Only one Email Address and Email Delivery Format may be specified. Any data entered and " +
			"saved will override prior Delivery Type selections.";
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getName()
     */
    public String getName() {
	return NAME;
    }
    
    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getTitle()
     */
    public String getTitle() {
	return "Email Message Delivery";
    }

    /**
     * This implementation returns an address field.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getPreferenceKeys()
     */
    public LinkedHashMap<String, String> getPreferenceKeys() {
	LinkedHashMap<String, String> prefKeys = new LinkedHashMap<String, String>();
	prefKeys.put(EMAIL_ADDR_PREF_KEY, "Email Address (\"abc@def.edu\")");
	prefKeys.put(EMAIL_DELIV_FRMT_PREF_KEY, "Email Delivery Format (text or html)");
	return prefKeys;
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#validatePreferenceValues()
     */
    public void validatePreferenceValues(HashMap prefs) throws ErrorList {
	boolean error = false;
	ErrorList errorList = new ErrorList();
	String[] validformats = {"text","html"};
	
	if (!prefs.containsKey(getName()+"."+EMAIL_ADDR_PREF_KEY)) {
	    errorList.addError("Email Address is a required field.");
	    error = true;
	} else {
	    String addressValue = (String) prefs.get(getName()+"."+EMAIL_ADDR_PREF_KEY);
	    EmailValidator validator = EmailValidator.getInstance();
	    if (!validator.isValid(addressValue)) {
		errorList.addError("Email Address is required and must be properly formatted - \"abc@def.edu\".");
		error = true;
	    }
	}
	
	// validate format
	if (!prefs.containsKey(getName()+"."+EMAIL_DELIV_FRMT_PREF_KEY)) {
	    errorList.addError("Email Delivery Format is required.");
	    error = true;
	} else {
	    String formatValue = (String) prefs.get(getName()+"."+EMAIL_DELIV_FRMT_PREF_KEY);
	    Set<String> formats = new HashSet<String>();
	    for (int i=0; i < validformats.length ; i++) {
		formats.add(validformats[i]);
	    }
	    
	    if (!formats.contains(formatValue)) {
		errorList.addError("Email Delivery Format is required and must be entered as \"text\" or \"html\".");
		error = true;
	    }
	}
	
	if (error) throw errorList;
    }
}