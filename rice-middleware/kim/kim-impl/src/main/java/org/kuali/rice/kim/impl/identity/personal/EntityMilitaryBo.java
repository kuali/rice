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
package org.kuali.rice.kim.impl.identity.personal;

import org.joda.time.DateTime;
import org.kuali.rice.kim.api.identity.personal.EntityMilitaryContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "KRIM_ENTITY_MLTRY_T")
public class EntityMilitaryBo extends PersistableBusinessObjectBase implements EntityMilitaryContract {
    private static final long serialVersionUID = -287520618905316980L;


    @Id
    @GeneratedValue(generator = "KRIM_MLTRY_ID_S")
    @PortableSequenceGenerator(name = "KRIM_MLTRY_ID_S")
    @Column(name = "MLTRY_ID")
    private String id;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "DSCHRG_DT")
    @Temporal(TemporalType.DATE)
    private Date dischageDateValue;

    @Column(name = "SLCTV_SRV_NUM")
    private String selectiveServiceNumber;

    @Column(name = "SLCTV_SRV")
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    private boolean selectiveService;

    @ManyToOne(targetEntity = EntityMilitaryRelationshipStatusBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "RLTNSHP_TYP_CD", insertable = false, updatable = false)
    private EntityMilitaryRelationshipStatusBo relationshipStatus;

    @Column(name = "ACTV_IND")
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    private boolean active;


    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public EntityMilitaryRelationshipStatusBo getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(EntityMilitaryRelationshipStatusBo relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    @Override
    public boolean isSelectiveService() {
        return selectiveService;
    }

    public void setSelectiveService(boolean selectiveService) {
        this.selectiveService = selectiveService;
    }

    @Override
    public String getSelectiveServiceNumber() {
        return selectiveServiceNumber;
    }

    public void setSelectiveServiceNumber(String selectiveServiceNumber) {
        this.selectiveServiceNumber = selectiveServiceNumber;
    }

    @Override
    public DateTime getDischargeDate() {
        return new DateTime(dischageDateValue);
    }

    public Date getDischageDateValue(){
        return new Date(dischageDateValue.getTime());
    }

    public void setDischageDateValue(Date dischageDateValue) {
        this.dischageDateValue = dischageDateValue;
    }


    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
