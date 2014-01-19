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
package org.kuali.rice.location.impl.postalcode;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.impl.country.CountryBo;
import org.kuali.rice.location.impl.county.CountyBo;
import org.kuali.rice.location.impl.state.StateBo;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@IdClass(PostalCodeId.class)
@Entity
@Table(name = "KRLC_PSTL_CD_T")
public class PostalCodeBo extends PersistableBusinessObjectBase implements PostalCodeEbo {

    private static final long serialVersionUID = 3951927731999264335L;

    @Id
    @Column(name = "POSTAL_CD")
    private String code;

    @Id
    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;

    @Column(name = "POSTAL_CITY_NM")
    private String cityName;

    @Column(name = "POSTAL_STATE_CD")
    private String stateCode;

    @Column(name = "COUNTY_CD")
    private String countyCode;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @ManyToOne(targetEntity = CountryBo.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "POSTAL_CNTRY_CD", insertable = false, updatable = false)
    private CountryBo country;

    @ManyToOne(targetEntity = StateBo.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
            @JoinColumn(name = "POSTAL_STATE_CD", referencedColumnName="POSTAL_STATE_CD", insertable = false, updatable = false),
            @JoinColumn(name = "POSTAL_CNTRY_CD", referencedColumnName="POSTAL_CNTRY_CD",insertable = false, updatable = false)
    })
    private StateBo state;

    @ManyToOne(targetEntity = CountyBo.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
            @JoinColumn(name = "COUNTY_CD", referencedColumnName="COUNTY_CD", insertable = false, updatable = false),
            @JoinColumn(name="POSTAL_STATE_CD", referencedColumnName="STATE_CD", insertable = false, updatable = false),
            @JoinColumn(name="POSTAL_CNTRY_CD", referencedColumnName="POSTAL_CNTRY_CD", insertable = false, updatable = false)
    })
    private CountyBo county;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public CountryBo getCountry() {
        return country;
    }

    public void setCountry(CountryBo country) {
        this.country = country;
    }

    public StateBo getState() {
        return state;
    }

    public void setState(StateBo state) {
        this.state = state;
    }

    public CountyBo getCounty() {
        return county;
    }

    public void setCounty(CountyBo county) {
        this.county = county;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return An immutable PostalCode if the passed in mutable is not null.  If the mutable reference was null, then
     * null is returned.
     */
    public static PostalCode to(PostalCodeBo bo) {
        if (bo == null) {
            return null;
        }

        return PostalCode.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return a new mutable PostalCodeBo if the passed in mutable is not null.  If the immutable reference was null,
     * then null is returned.
     */
    public static PostalCodeBo from(PostalCode im) {
        if (im == null) {
            return null;
        }

        PostalCodeBo bo = new PostalCodeBo();
        bo.code = im.getCode();
        bo.countryCode = im.getCountryCode();
        bo.cityName = im.getCityName();
        bo.active = im.isActive();
        bo.stateCode = im.getStateCode();
        bo.cityName = im.getCityName();
        bo.versionNumber = im.getVersionNumber();

        return bo;
    }
}

