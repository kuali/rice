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
 * This class represents a Principal entity in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PrincipalDTO extends AbstractEntityBaseDTO implements java.security.Principal {
    private static final long serialVersionUID = -7894319178912177679L;

    private String name;

    /**
     * This overridden method ...
     *
     * @see java.security.Principal#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
