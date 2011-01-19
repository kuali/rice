/*
 * Copyright 2007-2009 The Kuali Foundation
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

package org.kuali.rice.kns.bo;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * 
 */
@IdClass(org.kuali.rice.kns.bo.PostalCodeImplId.class)
@Entity
@Table(name="KR_POSTAL_CODE_T")
public class PostalCodeImpl extends PersistableBusinessObjectBase implements Inactivateable, PostalCode {

	@Id
    private String postalCountryCode;
	@Id
    private String postalCode;
	@Column(name="POSTAL_STATE_CD")
    private String postalStateCode;
	@Column(name="POSTAL_CITY_NM")
    private String postalCityName;
	@Transient
    private String buildingCode;
	@Transient
    private String buildingRoomNumber;
    @Type(type="yes_no")
	@Column(name="ACTV_IND")
    private boolean active;
    @Column(name="COUNTY_CD")
    private String countyCode;

    @ManyToOne(targetEntity=org.kuali.rice.kns.bo.StateImpl.class,fetch=FetchType.LAZY)
	@JoinColumns({@JoinColumn(name="POSTAL_CNTRY_CD",insertable=false,updatable=false),@JoinColumn(name="POSTAL_STATE_CD",insertable=false,updatable=false)})
    private State state;
    
    @ManyToOne(targetEntity=org.kuali.rice.kns.bo.CountryImpl.class,fetch=FetchType.LAZY)
	@JoinColumn(name="POSTAL_CNTRY_CD",insertable=false,updatable=false)
    private Country country;
    
    @ManyToOne(targetEntity=org.kuali.rice.kns.bo.CountyImpl.class,fetch=FetchType.LAZY)
	@JoinColumns({@JoinColumn(name="POSTAL_CNTRY_CD",insertable=false,updatable=false),@JoinColumn(name="POSTAL_STATE_CD",insertable=false,updatable=false),@JoinColumn(name="COUNTY_CD",insertable=false,updatable=false)})
    private County county;
    
    /**
     * Default no-arg constructor.
     */
    public PostalCodeImpl() {

    }

    /**
     * Gets the postalCode attribute.
     * 
     * @return Returns the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postalCode attribute.
     * 
     * @param postalCode The postalZipCode to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the postalStateCode attribute.
     * 
     * @return Returns the postalStateCode
     */
    public String getPostalStateCode() {
        return postalStateCode;
    }

    /**
     * Sets the postalStateCode attribute.
     * 
     * @param postalStateCode The postalStateCode to set.
     */
    public void setPostalStateCode(String postalStateCode) {
        this.postalStateCode = postalStateCode;
    }

    /**
     * Gets the postalCityName attribute.
     * 
     * @return Returns the postalCityName
     */
    public String getPostalCityName() {
        return postalCityName;
    }

    /**
     * Sets the postalCityName attribute.
     * 
     * @param postalCityName The postalCityName to set.
     */
    public void setPostalCityName(String postalCityName) {
        this.postalCityName = postalCityName;
    }

    /**
     * Gets the state attribute.
     * 
     * @return Returns the state.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state attribute value.
     * 
     * @param state The state to set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    /**
     * Gets the postalCountryCode attribute.
     * 
     * @return Returns the postalCountryCode.
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }

    /**
     * Sets the postalCountryCode attribute value.
     * 
     * @param postalCountryCode The postalCountryCode to set.
     */
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }

    /**
     * Gets the country attribute.
     * 
     * @return Returns the country.
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the country attribute value.
     * 
     * @param country The country to set.
     */
    public void setCountry(Country country) {
        this.country = country;
    }
    
    
    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    
}
