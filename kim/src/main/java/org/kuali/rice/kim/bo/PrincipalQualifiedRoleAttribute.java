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

import java.util.LinkedHashMap;

/**
 * Business object that represents a single qualified role attribute record associated with a principal.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PrincipalQualifiedRoleAttribute extends AbstractQualifiedRoleAttribute {
    private static final long serialVersionUID = -3834313283054550673L;

    private Long principalId;

    private Principal principal;
    
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
     * @return the principal
     */
    public Principal getPrincipal() {
        return this.principal;
    }

    /**
     * @param principal the principal to set
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
}