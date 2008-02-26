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
package org.kuali.rice.kim.service;

import java.util.List;

import org.kuali.rice.kim.bo.Principal;

/**
 * Service API for accessing KIM Principal services.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface PrincipalService {
    /**
     * KIM service API method that returns a complete collection of Principal objects for the application.
     * 
     * @return         List of Principal objects for the application
     * 
     */
    public List<Principal> getAllPrincipals();
    
    /**
     * KIM service API method that returns a complete collection of Principal names for the 
     * application.
     * 
     * @return         List of Principal names for the application
     * 
     */
    public List<String> getAllPrincipalNames();
    
    /**
     * Determines if a given principal is member of a given group.
     * 
     * @param principalName
     * @param groupName
     * @return boolean
     */
    public boolean isMemberOfGroup(String principalName, String groupName);

    /**
     * This method returns a fully populate Principal object given the passed in principalName.
     * 
     * @param principalName
     * @return Principal
     */
    public Principal getPrincipal(String principalName);
    
    /**
     * This method returns all of the fully populated Principal objects associated with a given person.
     * 
     * @param personId
     * @return List<Principal>
     */
    public List<Principal> getPrincipalsForPerson(Long personId);
    
    /**
     * This method returns all of the principal names associated with a given person.
     * 
     * @param personId
     * @return List<String>
     */
    public List<String> getPrincipalNamesForPerson(Long personId);
    
    /**
     * This method returns all of the fully populated Principal objects associated with a given entity.
     * 
     * @param entityId
     * @return List<Principal>
     */
    public List<Principal> getPrincipalsForEntity(Long entityId);
    
    /**
     * This method returns all of the principal names associated with a given entity.
     * 
     * @param entityId
     * @return List<String>
     */
    public List<String> getPrincipalNamesForEntity(Long entityId);
}
