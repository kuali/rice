/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.kuali.core.UserSession;

/**
 * This class will hold all of our thread local variables and accessors for those
 * 
 * 
 */
public class GlobalVariables {

    private static ThreadLocal<UserSession> userSessions = new ThreadLocal<UserSession>();
    private static ThreadLocal<String> hideSessionFromTestsMessage = new ThreadLocal<String>();
    private static ThreadLocal<ErrorMap> errorMaps = new ThreadLocal<ErrorMap>();
    // todo: generic collections
    private static ThreadLocal<ArrayList> messageLists = new ThreadLocal<ArrayList>();
    private static ThreadLocal<HashMap> auditErrorMaps = new ThreadLocal<HashMap>();

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

    /**
     * @return ErrorMap containing error messages.
     */
    public static ErrorMap getErrorMap() {
        return errorMaps.get();
    }

    /**
     * Sets a new (clean) ErrorMap
     * 
     * @param errorMap
     */
    public static void setErrorMap(ErrorMap errorMap) {
        errorMaps.set(errorMap);
    }

    /**
     * @return ArrayList containing messages.
     */
    public static ArrayList getMessageList() {
        return messageLists.get();
    }

    /**
     * Sets a new message list
     * 
     * @param messageList
     */
    public static void setMessageList(ArrayList messageList) {
        messageLists.set(messageList);
    }

    /**
     * @return ArrayList containing audit error messages.
     */
    public static HashMap getAuditErrorMap() {
        return auditErrorMaps.get();
    }

    /**
     * Sets a new (clean) AuditErrorList
     * 
     * @param errorMap
     */
    public static void setAuditErrorMap(HashMap errorMap) {
        auditErrorMaps.set(errorMap);
    }

    /**
     * Clears out GlobalVariable objects
     */
    public static void clear() {
        errorMaps.set(new ErrorMap());
        auditErrorMaps.set(new HashMap());
        messageLists.set(new ArrayList());
    }
}