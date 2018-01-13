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
package org.kuali.rice.krad.uif.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides cache objects for KIM authorization during a single request
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RequestAuthorizationCache implements Serializable {
    private static final long serialVersionUID = 4773874972787299349L;

    private Map<String, Boolean> permissionResultCache;

    public RequestAuthorizationCache() {
        permissionResultCache = new HashMap<String, Boolean>();
    }

    /**
     * Map that can be used to cache results of permission checks for a request
     *
     * @return
     */
    public Map<String, Boolean> getPermissionResultCache() {
        return permissionResultCache;
    }

    public boolean hasPermissionResult(String resultKey) {
        return permissionResultCache.containsKey(resultKey);
    }

    public Boolean getPermissionResult(String resultKey) {
        Boolean result = null;

        if (hasPermissionResult(resultKey)) {
            result = Boolean.valueOf(permissionResultCache.get(resultKey));
        }

        return result;
    }

    public void addPermissionResult(String resultKey, boolean result) {
        permissionResultCache.put(resultKey, Boolean.valueOf(result));
    }
}
