/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.SessionTicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds info about the User Session
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserSession implements Serializable {
    private static final long serialVersionUID = 4532616762540067557L;

    private static final Object NULL_VALUE = new Object();

    private Person person;
    private Person backdoorUser;
    private AtomicInteger nextObjectKey;
    private ConcurrentHashMap<String, Object> objectMap;
    private String kualiSessionId;

    /**
     * Returns the session id. The session id is a unique identifier for the session.
     * @return the kualiSessionId
     */
    public String getKualiSessionId() {
        return this.kualiSessionId;
    }

    /**
     * Sets the session id.
     * @param kualiSessionId the kualiSessionId to set
     */
    public void setKualiSessionId(String kualiSessionId) {
        this.kualiSessionId = kualiSessionId;
    }

    /**
     * Creates a user session for the principal specified in the parameter.
     * Take in a netid, and construct the user from that.
     *
     * @param principalName
     */
    public UserSession(String principalName) {
        initPerson(principalName);
        this.nextObjectKey = new AtomicInteger(0);
        this.objectMap = new ConcurrentHashMap<String, Object>();
    }

    /**
     * Loads the Person object from KIM. Factored out for testability.
     * @param principalName the principalName
     */
    protected void initPerson(String principalName) {
        this.person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
        if (this.person == null) {
            throw new IllegalArgumentException(
                    "Failed to locate a principal with principal name '" + principalName + "'");
        }
    }

    /**
     * Returns the id of the current user.
     * @return the principalId of the current user in the system, backdoor principalId if backdoor is set
     */
    public String getPrincipalId() {
        if (backdoorUser != null) {
            return backdoorUser.getPrincipalId();
        }
        return person.getPrincipalId();
    }

    /**
     * Returns the name of the current user.
     * @return the principalName of the current user in the system, backdoor principalName if backdoor is set
     */
    public String getPrincipalName() {
        if (backdoorUser != null) {
            return backdoorUser.getPrincipalName();
        }
        return person.getPrincipalName();
    }

    /**
     * Returns who is logged in. If the backdoor is in use, this will return the network id of the person that is
     * standing in as the backdoor user.
     *
     * @return String
     */
    public String getLoggedInUserPrincipalName() {
        if (person != null) {
            return person.getPrincipalName();
        }
        return "";
    }

    /**
     * Returns who is logged in. If the backdoor is in use, this will return the network id of the person that is
     * standing in as the backdoor user.
     *
     * @return String
     */
    public String getLoggedInUserPrincipalId() {
        if (person != null) {
            return person.getPrincipalId();
        }
        return "";
    }

    /**
     * Returns a Person object for the current user.
     * @return the KualiUser which is the current user in the system, backdoor if backdoor is set
     */
    public Person getPerson() {
        if (backdoorUser != null) {
            return backdoorUser;
        }
        return person;
    }

    /**
     * Returns the actual current user even if the backdoor is in use.
     * @return the KualiUser which is the current user in the system
     */
    public Person getActualPerson() {
        return person;
    }

    /**
     * override the current user in the system by setting the backdoor networkId, which is useful when dealing with
     * routing or other reasons why you would need to assume an identity in the system
     *
     * @param principalName
     */
    public void setBackdoorUser(String principalName) {
        // only allow backdoor in non-production environments
        if (!isProductionEnvironment()) {
            this.backdoorUser = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
        }
    }

    /**
     * Helper method to check if we are in a production environment.
     *
     * @return boolean indicating if we are in a production environment
     */
    public boolean isProductionEnvironment() {
        return ConfigContext.getCurrentContextConfig().isProductionEnvironment();
    }
    //KULRICE-12287:Add a banner to Rice screens to show which environment you are in
    public String getCurrentEnvironment() {
        return ConfigContext.getCurrentContextConfig().getEnvironment();
    }

    public boolean isDisplayTestBanner() {
        boolean isProd = this.isProductionEnvironment();
        boolean isBannerEnabled = ConfigContext.getCurrentContextConfig().getBooleanProperty("test.banner.enabled", false);
        return !isProd && isBannerEnabled;
    }

    /**
     * clear the backdoor user
     */
    public void clearBackdoorUser() {
        this.backdoorUser = null;
    }

    /**
     * allows adding an arbitrary object to the session and returns a string key that can be used to later access this
     * object from
     * the session using the retrieveObject method in this class. This allows for a prefix to be placed in front of the
     * incremented key. So if the prefix is "searchResults" and the nextObjectKey (local int that holds the key value)
     * is 2 then
     * the new key will be "searchResults3". "searchResults3" will be returned from the method.
     *
     * @param object
     */
    public String addObjectWithGeneratedKey(Serializable object, String keyPrefix) {
        String objectKey = keyPrefix + nextObjectKey.incrementAndGet();
        addObject(objectKey, object);
        return objectKey;
    }

    /**
     * allows adding an arbitrary object to the session and returns a string key that can be used to later access this
     * object from
     * the session using the retrieveObject method in this class. The key is generated from an integer and incremented
     * for every
     * object added.  So the first object added with have a key of "1".  This key will be returned from the method.
     *
     * @param object
     */
    public String addObjectWithGeneratedKey(Object object) {
        String objectKey = nextObjectKey.incrementAndGet() + "";
        addObject(objectKey, object);
        return objectKey;
    }

    /**
     * Allows adding an arbitrary object to the session with static a string key that can be used to later access this
     * object from the session using the retrieveObject method in this class.
     *
     * @param key the mapping key
     * @param object the object to store
     */
    public void addObject(String key, Object object) {
        if (object != null) {
            objectMap.put(key, object);
        } else {
            objectMap.put(key, NULL_VALUE);
        }
    }

    /**
     * Either allows adding an arbitrary object to the session based on a key (if there is not currently an object
     * associated with that key) or returns the object already associated with that key.
     *
     * @see ConcurrentHashMap#putIfAbsent(Object, Object)
     *
     * @param key the mapping key
     * @param object the object to store
     */
    public void addObjectIfAbsent(String key, Object object) {
        if (object != null) {
            objectMap.putIfAbsent(key, object);
        } else {
            objectMap.putIfAbsent(key, NULL_VALUE);
        }
    }

    /**
     * Allows for fetching an object that has been put into the userSession based on the key that would have been
     * returned when adding the object.
     *
     * @param objectKey the mapping key
     *
     * @return the stored object
     */
    public Object retrieveObject(String objectKey) {
        if (objectKey == null) {
            return null;
        }
        Object object = objectMap.get(objectKey);

        if (!NULL_VALUE.equals(object)) {
            return object;
        } else {
            return null;
        }
    }

    /**
     * allows for removal of an object from session that has been put into the userSession based on the key that would
     * have been
     * assigned
     *
     * @param objectKey
     */
    public void removeObject(String objectKey) {
        if (objectKey != null) {
            this.objectMap.remove(objectKey);
        }
    }

    /**
     * allows for removal of an object from session that has been put into the userSession based on a key that starts
     * with the given
     * prefix
     */
    public void removeObjectsByPrefix(String objectKeyPrefix) {
        synchronized (objectMap) {
            List<String> removeKeys = new ArrayList<String>();
            for (String key : objectMap.keySet()) {
                if (key.startsWith(objectKeyPrefix)) {
                    removeKeys.add(key);
                }
            }

            for (String key : removeKeys) {
                this.objectMap.remove(key);
            }
        }
    }

    /**
     * @return boolean indicating if the backdoor is in use
     */
    public boolean isBackdoorInUse() {
        return backdoorUser != null;
    }

    /**
     * Adds the given SessionTicket to the objectMap and returns the associated key
     *
     * @param ticket - SessionTicket to add
     * @return the objectMap key for the ticket as a String
     */
    public String putSessionTicket(SessionTicket ticket) {
        return addObjectWithGeneratedKey(ticket);
    }

    /**
     * Retrieves all SessionTicket instances currently in the UserSession#objectMap
     *
     * @return List<SessionTicket> contained in user session
     */
    public List<SessionTicket> getAllSessionTickets() {
        List<SessionTicket> sessionTickets = new ArrayList<SessionTicket>();

        synchronized (objectMap) {
            for (Object object : objectMap.values()) {
                if (object instanceof SessionTicket) {
                    sessionTickets.add((SessionTicket) object);
                }
            }
        }

        return sessionTickets;
    }

    /**
     * Retrieves all SessionTicket instances currently in the UserSession#objectMap that are of a given ticket type
     *
     * @return List<SessionTicket> contained in user session
     */
    public List<SessionTicket> getAllSessionTicketsByType(String ticketTypeName) {
        List<SessionTicket> sessionTickets = new ArrayList<SessionTicket>();

        for (SessionTicket ticket : getAllSessionTickets()) {
            if (StringUtils.equalsIgnoreCase(ticket.getTicketTypeName(), ticketTypeName)) {
                sessionTickets.add(ticket);
            }
        }

        return sessionTickets;
    }

    /**
     * Determines if the UserSession contains a ticket of the given type that matches the given context. To match context
     * the ticket must
     * contain all the same keys at the given context and the values must be equal with the exception of case
     *
     * @param ticketTypeName - Name of the ticket type to match
     * @param matchContext - Map on context parameters to match on
     * @return true if a ticket was found in the UserSession that matches the request, false if one was not found
     */
    public boolean hasMatchingSessionTicket(String ticketTypeName, Map<String, String> matchContext) {
        boolean hasTicket = false;

        for (SessionTicket ticket : getAllSessionTicketsByType(ticketTypeName)) {
            Map<String, String> ticketContext = ticket.getTicketContext();

            boolean keySetMatch = ticketContext.keySet().equals(matchContext.keySet());
            if (keySetMatch) {
                boolean valuesMatch = true;
                for (String contextKey : ticketContext.keySet()) {
                    String ticketValue = ticketContext.get(contextKey);
                    String matchValue = matchContext.get(contextKey);
                    if (!StringUtils.equalsIgnoreCase(ticketValue, matchValue)) {
                        valuesMatch = false;
                    }
                }

                if (valuesMatch) {
                    hasTicket = true;
                    break;
                }
            }
        }

        return hasTicket;
    }

    /**
     * retrieves an unmodifiable view of the objectMap.
     */
    public Map<String, Object> getObjectMap() {
        return Collections.unmodifiableMap(this.objectMap);
    }

    /**
     * clear the objectMap
     */
    public void clearObjectMap() {
        this.objectMap = new ConcurrentHashMap<String, Object>();
    }
}
