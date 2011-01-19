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
package org.kuali.rice.kns.util;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class will hold all of our thread local variables and accessors for those
 *
 *
 */
public class GlobalVariables {

    private static ThreadLocal<UserSession> userSessions = new ThreadLocal<UserSession>();
    private static ThreadLocal<String> hideSessionFromTestsMessage = new ThreadLocal<String>();
    private static ThreadLocal<KualiForm> kualiForms = new ThreadLocal<KualiForm>();
    
    private static ThreadLocal<MessageMap> messageMaps = new ThreadLocal<MessageMap>()  {
		@Override
		protected MessageMap initialValue() {
			return new MessageMap();
		}
	};

    private static ThreadLocal<MessageList> messageLists = new ThreadLocal<MessageList>() {
		@Override
		protected MessageList initialValue() {
			return new MessageList();
		}
	};
	
    private static ThreadLocal<HashMap<String, AuditCluster>> auditErrorMaps = new ThreadLocal<HashMap<String, AuditCluster>>() {
    	@Override
    	protected HashMap<String, AuditCluster> initialValue() {
    		return new HashMap<String, AuditCluster>();
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

    /**
     * @return ArrayList containing messages.
     */
    public static MessageList getMessageList() {
        return messageLists.get();
    }

    /**
     * Sets a new message list
     *
     * @param messageList
     */
    public static void setMessageList(MessageList messageList) {
        messageLists.set(messageList);
    }

    /**
     * @return ArrayList containing audit error messages.
     */
    public static Map<String, AuditCluster> getAuditErrorMap() {
        return auditErrorMaps.get();
    }

    /**
     * Sets a new (clean) AuditErrorList
     *
     * @param errorMap
     */
    public static void setAuditErrorMap(HashMap<String, AuditCluster> errorMap) {
        auditErrorMaps.set(errorMap);
    }

    /**
     * @return KualiForm that has been assigned to this thread of execution.
     */
    public static KualiForm getKualiForm() {
        return kualiForms.get();
    }

    /**
     * sets the kualiForm object into the global variable for this thread
     *
     * @param kualiForm
     */
    public static void setKualiForm(KualiForm kualiForm) {
    	kualiForms.set(kualiForm);
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
        auditErrorMaps.set(new HashMap<String, AuditCluster>());
        messageLists.set(new MessageList());
        requestCaches.set(new HashMap<String,Object>() );
        kualiForms.set(null);
    }
}
