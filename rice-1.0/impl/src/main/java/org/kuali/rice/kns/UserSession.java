/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.SessionTicket;
import org.kuali.rice.kns.web.EditablePropertiesHistoryHolder;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * Holds info about the User Session
 */
public class UserSession implements Serializable {

    private static final long serialVersionUID = 4532616762540067557L;

    private Person person;
    private Person backdoorUser;
    private int nextObjectKey;
    private Map<String,Object> objectMap;
    private String kualiSessionId;
    private EditablePropertiesHistoryHolder editablePropertiesHistoryHolder;

    /**
	 * @return the kualiSessionId
	 */
	public String getKualiSessionId() {
		return this.kualiSessionId;
	}

	/**
	 * @param kualiSessionId the kualiSessionId to set
	 */
	public void setKualiSessionId(String kualiSessionId) {
		this.kualiSessionId = kualiSessionId;
	}

	// KULRICE-4443 Allow workflow sessions to fail over between nodes to prevent document loss by removing transient modifier
	private Map<String,KualiWorkflowDocument> workflowDocMap = new HashMap<String,KualiWorkflowDocument>();

    /**
     * Take in a netid, and construct the user from that.
     * 
     * @param principalName
     */
    public UserSession(String principalName) {
        this.person = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
        if (this.person == null) {
        	throw new IllegalArgumentException("Failed to locate a principal with principal name '" + principalName + "'");
        }
        this.nextObjectKey = 0;
        this.objectMap = new HashMap<String,Object>();
        this.editablePropertiesHistoryHolder = new EditablePropertiesHistoryHolder();
    }

    
    /**
     * @return the principalId of the current user in the system, backdoor principalId if backdoor is set
     */
    public String getPrincipalId() {
        if (backdoorUser != null) {
            return backdoorUser.getPrincipalId();
        }
        else {
            return person.getPrincipalId();
        }
    }

    /**
     * @return the principalName of the current user in the system, backdoor principalName if backdoor is set
     */
    public String getPrincipalName() {
        if (backdoorUser != null) {
            return backdoorUser.getPrincipalName();
        }
        else {
            return person.getPrincipalName();
        }
    }

    
    /**
     * This returns who is logged in. If the backdoor is in use, this will return the network id of the person that is standing in
     * as the backdoor user.
     * 
     * @return String
     */
    public String getLoggedInUserPrincipalName() {
    	if ( person != null ) {
    		return person.getPrincipalName();
    	} else {
    		return "";
    	}
    }

    /**
     * @return the KualiUser which is the current user in the system, backdoor if backdoor is set
     */
    public Person getPerson() {
        if (backdoorUser != null) {
            return backdoorUser;
        }
        else {
            return person;
        }
    }

    /**
     * override the current user in the system by setting the backdoor networkId, which is useful when dealing with routing or other
     * reasons why you would need to assume an identity in the system
     * 
     * @param principalName
     */
    public void setBackdoorUser(String principalName) {
       // only allow backdoor in non-production environments
       if ( !KNSServiceLocator.getKualiConfigurationService().isProductionEnvironment() ) {
        this.backdoorUser = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
        this.workflowDocMap = new HashMap<String,KualiWorkflowDocument>();
       }
    }

    /**
     * clear the backdoor user
     * 
     */
    public void clearBackdoorUser() {
        this.backdoorUser = null;
        this.workflowDocMap = new HashMap<String,KualiWorkflowDocument>();
    }

    /**
     * allows adding an arbitrary object to the session and returns a string key that can be used to later access this object from
     * the session using the retrieveObject method in this class. This allows for a prefix to be placed in front of the 
     * incremented key. So if the prefix is "searchResults" and the nextObjectKey (local int that holds the key value) is 2 then
     * the new key will be "searchResults3". "searchResults3" will be returned from the method.
     * 
     * @param object
     */
    public String addObject(Object object, String keyPrefix) {
        String objectKey = keyPrefix + nextObjectKey++;
        objectMap.put(objectKey, object);
        return objectKey;
    }

    /**
     * allows adding an arbitrary object to the session with static a string key that can be used to later access this object from
     * the session using the retrieveObject method in this class
     * 
     * @param object
     * 
     */
    public void addObject(String key, Object object) {

        objectMap.put(key, object);

    }


    /**
     * allows adding an arbitrary object to the session and returns a string key that can be used to later access this object from
     * the session using the retrieveObject method in this class. The key is generated from an integer and incremented for every 
     * object added.  So the first object added with have a key of "1".  This key will be returned from the method.
     * 
     * @param object
     */
    public String addObject(Object object) {
        String objectKey = nextObjectKey++ + "";
        objectMap.put(objectKey, object);
        return objectKey;
    }

    /**
     * allows for fetching an object that has been put into the userSession based on the key that would have been returned when
     * adding the object
     * 
     * @param objectKey
     */
    public Object retrieveObject(String objectKey) {
        return this.objectMap.get(objectKey);
    }

    /**
     * allows for removal of an object from session that has been put into the userSession based on the key that would have been
     * assigned
     * 
     * @param objectKey
     */
    public void removeObject(String objectKey) {
        this.objectMap.remove(objectKey);
    }

    /**
     * allows for removal of an object from session that has been put into the userSession based on a key that starts with the given
     * prefix
     */
    public void removeObjectsByPrefix(String objectKeyPrefix) {
        List<String> removeKeys = new ArrayList<String>();
        for (Iterator<String> iter = objectMap.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            if (key.startsWith(objectKeyPrefix)) {
                removeKeys.add(key);
            }
        }

        for (Iterator<String> iter = removeKeys.iterator(); iter.hasNext();) {
            String key = iter.next();
            this.objectMap.remove(key);
        }
    }

    /**
     * @return boolean indicating if the backdoor is in use
     */
    public boolean isBackdoorInUse() {
        return backdoorUser != null;
    }

    /**
     * retrieve a flexdoc from the userSession based on the document id
     * 
     * @param docId
     */
    public KualiWorkflowDocument getWorkflowDocument(String docId) {
        if (workflowDocMap == null) {
            workflowDocMap = new HashMap<String,KualiWorkflowDocument>();
        }
        if (workflowDocMap.containsKey(docId)) {
            return workflowDocMap.get(docId);
        }
        else {
            return null;
        }
    }

    /**
     * set a flexDoc into the userSession which will be stored under the document id
     */
    public void setWorkflowDocument(KualiWorkflowDocument workflowDocument) {
        try {
            if (workflowDocMap == null) {
                workflowDocMap = new HashMap<String,KualiWorkflowDocument>();
            }
            workflowDocMap.put(workflowDocument.getRouteHeaderId().toString(), workflowDocument);
        }
        catch (WorkflowException e) {
            throw new IllegalStateException("could not save the document in the session msg: " + e.getMessage());
        }
    }
    
    /**
     * @return this session's editable properties history holder
     */
    public EditablePropertiesHistoryHolder getEditablePropertiesHistoryHolder() {
    	return editablePropertiesHistoryHolder;
    }
    
	/**
	 * Adds the given SessionTicket to the objectMap and returns the associated key
	 * 
	 * @param ticket
	 *            - SessionTicket to add
	 * @return the objectMap key for the ticket as a String
	 */
	public String putSessionTicket(SessionTicket ticket) {
		return addObject(ticket);
	}

	/**
	 * Retrieves all SessionTicket instances currently in the UserSession#objectMap
	 * 
	 * @return List<SessionTicket> contained in user session
	 */
	public List<SessionTicket> getAllSessionTickets() {
		List<SessionTicket> sessionTickets = new ArrayList<SessionTicket>();

		for (Object object : objectMap.values()) {
			if (object instanceof SessionTicket) {
				sessionTickets.add((SessionTicket) object);
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
	 * Determines if the UserSession contains a ticket of the given type that matches the given context. To match context the ticket must
	 * contain all the same keys at the given context and the values must be equal with the exception of case
	 * 
	 * @param ticketTypeName
	 *            - Name of the ticket type to match
	 * @param matchContext
	 *            - Map on context parameters to match on
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
}
