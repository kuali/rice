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
package org.kuali.rice.kim.impl.common.active;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.krad.bo.DataObjectBase;

@MappedSuperclass
public abstract class ActiveFromToBo extends DataObjectBase {
    private static final long serialVersionUID = -7272085876524380076L;
    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;
    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    public boolean isActive(Timestamp activeAsOfDate) {
        return InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), new DateTime(activeAsOfDate.getTime()));
    }

    public boolean isActive(DateTime activeAsOfDate) {
        return InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), activeAsOfDate);
    }

    public boolean isActive() {
        return InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), null);
    }

    public DateTime getActiveFromDate() {
        return this.activeFromDateValue == null ? null : new DateTime(this.activeFromDateValue.getTime());
    }

    public DateTime getActiveToDate() {
        return this.activeToDateValue == null ? null : new DateTime(this.activeToDateValue.getTime());
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
}
