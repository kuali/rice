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
package edu.iu.uis.eden.user;

import java.util.List;

import edu.iu.uis.eden.XmlLoader;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;

/**
 * The UserService provides retrieval and search capibilities for WorkflowUsers.
 * It also provides the a factory method for creating blank users.
 * 
 * The UserService extends XmlLoader so it is possible to import users from XML
 * if the implementing class provides an xml loading implementation.
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface UserService extends XmlLoader {
    
	/**
	 * Retrieves the capabilities of this user service.  This essentially provides the core with information
	 * on the kinds of activities which the User service can perform such as reporting, editing, etc.  This
	 * is primarily used by the web-tier of the application to aid in delivery of web-based user services.
	 *
	 * @since 2.2
	 */
	public UserCapabilities getCapabilities();
	
    /**
     * Retrieve the WorkflowUser who has the supplied UserId.  If the user does
     * not have a WorkflowId assigned, assign the user a WorkflowId before 
     * returning the WorkflowUser object
     * 
     * @param the UserId of the user to lookup
     * @return the user which matches the given id if no user could be found
     * @throws EdenUserNotFoundException if no user could be located for the given id or if
     * the given UserId is of an invalid type
     */
    public WorkflowUser getWorkflowUser(UserId userId) throws EdenUserNotFoundException;

    /**
     * Similar to getWorkflowUser(UserId) except that the id is represented as a UserIdVO
     * instead of a UserId.
     *
     * @param the UserIdVO of the user to lookup
     * @return the user which matches the given id or null if no user could be found
     * @throws EdenUserNotFoundException if no user could be located for the given id or if
     * the given UserId is of an invalid type
     */
    public WorkflowUser getWorkflowUser(UserIdVO userId) throws EdenUserNotFoundException;
    
    /**
     * Invokes a search for WorkflowUsers using an example user object as the search criteria.
     * 
     * @param user a WorkflowUser object containing an example user to search for, this will usually
     * consist of a user object with some of the fields filled in
     * @param useWildCards if true then wildcards should be used on the various WorkflowUser fields
     * @return a List of WorkflowUsers which match the given criteria
     */
    public List<WorkflowUser> search(WorkflowUser user, boolean useWildCards);
    
    /**
     * Returns an empty WorkflowUser object.  Since the WorkflowUser implementation is institution-specific
     * this method allows for the creation of a new WorkflowUser instance.  This will typically be used
     * in conjuction with the search method.
     * 
     * @return an empty WorkflowUser object
     */
    public WorkflowUser getBlankUser();

    /**
     * Saves the given WorkflowUser to the underlying data store.  If this service does not support persistence
     * then an UnsupportedOperationException will be thrown.
     * 
     * @since 2.2
     */
    public void save(WorkflowUser user);
    
    /**
     * Make a copy of the given WorkflowUser.  If preserveKeys is true then the keys need to be preserved, 
     * otherwise they should be set to null on the copy.
     * 
     * This code can assume that the user being passed in was produced by this service.  Therefore, the user
     * instance passed in can be safely cast to the appropriate implementation class if necessary.
     * 
     * @since 2.2
     */
    public WorkflowUser copy(WorkflowUser user, boolean preserveKeys);
}
