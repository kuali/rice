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
package org.kuali.rice.location.impl.county;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.location.api.county.County;
import org.kuali.rice.location.framework.county.CountyEbo;
import org.kuali.rice.location.impl.country.CountryBo;
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

@IdClass(CountyId.class)
@Entity
@Table(name = "KRLC_CNTY_T")
public class CountyBo extends PersistableBusinessObjectBase implements CountyEbo {

    private static final long serialVersionUID = -5988292910408345009L;

    @Id
    @Column(name = "COUNTY_CD")
    private String code;

    @Id
    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;

    @Id
    @Column(name = "STATE_CD")
    private String stateCode;

    @Column(name = "COUNTY_NM")
    private String name;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @ManyToOne(targetEntity = CountryBo.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "POSTAL_CNTRY_CD", insertable = false, updatable = false)
    private CountryBo country;

    @ManyToOne(targetEntity = StateBo.class, fetch = FetchType.EAGER)
    @JoinColumns(value = {
            @JoinColumn(name = "STATE_CD", referencedColumnName = "POSTAL_STATE_CD", insertable = false, updatable = false),
            @JoinColumn(name = "POSTAL_CNTRY_CD", referencedColumnName = "POSTAL_CNTRY_CD", insertable = false, updatable = false)
    })
    private StateBo state;

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
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return An immutable County if the passed in mutable is not null.  If the mutable reference was null,
     * then null is returned.
     */
    public static County to(CountyBo bo) {
        if (bo == null) {
            return null;
        }

        return County.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return a new mutable CountyBo if the passed in immutable is not null.  If the immutable reference was null,
     * then null is returned.
     */
    public static CountyBo from(County im) {
        if (im == null) {
            return null;
        }

        CountyBo bo = new CountyBo();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.countryCode = im.getCountryCode();
        bo.stateCode = im.getStateCode();
        bo.active = im.isActive();
        bo.versionNumber = im.getVersionNumber();

        return bo;
    }
}

