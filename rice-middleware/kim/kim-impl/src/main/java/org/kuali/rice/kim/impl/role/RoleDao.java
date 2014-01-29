/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.impl.role;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;

public interface RoleDao {

    /* Evaluate JPA Conversion */
    
    List<DelegateMemberBo> getDelegationPrincipalsForPrincipalIdAndDelegationIds(Collection<String> delegationIds, String principalId);

    List<DelegateMemberBo> getDelegationGroupsForGroupIdsAndDelegationIds(Collection<String> delegationIds, List<String> groupIds);

    List<RoleMemberBo> getRoleMembershipsForRoleIdsAsMembers(Collection<String> roleIds, Map<String, String> qualification);

    List<RoleMemberBo> getRoleMembersForRoleIdsWithFilters(Collection<String> roleIds, String principalId, Collection<String> groupIds, Map<String, String> qualification);

    /* KEEP */
    List<RoleMemberBo> getRoleMembersForRoleIds(Collection<String> roleIds, String memberTypeCode, Map<String, String> qualification);
}
