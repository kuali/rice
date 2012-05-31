/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

/**
 *
 * This service provides operations for checking group membership and querying for group data.
 *
 * <p>A group is a collection of principals.  It's membership consists of direct principal
 * assignment and/or nested group membership.  All groups are uniquely identified by a namespace
 * code plus a name.
 *
 * <p>As mentioned previously, groups support nested group membership.  A principal or group is
 * considered to be a "member" of a group if it is either directly assigned to the group or
 * indirectly assigned (via a nested group membership).  A principal or group is said to be a
 * "direct" member of another group only if it is directly assigned as a member of the group,
 * and not via a nested group assignment.
 *
 * <p>This service provides read-only operations.  For write operations, see
 * {@link GroupUpdateService}.
 *
 * @see GroupUpdateService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = KIMWebServiceConstants.GroupService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupService {

    /** Get all the groups for a given principal.
     *
     * <p>This will include all groups directly assigned as well as those inferred
     * by the fact that they are members of higher level groups.
     */
    List<GroupInfo> getGroupsForPrincipal(@WebParam(name="principalId") String principalId);

    /**
     * Get all the groups within a namespace for a given principal.
     *
     * <p>This is the same as the {@link #getGroupsForPrincipal(String)} method except that
     * the results will be filtered by namespace after retrieval.
     */
    List<GroupInfo> getGroupsForPrincipalByNamespace(@WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode);

    /**
     * Query for groups based on the given search criteria which is a Map of group field names to values.
     *
     * <p>This method returns it's results as a List of group ids that match the given search criteria.
     */
    List<String> lookupGroupIds(@WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> searchCriteria);

    /**
     * Query for groups based on the given search criteria which is a Map of group field names to values.
     *
     * <p>This method returns it's results as a List of GroupInfo objects that match the given search criteria.
     */
    List<? extends Group> lookupGroups(@WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> searchCriteria);

    /**
     * Get the group by the given id.
     */
    GroupInfo getGroupInfo(@WebParam(name="groupId") String groupId);

    /**
     * Get the group by the given namesapce code and name.
     */
    GroupInfo getGroupInfoByName(@WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="groupName") String groupName);

    /**
     * Gets all groups for the given collection of group ids.
     *
     * <p>The result is a Map containing the group id as the key and the group info as the value.
     */
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, GroupInfo> getGroupInfos(@WebParam(name="groupIds") Collection<String> groupIds);

	/**
	 * Check whether the give principal is a member of the group.
	 *
	 * <p>This will also return true if the principal is a member of a groups assigned to this group.
	 */
	boolean isMemberOfGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId);

	/**
	 * Check whether the give principal is a member of the group.
	 *
	 * <p>This will not recurse into contained groups.
	 */
	boolean isDirectMemberOfGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId);

	/**
     * Get all the groups for the given principal.  Recurses into parent groups
     * to provide a comprehensive list.
     */
	List<String> getGroupIdsForPrincipal(@WebParam(name="principalId") String principalId);

	/**
     * Get all the groups for the given principal in the given namespace.  Recurses into
     * parent groups to provide a comprehensive list.
     */
	List<String> getGroupIdsForPrincipalByNamespace(@WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode);

    /**
     * Get the groupIds in which the principal has direct membership only.
     */
    List<String> getDirectGroupIdsForPrincipal(@WebParam(name="principalId") String principalId);


    /**
     * Check whether the group identified by groupMemberId is a member of the group
     * identified by groupId.  This will recurse through all groups.
     */
    boolean isGroupMemberOfGroup(@WebParam(name="groupMemberId") String groupMemberId, @WebParam(name="groupId") String groupId);

    /**
     * Checks if the group with the given id is active.  Returns true if it is, false otherwise.
     */
    boolean isGroupActive( @WebParam(name="groupId") String groupId );

    /**
     * Get all the principals of the given group.  Recurses into contained groups
     * to provide a comprehensive list.
     */
	List<String> getMemberPrincipalIds(@WebParam(name="groupId") String groupId);

    /**
     * Get all the principals directly assigned to the given group.
     */
	List<String> getDirectMemberPrincipalIds(@WebParam(name="groupId") String groupId);

	/**
	 * Get all the groups contained by the given group.  Recurses into contained groups
     * to provide a comprehensive list.
	 */
	List<String> getMemberGroupIds( @WebParam(name="groupId") String groupId );

    /**
     * Get all the groups which are direct members of the given group.
     */
	List<String> getDirectMemberGroupIds( @WebParam(name="groupId") String groupId );

	/**
     * Get the groups which are parents of the given group.
     *
     * <p>This will recurse into groups above the given group and build a complete
     * list of all groups included above this group.
     */
    List<String> getParentGroupIds(@WebParam(name="groupId") String groupId);

    /**
     * Get the groupIds which that are directly above this group.
     */
    List<String> getDirectParentGroupIds(@WebParam(name="groupId") String groupId);

	/**
	 * Get all the attributes of the given group.
	 */
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String,String> getGroupAttributes( @WebParam(name="groupId") String groupId );

    /**
     * Get the membership info for the members of all the groups with the given group ids.
     *
     * <p>The collection of GroupMembershipInfo will contain members for all the groups in no defined order.
     * The values returned may or may not be grouped by group id.
     */
	Collection<GroupMembershipInfo> getGroupMembers( @WebParam(name="groupIds") List<String> groupIds );

	/**
	 * Get the membership info for the members of the group with the given id.
	 *
	 * <p>Only GroupMembershipInfo for direct group members is returned.
	 */
    Collection<GroupMembershipInfo> getGroupMembersOfGroup( @WebParam(name="groupId") String groupId );
}
