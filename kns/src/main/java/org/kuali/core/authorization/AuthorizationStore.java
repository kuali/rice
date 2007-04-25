/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;


/**
 * AuthorizationStore manages authorization information.
 */
public class AuthorizationStore {
    // logger
    private static Log LOG = LogFactory.getLog(AuthorizationStore.class);

    // authorizationsByTargetType = HashMap( targetType, HashMap( action, Set(groupName) ) )
    private HashMap authorizationsByTargetType;


    /**
     * Constructs an AuthorizationServiceImpl instance
     */
    public AuthorizationStore() {
        authorizationsByTargetType = new HashMap();
    }


    /**
     * @see org.kuali.core.service.AuthorizationService#isAuthorized(String, String, String)
     */
    public boolean isAuthorized(UniversalUser user, String action, String targetType) {
        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }
        if (StringUtils.isBlank(action)) {
            throw new IllegalArgumentException("invalid (blank) action");
        }
        if (StringUtils.isBlank(targetType)) {
            throw new IllegalArgumentException("invalid (blank) targetType");
        }

        LOG.debug("checking authorization for (user,action,targetType) = (" + user.getPersonUserIdentifier() + "," + action + "," + targetType + ")");

        boolean isAuthorized = false;

        Set authorizedGroups = authorizedGroups(action, targetType);
        if ((authorizedGroups != null) && !authorizedGroups.isEmpty()) {
            for (Iterator i = authorizedGroups.iterator(); !isAuthorized && i.hasNext();) {
                String group = (String) i.next();

                isAuthorized = user.isMember( group );
            }
        }

        LOG.debug("returning " + (isAuthorized ? "isAuthorized" : "isNotAuthorized") + " for (user,action,targetType) = (" + user.getPersonUserIdentifier() + "," + action + "," + targetType + ")");

        return isAuthorized;
    }


    /**
     * Add authorization for <code>{@link org.kuali.core.service.AuthorizationService}</code>
     * 
     * @param groupName
     * @param action
     * @param targetType
     * 
     * @see org.kuali.core.service.AuthorizationService
     */
    public void addAuthorization(String groupName, String action, String targetType) {
        if (StringUtils.isBlank(groupName)) {
            throw new IllegalArgumentException("invalid (blank) groupName");
        }
        if (StringUtils.isBlank(action)) {
            throw new IllegalArgumentException("invalid (blank) action");
        }
        if (StringUtils.isBlank(targetType)) {
            throw new IllegalArgumentException("invalid (blank) targetType");
        }

        LOG.debug("adding authorization for (group,action,targetType) = (" + groupName + "," + action + "," + targetType + ")");

        Map authorizedActions = authorizedActions(targetType);
        if (authorizedActions == null) {
            authorizedActions = new HashMap();
        }
        Set authorizedGroups = authorizedGroups(authorizedActions, targetType);
        if (authorizedGroups == null) {
            authorizedGroups = new HashSet();
        }

        authorizedGroups.add(groupName);
        authorizedActions.put(action, authorizedGroups);
        authorizationsByTargetType.put(targetType, authorizedActions);
    }


    /**
     * @param action
     * @param targetType
     * @return possibly empty Set of groupnames of groups which are authorized to perform the given action for the given targetType
     */
    private Set authorizedGroups(String action, String targetType) {
        Set groupnameSet = null;

        Map actionMap = (Map) authorizationsByTargetType.get(targetType);
        if (actionMap != null) {
            groupnameSet = (Set) actionMap.get(action);
        }

        return groupnameSet;
    }

    /**
     * @param authorizedActions
     * @param action
     * @return possibly empty Set of groupnames of groups which are authorized to perform the given action from the given map of
     *         actions
     */
    public Set authorizedGroups(Map authorizedActions, String action) {
        Set groupnameSet = (Set) authorizedActions.get(action);

        return groupnameSet;
    }

    /**
     * @param targetType
     * @return possibly empty Map of actions associated with the given targetType
     */
    public Map authorizedActions(String targetType) {
        Map actionMap = (Map) authorizationsByTargetType.get(targetType);

        return actionMap;
    }
}
