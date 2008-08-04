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

import java.util.ArrayList;

import javax.persistence.Transient;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.OjbCharBooleanConversion;

/**
 * This is a description of what this class does - pberres don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KOM_ORGANIZATIONS_T")
public class Organization extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -6664771656002221668L;
    @Id
	@Column(name="ID")
	private Long id;
    @Column(name="SHORT_NAME")
	private String shortName;
    @Column(name="NAME")
	private String name;
    @Column(name="PARENT_ORGANIZATION_ID")
	private Long parentOrganizationId;
    @Column(name="CATEGORY_ID")
	private Long categoryId;
    @Column(name="ACTIVE")
	private String active;

    @Transient
    private ArrayList<Organization> organizationParents = new ArrayList<Organization>(0);
    @Transient
    private OrganizationCategory organizationCategory;
    @Transient
    private Organization parent;
    @Transient
    private ArrayList<OrganizationsContexts> organizationsContexts = new ArrayList<OrganizationsContexts>(0);

    public OrganizationCategory getOrganizationCategory() {
        return this.organizationCategory;
    }

    public void setOrganizationCategory(OrganizationCategory organizationCategory) {
        this.organizationCategory = organizationCategory;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("shortName", getShortName());
        propMap.put("name", getName());
        propMap.put("categoryId", getCategoryId());
        propMap.put("active", getActive());
        return propMap;
    }

    public Long getParentOrganizationId() {
        return this.parentOrganizationId;
    }

    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public Organization getParent() {
        return this.parent;
    }

    public void setParent(Organization parent) {
        this.parent = parent;
    }

    public ArrayList<Organization> getOrganizationParents() {
        return this.organizationParents;
    }

    public void setOrganizationParents(ArrayList<Organization> organizationParents) {
        this.organizationParents = organizationParents;
    }

    public ArrayList<OrganizationsContexts> getOrganizationsContexts() {
        return this.organizationsContexts;
    }

    public void setOrganizationsContexts(ArrayList<OrganizationsContexts> organizationsContexts) {
        this.organizationsContexts = organizationsContexts;
    }

}

