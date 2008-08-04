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
 * This is a Data Transfer Object (DTO) that is used by the service layer.
 *
 * An instance of this class represents a single meta-data attribute associated with a Group in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleAttributeDTO extends AbstractAttributeBaseDTO {
    private static final long serialVersionUID = 282694900876199437L;

    private Long roleId;

    /**
     * @return the roleId
     */
    public Long getRoleId() {
        return this.roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
