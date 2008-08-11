/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.rice.kim.bo.Namespace;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.service.NamespaceService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is the default KIM Namespace implementation that is provided by Rice. This will mature over time as the KIM component
 * is developed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NamespaceServiceImpl implements NamespaceService {


    /**
     * This method returns a list of the Namespaces names
     *
     * @see org.kuali.rice.kim.service.NamespaceService#getAllNamespaceNames()
     */
    public List<String> getAllNamespaceNames() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final Collection<Namespace> namespaces = (Collection<Namespace>) KNSServiceLocator.getBusinessObjectService().findAll(Namespace.class);
        final ArrayList<String> names = new ArrayList<String>(namespaces.size());
        for (Namespace namespace : namespaces) {
            names.add(namespace.getName());
        }
        return names;

    }

    /**
     * This method returns all the Namespaces defined
     *
     * @see org.kuali.rice.kim.service.NamespaceService#getAllNamespaces()
     */
    public List<NamespaceDTO> getAllNamespaces() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final Collection<Namespace> namespaces = (Collection<Namespace>) KNSServiceLocator.getBusinessObjectService().findAll(Namespace.class);
        final ArrayList<NamespaceDTO> namespacesDto = new ArrayList<NamespaceDTO>(namespaces.size());
        for (Namespace namespace : namespaces) {
            namespacesDto.add(Namespace.toDTO(namespace));
        }
        return namespacesDto;
    }

    /**
     * This method returns all the permission names for a namespace
     *
     * @see org.kuali.rice.kim.service.NamespaceService#getPermissionNames(java.lang.String)
     */
    public List<String> getPermissionNames(final String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final ArrayList<Permission> permissions = findPermissions(namespaceName);
        final ArrayList<String> names = new ArrayList<String>(permissions.size());
        for (Permission permission : permissions) {
            names.add(permission.getName());
        }
        return names;
    }

    /**
     * This method returns the permissions for a Namespace
     *
     * @see org.kuali.rice.kim.service.NamespaceService#getPermissions(java.lang.String)
     */
    public List<PermissionDTO> getPermissions(final String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final ArrayList<Permission> permissions = findPermissions(namespaceName);

        final ArrayList<PermissionDTO> permissionsDto = new ArrayList<PermissionDTO>(permissions.size());
        for (Permission permission : permissions) {
            permissionsDto.add(Permission.toDTO(permission));
        }
        return permissionsDto;
    }

    /**
     *
     * This method finds the permissions for a specific Namespace
     *
     * @param namespaceName
     * @return list of Permissions
     */
    private ArrayList<Permission> findPermissions(final String namespaceName) {
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", namespaceName);
        final Collection<Namespace> namespace = (Collection<Namespace>) KNSServiceLocator.getBusinessObjectService().findMatching(Namespace.class, criteria);
        return namespace.iterator().next().getNamespacePermissions();
    }
}
