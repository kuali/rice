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
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kim.bo.Namespace;
import org.kuali.rice.kim.bo.NamespaceDefaultAttribute;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.service.NamespaceService;

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
            namespacesDto.add(namespaceToDTO(namespace));
        }
        return namespacesDto;
    }

    /**
     * This method returns all the permission names
     *
     * @see org.kuali.rice.kim.service.NamespaceService#getPermissionNames(java.lang.String)
     */
    public List<String> getPermissionNames(final String namespaceName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final Collection<Permission> permissions = (Collection<Permission>) KNSServiceLocator.getBusinessObjectService().findAll(Permission.class);
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
        final Collection<Permission> permissions = (Collection<Permission>) KNSServiceLocator.getBusinessObjectService().findAll(Permission.class);
        final ArrayList<PermissionDTO> permissionsDto = new ArrayList<PermissionDTO>(permissions.size());
        for (Permission permission : permissions) {
            permissionsDto.add(permissionToDTO(permission, namespaceToDTO(permission.getNamespace())));
        }
        return permissionsDto;
    }

    /**
     * This method creates a NamespaceDTO from a Namespace
     *
     * @param namespace
     * @return NamespaceDTO
     */
    private static NamespaceDTO namespaceToDTO(final Namespace namespace) {
        final NamespaceDTO dto = new NamespaceDTO();
        dto.setDescription(namespace.getDescription());
        dto.setId(namespace.getId());
        dto.setName(namespace.getName());
        final HashMap<String, NamespaceDefaultAttributeDTO> namespaceDefaultAttributes = new HashMap<String, NamespaceDefaultAttributeDTO>();
        final Iterator<NamespaceDefaultAttribute> i = namespace.getNamespaceAttributes().iterator();
        while (i.hasNext()) {
            final NamespaceDefaultAttribute nda = i.next();
            namespaceDefaultAttributes.put(nda.getAttributeName(), namespaceDefaultAttributeToDTO(nda));
        }
        dto.setNamespaceDefaultAttributes(namespaceDefaultAttributes);

        final HashMap<String, PermissionDTO> namespacePermissions = new HashMap<String, PermissionDTO>();
        final Iterator<Permission> i2 = namespace.getNamespacePermissions().iterator();
        while (i2.hasNext()) {
            final Permission permission = i2.next();
            namespacePermissions.put(permission.getName(), permissionToDTO(permission, dto));
        }
        dto.setNamespacePermissions(namespacePermissions);
        return dto;
    }

    /**
     * This method creates a NamespaceDefaultAttributeDTO from a NamespaceDefaultAttribute
     *
     * @param nda
     * @return NamespaceDefaultAttributeDTO
     */
    private static NamespaceDefaultAttributeDTO namespaceDefaultAttributeToDTO(final NamespaceDefaultAttribute nda) {
        final NamespaceDefaultAttributeDTO dto = new NamespaceDefaultAttributeDTO();
        dto.setActive(nda.getActive());
        dto.setAttributeName(nda.getAttributeName());
        dto.setAttributeTypeId(nda.getAttributeTypeId());
        dto.setDescription(nda.getDescription());
        dto.setId(nda.getId());
        dto.setNamespaceId(nda.getNamespaceId());
        dto.setRequired(nda.getRequired());
        return dto;
    }

    /**
     * This method creates a PermissionDTO from a Permission
     *
     * @param permission
     * @return PermissionDTO
     */
    private static PermissionDTO permissionToDTO(final Permission permission, final NamespaceDTO namespaceDTO) {
        final PermissionDTO dto = new PermissionDTO();
        dto.setDescription(permission.getDescription());
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setNamespaceId(permission.getNamespaceId());
//        dto.setNamespaceDto(namespaceDTO);
        return dto;
    }
}
