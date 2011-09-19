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

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.KimConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@WebService(name = "groupServiceSoap", targetNamespace = KimConstants.Namespaces.KIM_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupService {

    /**
     * Get all the groups for a given principal.
     *
     * <p>
     * This will include all groups directly assigned as well as those inferred
     * by the fact that they are members of higher level groups.
     * </p>
     *
     * @param principalId The id of the Principal
     * @return a list of Group objects in which the given Principal is a member of.  An empty list is returned if an invalid or
     *         non-existant principalId is supplied.
     */
    @WebMethod(operationName = "getGroupsByPrincipalId")
    @XmlElementWrapper(name = "groups", required = true)
    @XmlElement(name = "group", required = false)
    @WebResult(name = "groups")
    List<Group> getGroupsByPrincipalId(@WebParam(name = "principalId") String principalId) throws RiceIllegalArgumentException;


    /**
     * Get all the groups within a namespace for a given principal.
     *
     * <p>
     * This will include all groups directly assigned as well as those inferred
     * by the fact that they are members of higher level groups, and filtered by Group namespace.
     * </p>
     *
     * @param principalId The id of the Principal
     * @param namespaceCode The namespace code of the desired Groups to return
     * @return a list of Group objects in which the given Principal is a member of, filtered by Group namespace.  An empty list is returned if an invalid or
     *         non-existant principalId is supplied.
     */
    @WebMethod(operationName = "getGroupsByPrincipalIdAndNamespaceCode")
    @XmlElementWrapper(name = "groups", required = true)
    @XmlElement(name = "group", required = false)
    @WebResult(name = "groups")
    List<Group> getGroupsByPrincipalIdAndNamespaceCode(@WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode) throws RiceIllegalArgumentException;

    /**
     * Query for groups based on the given search criteria which is a Map of group field names to values.
     *
     * <p>
     * This method returns it's results as a List of group ids that match the given search criteria.
     * </p>
     *
     * @param queryByCriteria the criteria.  Cannot be null.
     * @return a list of groupId Strings in which the given criteria match Group properties.  An empty list is returned if an invalid or
     *         non-existent criteria is supplied.
     */
    @WebMethod(operationName = "findGroupIds")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
    List<String> findGroupIds(@WebParam(name = "query") QueryByCriteria queryByCriteria);

    /**
     * Query for groups based on the given search criteria which is a Map of group field names to values.
     *
     * <p>
     * This method returns it's results as a List of Groups that match the given search criteria.
     * </p>
     *
     * @param queryByCriteria the criteria.  Cannot be null.
     * @return a list of Group objects in which the given criteria match Group properties.  An empty list is returned if an invalid or
     *         non-existent criteria is supplied.
     */
    @WebMethod(operationName = "findGroups")
    @WebResult(name = "findGroup")
    GroupQueryResults findGroups(@WebParam(name = "query") QueryByCriteria queryByCriteria);

    /**
     * Query for group members based on the given search criteria which is a Map of group member field names to values.
     *
     * <p>
     * This method returns it's results as a List of GroupMemberss that match the given search criteria.
     * </p>
     *
     * @param queryByCriteria the criteria.  Cannot be null.
     * @return a list of GroupMember objects in which the given criteria match Group properties.  An empty list is returned if an invalid or
     *         non-existent criteria is supplied.
     */
    @WebMethod(operationName = "findGroupMembers")
    @WebResult(name = "findGroupMember")
    GroupMemberQueryResults findGroupMembers(@WebParam(name = "query") QueryByCriteria queryByCriteria);
    /**
     * Lookup a Group based on the passed in id.
     *
     *
     * @param groupId String that matches the desired Groups id
     * @return a Group with the given id value.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getGroup")
    @WebResult(name = "group")
    Group getGroup(@WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

    /**
     * Lookup a Group based on the passed in namespace and name.
     *
     *
     * @param namespaceCode String that matches the desired Group's namespaceCode
     * @param groupName     String that matches the desired Group's name
     * @return a Group with the given namespace and name values.  A null reference is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getGroupByNameAndNamespaceCode")
    @WebResult(name = "group")
    Group getGroupByNameAndNamespaceCode(@WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "groupName") String groupName) throws RiceIllegalArgumentException;

    /**
     * Gets all groups for the given collection of group ids.
     *
     * <p>The result is a Map containing the group id as the key and the Group as the value.</p>
     *
     * @param groupIds Collection that matches the desired Groups' id
     * @return a Map of Groups with the given id values.  An empty Map is returned if an invalid or
     *         non-existant id is supplied.
     */
    @WebMethod(operationName = "getGroups")
    @XmlElementWrapper(name = "groups", required = true)
    @XmlElement(name = "group", required = false)
    @WebResult(name = "groups")
    List<Group> getGroups(@WebParam(name="groupIds") Collection<String> groupIds);


    /**
     * Check whether the give principal is a member of the group.
     *
     * <p>Will return true if the principal is a member of the group or a group assigned to this group.</p>
     *
     * @param principalId Id of the principal
     * @param groupId     Id string of group
     * @return true if principal is a member of the group or a member of a group assigned to the the group.
     */
    @WebMethod(operationName = "isMemberOfGroup")
    @WebResult(name = "isMember")
	boolean isMemberOfGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

	/**
	 * Check whether the give principal is a member of the group.
	 *
	 * <p>This will not recurse into contained groups.
	 */
    /**
     * Check whether the give principal is a member of the group.
     *
     * <p>This method does not recurse into contained groups.</p>
     *
     * @param principalId Id of the principal
     * @param groupId     Id string of group
     * @return true if principal is a direct member of the group.
     */
    @WebMethod(operationName = "isDirectMemberOfGroup")
    @WebResult(name = "isDirectMember")
	boolean isDirectMemberOfGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

    /**
     * Get all the groups for the given principal.  Recurses into parent groups
     * to provide a comprehensive list.
     *
     * <p>
     * This returns id for all groups for a given principal id.
     * </p>
     *
     * @param principalId Id of a Principal
     * @return a list of Group Ids in which the principal is a member of.
     */
    @WebMethod(operationName = "getGroupIdsByPrincipalId")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
	List<String> getGroupIdsByPrincipalId(@WebParam(name = "principalId") String principalId) throws RiceIllegalArgumentException;

    /**
     * Get all the groups for the given principal.  Recurses into parent groups
     * to provide a comprehensive list.  This is limited to the passed in Group's namespace.
     *
     * <p>
     * This returns id for all groups for a given principal id, limited to specific Group namespace.
     * </p>
     *
     * @param principalId Id of a Principal
     * @param namespaceCode Namspace code to limit group results to
     * @return a list of Group Ids in which the principal is a member of, limited to the passed in namespace.
     */
    @WebMethod(operationName = "getGroupIdsByPrincipalIdAndNamespaceCode")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
	List<String> getGroupIdsByPrincipalIdAndNamespaceCode(@WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode) throws RiceIllegalArgumentException;


    /**
     * Get all the groups for the given principal.  Does not recurse into parent groups.
     *
     * <p>
     * This returns id for all groups for a given principal id.
     * </p>
     *
     * @param principalId Id of a Principal
     * @return a list of Group Ids in which the principal is directly a member of.
     */
    @WebMethod(operationName = "getDirectGroupIdsByPrincipalId")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
    List<String> getDirectGroupIdsByPrincipalId(@WebParam(name = "principalId") String principalId) throws RiceIllegalArgumentException;


    /**
     * Check whether the group identified by groupMemberId is a member of the group
     * identified by groupId.  This will recurse through all groups.
     *
     * <p>Will return true if the group is a member of the group or a group assigned to this group.</p>
     *
     * @param groupMemberId Id of the principal
     * @param groupId     Id string of group
     * @return true if group is a member of the group or a member of a group assigned to the the group.
     */
    @WebMethod(operationName = "isGroupMemberOfGroup")
    @WebResult(name = "isMember")
    boolean isGroupMemberOfGroup(@WebParam(name="groupMemberId") String groupMemberId, @WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

    /**
     * Checks if the group with the given id is active.  Returns true if it is, false otherwise.
     */
    //boolean isGroupActive( @WebParam(name="groupId") String groupId );


    /**
     * Returns all principal ids that are members of the given group id.  Recurses into contained groups for
     * comprehensive list.
     *
     * <p>Will return a list of all principal ids for members this group.</p>
     *
     * @param groupId     Id string of group
     * @return List of principal ids
     */
    @WebMethod(operationName = "getMemberPrincipalIds")
    @XmlElementWrapper(name = "principalIds", required = true)
    @XmlElement(name = "principalId", required = false)
    @WebResult(name = "principalIds")
	List<String> getMemberPrincipalIds(@WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;


    /**
     * Returns all principal ids that are direct members of the given group id.
     *
     * <p>Will return a list of all principal ids for direct members this group.</p>
     *
     * @param groupId     Id string of group
     * @return List of direct member principal ids.
     */
    @WebMethod(operationName = "getDirectMemberPrincipalIds")
    @XmlElementWrapper(name = "principalIds", required = true)
    @XmlElement(name = "principalId", required = false)
    @WebResult(name = "principalIds")
	List<String> getDirectMemberPrincipalIds(@WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;


    /**
     * Returns all group ids that are members of the given group id.  Recurses into contained groups for
     * a comprehensive list.
     *
     * <p>Will return a list of all group ids for members this group.</p>
     *
     * @param groupId     Id string of group
     * @return List of group ids
     */
    @WebMethod(operationName = "getMemberGroupIds")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
	List<String> getMemberGroupIds( @WebParam(name="groupId") String groupId ) throws RiceIllegalArgumentException;


    /**
     * Returns all group ids that are direct members of the given group id.
     *
     * <p>Will return a list of all group ids for direct members this group.</p>
     *
     * @param groupId     Id string of group
     * @return List of direct member group ids.
     */
    @WebMethod(operationName = "getDirectMemberOfGroup")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
	List<String> getDirectMemberGroupIds( @WebParam(name="groupId") String groupId ) throws RiceIllegalArgumentException;


    /**
     * Returns all parent groups ids that the given group id is a member of. Recurses parent groups for
     * a comprehensive list.
     *
     * <p>Will return a list of all group ids that the given group id is a member of.</p>
     *
     * @param groupId     Id string of group
     * @return List of parent group ids.
     */
    @WebMethod(operationName = "getParentGroupIds")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
    List<String> getParentGroupIds(@WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;


    /**
     * Returns all parent groups ids that the given group id is a member of.
     *
     * <p>Will return a list of all group ids that the given group id is a member of.</p>
     *
     * @param groupId     Id string of group
     * @return List of parent group ids.
     */
    @WebMethod(operationName = "getDirectParentGroupIds")
    @XmlElementWrapper(name = "groupIds", required = true)
    @XmlElement(name = "groupId", required = false)
    @WebResult(name = "groupIds")
    List<String> getDirectParentGroupIds(@WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

	/**
	 * Get all the attributes of the given group.
	 */
    @WebMethod(operationName = "getAttributes")
    @WebResult(name = "attributes")
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    Map<String, String> getAttributes( @WebParam(name="groupId") String groupId ) throws RiceIllegalArgumentException;


    /**
     * Get all GroupMembers all the groups with a given group id.
     *
     * <p>
     * The collection of GroupMembers will contain members for a the group in no defined order.
     * </p>
     *
     * @param groupIds     Id of group
     * @return Collection of GroupMembers.
     */
    @WebMethod(operationName = "getMembersOfGroup")
    @XmlElementWrapper(name = "members", required = true)
    @XmlElement(name = "member", required = false)
    @WebResult(name = "members")
	List<GroupMember> getMembersOfGroup( @WebParam(name="groupId") String groupIds ) throws RiceIllegalArgumentException;


    /**
     * Get all GroupMembers all the groups with the given group ids.
     *
     * <p>
     * The collection of GroupMembers will contain members for all the groups in no defined order.
     * The values returned may or may not be grouped by group id.
     * </p>
     *
     * @param groupIds     Ids of groups
     * @return Collection of GroupMembers.
     */
    @WebMethod(operationName = "getMembers")
    @XmlElementWrapper(name = "members", required = true)
    @XmlElement(name = "member", required = false)
    @WebResult(name = "members")
	List<GroupMember> getMembers( @WebParam(name="groupIds") List<String> groupIds ) throws RiceIllegalArgumentException;


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
	Group createGroup(@WebParam(name="group") Group group) throws RiceIllegalArgumentException;

    /**
     * Updates an existing group using the given Group.
     *
     * <p>
     * This will attempt to update an existing Group.  For this to return without exceptions, the passed in Group
     * must have it's Id set and be a valid group that already exists.
     * </p>
     *
     * @param group The group to be updated
     * @return a the Group that has been updated.
     */
    @WebMethod(operationName = "updateGroup")
    @WebResult(name = "group")
	Group updateGroup(@WebParam(name="group") Group group) throws RiceIllegalArgumentException;

	/**
     * Updates a group using the given Group.
     *
     * <p>
     * This will attempt to update an existing group with data from the passed in group.  If the passed in groupId and the group.id values are different
     * this method will inactivate the old group and create a new group with the same members with the passed in groups properties.
     * </p>
     *
     * @param groupId Id of the Group to be updated
     * @param group   Group object to use for update
     * @return a the Group that has been updated.
     */
    @WebMethod(operationName = "updateGroupWithId")
    @WebResult(name = "group")
    Group updateGroup(@WebParam(name="groupId") String groupId, @WebParam(name="group") Group group) throws RiceIllegalArgumentException;

    /**
     * Adds the group with the id supplied in childId as a member of the group with the id supplied in parentId.
     *
     * @param childId Id of the Group to be added to the members of Parent
     * @param parentId  Id of the Group object to add the member to
     * @return true if the member was added successfully.
     */
    @WebMethod(operationName = "addGroupToGroup")
    @WebResult(name = "addedToGroup")
    boolean addGroupToGroup(@WebParam(name="childId") String childId, @WebParam(name="parentId") String parentId) throws RiceIllegalArgumentException;

    /**
     * Removes the group with the id supplied in childId from the group with the id supplied in parentId.
     *
     * @param childId Id of the Group to be removed from the members of Parent
     * @param parentId  Id of the Group object to remove the member from
     * @return true if the member was removed successfully.
     */
    @WebMethod(operationName = "removeGroupFromGroup")
    @WebResult(name = "removedFromGroup")
    boolean removeGroupFromGroup(@WebParam(name="childId") String childId, @WebParam(name="parentId") String parentId) throws RiceIllegalArgumentException;

    /**
     * Add the principal with the given principalId as a member of the group with the given groupId.
     *
     * @param principalId Id of the Principal to be added to the members of the Parent Group
     * @param groupId  Id of the Group object to add the member to
     * @return true if the member was added successfully.
     */
    @WebMethod(operationName = "addPrincipalToGroup")
    @WebResult(name = "addedToGroup")
    boolean addPrincipalToGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

    /**
     * Removes the member principal with the given principalId from the group with the given groupId.
     *
     * @param principalId Id of the Principal to be removed from the members of the Parent Group
     * @param groupId  Id of the Group object to remove the member from
     * @return true if the member was removed successfully.
     */
    @WebMethod(operationName = "removePrincipalFromGroup")
    @WebResult(name = "removedFromGroup")
    boolean removePrincipalFromGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws RiceIllegalArgumentException;

    /**
     * Removes all members from the group with the given groupId.
     *
     * @param groupId  Id of the Group object to remove the members from
     */
    @WebMethod(operationName = "removeAllMembers")
    void removeAllMembers( @WebParam(name="groupId") String groupId ) throws RiceIllegalArgumentException;
}
