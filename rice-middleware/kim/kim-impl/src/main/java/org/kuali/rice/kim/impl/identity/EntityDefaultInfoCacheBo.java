/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.impl.identity;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoDefault;

/**
 * Used to store a cache of person information to be used if the user's information disappears from KIM.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_CACHE_T")
public class EntityDefaultInfoCacheBo {

    private static final String UNAVAILABLE = "Unavailable";

    @Id
    @Column(name = "PRNCPL_ID")
    private String principalId;

    @Column(name = "PRNCPL_NM")
    private String principalName;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "ENTITY_TYP_CD")
    private String entityTypeCode;

    @Column(name = "FIRST_NM")
    private String firstName = "";

    @Column(name = "MIDDLE_NM")
    private String middleName = "";

    @Column(name = "LAST_NM")
    private String lastName = "";

    @Column(name = "PRSN_NM")
    private String name = "";

    @Column(name = "CAMPUS_CD")
    private String campusCode = "";

    @Column(name = "PRMRY_DEPT_CD")
    private String primaryDepartmentCode = "";

    @Column(name = "EMP_ID")
    private String employeeId = "";

    @Column(name = "LAST_UPDT_TS")
    private Timestamp lastUpdateTimestamp;

    @Column(name="OBJ_ID", length=36, unique=true, nullable = false)
    protected String objectId;

    public EntityDefaultInfoCacheBo() {
    }

    public EntityDefaultInfoCacheBo(EntityDefault entity) {
        if (entity != null) {
            entityId = entity.getEntityId();
            if (entity.getPrincipals() != null && !entity.getPrincipals().isEmpty()) {
                principalId = entity.getPrincipals().get(0).getPrincipalId();
                if (entity.getPrincipals().get(0).getPrincipalName() != null) {
                    principalName = entity.getPrincipals().get(0).getPrincipalName();
                } else {
                    principalName = UNAVAILABLE;
                }
            }
            if (entity.getEntityTypeContactInfos() != null && !entity.getEntityTypeContactInfos().isEmpty()) {
                entityTypeCode = entity.getEntityTypeContactInfos().get(0).getEntityTypeCode();
            }
            if (entity.getName() != null) {
                firstName = entity.getName().getFirstNameUnmasked();
                middleName = entity.getName().getMiddleNameUnmasked();
                lastName = entity.getName().getLastNameUnmasked();
                name = entity.getName().getCompositeNameUnmasked();
            }
            if (entity.getDefaultAffiliation() != null) {
                campusCode = entity.getDefaultAffiliation().getCampusCode();
            }
            if (entity.getEmployment() != null) {
                primaryDepartmentCode = entity.getEmployment().getPrimaryDepartmentCode();
                employeeId = entity.getEmployment().getEmployeeId();
            }
        }
    }

    public EntityDefault convertCacheToEntityDefaultInfo() {
        EntityDefault.Builder info = EntityDefault.Builder.create(this.entityId);
        // identity info
        info.setActive(this.isActive());
        // principal info
        Principal.Builder principalInfo = null;
        if (this.getPrincipalName() != null) {
            principalInfo = Principal.Builder.create(this.getPrincipalName());
        } else {
            principalInfo = Principal.Builder.create(UNAVAILABLE);
        }
        principalInfo.setEntityId(this.getEntityId());
        principalInfo.setPrincipalId(this.getPrincipalId());
        principalInfo.setActive(this.isActive());
        info.setPrincipals(Collections.singletonList(principalInfo));
        // name info
        EntityName.Builder nameInfo = EntityName.Builder.create();
        nameInfo.setEntityId(this.getEntityId());
        nameInfo.setFirstName(this.getFirstName());
        nameInfo.setLastName(this.getLastName());
        nameInfo.setMiddleName(this.getMiddleName());
        info.setName(nameInfo);
        // identity type information
        EntityTypeContactInfoDefault.Builder entityTypeInfo = EntityTypeContactInfoDefault.Builder.create();
        entityTypeInfo.setEntityTypeCode(this.getEntityTypeCode());
        info.setEntityTypeContactInfos(Collections.singletonList(entityTypeInfo));
        // affiliations
        EntityAffiliation.Builder aff = EntityAffiliation.Builder.create();
        aff.setCampusCode(this.getCampusCode());
        aff.setDefaultValue(true);
        aff.setEntityId(info.getEntityId());
        info.setDefaultAffiliation(aff);
        info.setAffiliations(Collections.singletonList(aff));
        // employment information
        EntityEmployment.Builder empInfo = EntityEmployment.Builder.create();
        empInfo.setEmployeeId(this.getEmployeeId());
        empInfo.setPrimary(true);
        empInfo.setPrimaryDepartmentCode(this.getPrimaryDepartmentCode());
        info.setEmployment(empInfo);
        // external identifiers
        info.setExternalIdentifiers(Collections.singletonList(EntityExternalIdentifier.Builder.create()));
        return info.build();
    }

    @PrePersist
    protected void prePersist() {
        if (StringUtils.isEmpty(getObjectId())) {
            setObjectId(UUID.randomUUID().toString());
        }

        lastUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }
    @PreUpdate
    protected void preUpdate() {
        if (StringUtils.isEmpty(getObjectId())) {
            setObjectId(UUID.randomUUID().toString());
        }

        lastUpdateTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public boolean isActive() {
        return false;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getPrimaryDepartmentCode() {
        return primaryDepartmentCode;
    }

    public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
        this.primaryDepartmentCode = primaryDepartmentCode;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public Timestamp getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return this.objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
