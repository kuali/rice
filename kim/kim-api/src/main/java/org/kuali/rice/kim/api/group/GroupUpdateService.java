/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.api.group;

import org.kuali.rice.kim.util.KimConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "GroupUpdateService", targetNamespace = KimConstants.Namespaces.KIM_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupUpdateService {

    /**
     * Creates a new group using the given Group.
     *
     * <p>
     * This will attempt to create a new Group
     * </p>
     *
     * @param group The new group to be created
     * @return a the Group that has been created.
     */
    @WebMethod(operationName = "createGroup")
    @WebResult(name = "group")
	Group createGroup(@WebParam(name="group") Group group) throws UnsupportedOperationException;

	/**
     * Updates a group using the given Group.
     *
     * <p>
     * This will attempt to update an existing group with data from the passed in group
     * </p>
     *
     * @param groupId Id of the Group to be updated
     * @param group   Group object to use for update
     * @return a the Group that has been updated.
     */
    @WebMethod(operationName = "updateGroup")
    @WebResult(name = "group")
    Group updateGroup(@WebParam(name="groupId") String groupId, @WebParam(name="group") Group group) throws UnsupportedOperationException;

    /**
     * Adds the group with the id supplied in childId as a member of the group with the id supplied in parentId.
     *
     * @param childId Id of the Group to be added to the members of Parent
     * @param parentId  Id of the Group object to add the member to
     * @return true if the member was added successfully.
     */
    @WebMethod(operationName = "addGroupToGroup")
    @WebResult(name = "addedToGroup")
    boolean addGroupToGroup(@WebParam(name="childId") String childId, @WebParam(name="parentId") String parentId) throws UnsupportedOperationException;

    /**
     * Removes the group with the id supplied in childId from the group with the id supplied in parentId.
     *
     * @param childId Id of the Group to be removed from the members of Parent
     * @param parentId  Id of the Group object to remove the member from
     * @return true if the member was removed successfully.
     */
    @WebMethod(operationName = "removeGroupToGroup")
    @WebResult(name = "removedFromGroup")
    boolean removeGroupFromGroup(@WebParam(name="childId") String childId, @WebParam(name="parentId") String parentId) throws UnsupportedOperationException;

    /**
     * Add the principal with the given principalId as a member of the group with the given groupId.
     *
     * @param principalId Id of the Principal to be added to the members of the Parent Group
     * @param groupId  Id of the Group object to add the member to
     * @return true if the member was added successfully.
     */
    @WebMethod(operationName = "addPrincipalToGroup")
    @WebResult(name = "addedToGroup")
    boolean addPrincipalToGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws UnsupportedOperationException;

    /**
     * Removes the member principal with the given principalId from the group with the given groupId.
     *
     * @param principalId Id of the Principal to be removed from the members of the Parent Group
     * @param groupId  Id of the Group object to remove the member from
     * @return true if the member was removed successfully.
     */
    @WebMethod(operationName = "removePrincipalToGroup")
    @WebResult(name = "removedFromGroup")
    boolean removePrincipalFromGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws UnsupportedOperationException;

    /**
     * Removes all members from the group with the given groupId.
     *
     * @param groupId  Id of the Group object to remove the members from
     */
    @WebMethod(operationName = "removeAllMembers")
    void removeAllMembers( @WebParam(name="groupId") String groupId ) throws UnsupportedOperationException;
}
