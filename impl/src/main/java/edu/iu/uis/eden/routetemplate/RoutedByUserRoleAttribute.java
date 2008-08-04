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
package edu.iu.uis.eden.routetemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;

/**
 * RoleAttribute that exposes a document's user who routed the document
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoutedByUserRoleAttribute extends UnqualifiedRoleAttribute {
    private static final String ROUTED_BY_USER_ROLE_KEY = "ROUTED_BY_USER";
    private static final String ROUTED_BY_USER_ROLE_LABEL = "Routed By User";

    private static final Role ROLE = new Role(RoutedByUserRoleAttribute.class, ROUTED_BY_USER_ROLE_KEY, ROUTED_BY_USER_ROLE_LABEL);
    private static final List<Role> ROLES;
    static {
        ArrayList<Role> roles = new ArrayList<Role>(1);
        roles.add(ROLE);
        ROLES = Collections.unmodifiableList(roles);
    }

    public RoutedByUserRoleAttribute() {
        super(ROLES);
    }

    public ResolvedQualifiedRole resolveRole(RouteContext routeContext, String roleName) throws EdenUserNotFoundException {
        // sounds like the role label should be specified as the first parameter here,
        // but I'll follow AccountAttribute's lead and specify the role key
        List members = new ArrayList(1);
        members.add(routeContext.getDocument().getRoutedByUser().getWorkflowUserId());
        return new ResolvedQualifiedRole(ROUTED_BY_USER_ROLE_LABEL, members);
    }
}