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
package org.kuali.rice.legacy;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.data.jpa.converters.KualiDecimalConverter;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.KeyValuesFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.document.TransactionalDocumentBase;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LegacyTravelAuthorization extends TransactionalDocumentBase {
    private static final long serialVersionUID = -6609385831976630737L;

    // trip begin date
//    @Temporal(TemporalType.DATE)
//    @Column(name="TRVL_BGN_DT")
//    @Label("Trip Begin Date")
    private Date tripBegin;

    // trip end date
//    @Temporal(TemporalType.DATE)
//    @Column(name="TRVL_END_DT")
//    @Label("Trip End Date")
    private Date tripEnd;

    // travel description
//    @Column(name="TRVL_DESC",length=255)
//    @Label("Business Purpose")
    private String tripDescription;

    // travel destination
//    @Column(name="TRVL_DEST_ID",length=40)
    private String tripDestinationId;
//    @Transient
//    private TravelDestination tripDestination;

    // traveler
//    @Column(name="TRAVELER_DTL_ID",length=40)
    private String travelerDetailId;
//    @Transient
//    private TravelerDetail travelerDetail;

    // travel type code
//    @Column(name = "TRVL_TYP_CD", length = 40)
//    @Label("Travel type code")
//    @Description("Trip Type")
//    @KeyValuesFinderClass(TripTypeKeyValuesFinder.class)
//    @UifDisplayHints({
//            @UifDisplayHint(UifDisplayHintType.DROPDOWN),
//            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    private String travelTypeCode;

    // expense limit
//    @Column(name="EXP_LMT",length=19,precision=2)
//    @Label("Expense Limit")
//    @Convert(converter=KualiDecimalConverter.class)
//    @Description("Expense limit imposed by department or grant or some other budgetary restrictions on trip.")
    private KualiDecimal expenseLimit;

    // contact number
//    @Column(name="CELL_PH_NUM",length=20)
//    @Label("Contact Number")
//    @Description("This is the contact phone number during the trip.")
    private String cellPhoneNumber;

//    @OneToMany(fetch= FetchType.EAGER, orphanRemoval=true, cascade= {CascadeType.ALL}, mappedBy = "travelAuthorizationDocument")
//    private List<TravelPerDiemExpense> dailyExpenseEstimates = new ArrayList<TravelPerDiemExpense>();
//
//    @OneToMany(fetch= FetchType.EAGER, orphanRemoval=true, cascade= {CascadeType.ALL}, mappedBy = "travelAuthorizationDocument")
//    private List<TravelExpenseItem> actualExpenseItems = new ArrayList<TravelExpenseItem>();

    public Date getTripBegin() {
        return tripBegin;
    }

    public void setTripBegin(Date tripBegin) {
        this.tripBegin = tripBegin;
    }

    public Date getTripEnd() {
        return tripEnd;
    }

    public void setTripEnd(Date tripEnd) {
        this.tripEnd = tripEnd;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public String getTravelerDetailId() {
        return travelerDetailId;
    }

    public void setTravelerDetailId(String travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

//    public TravelerDetail getTravelerDetail() {
//        return travelerDetail;
//    }
//
//    public void setTravelerDetail(TravelerDetail travelerDetail) {
//        this.travelerDetail = travelerDetail;
//    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public KualiDecimal getExpenseLimit() {
        return expenseLimit;
    }

    public void setExpenseLimit(KualiDecimal expenseLimit) {
        this.expenseLimit = expenseLimit;
    }

    public String getTripDestinationId() {
        return tripDestinationId;
    }

    public void setTripDestinationId(String tripDestinationId) {
        this.tripDestinationId = tripDestinationId;
    }

//    public TravelDestination getTripDestination() {
//        return tripDestination;
//    }
//
//    public void setTripDestination(TravelDestination tripDestination) {
//        this.tripDestination = tripDestination;
//    }

//    public List<TravelPerDiemExpense> getDailyExpenseEstimates() {
//        return dailyExpenseEstimates;
//    }
//
//    public void setDailyExpenseEstimates(List<TravelPerDiemExpense> dailyExpenseEstimates) {
//        this.dailyExpenseEstimates = dailyExpenseEstimates;
//    }

//    public List<TravelExpenseItem> getActualExpenseItems() {
//        return actualExpenseItems;
//    }
//
//    public void setActualExpenseItems(List<TravelExpenseItem> actualExpenseItems) {
//        this.actualExpenseItems = actualExpenseItems;
//    }

    public String getTravelTypeCode() {
        return travelTypeCode;
    }

    public void setTravelTypeCode(String travelTypeCode) {
        this.travelTypeCode = travelTypeCode;
    }
}
