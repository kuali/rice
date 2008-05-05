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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Abstract class from which GroupQualifiedRoleAttribute and PersonQualifiedRoleAttribute extend. For simplicity, KIM interfaces may return
 * objects of this class, which then can be cast to the appropriate qualified role sub-class.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class AbstractQualifiedRoleAttribute extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -5155126090045426553L;
    private Long id;
    private String attributeName;
    private String attributeValue;
    private Long roleId;
    private Role role;

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

    /**
     * This method ...
     *
     * @return Role
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * This method ...
     *
     * @param role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * This method ...
     *
     * @return Long
     */
    public Long getId() {
        return this.id;
    }

    /**
     * This method ...
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * This method ...
     *
     * @return String
     */
    public String getAttributeName() {
        return this.attributeName;
    }

    /**
     *
     * This method ...
     *
     * @param attributeName
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     *
     * This method ...
     *
     * @return String
     */
    public String getAttributeValue() {
        return this.attributeValue;
    }

    /**
     *
     * This method ...
     *
     * @param attributeValue
     */
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    /**
     *
     * This overridden method ...
     *
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("role", getRole());
        propMap.put("attributeName", getAttributeName());
        propMap.put("attributeValue", getAttributeValue());
        return propMap;
    }
}
