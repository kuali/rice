/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_AFLTN_MT")
public class PersonDocumentAffiliation extends PersonDocumentBoDefaultBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_AFLTN_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_AFLTN_ID_S")
    @Id
    @Column(name = "ENTITY_AFLTN_ID")
    protected String entityAffiliationId;

    @Column(name = "AFLTN_TYP_CD")
    protected String affiliationTypeCode;

    @Column(name = "CAMPUS_CD")
    protected String campusCode;

    @JoinFetch(value= JoinFetchType.OUTER)
    @ManyToOne(targetEntity = EntityAffiliationTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "AFLTN_TYP_CD", referencedColumnName = "AFLTN_TYP_CD", insertable = false, updatable = false)
    protected EntityAffiliationTypeBo affiliationType;

    @Transient
    protected PersonDocumentEmploymentInfo newEmpInfo;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentEmploymentInfo.class, cascade = { CascadeType.REFRESH, CascadeType.PERSIST })
    @JoinColumns({ 
        @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false), 
        @JoinColumn(name = "ENTITY_AFLTN_ID", referencedColumnName = "ENTITY_AFLTN_ID", insertable = false, updatable = false) })
    protected List<PersonDocumentEmploymentInfo> empInfos;

    public PersonDocumentAffiliation() {
        empInfos = new ArrayList<PersonDocumentEmploymentInfo>();
        setNewEmpInfo(new PersonDocumentEmploymentInfo());
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.EntityAffiliationContract#getAffiliationTypeCode()
	 */
    public String getAffiliationTypeCode() {
        if (affiliationTypeCode == null) {
            return "";
        }
        return affiliationTypeCode;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.EntityAffiliationContract#getCampusCode()
	 */
    public String getCampusCode() {
        return campusCode;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.EntityAffiliationContract#getEntityAffiliationId()
	 */
    public String getEntityAffiliationId() {
        if (entityAffiliationId == null) {
            return "";
        }
        return entityAffiliationId;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.EntityAffiliationContract#setAffiliationTypeCode(java.lang.String)
	 */
    public void setAffiliationTypeCode(String affiliationTypeCode) {
        this.affiliationTypeCode = affiliationTypeCode;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.EntityAffiliationContract#setCampusCode(java.lang.String)
	 */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public void setEntityAffiliationId(String entityAffiliationId) {
        this.entityAffiliationId = entityAffiliationId;
    }

    public PersonDocumentEmploymentInfo getNewEmpInfo() {
        return this.newEmpInfo;
    }

    public void setNewEmpInfo(PersonDocumentEmploymentInfo newEmpInfo) {
        this.newEmpInfo = newEmpInfo;
    }

    public List<PersonDocumentEmploymentInfo> getEmpInfos() {
        return this.empInfos;
    }

    public void setEmpInfos(List<PersonDocumentEmploymentInfo> empInfos) {
        this.empInfos = empInfos;
    }

    public EntityAffiliationTypeBo getAffiliationType() {
        return this.affiliationType;
    }

    public boolean isEmploymentAffiliationType() {
        if (affiliationType == null) {
            return false;
        }
        return this.affiliationType.isEmploymentAffiliationType();
    }

    public void setAffiliationType(EntityAffiliationTypeBo affiliationType) {
        this.affiliationType = affiliationType;
    }
}
