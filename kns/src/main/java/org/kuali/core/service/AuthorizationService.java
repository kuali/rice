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
package org.kuali.core.service;

import java.util.Set;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.DocumentEntry;


/**
 * Service used to control user access to classes which implement AuthorizationTarget
 */
public interface AuthorizationService {
    /**
     * @param user
     * @param action
     * @param targetType
     * @return true if the given user is a member of a group which is allowed to take the given action on objects matching the given
     *         targetType
     */
    public boolean isAuthorized(UniversalUser user, String action, String targetType);

    /**
     * @param user
     * @param action
     * @param targetType
     * @return Set containing the names of the workgroups authorized for the action on the target type.
     */
    public Set getAuthorizedWorkgroups(String action, String targetType);

    /**
     * Checks security of field. If field is secured by a workgroup, checks user is in workgroup.
     * 
     * @return true if user is authorized to view attribute
     */
    public boolean isAuthorizedToViewAttribute(UniversalUser user, String entryName, String attributeName);
    
//    public void completeInitialization( DataDictionary dataDictionary );
    
    public void setupAuthorizations(DocumentEntry documentEntry) ;
}
