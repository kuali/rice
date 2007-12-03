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
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.exception.NotificationAutoRemoveException;
import org.kuali.notification.exception.NotificationMessageDeliveryException;

/**
 * This class is responsible for describing the SMS delivery mechanism for
 * the system.  It is not yet fully implemented - this class is just a stub.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SMSMessageDeliverer implements NotificationMessageDeliverer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(SMSMessageDeliverer.class);

    private static final String MOBILE_NUMBER = "sms_mobile_number";
    
    /**
     * Constructs a SMSMessageDeliverer.java.
     */
    public SMSMessageDeliverer() {
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#deliverMessage(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public void deliverMessage(NotificationMessageDelivery messageDelivery)
	    throws NotificationMessageDeliveryException {
    }
    
    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#autoRemoveMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public void autoRemoveMessageDelivery(NotificationMessageDelivery messageDelivery) throws NotificationAutoRemoveException {
	// we can't remove an sms message once it has been sent
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#dismissMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery, java.lang.String, java.lang.String)
     */
    public void dismissMessageDelivery(NotificationMessageDelivery messageDelivery, String user, String cause) {
        // we can't remove an sms message once it has been sent
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getDescription()
     */
    public String getDescription() {
	return "This is the default SMS message delivery type.  Please note that you may incur charges for each SMS message that you receive to your mobile phone.";
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getName()
     */
    public String getName() {
	return "SMS";
    }
    
    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getTitle()
     */
    public String getTitle() {
	return "SMS Message Delivery";
    }

    /**
     * This implementation returns an address field.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getPreferenceKeys()
     */
    public LinkedHashMap getPreferenceKeys() {
	LinkedHashMap<String, String> prefKeys = new LinkedHashMap<String, String>();
	prefKeys.put(MOBILE_NUMBER, "Mobile Phone Number (\"555-555-5555\")");
	return prefKeys;
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#validatePreferenceValues()
     */
    public void validatePreferenceValues(HashMap prefs) throws ErrorList {
	boolean error = false;
	ErrorList errorList = new ErrorList();
	
	if (!prefs.containsKey(getName()+"."+MOBILE_NUMBER)) {
	    errorList.addError("Mobile Phone Number is a required field.");
	    error = true;
	} else {
	    String mobileNumber = (String) prefs.get(getName()+"."+MOBILE_NUMBER);
	    if(StringUtils.isBlank(mobileNumber)) {
		errorList.addError("Mobile Phone Number is a required.");
		error = true;
	    }
	}
	if (error) throw errorList;
    }
}