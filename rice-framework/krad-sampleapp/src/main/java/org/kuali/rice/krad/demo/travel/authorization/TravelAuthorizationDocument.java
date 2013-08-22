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
package org.kuali.rice.krad.demo.travel.authorization;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.NonPersistentProperty;
import org.kuali.rice.krad.data.provider.annotation.ShortLabel;
import org.kuali.rice.krad.demo.travel.authorization.dataobject.TravelerDetail;
import org.kuali.rice.krad.document.TransactionalDocumentBase;


/**
 * Travel authorization transactional document.
 *
 * <p>
 *  This is a sample KRAD transactional document that demonstrates how
 *  to implement transactional documents within the KRAD UIF.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "TRVL_AUTH_DOC_T")
@AttributeOverrides({
    @AttributeOverride(name="documentNumber", column=@Column(name="FDOC_NBR",insertable=true,updatable=true,length=14))
})
@AssociationOverrides({
	@AssociationOverride(name="pessimisticLocks",joinColumns= {@JoinColumn(
            name = "FDOC_NBR", insertable = false, updatable = false)})
})
public class TravelAuthorizationDocument extends TransactionalDocumentBase {
	private static final long serialVersionUID = -6609385831976630737L;

	@Column(name="TRVL_ID",length=19)
	@Label("TEM Doc #")
    private String travelDocumentIdentifier;

	@Column(name="TRIP_BGN_DT")
	@Temporal(TemporalType.DATE)
    private Date tripBegin;

	@Column(name="TRIP_END_DT")
	@Temporal(TemporalType.DATE)
    private Date tripEnd;

	@Column(name="TRIP_DESC",length=255)
	@Label("Business Purpose")
	private String tripDescription;
    @Column(name="TRIP_TYP_CD",length=3)
    private String tripTypeCode;

    // Traveler section
	@Column(name="TRAVELER_DTL_ID",length=19,precision=0)
    private Integer travelerDetailId;
	@Transient
    private TravelerDetail travelerDetail;

    // Special Circumstances
	@Column(name="EXP_LMT",length=19,precision=2)
	@Label("If there is an expense limit imposed by department or grant or some other budgetary restrictions on this trip, please enter the expense limit here $")
	@Description("Expense limit imposed by department or grant or some other budgetary restrictions on trip.")
    private KualiDecimal expenseLimit;
	@Column(name="DELINQUENT_TR_EXCEPTION",length=1)
	@Label("Why oh Why?")
    private Boolean questionForTaWhy;
	@Transient
	@NonPersistentProperty
	@Label("Question for TA - is anyone traveling with you?")
	@Size(max=255)
    private String questionForTa;
	@Transient
	@NonPersistentProperty
	@Label("Question for TA documents - not free form: Carrying Fruit?")
    private Boolean questionForTaDocWhy;
	@Transient
	@NonPersistentProperty
	@Label("Do you have large pets traveling with you?")
	@Size(max=255)
    private String questionForTaDoc;

    // Emergency Contact
	@Column(name="CELL_PH_NUM",length=20)
	@Label("Traveler's Cell or Other Contact Number During Trip")
    private String cellPhoneNumber;
	@Column(name="RGN_FAMIL",length=255)
    private String regionFamiliarity;
	@Column(name="CTZN_CNTRY_CD",length=2)
    private String citizenshipCountryCode;
	@Transient
	@NonPersistentProperty
	@Label("Modes of Transportation while out-of-country")
	@ShortLabel("Transportation Modes")
    private String transportationModeCode;

    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

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


    public void setTripTypeCode(String tripTypeCode) {
        this.tripTypeCode = tripTypeCode;
    }


    public String getTripTypeCode() {
        return tripTypeCode;
    }


    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }


    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }



    public TravelerDetail getTravelerDetail() {
        return travelerDetail;
    }


    public void setTravelerDetail(TravelerDetail travelerDetail) {
        this.travelerDetail = travelerDetail;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }


    public void setTravelerDetailId(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }


    public String getRegionFamiliarity() {
        return regionFamiliarity;
    }


    public void setRegionFamiliarity(String regionFamiliarity) {
        this.regionFamiliarity = regionFamiliarity;
    }


    public String getCitizenshipCountryCode() {
        return citizenshipCountryCode;
    }


    public void setCitizenshipCountryCode(String citizenshipCountryCode) {
        this.citizenshipCountryCode = citizenshipCountryCode;
    }


    public String getTransportationModeCode() {
        return transportationModeCode;
    }


    public void setTransportationModeCode(String transportationModeCode) {
        this.transportationModeCode = transportationModeCode;
    }


    public KualiDecimal getExpenseLimit() {
        return expenseLimit;
    }


    public void setExpenseLimit(KualiDecimal expenseLimit) {
        this.expenseLimit = expenseLimit;
    }


    public Boolean getQuestionForTaWhy() {
        return questionForTaWhy;
    }


    public void setQuestionForTaWhy(Boolean questionForTaWhy) {
        this.questionForTaWhy = questionForTaWhy;
    }


    public String getQuestionForTa() {
        return questionForTa;
    }


    public void setQuestionForTa(String questionForTa) {
        this.questionForTa = questionForTa;
    }


    public Boolean getQuestionForTaDocWhy() {
        return questionForTaDocWhy;
    }


    public void setQuestionForTaDocWhy(Boolean questionForTaDocWhy) {
        this.questionForTaDocWhy = questionForTaDocWhy;
    }


    public String getQuestionForTaDoc() {
        return questionForTaDoc;
    }


    public void setQuestionForTaDoc(String questionForTaDoc) {
        this.questionForTaDoc = questionForTaDoc;
    }

}
