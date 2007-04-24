/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;

import edu.iu.uis.eden.user.UserService;


/**
 * This interface defines methods that a KualiUser Service must provide.
 * 
 * 
 */
public interface UniversalUserService extends UserService {

    /**
     * Return the UniversalUser according to the UserId supplied.
     * 
     * @param userId Accepts any UserID object that meets the UserId interface
     * @return Returns a populated UniversalUser object if the User is found, otherwise throws a UserNotFoundException
     * @throws UserNotFoundException
     */
    public UniversalUser getUniversalUser(UserId userId) throws UserNotFoundException;
    
    public UniversalUser getUniversalUserByAuthenticationUserId( String authenticationUserId ) throws UserNotFoundException;
    
    /**
     * Return the UniversalUser according to the universal user ID supplied.
     * 
     * @param personUniversalIdentifier
     * @return Returns a populated UniversalUser object if the User is found, otherwise throws a UserNotFoundException
     * @throws UserNotFoundException
     */
    public UniversalUser getUniversalUser(String personUniversalIdentifier) throws UserNotFoundException;
    
    /**
     * Compares the universal user ID passed in with that in the UniversalUser object.  If they are the same, it returns the
     * original object.  Otherwise, it pulls the UniversalUser from persistent storage based on the sourcePersonUniversalIdentifier.
     */
    public UniversalUser updateUniversalUserIfNecessary(String sourcePersonUniversalIdentifier, UniversalUser currentSourceUniversalUser);

    /**
     * Finds UniversalUser objects based upon a Map with propertyName->value.
     */
    public Collection findUniversalUsers(Map fieldValues);

    /**
     * Finds UniversalUser objects based upon a Map with propertyName->value where the propertyNames are relative
     * to the original business object, this does a back-end join with the universal user data.
     */
    public Collection findWithUniversalUserJoin(Class businessObjectClass, Map fieldValues, boolean unbounded);

    /**
     * Returns if any of the propertyNames in the Map keys represent properties on a child UniversalUser object
     * on the passed in business object class.
     */
    public boolean hasUniversalUserProperty(Class businessObjectClass, Map fieldValues);
    
    /**
     * 
     * This method takes a map on its way to populate a business object and replaces all user identifiers with their corresponding universal users
     * @param businessObject
     * @param fieldValues
     * @return
     */
    public Map resolveUserIdentifiersToUniversalIdentifiers(PersistableBusinessObject businessObject, Map fieldValues);
 
    public Map<String,KualiModuleUser> getModuleUsers( UniversalUser user );

    /**
     * Checks if the user is a member of a given KualiGroup (workgroup)
     */
    public boolean isMember( UniversalUser user, String groupName );
    
    /**
     * Checks if the user is a member of a given KualiGroup (workgroup)
     */
    public boolean isMember(UniversalUser user, KualiGroup kualiGroup);
 
    /**
     * Gets all groups to which a user belongs.
     */
    public List<KualiGroup> getUsersGroups( UniversalUser user );
    
    /**
     * Checks if the user belongs to the established supervisor workgroup.
     */
    public boolean isSupervisorUser( UniversalUser user );
    
    /**
     * Checks if the user belongs to the established workflow exception workgroup.
     */
    public boolean isWorkflowExceptionUser( UniversalUser user );
    
    /**
     * Loads a map of all the user's KualiModuleUser properties.
     */
    public Map<String,Map<String,String>> loadModuleUserProperties( UniversalUser user );
     
}