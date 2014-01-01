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
package org.kuali.rice.krad.uif.util;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.kim.api.common.assignee.Assignee;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.common.template.TemplateQueryResults;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionQueryResults;
import org.kuali.rice.kim.api.permission.PermissionService;

/**
 * Mock permission support supporting UIF unit tests.
 * 
 * <p>
 * All permission checks return true, and throws UnsupportedOperationException for all introspection
 * or modification operations.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockPermissionService implements PermissionService {

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#createPermission(org.kuali.rice.kim.api.permission.Permission)
     */
    @Override
    public Permission createPermission(Permission permission) throws RiceIllegalArgumentException,
            RiceIllegalStateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#updatePermission(org.kuali.rice.kim.api.permission.Permission)
     */
    @Override
    public Permission updatePermission(Permission permission) throws RiceIllegalArgumentException,
            RiceIllegalStateException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#hasPermission(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName)
            throws RiceIllegalArgumentException {
        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#isAuthorized(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName,
            Map<String, String> qualification) throws RiceIllegalArgumentException {
        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#hasPermissionByTemplate(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public boolean hasPermissionByTemplate(String principalId, String namespaceCode, String permissionTemplateName,
            Map<String, String> permissionDetails) throws RiceIllegalArgumentException {
        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#isAuthorizedByTemplate(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public boolean isAuthorizedByTemplate(String principalId, String namespaceCode, String permissionTemplateName,
            Map<String, String> permissionDetails, Map<String, String> qualification)
            throws RiceIllegalArgumentException {
        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getPermissionAssignees(java.lang.String,
     *      java.lang.String, java.util.Map)
     */
    @Override
    public List<Assignee> getPermissionAssignees(String namespaceCode, String permissionName,
            Map<String, String> qualification) throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getPermissionAssigneesByTemplate(java.lang.String,
     *      java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public List<Assignee> getPermissionAssigneesByTemplate(String namespaceCode, String permissionTemplateName,
            Map<String, String> permissionDetails, Map<String, String> qualification)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#isPermissionDefined(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean isPermissionDefined(String namespaceCode, String permissionName) throws RiceIllegalArgumentException {
        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#isPermissionDefinedByTemplate(java.lang.String,
     *      java.lang.String, java.util.Map)
     */
    @Override
    public boolean isPermissionDefinedByTemplate(String namespaceCode, String permissionTemplateName,
            Map<String, String> permissionDetails) throws RiceIllegalArgumentException {
        return true;
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getAuthorizedPermissions(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<Permission> getAuthorizedPermissions(String principalId, String namespaceCode, String permissionName,
            Map<String, String> qualification) throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getAuthorizedPermissionsByTemplate(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map, java.util.Map)
     */
    @Override
    public List<Permission> getAuthorizedPermissionsByTemplate(String principalId, String namespaceCode,
            String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getPermission(java.lang.String)
     */
    @Override
    public Permission getPermission(String id) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#findPermByNamespaceCodeAndName(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Permission findPermByNamespaceCodeAndName(String namespaceCode, String name)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#findPermissionsByTemplate(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<Permission> findPermissionsByTemplate(String namespaceCode, String templateName)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getPermissionTemplate(java.lang.String)
     */
    @Override
    public Template getPermissionTemplate(String id) throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#findPermTemplateByNamespaceCodeAndName(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Template findPermTemplateByNamespaceCodeAndName(String namespaceCode, String name)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getAllTemplates()
     */
    @Override
    public List<Template> getAllTemplates() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#getRoleIdsForPermission(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<String> getRoleIdsForPermission(String namespaceCode, String permissionName)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#findPermissions(org.kuali.rice.core.api.criteria.QueryByCriteria)
     */
    @Override
    public PermissionQueryResults findPermissions(QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.kuali.rice.kim.api.permission.PermissionService#findPermissionTemplates(org.kuali.rice.core.api.criteria.QueryByCriteria)
     */
    @Override
    public TemplateQueryResults findPermissionTemplates(QueryByCriteria queryByCriteria)
            throws RiceIllegalArgumentException {
        throw new UnsupportedOperationException();
    }

}
