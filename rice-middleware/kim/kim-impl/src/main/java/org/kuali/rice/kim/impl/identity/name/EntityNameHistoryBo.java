/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.impl.identity.name;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistory;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistoryContract;
import org.kuali.rice.kim.api.identity.name.EntityNameHistory;
import org.kuali.rice.kim.api.identity.name.EntityNameHistoryContract;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationHistoryBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBase;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusHistoryBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeHistoryBo;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Entity
@Table(name = "KRIM_HIST_ENTITY_NM_T")
public class EntityNameHistoryBo extends EntityNameBase implements EntityNameHistoryContract {
    private static final long serialVersionUID = -8670268472560378016L;
    @Id
    @GeneratedValue(generator = "KRIM_HIST_NM_ID_S")
    @PortableSequenceGenerator(name = "KRIM_HIST_NM_ID_S")
    @Column(name ="HIST_ID")
    private Long historyId;

    @Column(name = "ENTITY_NM_ID")
    private String id;

    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;

    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    @Transient
    private EntityNameTypeHistoryBo nameType;


    @Override
    public Long getHistoryId() {
        return historyId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Timestamp getActiveFromDateValue() {
        return activeFromDateValue;
    }

    public void setActiveFromDateValue(Timestamp activeFromDateValue) {
        this.activeFromDateValue = activeFromDateValue;
    }

    public Timestamp getActiveToDateValue() {
        return activeToDateValue;
    }

    public void setActiveToDateValue(Timestamp activeToDateValue) {
        this.activeToDateValue = activeToDateValue;
    }

    public boolean isActive(Timestamp activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), new DateTime(
                activeAsOfDate.getTime()));
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), activeAsOfDate);
    }

    @Override
    public boolean isActiveNow() {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), null);
    }

    @Override
    public DateTime getActiveFromDate() {
        return this.activeFromDateValue == null ? null : new DateTime(this.activeFromDateValue.getTime());
    }

    @Override
    public DateTime getActiveToDate() {
        return this.activeToDateValue == null ? null : new DateTime(this.activeToDateValue.getTime());
    }

    @Override
    public EntityNameTypeHistoryBo getNameType() {
        return nameType;
    }

    public void setNameType(EntityNameTypeHistoryBo nameType) {
        this.nameType = nameType;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static EntityNameHistory to(EntityNameHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityNameHistory.Builder.create(bo).build();
    }


    /**
     * Converts a main object to its historical counterpart
     * @param im immutable object
     * @return the history bo
     */
    public static EntityNameHistoryBo from(EntityNameHistory im) {
        if (im == null) {
            return null;
        }

        EntityNameHistoryBo bo = new EntityNameHistoryBo();

        bo.setId(im.getId());
        bo.setActive(im.isActive());

        bo.setEntityId(im.getEntityId());
        bo.setNameType(EntityNameTypeHistoryBo.from(im.getNameType()));
        if (im.getNameType() != null) {
            bo.setNameCode(im.getNameType().getCode());
        }

        bo.setFirstName(im.getFirstNameUnmasked());
        bo.setLastName(im.getLastNameUnmasked());
        bo.setMiddleName(im.getMiddleNameUnmasked());
        bo.setNamePrefix(im.getNamePrefixUnmasked());
        bo.setNameTitle(im.getNameTitleUnmasked());
        bo.setNameSuffix(im.getNameSuffixUnmasked());
        bo.setNoteMessage(im.getNoteMessage());
        if (im.getNameChangedDate() != null) {
            bo.setNameChangedDate(new Timestamp(im.getNameChangedDate().getMillis()));
        }

        bo.setDefaultValue(im.isDefaultValue());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));


        return bo;
    }
}
