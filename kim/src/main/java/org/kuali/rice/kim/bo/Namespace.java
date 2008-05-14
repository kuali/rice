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
package org.kuali.rice.kim.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.NamespaceDefaultAttributeDTO;
import org.kuali.rice.kim.dto.PermissionDTO;

public class Namespace extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 9118112248900436184L;
	private Long id;
	private String name;
	private String description;

    private ArrayList<NamespaceDefaultAttribute> namespaceAttributes;
    private ArrayList<Permission> namespacePermissions;

    public Namespace() {
        this.namespaceAttributes = new TypedArrayList(NamespaceDefaultAttribute.class);
        this.namespacePermissions = new TypedArrayList(Permission.class);
    }

    public Long getId() {
	    return this.id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getName() {
	    return this.name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getDescription() {
	    return this.description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("description", getDescription());
        return propMap;
	}

	public void refresh() {
		// not doing this unless we need it
	}

    public ArrayList<NamespaceDefaultAttribute> getNamespaceAttributes() {
        return this.namespaceAttributes;
    }

    public void setNamespaceAttributes(ArrayList<NamespaceDefaultAttribute> namespaceAttributes) {
        this.namespaceAttributes = namespaceAttributes;
    }

    public ArrayList<Permission> getNamespacePermissions() {
        return this.namespacePermissions;
    }

    public void setNamespacePermissions(ArrayList<Permission> namespacePermissions) {
        this.namespacePermissions = namespacePermissions;
    }

    /**
     * This method creates a NamespaceDTO from a Namespace
     *
     * @param namespace
     * @return NamespaceDTO
     */
    public static NamespaceDTO toDTO(final Namespace namespace) {
        final NamespaceDTO dto = new NamespaceDTO();
        dto.setDescription(namespace.getDescription());
        dto.setId(namespace.getId());
        dto.setName(namespace.getName());
        final HashMap<String, NamespaceDefaultAttributeDTO> namespaceDefaultAttributes = new HashMap<String, NamespaceDefaultAttributeDTO>();
        final Iterator<NamespaceDefaultAttribute> i = namespace.getNamespaceAttributes().iterator();
        while (i.hasNext()) {
            final NamespaceDefaultAttribute nda = i.next();
            namespaceDefaultAttributes.put(nda.getAttributeName(), NamespaceDefaultAttribute.toDTO(nda));
        }
        dto.setNamespaceDefaultAttributes(namespaceDefaultAttributes);

        final HashMap<String, PermissionDTO> namespacePermissions = new HashMap<String, PermissionDTO>();
        final Iterator<Permission> i2 = namespace.getNamespacePermissions().iterator();
        while (i2.hasNext()) {
            final Permission permission = i2.next();
            namespacePermissions.put(permission.getName(), Permission.toDTO(permission));
        }
        dto.setNamespacePermissions(namespacePermissions);
        return dto;
    }


}
