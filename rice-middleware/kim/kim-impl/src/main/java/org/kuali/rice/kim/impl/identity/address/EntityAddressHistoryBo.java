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
package org.kuali.rice.kim.impl.identity.address;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.address.EntityAddressHistory;
import org.kuali.rice.kim.api.identity.address.EntityAddressHistoryContract;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

@Entity
@Table(name = "KRIM_HIST_ENTITY_ADDR_T")
public class EntityAddressHistoryBo extends EntityAddressBase implements EntityAddressHistoryContract {
    private static final long serialVersionUID = -8670268472560378016L;
    @Id
    @GeneratedValue(generator = "KRIM_HIST_ENTITY_ADDR_ID_S")
    @PortableSequenceGenerator(name = "KRIM_HIST_ENTITY_ADDR_ID_S")
    @Column(name ="HIST_ID")
    private Long historyId;

    @Column(name = "ENTITY_ADDR_ID")
    private String id;

    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;

    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    //@ManyToOne(fetch = FetchType.EAGER, cascade = {})
    //@JoinColumn(name = "ADDR_TYP_CD", referencedColumnName = "ADDR_TYP_CD", insertable = false, updatable = false)
    @Transient
    private EntityAddressTypeHistoryBo addressType;

    @Override
    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public EntityAddressTypeHistoryBo getAddressType() {
        return addressType;
    }

    public void setAddressType(EntityAddressTypeHistoryBo addressType) {
        this.addressType = addressType;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static EntityAddressHistory to(EntityAddressHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityAddressHistory.Builder.create(bo).build();
    }
    /**
     * Converts a main object to its historical counterpart
     * @param address immutable object
     * @return the history bo
     */
    /*public static EntityAddressHistoryBo from(EntityAddress address,
            Timestamp fromDate,
            Timestamp toDate) {
        if (address == null) {
            return null;
        }

        EntityAddressHistoryBo bo = (EntityAddressHistoryBo) EntityAddressBo.from(address);
        bo.setAddressType(EntityAddressTypeHistoryBo.from(address.getAddressType(), fromDate, toDate));
        bo.setActiveFromDateValue(fromDate == null? null :fromDate);
        bo.setActiveToDateValue(toDate == null? null :toDate);

        return bo;
    }*/

    /**
     * Converts a main object to its historical counterpart
     * @param im immutable object
     * @return the history bo
     */
    public static EntityAddressHistoryBo from(EntityAddressHistory im) {
        if (im == null) {
            return null;
        }

        EntityAddressHistoryBo bo = new EntityAddressHistoryBo();

        bo.setActive(im.isActive());
        bo.setEntityTypeCode(im.getEntityTypeCode());
        if (im.getAddressType() != null) {
            bo.setAddressTypeCode(im.getAddressType().getCode());
        }

        bo.setAddressType(EntityAddressTypeHistoryBo.from(im.getAddressType()));
        bo.setDefaultValue(im.isDefaultValue());
        bo.setAttentionLine(im.getAttentionLineUnmasked());
        bo.setLine1(im.getLine1Unmasked());
        bo.setLine2(im.getLine2Unmasked());
        bo.setLine3(im.getLine3Unmasked());
        bo.setCity(im.getCityUnmasked());
        bo.setStateProvinceCode(im.getStateProvinceCodeUnmasked());
        bo.setCountryCode(im.getCountryCodeUnmasked());
        bo.setPostalCode(im.getPostalCodeUnmasked());
        bo.setAddressFormat(im.getAddressFormat());
        if (im.getModifiedDate() != null) {
            bo.setModifiedDate(new Timestamp(im.getModifiedDate().getMillis()));
        }

        if (im.getValidatedDate() != null) {
            bo.setValidatedDate(new Timestamp(im.getValidatedDate().getMillis()));
        }

        bo.setValidated(im.isValidated());
        bo.setNoteMessage(im.getNoteMessage());
        bo.setId(im.getId());
        bo.setEntityId(im.getEntityId());
        bo.setActive(im.isActive());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));


        return bo;
    }
}
