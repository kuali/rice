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
package edu.sampleu.travel.dataobject;

import edu.sampleu.travel.options.MileageRateKeyValues;
import edu.sampleu.travel.options.TravelDestinationKeyValues;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.KeyValuesFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;

import javax.persistence.CascadeType;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class provides the per diem expenses.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "TRVL_PD_EXP_T")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY, UifAutoCreateViewType.LOOKUP})
public class TravelPerDiemExpense extends DataObjectBase implements Serializable {

    private static final long serialVersionUID = 6269893036439679855L;

    @Id
    @Column(name = "PD_EXP_ID", length = 10)
    @GeneratedValue(generator = "TRVL_PD_EXP_ID_S")
    @PortableSequenceGenerator(name = "TRVL_PD_EXP_ID_S")
    @Label("Id")
    @Description("Unique identifier for per diem expense item")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String travelPerDiemExpenseId;

    @Column(name="TRVL_AUTH_DOC_ID", length=40)
    @Label("Travel Authorization Document Id")
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    private String travelAuthorizationDocumentId;

    @Relationship(foreignKeyFields="travelAuthorizationDocumentId")
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
    @JoinColumn(name = "TRVL_AUTH_DOC_ID", referencedColumnName = "TRVL_AUTH_DOC_ID",  insertable = false, updatable = false)
    @InheritProperties({
            @InheritProperty(name="documentNumber",
                    label=@Label("Travel Authorization Document"),
                    displayHints=@UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)))})
    private TravelAuthorizationDocument travelAuthorizationDocument;

    @Column(name="TRVL_DEST_ID", length=40)
    @Label("Primary Destination")
    @Description("Primary Destination related to per diem expense")
    @KeyValuesFinderClass(TravelDestinationKeyValues.class)
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NONE),
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    private String travelDestinationId;

    @Relationship(foreignKeyFields="travelDestinationId")
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
    @JoinColumn(name="TRVL_DEST_ID", insertable=false, updatable=false)
    @InheritProperties({
            @InheritProperty(name="travelDestinationId",
                    label=@Label("Primary Destination"),
                    displayHints=@UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)))})
    @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)
    private TravelDestination travelDestination;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PD_DT")
    @Label("Date of Use")
    private Date perDiemDate;

    @Column(name = "BKFST_VAL")
    @Label("Breakfast Value")
    private BigDecimal breakfastValue;

    @Column(name = "LNCH_VAL")
    @Label("Lunch Value")
    private BigDecimal lunchValue;

    @Column(name = "DNNR_VAL")
    @Label("Dinner Value")
    private BigDecimal dinnerValue;

    @Column(name = "INCD_VAL")
    @Label("Amount estimated for incidentals")
    private BigDecimal incidentalsValue;

    @Column(name="MLG_RT_ID", length=40)
    @Label("Mileage Rate")
    @Description("Mileage Rate Code Used")
    @KeyValuesFinderClass(MileageRateKeyValues.class)
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NONE),
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    private String mileageRateId;

    @Relationship(foreignKeyFields="mileageRateId")
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
    @JoinColumn(name="MLG_RT_ID", insertable=false, updatable=false)
    @InheritProperties({
            @InheritProperty(name="mileageRateCd",
                    label=@Label("Mileage rate"),
                    displayHints=@UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)))})
    private TravelMileageRate mileageRate;

    @Column(name = "MLG_EST")
    @Label("Number of estimated miles")
    private BigDecimal estimatedMileage;

    public String getTravelPerDiemExpenseId() {
        return travelPerDiemExpenseId;
    }

    public void setTravelPerDiemExpenseId(String travelPerDiemExpenseId) {
        this.travelPerDiemExpenseId = travelPerDiemExpenseId;
    }

    public String getTravelAuthorizationDocumentId() {
        if (StringUtils.isBlank(travelAuthorizationDocumentId)
                && this.travelAuthorizationDocument != null) {
            return this.travelAuthorizationDocument.getDocumentNumber();
        }

        return travelAuthorizationDocumentId;
    }

    public void setTravelAuthorizationDocumentId(String travelAuthorizationDocumentId) {
        this.travelAuthorizationDocumentId = travelAuthorizationDocumentId;
    }

    public TravelAuthorizationDocument getTravelAuthorizationDocument() {
        return travelAuthorizationDocument;
    }

    public void setTravelAuthorizationDocument(TravelAuthorizationDocument travelAuthorizationDocument) {
        this.travelAuthorizationDocument = travelAuthorizationDocument;
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

    public String getTravelDestinationId() {
        return travelDestinationId;
    }

    public void setTravelDestinationId(String travelDestinationId) {
        this.travelDestinationId = travelDestinationId;
    }

    public TravelDestination getTravelDestination() {
        return travelDestination;
    }

    public void setTravelDestination(TravelDestination travelDestination) {
        this.travelDestination = travelDestination;
    }

    public String getMileageRateId() {
        return mileageRateId;
    }

    public void setMileageRateId(String mileageRateId) {
        this.mileageRateId = mileageRateId;
    }

    public TravelMileageRate getMileageRate() {
        return mileageRate;
    }

    public void setMileageRate(TravelMileageRate mileageRate) {
        this.mileageRate = mileageRate;
    }

    public BigDecimal getEstimatedMileage() {
        return estimatedMileage;
    }

    public void setEstimatedMileage(BigDecimal estimatedMileage) {
        this.estimatedMileage = estimatedMileage;
    }
}