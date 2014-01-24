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
package org.kuali.rice.kim.impl.identity.citizenship;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@MappedSuperclass
public abstract class EntityCitizenshipBase extends DataObjectBase implements EntityCitizenshipContract {
    private static final long serialVersionUID = 1L;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;

    @Column(name = "CTZNSHP_STAT_CD")
    private String statusCode;

    @Column(name = "STRT_DT")
    private Timestamp startDateValue;


    @Column(name = "END_DT")
    private Timestamp endDateValue;

    @Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;


    @Override
    public DateTime getStartDate() {
        if (this.startDateValue != null) {
            return new DateTime(this.startDateValue);
        }

        return null;
    }

    @Override
    public DateTime getEndDate() {
        if (this.endDateValue != null) {
            return new DateTime(this.endDateValue);
        }

        return null;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Timestamp getStartDateValue() {
        return startDateValue;
    }

    public void setStartDateValue(Timestamp startDateValue) {
        this.startDateValue = startDateValue;
    }

    public Timestamp getEndDateValue() {
        return endDateValue;
    }

    public void setEndDateValue(Timestamp endDateValue) {
        this.endDateValue = endDateValue;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
