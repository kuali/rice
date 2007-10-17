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
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.deliverer.impl.AOLInstantMessageDeliverer;
import org.kuali.notification.deliverer.impl.EmailMessageDeliverer;
import org.kuali.notification.deliverer.impl.KEWActionListMessageDeliverer;
import org.kuali.notification.deliverer.impl.SMSMessageDeliverer;
import org.kuali.notification.service.NotificationMessageDelivererRegistryService;

/**
 * NotificationMessageDelivererRegistryService implementation - for now we use a HashMap to do this registration, in the future we'll use resource loading.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDelivererRegistryServiceImpl implements NotificationMessageDelivererRegistryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NotificationMessageDeliveryResolverServiceImpl.class);
    
    // holds information about the registered deliverer types
    private HashMap<String, Class> messageDelivererTypes;
    
    /**
     * Constructs an instance of the NotificationMessageDelivererRegistryServiceImpl class and sets up the 
     * registered NotificationMessageDeliverers in the system. These are the hardcoded message deliverers 
     * that we support out of the box with KEN 1.0.
     * 
     * TODO: we'll need to implement a plugin registry discovery mechanism long term.
     */
    public NotificationMessageDelivererRegistryServiceImpl() {
	KEWActionListMessageDeliverer kewActionList = new KEWActionListMessageDeliverer();
	EmailMessageDeliverer email = new EmailMessageDeliverer();
	SMSMessageDeliverer sms = new SMSMessageDeliverer();
	AOLInstantMessageDeliverer aim = new AOLInstantMessageDeliverer();
	
	messageDelivererTypes = new HashMap<String, Class>(2);
	messageDelivererTypes.put(kewActionList.getName(), kewActionList.getClass());
	messageDelivererTypes.put(email.getName(), email.getClass());
	//messageDelivererTypes.put(sms.getName(), sms.getClass());
	//messageDelivererTypes.put(aim.getName(), aim.getClass());
    }
    
    /**
     * Implements by constructing instances of each registered class and adding to an ArrayList that
     * gets passed back to the calling method.
     * @see org.kuali.notification.service.NotificationMessageDelivererRegistryService#getAllDelivererTypes()
     */
    public ArrayList getAllDelivererTypes() {
	ArrayList<NotificationMessageDeliverer>  delivererTypes = new ArrayList();
	
	Set<Entry<String, Class>> registeredTypes = messageDelivererTypes.entrySet();
	
	// iterate over each type and add an instance of each to the returning ArrayList
	for(Entry<String, Class> entry: registeredTypes ) {
	    try {
		delivererTypes.add((NotificationMessageDeliverer) entry.getValue().newInstance());
	    } catch (InstantiationException e) {
		LOG.error(e.getStackTrace());
	    } catch (IllegalAccessException e) {
		LOG.error(e.getStackTrace());
	    }
	}
	
	return delivererTypes;
    }

    /**
     * Implements by calling getDelivererByName for the delivery type name within the messageDelivery object.
     * @see org.kuali.notification.service.NotificationMessageDelivererRegistryService#getDeliverer(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public NotificationMessageDeliverer getDeliverer(NotificationMessageDelivery messageDelivery) {
        NotificationMessageDeliverer nmd = getDelivererByName(messageDelivery.getMessageDeliveryTypeName());
        if (nmd == null) {
            LOG.error("The message deliverer type ('" + messageDelivery.getMessageDeliveryTypeName() + "') " +
                      "associated with message delivery id='" + messageDelivery.getId() + "' was not found in the message deliverer registry.  This deliverer " +
	              "plugin is not in the system.");
	}
        return nmd;
    }

    /**
     * Implements by doing a key lookup in the hashmap that acts as the deliverer plugin registry.  The deliverer name is the key in the hashmap for 
     * all registered deliverers.
     * @see org.kuali.notification.service.NotificationMessageDelivererRegistryService#getDelivererByName(java.lang.String)
     */
    public NotificationMessageDeliverer getDelivererByName(String messageDelivererName) {
	Class clazz = messageDelivererTypes.get(messageDelivererName);
	
	if(clazz == null) {
	    LOG.error("The message deliverer type ('" + messageDelivererName + "') " +
                      " was not found in the message deliverer registry.  This deliverer " +
                      "plugin is not in the system.");
	    return null;
	}
	
	NotificationMessageDeliverer messageDeliverer = null;
	try {
	    messageDeliverer = (NotificationMessageDeliverer) clazz.newInstance();
	} catch (InstantiationException e) {
	    LOG.error(e.getStackTrace());
	} catch (IllegalAccessException e) {
	    LOG.error(e.getStackTrace());
	}
	
	return messageDeliverer;
    }
}