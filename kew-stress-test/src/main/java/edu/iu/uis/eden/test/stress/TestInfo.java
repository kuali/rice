/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.test.stress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.iu.uis.eden.clientapp.vo.UserIdVO;


public class TestInfo {

    private static List routeHeaderIds = Collections.synchronizedList(new ArrayList());
    private static List users = Collections.synchronizedList(new ArrayList());
    private static long serverCalls;
    private static long documentApprovals;
    private static long documentAcks;
    private static long documentFYIs;
    
    public static long getDocumentAcks() {
        return documentAcks;
    }
    public static long getDocumentApprovals() {
        return documentApprovals;
    }
    public static long getDocumentFYIs() {
        return documentFYIs;
    }
    public static List getRouteHeaderIds() {
        return routeHeaderIds;
    }
    public static void addRouteHeaderId(Long routeHeaderId) {
        routeHeaderIds.add(routeHeaderId);
    }
    public static void addUser(UserIdVO user) {
        users.add(user);
    }
    public static long getServerCalls() {
        return serverCalls;
    }
    public static void setServerCalls(long serverCalls) {
        TestInfo.serverCalls = serverCalls;
    }
    public static Long getRandomRouteHeaderId() {
        if (routeHeaderIds.isEmpty()) {
            return null;
        }
        Long routeHeaderId = null;
        synchronized (routeHeaderIds) {
            routeHeaderId = (Long) StressTestUtils.getRandomListObject(routeHeaderIds);
            routeHeaderIds.remove(routeHeaderId);
        }
        return routeHeaderId;
    }
    public static UserIdVO getRandomUser() {
        if (users.isEmpty()) {
            return null;
        }
        return (UserIdVO) StressTestUtils.getRandomListObject(users);
    }
    
    public synchronized static void markCallToServer() {
        serverCalls++;
    }
    public synchronized static void markDocumentAcks() {
        documentAcks++;
    }
    public synchronized static void markDocumentApprovals() {
        documentApprovals++;
    }
    public synchronized static void markDocumentFYIs() {
        TestInfo.documentFYIs++;
    }
}
