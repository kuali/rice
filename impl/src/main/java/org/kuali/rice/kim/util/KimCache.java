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
package org.kuali.rice.kim.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides ThreadLocal caches that can be cleared out by thread-aware code to ensure that
 * cached results are not available to the incorrect code when threads are shared. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimCache {
    private static ThreadLocal<Map<String,Object>> requestCaches = new ThreadLocal<Map<String,Object>>();

    public static Object getRequestCache( String cacheName ) {
    	return requestCaches.get().get(cacheName);
    }
    
    public static void setRequestCache( String cacheName, Object cacheObject ) {
    	requestCaches.get().put(cacheName, cacheObject);
    }

    /**
     * Clears out the cache objects
     */
    public static void init() {
        requestCaches.set(new HashMap<String,Object>() );
    }

}
