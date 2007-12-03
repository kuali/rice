/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.messaging;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Service for interfacing with the queue of asynchronous messages.
 * 
 * @see PersistedMessage
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface MessageQueueService {

    public List<PersistedMessage> findByServiceName(QName serviceName, String methodName);

    public void delete(PersistedMessage routeQueue);

    public void save(PersistedMessage routeQueue);

    public List<PersistedMessage> findAll();

    public List<PersistedMessage> findAll(int maxRows);

    /**
     * Finds the PersistedMessage identified by the passed-in primary key, if one is 
     * available, otherwise returns a null object.
     * 
     * @param routeQueueId The primary key routeQueueId of the message desired.
     * @return A populated PersistedMessage instance, if the routeQueueId exists, otherwise 
     *         a null object.
     */
    public PersistedMessage findByRouteQueueId(Long routeQueueId);

//    public List getNextDocuments();

    /**
     * Returns a List of RouteQueue documents which are queued for routing.  Will not
     * return more RouteQueues than the value of maxDocuments.
     */
    public List<PersistedMessage> getNextDocuments(Integer maxDocuments);

    public PersistedMassagePayload findByPersistedMessageByRouteQueueId(Long routeQueueId);
    
    /**
     * Finds the persisted messages that match the values passed into the 
     * criteriaValues Map, with an auto-wildcard function, if no wildcard 
     * is passed in.
     * 
     * @param criteriaValues A Map of Key/Value pairs, where the Key is a string holding the field 
     *                       name, and the Value is a string holding the value to match.
     * @param maxRows the maximum number of rows to return from the query.  If -1, then all rows will be returned.
     * @return A populated (or empty) list containing the results of the search.  If no matches are made, 
     *         an empty list will be returned.
     */
    public List<PersistedMessage> findByValues(Map<String, String> criteriaValues, int maxRows);

    /**
     * Used to determine the maximum number of retries allowed by the system before the 
     * message goes into Exception.
     * 
     * @return The max retry attempts set in the system.
     */
    public Integer getMaxRetryAttempts();

    public PersistedMessage getMessage(ServiceInfo serviceInfo, AsynchronousCall methodCall);
}