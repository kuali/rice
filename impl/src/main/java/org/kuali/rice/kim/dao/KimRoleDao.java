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
package org.kuali.rice.kim.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimRoleDao {

	/**
	 * Returns a list of all the active RoleMemberImpl objects for the given principal and set of role IDs.
	 * 
	 * If the roleIds parameter is null, all RoleMemberImpls for the principal are returned.
	 */
	List<RoleMemberImpl> getRolePrincipalsForPrincipalIdAndRoleIds( Collection<String> roleIds, String principalId );
	
	List<GroupMemberImpl> getGroupPrincipalsForPrincipalIdAndGroupIds( Collection<String> groupIds, String principalId );
	
	List<RoleMemberImpl> getRoleGroupsForGroupIdsAndRoleIds( Collection<String> roleIds, Collection<String> groupIds );
	
	Map<String,RoleImpl> getRoleImplMap( Collection<String> roleIds );
	
	Map<String,KimDelegationImpl> getDelegationImplMapFromRoleIds(Collection<String> roleIds);
	
	List<KimDelegationImpl> getDelegationImplsForRoleIds(Collection<String> roleIds);
	
	List<KimDelegationMemberImpl> getDelegationPrincipalsForPrincipalIdAndDelegationIds( Collection<String> delegationIds, String principalId );

	List<KimDelegationMemberImpl> getDelegationGroupsForGroupIdsAndDelegationIds( Collection<String> delegationIds, List<String> groupIds );
	
	List<RoleMemberImpl> getRoleMembersForRoleIds( Collection<String> roleIds, String memberTypeCode );
	
	List<RoleMemberImpl> getRoleMembershipsForRoleIdsAsMembers(Collection<String> roleIds);

	List<RoleMemberImpl> getRoleMembersForRoleIdsWithFilters( Collection<String> roleIds, String principalId, List<String> groupIds );
	
	Map<String,List<KimDelegationMemberImpl>> getDelegationMembersForDelegationIds( List<String> delegationIds );
	
    List<RoleImpl> getRoles(Map<String,String> fieldValues);


}
