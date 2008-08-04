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

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.dto.PrincipalQualifiedRoleAttributeDTO;
import org.kuali.rice.kim.dto.PrincipalQualifiedRoleDTO;
import org.kuali.rice.kim.web.form.PrincipalQualifiedRole;

/**
 * Business object that represents a single qualified role attribute record associated with a principal.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@javax.persistence.Entity
@Table(name="KIM_PRNCPL_QLFD_ROLES_T")
public class PrincipalQualifiedRoleAttribute extends AbstractQualifiedRoleAttribute {
    private static final long serialVersionUID = 6701917498866245651L;

    @Column(name="PRINCIPAL_ID")
    private Long principalId;

	@Transient
    private Principal principal;

    /**
     * @return the principalId
     */
    public Long getPrincipalId() {
        return this.principalId;
    }

    /**
     * @param principalId
     *            the principalId to set
     */
    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    /**
     *
     * This method ...
     *
     * @return Principal
     */
    public Principal getPrincipal() {
        return this.principal;
    }

    /**
     *
     * This method ...
     *
     * @param principal
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.bo.AbstractQualifiedRole#toStringMapper()
     */
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = super.toStringMapper();
        propMap.put("principal", getPrincipal().toStringMapper());
        return propMap;
    }

    /**
     *
     * This method creates a DTO from a BO
     *
     * @param PrincipalQualifiedRoleAttribute qualifiedRoleAttribute
     * @return PrincipalQualifiedRoleAttributeDTO
     */
    public static PrincipalQualifiedRoleAttributeDTO toDTO(final PrincipalQualifiedRoleAttribute qualifiedRoleAttribute) {
    	final PrincipalQualifiedRoleAttributeDTO dto = new PrincipalQualifiedRoleAttributeDTO();

    	dto.setAttributeName(qualifiedRoleAttribute.getAttributeName());
    	dto.setAttributeValue(qualifiedRoleAttribute.getAttributeValue());
    	dto.setId(qualifiedRoleAttribute.getId());

    	dto.setRoleId(qualifiedRoleAttribute.getRoleId());
    	dto.setRoleDto(Role.toDTO(qualifiedRoleAttribute.getRole()));

    	dto.setPrincipalId(qualifiedRoleAttribute.getPrincipalId());
    	dto.setPrincipalDto(Principal.toDTO(qualifiedRoleAttribute.getPrincipal()));

    	return dto;
    }

    public static PrincipalQualifiedRoleDTO toDTO(final Principal p, final Role r) {
    	PrincipalQualifiedRoleDTO dto = new PrincipalQualifiedRoleDTO();

    	dto.setPrincipalId(p.getId());
    	dto.setPrincipalDto(Principal.toDTO(p));
    	dto.setRoleId(r.getId());
    	dto.setRoleDto(Role.toDTO(r));
    	dto.setQualifiedRoleAttributeDtos(new HashMap<String, PrincipalQualifiedRoleAttributeDTO>());

    	return dto;
    }
}
