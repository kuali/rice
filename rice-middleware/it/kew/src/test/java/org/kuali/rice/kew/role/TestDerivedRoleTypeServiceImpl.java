/*
 * Copyright 2005-2017 The Kuali Foundation
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

package org.kuali.rice.kew.role;

import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dummy derived class that returns 2 different test users for either the "IN" or "BL" chart qualifiers
 *
 * It's important to note that this class doesn't pass the received qualifiers to the RoleMemberships it creates.
 */
public class TestDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private static final String CHART_QUALIFIER = "chart";

    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        Role role = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName(namespaceCode, roleName);
        List<RoleMembership> members = new ArrayList<RoleMembership>();

        if (qualification.containsKey(CHART_QUALIFIER) && "IN".equals(qualification.get(CHART_QUALIFIER))) {
            members.add(getRoleMembershipForPrincipalId(role, "testuser1", null));
            members.add(getRoleMembershipForPrincipalId(role, "testuser2", null));
        }
        if (qualification.containsKey(CHART_QUALIFIER) && "BL".equals(qualification.get(CHART_QUALIFIER))) {
            members.add(getRoleMembershipForPrincipalId(role, "testuser3", null));
            members.add(getRoleMembershipForPrincipalId(role, "testuser4", null));
        }
        return members;
    }

    protected RoleMembership getRoleMembershipForPrincipalId(Role role, String principalId, Map<String, String> qualification) {
        return RoleMembership.Builder.create(role.getId(), principalId, principalId, MemberType.PRINCIPAL, qualification).build();
    }

}
