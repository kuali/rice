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
package org.kuali.rice.krad.util;

import org.kuali.rice.krad.UserSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all of our thread local variables and accessors for those
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class GlobalVariables {

    private GlobalVariables() {
        throw new UnsupportedOperationException("do not call");
    }

    private static ThreadLocal<UserSession> userSessions = new ThreadLocal<UserSession>();
    private static ThreadLocal<String> hideSessionFromTestsMessage = new ThreadLocal<String>();

    private static ThreadLocal<MessageMap> messageMaps = new ThreadLocal<MessageMap>()  {
		@Override
		protected MessageMap initialValue() {
			return new MessageMap();
		}
	};
    
    private static ThreadLocal<Map<String,Object>> requestCaches = new ThreadLocal<Map<String,Object>>() {
    	@Override
		protected HashMap<String, Object> initialValue() {
    		return new HashMap<String, Object>();
    	}
    };

    /**
     * @return the UserSession that has been assigned to this thread of execution it is important that this not be called by
     *         anything that lives outside
     */
    public static UserSession getUserSession() {
        String message = hideSessionFromTestsMessage.get();
        if (message != null) {
            throw new RuntimeException(message);
        }
        return userSessions.get();
    }

    /**
     * Sets an error message for tests that try to use the session without declaring it.
     * This method should be use by only KualiTestBase, not by other test code and especially not by production code.
     *
     * @param message the detail to throw, or null to allow access to the session
     */
    public static void setHideSessionFromTestsMessage(String message) {
        hideSessionFromTestsMessage.set(message);
    }

    /**
     * sets the userSession object into the global variable for this thread
     *
     * @param userSession
     */
    public static void setUserSession(UserSession userSession) {
        userSessions.set(userSession);
    }
    
    public static MessageMap getMessageMap() {
    	return messageMaps.get();
    }

    /**
     * Merges a message map into the global variables error map
     * @param messageMap
     */
    public static void mergeErrorMap(MessageMap messageMap) {
        getMessageMap().getErrorMessages().putAll(messageMap.getErrorMessages());
        getMessageMap().getWarningMessages().putAll(messageMap.getWarningMessages());
        getMessageMap().getInfoMessages().putAll(messageMap.getInfoMessages());
    }
    
    /**
     * Sets a new (clean) MessageMap
     *
     * @param messageMap
     */
    public static void setMessageMap(MessageMap messageMap) {
    	messageMaps.set(messageMap);
    }

    public static Object getRequestCache( String cacheName ) {
    	return requestCaches.get().get(cacheName);
    }

    public static void setRequestCache( String cacheName, Object cacheObject ) {
    	requestCaches.get().put(cacheName, cacheObject);
    }

    /**
     * Clears out GlobalVariable objects with the exception of the UserSession
     */
    public static void clear() {
        messageMaps.set(new MessageMap());
        requestCaches.set(new HashMap<String,Object>() );
    }
}
