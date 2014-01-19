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
package org.kuali.rice.location.impl.country;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.framework.country.CountryEbo;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KRLC_CNTRY_T")
public class CountryBo extends PersistableBusinessObjectBase implements CountryEbo {

    private static final long serialVersionUID = 5725250018402409870L;

    @Id
    @Column(name = "POSTAL_CNTRY_CD")
    private String code;

    @Column(name = "ALT_POSTAL_CNTRY_CD")
    private String alternateCode;

    @Column(name = "POSTAL_CNTRY_NM")
    private String name;

    @Column(name = "PSTL_CNTRY_RSTRC_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean restricted;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getAlternateCode() {
        return alternateCode;
    }

    public void setAlternateCode(String alternateCode) {
        this.alternateCode = alternateCode;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Converts a mutable CountryBo to an immutable Country representation.
     * @param bo
     * @return an immutable Country
     */
    public static Country to(CountryBo bo) {
        if (bo == null) {
            return null;
        }
        return Country.Builder.create(bo).build();
    }

    /**
     * Creates a CountryBo business object from an immutable representation of a Country.
     * @param immutable an immutable Country
     * @return a CountryBo
     */
    public static CountryBo from(Country immutable) {
        if (immutable == null) {
            return null;
        }

        CountryBo bo = new CountryBo();
        bo.code = immutable.getCode();
        bo.alternateCode = immutable.getAlternateCode();
        bo.name = immutable.getName();
        bo.restricted = immutable.isRestricted();
        bo.active = immutable.isActive();
        bo.versionNumber = immutable.getVersionNumber();

        return bo;
    }
}
