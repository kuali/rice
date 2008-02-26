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

/**
 * This is the Data Transfer Object (DTO) that is used for our service layer.
 * 
 * Represents a single qualified role attribute record associated with a principal.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PrincipalQualifiedRoleAttributeDTO extends QualifiedRoleAttributeDTO {
    private Long principalId;

    private PrincipalDTO principalDto;
    
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
    public void setPrincipalDto(PrincipalDTO principalDto) {
        this.principalDto = principalDto;
    }
}