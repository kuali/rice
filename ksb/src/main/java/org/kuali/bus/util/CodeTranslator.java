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
package org.kuali.bus.util;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.RiceConstants;

/**
 * Utility class to translate the various codes used in Eden into labels and vice versa.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CodeTranslator {

    public static final Map<String, String> routeQueueStatusLabels = getRouteQueueStatusLabels();

    private static Map<String, String> getRouteQueueStatusLabels() {
        Map<String, String> routeQueueStatusLabels = new HashMap<String, String>();
        routeQueueStatusLabels.put(RiceConstants.ROUTE_QUEUE_EXCEPTION, RiceConstants.ROUTE_QUEUE_EXCEPTION_LABEL);
        routeQueueStatusLabels.put(RiceConstants.ROUTE_QUEUE_QUEUED, RiceConstants.ROUTE_QUEUE_QUEUED_LABEL);
        routeQueueStatusLabels.put(RiceConstants.ROUTE_QUEUE_ROUTING, RiceConstants.ROUTE_QUEUE_ROUTING_LABEL);
        return routeQueueStatusLabels;
    }

    static public String getRouteQueueStatusLabel(String routeQueueStatusCode) {
        return routeQueueStatusLabels.get(routeQueueStatusCode);
    }

}
