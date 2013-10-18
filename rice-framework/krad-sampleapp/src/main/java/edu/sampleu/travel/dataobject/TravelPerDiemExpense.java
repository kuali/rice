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
package edu.sampleu.travel.dataobject;

import org.kuali.rice.krad.bo.DataObjectBase;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class provides the per diem expenses
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelPerDiemExpense extends DataObjectBase implements Serializable {

    private static final long serialVersionUID = 6269893036439679855L;

    private String travelPerDiemExpenseId;

    private Date perDiemDate;

    private BigDecimal breakfastValue;

    private BigDecimal lunchValue;

    private BigDecimal dinnerValue;

    private BigDecimal incidentalsValue;

    private String mileageRateCd;

    private BigDecimal estimatedMileage;

    public String getTravelPerDiemExpenseId() {
        return travelPerDiemExpenseId;
    }

    public void setTravelPerDiemExpenseId(String travelPerDiemExpenseId) {
        this.travelPerDiemExpenseId = travelPerDiemExpenseId;
    }

    public Date getPerDiemDate() {
        return perDiemDate;
    }

    public void setPerDiemDate(Date perDiemDate) {
        this.perDiemDate = perDiemDate;
    }

    public BigDecimal getBreakfastValue() {
        return breakfastValue;
    }

    public void setBreakfastValue(BigDecimal breakfastValue) {
        this.breakfastValue = breakfastValue;
    }

    public BigDecimal getLunchValue() {
        return lunchValue;
    }

    public void setLunchValue(BigDecimal lunchValue) {
        this.lunchValue = lunchValue;
    }

    public BigDecimal getDinnerValue() {
        return dinnerValue;
    }

    public void setDinnerValue(BigDecimal dinnerValue) {
        this.dinnerValue = dinnerValue;
    }

    public BigDecimal getIncidentalsValue() {
        return incidentalsValue;
    }

    public void setIncidentalsValue(BigDecimal incidentalsValue) {
        this.incidentalsValue = incidentalsValue;
    }

    public String getMileageRateCd() {
        return mileageRateCd;
    }

    public void setMileageRateCd(String mileageRateCd) {
        this.mileageRateCd = mileageRateCd;
    }

    public BigDecimal getEstimatedMileage() {
        return estimatedMileage;
    }

    public void setEstimatedMileage(BigDecimal estimatedMileage) {
        this.estimatedMileage = estimatedMileage;
    }

}
