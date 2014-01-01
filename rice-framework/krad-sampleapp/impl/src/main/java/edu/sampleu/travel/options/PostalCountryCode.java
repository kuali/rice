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
package edu.sampleu.travel.options;

import org.kuali.rice.core.api.mo.common.Coded;

/**
 * This class provides a small subset of postal country codes
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum PostalCountryCode implements Coded {
    AD("AD", "Andorra"),
    AO("AO", "Angola"),
    AI("AI", "Anguilla"),
    AU("AU", "Australia"),
    AT("AT", "Austria"),
    BN("BN", "Brunei Darussalam"),
    BG("BG", "Bulgaria"),
    TD("TD", "Chad"),
    CL("CL", "Chile"),
    CN("CN", "China"),
    YT("YT", "Mayotte"),
    MX("MX", "Mexico"),
    MC("MC", "Monaco"),
    MN("MN", "Mongolia"),
    ME("ME", "Montenegro"),
    MS("MS", "Montserrat"),
    MA("MA", "Morocco"),
    MZ("MZ", "Mozambique"),
    MM("MM", "Myanmar"),
    NO("NO", "Norway"),
    OM("OM", "Oman"),
    PK("PK", "Pakistan"),
    PW("PW", "Palau"),
    RO("RO", "Romania"),
    RU("RU", "Russian Federation"),
    RW("RW", "Rwanda"),
    ZA("ZA", "South Africa"),
    SS("SS", "South Sudan"),
    ES("ES", "Spain"),
    TV("TV", "Tuvalu"),
    UG("UG", "Uganda"),
    UA("UA", "Ukraine"),
    AE("AE", "United Arab Emirates"),
    GB("GB", "United Kingdom"),
    US("US", "United States"),
    ZW("ZW", "Zimbabwe");

    private final String code;
    private final String label;

    PostalCountryCode(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

}
