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
 * This class is responsible for describing the AOL Instant Messenger delivery mechanism for
 * the system; however, it is not yet integrated into the system and is just a stub.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AOLInstantMessageDeliverer implements NotificationMessageDeliverer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(AOLInstantMessageDeliverer.class);

    private static final String SCREEN_NAME = "aim_screen_name";
    
    /**
     * Constructs a AOLInstantMessageDeliverer.java.
     */
    public AOLInstantMessageDeliverer() {
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
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#dismissMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery, java.lang.String, java.lang.String)
     */
    public void dismissMessageDelivery(NotificationMessageDelivery messageDelivery, String user, String cause) {
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getDescription()
     */
    public String getDescription() {
	return "This is the default AOL Instant Messenger delivery type.";
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getName()
     */
    public String getName() {
	return "AIM";
    }
    
    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getTitle()
     */
    public String getTitle() {
	return "AOL Instant Messenger Delivery";
    }

    /**
     * This implementation returns a screen name field.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getPreferenceKeys()
     */
    public LinkedHashMap getPreferenceKeys() {
	LinkedHashMap<String, String> prefKeys = new LinkedHashMap<String, String>();
	prefKeys.put(SCREEN_NAME, "AIM Screen Name");
	return prefKeys;
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#validatePreferenceValues()
     */
    public void validatePreferenceValues(HashMap prefs) throws ErrorList {
	boolean error = false;
	ErrorList errorList = new ErrorList();
	
	if (!prefs.containsKey(getName()+"."+SCREEN_NAME)) {
	    errorList.addError("AIM Screen Name is a required field.");
	    error = true;
	} else {
	    String screenName = (String) prefs.get(getName()+"."+SCREEN_NAME);
	    if(StringUtils.isBlank(screenName)) {
		errorList.addError("AIM Screen Name is a required.");
		error = true;
	    }
	}
	if (error) throw errorList;
    }
}