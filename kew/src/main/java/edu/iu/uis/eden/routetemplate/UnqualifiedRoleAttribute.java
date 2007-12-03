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
import edu.iu.uis.eden.routeheader.DocumentContent;

/**
 * A simple base RoleAttribute implementation for roles that do not need to be qualified
 * prior to resolution.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class UnqualifiedRoleAttribute extends AbstractRoleAttribute {

    protected List<Role> roles;

    /**
     * No-arg constructor for subclasses that will override {@link #getRoleNames()} to provide their own roles list
     */
    public UnqualifiedRoleAttribute() {
        roles = Collections.emptyList();
    }

    /**
     * Constructor for subclasses that can provide a role list at construction time
     */
    public UnqualifiedRoleAttribute(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoleNames() {
        return roles;
    }

    /**
     * Returns a List<String> containing only the roleName parameter; i.e. no qualification occurs
     */
    public List<String> getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
        List<String> qualifiedRoleName = new ArrayList<String>(1);
        qualifiedRoleName.add(roleName);
        return qualifiedRoleName;
    }

    /**
     * Helper method for parsing the actual role name out from the class/rolename combination
     * as Role class combines the two and does expose the original role name
     * @param classAndRole the class and role string (e.g. org.blah.MyRoleAttribute!SOME_ROLE_NAME)
     * @return the role name portion of the class and role string (e.g. SOME_ROLE_NAME);
     */
    protected String parseRoleNameFromClassAndRole(String classAndRole) {
        return classAndRole.substring(classAndRole.indexOf("!") + 1);
    }

    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        // some sanity checking
        if (!roleName.equals(qualifiedRole)) {
            throw new IllegalArgumentException("UnqualifiedRoleAttribute resolveQualifiedRole invoked with differing role and qualified role (they should be the same)");
        }
        // this attribute should never be called to resolve any roles other than those it advertised as supporting!
        boolean valid = false;
        for (Role role: getRoleNames()) {
            if (parseRoleNameFromClassAndRole(role.getName()).equals(roleName)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new IllegalArgumentException("This attribute does not support the role: '" + roleName + "'");
        }
        return resolveRole(routeContext, roleName);
    }

    /**
     * Template method for subclasses to implement
     * @param routeContext the RouteContext
     * @param roleName the role name
     * @return a ResolvedQualifiedRole
     */
    protected abstract ResolvedQualifiedRole resolveRole(RouteContext routeContext, String roleName)  throws EdenUserNotFoundException;
}