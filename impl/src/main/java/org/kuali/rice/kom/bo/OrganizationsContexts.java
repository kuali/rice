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
package org.kuali.rice.kom.bo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Transient;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.OjbCharBooleanConversion;

/**
 * This is a description of what this class does - pberres don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KOM_ORGANIZATIONS_CONTEXTS_T")
public class OrganizationsContexts extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 9021359708162166484L;
    @Id
	@Column(name="ID")
	private Long id;
    @Column(name="ORGANIZATION_ID")
	private Long organizationId;
    @Column(name="CONTEXT_ID")
	private Long contextId;
    @Column(name="ACTIVE")
	private String active;
    @Transient
    private Organization organization;
    @Transient
    private OrganizationContext organizationContext;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrganizationId() {
        return this.organizationId;
    }
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    public Long getContextId() {
        return this.contextId;
    }
    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }
    public Boolean getActive() {
        return (Boolean)(new OjbCharBooleanConversion()).sqlToJava(active);
    }

    public void setActive(Boolean active) {
        this.active = (String)(new OjbCharBooleanConversion()).javaToSql(active);
    }
    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("organizationId", getOrganizationId());
        propMap.put("contextId", getContextId());
        propMap.put("active", getActive());
        return propMap;
    }
    public Organization getOrganization() {
        return this.organization;
    }
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    public OrganizationContext getOrganizationContext() {
        return this.organizationContext;
    }
    public void setOrganizationContext(OrganizationContext organizationContext) {
        this.organizationContext = organizationContext;
    }

    // Kludge
    public String getContextName() {
        if (organizationContext != null) {
        return organizationContext.getName();
        } else {
            return "not set";
        }
    }
}

