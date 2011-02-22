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
import org.kuali.rice.shareddata.api.country.Country;

import javax.persistence.*;

/**
 * 
 */
@IdClass(org.kuali.rice.kns.bo.StateImplId.class)
@Entity
@Table(name="KR_STATE_T")
public class StateImpl extends PersistableBusinessObjectBase implements Inactivateable, State {

	@Id
    private String postalCountryCode;
	@Id
    private String postalStateCode;
	@Column(name="POSTAL_STATE_NM")
    private String postalStateName;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
    private boolean active;

	@ManyToOne(targetEntity=Country.class,fetch=FetchType.EAGER)  //TODO: Make CountryBo once StateImpl is converted to StateBo and exists in shareddata
	@JoinColumn(name="POSTAL_CNTRY_CD",insertable=false,updatable=false)
    private Country country;  //TODO: Make CountryBo once StateImpl is converted to StateBo and exists in shareddata

    /**
     * Default no-arg constructor.
     */
    public StateImpl() {

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
     * Gets the postalStateName attribute.
     * 
     * @return Returns the postalStateName
     */
    public String getPostalStateName() {
        return postalStateName;
    }

    /**
     * Sets the postalStateName attribute.
     * 
     * @param postalStateName The postalStateName to set.
     */
    public void setPostalStateName(String postalStateName) {
        this.postalStateName = postalStateName;
    }


    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getPostalStateCode() + " - " + getPostalStateName();
        return theString;
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
}
