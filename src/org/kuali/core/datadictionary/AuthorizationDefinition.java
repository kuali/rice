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

package org.kuali.core.datadictionary;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.CompletionException;
import org.kuali.core.service.KualiGroupService;


/**
 * An authorization definition consists of an action (String) and a set of groups authorized to perform that action.
 * 
 * 
 */
public class AuthorizationDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(AuthorizationDefinition.class);

    private String action;

    private Set authorizedGroups;


    public AuthorizationDefinition() {
        LOG.debug("creating new AuthorizationDefinition");

        authorizedGroups = new HashSet();
    }


    /**
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return action to be associated with this authorizationDefinition
     */
    public String getAction() {
        return action;
    }


    /**
     * Adds the given groupName to the set of authorized groupNames
     * 
     * @param groupName
     */
    public void addGroupName(String groupName) {
        if (StringUtils.isBlank(groupName)) {
            throw new IllegalArgumentException("invalid (blank) groupName");
        }

        authorizedGroups.add(groupName);
    }

    /**
     * @return current set of groupNames
     */
    public Set getGroupNames() {
        return Collections.unmodifiableSet(authorizedGroups);
    }


    /**
     * Does nothing useful, since real validation is deferred until the validateWorkgroups method below
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        // validate non-empty group list
        if (authorizedGroups.isEmpty()) {
            throw new CompletionException("empty workgroups list for action '" + action + "'");
        }

        // validation of workgroup names deferred until later, since it requires access to a service which may not have been
        // initialized yet
    }


    public void validateWorkroups(KualiGroupService kualiGroupService) {
        //
        // commented out because it increase webapp startup time by 30 seconds or more

        // // validate existence of all specified groups
        // for (Iterator i = authorizedGroups.iterator(); i.hasNext();) {
        // String groupName = (String) i.next();
        //
        // if (!kualiGroupService.groupExists(groupName)) {
        // throw new CompletionException("unable to retrieve group named '" + groupName + "'");
        // }
        // }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "AuthorizationDefinition for action " + getAction();
    }
}
