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

import org.kuali.rice.kim.bo.role.impl.KimDelegationGroupImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationPrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationRoleImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleGroupImpl;
import org.kuali.rice.kim.bo.role.impl.RolePrincipalImpl;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimRoleDao {

	List<RolePrincipalImpl> getRolePrincipalsForPrincipalIdAndRoleIds( Collection<String> roleIds, String principalId );
	
	List<RoleGroupImpl> getRoleGroupsForGroupIdsAndRoleIds( Collection<String> roleIds, Collection<String> groupIds );
	
	Map<String,KimRoleImpl> getRoleImplMap( Collection<String> roleIds );
	
	Map<String,KimDelegationImpl> getDelegationImplMapFromRoleIds(Collection<String> roleIds);
	
	List<KimDelegationPrincipalImpl> getDelegationPrincipalsForPrincipalIdAndDelegationIds( Collection<String> delegationIds, String principalId );

	List<KimDelegationGroupImpl> getDelegationGroupsForGroupIdsAndDelegationIds( Collection<String> delegationIds, List<String> groupIds );
	
	List<RolePrincipalImpl> getRolePrincipalsForRoleIds( Collection<String> roleIds );
	List<RoleGroupImpl> getRoleGroupsForRoleIds( Collection<String> roleIds );
	
	Map<String,List<KimDelegationPrincipalImpl>> getDelegationPrincipalsForDelegationIds( List<String> delegationIds );
	
	Map<String,List<KimDelegationGroupImpl>> getDelegationGroupsForDelegationIds( List<String> delegationIds );
	
	public Map<String,List<KimDelegationRoleImpl>> getDelegationRolesForDelegationIds( List<String> delegationIds);

}
