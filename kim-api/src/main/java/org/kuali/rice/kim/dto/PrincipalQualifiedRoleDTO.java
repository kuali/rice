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
package org.kuali.rice.kim.dto;

import java.util.HashMap;

/**
 * This is the Data Transfer Object (DTO) that is used for our service layer.
 * 
 * Primarily a helper business object that provides a list of qualified role attributes for 
 * a specific principalDto and role.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PrincipalQualifiedRoleDTO extends QualifiedRoleDTO {
   	private Long principalId;
   	
   	private PrincipalDTO principalDto;
   	
   	private HashMap<String,PrincipalQualifiedRoleAttributeDTO> qualifiedRoleAttributeDtos;
   	
	/**
     * @return the principalId
     */
    public Long getPrincipalId() {
        return this.principalId;
    }

    /**
     * @param principalId the principalId to set
     */
    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    /**
     * @return the principalDto
     */
    public PrincipalDTO getPrincipalDto() {
        return this.principalDto;
    }

    /**
     * @param principalDto the principalDto to set
     */
    public void setPrincipalDto(PrincipalDTO principal) {
        this.principalDto = principal;
    }

    /**
     * @return the qualifiedRoleAttributeDtos
     */
    public HashMap<String,PrincipalQualifiedRoleAttributeDTO> getQualifiedRoleAttributeDtos() {
        return this.qualifiedRoleAttributeDtos;
    }

    /**
     * @param qualifiedRoleAttributeDtos the qualifiedRoleAttributeDtos to set
     */
    public void setQualifiedRoleAttributeDtos(HashMap<String,PrincipalQualifiedRoleAttributeDTO> qualifiedRoleAttributeDtos) {
        this.qualifiedRoleAttributeDtos = qualifiedRoleAttributeDtos;
    }
}