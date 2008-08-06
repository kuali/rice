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

package org.kuali.rice.kns.datadictionary;

import java.util.Set;

import org.kuali.rice.kns.datadictionary.exception.CompletionException;


/**
                The authorization element defines the workgroups which are
                authorized to perform a specified action on a document.

                DD: See AuthorizationDefinition.java.

                JSTL: authorization is a Map which is accessed using a key which is the
                name of the action.  Each entry contains the following keys:
                    * workgroup name of first workgroup
                    * workgroup name of second workgroup
                    etc.
                The corresponding value for each entry is a workgroup ExportMap
 */
public class AuthorizationDefinition extends DataDictionaryDefinitionBase {

    protected String action;

    protected Set<String> authorizedGroups;


    public AuthorizationDefinition() {}


    /**
     * The type of action which is restricted to the contained workgroups.
     * Currently, the only valid action is "initiate"
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
     * Does nothing useful, since real validation is deferred until the validateWorkgroups method below
     * 
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {

        // validation of workgroup names deferred until later, since it requires access to a service which may not have been
        // initialized yet
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "AuthorizationDefinition for action " + getAction();
    }


    public Set<String> getAuthorizedGroups() {
        return this.authorizedGroups;
    }


    /*
                  The workgroups element contains workgroup elements.
                    These define the workgroups that are allowed to take various
                    actions on a document.

                    JSTL: workgroups is a Map which is accessed by a key of "workgroups".
                    This map contains entries with the following keys:
                        * "0"
                        * "1"
                        etc.
                    The corresponding value for each entry is a workgroup name.

                    The workgroup element defines the name of a workgroup.
                    Members of that workgroup are allowed to perform
                    the authorization-specified action on the document.
     */
    public void setAuthorizedGroups(Set<String> authorizedGroups) {
        // validate non-empty group list
        if (authorizedGroups.isEmpty()) {
            throw new CompletionException("empty workgroups list for action '" + action + "'");
        }
        this.authorizedGroups = authorizedGroups;
    }
}
