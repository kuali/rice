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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;


@Entity
@Table(name="TRVL_TRAVELER_DTL_T")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY, UifAutoCreateViewType.LOOKUP})
public class TravelerDetail extends DataObjectBase implements MutableInactivatable {
	private static final long serialVersionUID = -7169083136626617130L;

    @Id
    @GeneratedValue(generator = "TRVL_TRAVELER_DTL_ID_S")
    @SequenceGenerator(name = "TRVL_TRAVELER_DTL_ID_S", sequenceName = "TRVL_TRAVELER_DTL_ID_S", allocationSize = 5)
    @Column(name = "id", length = 40, nullable = false)
	protected String id;
    @Column(name = "doc_nbr", length=14)
    protected String documentNumber;
    @Column(name = "EMP_PRINCIPAL_ID")
    protected String principalId;
    @Relationship(foreignKeyFields="principalId")
    @Transient
    @InheritProperties({
    		@InheritProperty(name="principalName",label=@Label("Traveler User ID")),
    		@InheritProperty(name="name",label=@Label("Traveler Name"))
    })
	private Person person;
    @Column(name = "first_nm", length = 40, nullable = false)
    protected String firstName;
    @Column(length = 40, nullable = true)
    protected String middleName;
    @Column(name = "last_nm", length = 40, nullable = false)
    protected String lastName;
    @Column(name = "addr_line_1", length = 50, nullable = false)
    protected String streetAddressLine1;
    @Column(name = "addr_line_2", length = 50, nullable = true)
    protected String streetAddressLine2;
    @Column(name = "city_nm", length = 50, nullable = true)
    protected String cityName;
    @Column(name = "postal_state_cd", length = 2, nullable = false)
    protected String stateCode;
    @Column(name = "postal_cd", length = 11, nullable = false)
    protected String zipCode;
    @Column(name = "country_cd", length = 2, nullable = true)
    protected String countryCode;
    @Column(name = "citizenship", length = 40, nullable = true)
    protected String citizenship;
    @Column(name = "email_addr", length = 50, nullable = true)
    protected String emailAddress;
    @Transient
    protected Date dateOfBirth;
    @Column(name = "gender", length = 1, nullable = false)
    protected String gender;
    @Column(name = "phone_nbr", length = 20, nullable = true)
    protected String phoneNumber;
    @Column(name = "traveler_typ_cd", length = 3, nullable = false)
    protected String travelerTypeCode;
//    @ManyToOne
//    @JoinColumn(name = "traveler_typ_cd", insertable=false, updatable=false)
    @Transient
    protected TravelerType travelerType;
    @Column(name = "customer_num", length = 40, nullable = true)
    protected String customerNumber;
    //protected AccountsReceivableCustomer customer;
    @Transient
    protected boolean liabilityInsurance;

    @Column(name = "drive_lic_num", length = 20, nullable = true)
    protected String driversLicenseNumber;
    @Transient
    protected String driversLicenseState;
    @Column(name = "drive_lic_exp_dt")
    @Temporal(TemporalType.DATE)
    protected Date driversLicenseExpDate;

    // Notification
    @Transient
    protected Boolean notifyTAFinal = Boolean.FALSE;
    @Transient
    protected Boolean notifyTAStatusChange = Boolean.FALSE;
    @Transient
    protected Boolean notifyTERFinal = Boolean.FALSE;
    @Transient
    protected Boolean notifyTERStatusChange = Boolean.FALSE;

    @Column(name = "ACTV_IND", nullable = false, length = 1)
    protected Boolean active = Boolean.TRUE;
    @Column(name = "non_res_alien", length = 1, nullable = true)
    protected Boolean nonResidentAlien = Boolean.FALSE;
    @Transient
    protected Boolean motorVehicleRecordCheck = Boolean.FALSE;

    /**
     * This method returns the document number this TravelerDetail object is associated with
     *
     * @return document number
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * This method sets the document number this TravelerDetail object will be associated with
     *
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getName() {
        String name = "";
        if(StringUtils.isNotBlank(getFirstName()))
            name += getFirstName();
        if(StringUtils.isNotBlank(getMiddleName()))
            name += " " + getMiddleName();
        if(StringUtils.isNotBlank(getLastName()))
            name += " " + getLastName();
        return name;
    }

    public String getStreetAddressLine1() {
        return streetAddressLine1;
    }


    public void setStreetAddressLine1(String streetAddressLine1) {
        this.streetAddressLine1 = streetAddressLine1;
    }

    public String getStreetAddressLine2() {
        return streetAddressLine2;
    }


    public void setStreetAddressLine2(String streetAddressLine2) {
        this.streetAddressLine2 = streetAddressLine2;
    }

    public String getCityName() {
        return cityName;
    }


    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateCode() {
        return stateCode;
    }


    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipCode() {
        return zipCode;
    }


    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountryCode() {
        return countryCode;
    }


    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTravelerTypeCode() {
        return travelerTypeCode;
    }


    public void setTravelerTypeCode(String travelerTypeCode) {
        this.travelerTypeCode = travelerTypeCode;
    }

    public TravelerType getTravelerType() {
        return travelerType;
    }

    public void setTravelerType(TravelerType travelerType) {
        this.travelerType = travelerType;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public boolean isLiabilityInsurance() {
        return liabilityInsurance;
    }

    public void setLiabilityInsurance(boolean liabilityInsurance) {
        this.liabilityInsurance = liabilityInsurance;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public String getDriversLicenseState() {
        return driversLicenseState;
    }

    public void setDriversLicenseState(String driversLicenseState) {
        this.driversLicenseState = driversLicenseState;
    }

    public Date getDriversLicenseExpDate() {
        return driversLicenseExpDate;
    }

    public void setDriversLicenseExpDate(Date driversLicenseExpDate) {
        this.driversLicenseExpDate = driversLicenseExpDate;
    }

    public boolean getNotifyTAFinal() {
        return notifyTAFinal;
    }

    public boolean getNotifyTAStatusChange() {
        return notifyTAStatusChange;
    }

    public boolean getNotifyTERFinal() {
        return notifyTERFinal;
    }

    public boolean getNotifyTERStatusChange() {
        return notifyTERStatusChange;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isNotifyTAFinal() {
        return notifyTAFinal;
    }

    public void setNotifyTAFinal(boolean notifyTAFinal) {
        this.notifyTAFinal = notifyTAFinal;
    }

    public boolean isNotifyTAStatusChange() {
        return notifyTAStatusChange;
    }

    public void setNotifyTAStatusChange(boolean notifyTAStatusChange) {
        this.notifyTAStatusChange = notifyTAStatusChange;
    }

    public boolean isNotifyTERFinal() {
        return notifyTERFinal;
    }

    public void setNotifyTERFinal(boolean notifyTERFinal) {
        this.notifyTERFinal = notifyTERFinal;
    }

    public boolean isNotifyTERStatusChange() {
        return notifyTERStatusChange;
    }

    public void setNotifyTERStatusChange(boolean notifyTERStatusChange) {
        this.notifyTERStatusChange = notifyTERStatusChange;
    }

    public boolean isMotorVehicleRecordCheck() {
        return motorVehicleRecordCheck;
    }

    public void setMotorVehicleRecordCheck(boolean motorVehicleRecordCheck) {
        this.motorVehicleRecordCheck = motorVehicleRecordCheck;
    }

    public boolean isNonResidentAlien() {
        return nonResidentAlien;
    }

    public void setNonResidentAlien(boolean nonResidentAlien) {
        this.nonResidentAlien = nonResidentAlien;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
