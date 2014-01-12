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
 * This class provides state postal codes for US
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum PostalStateCode implements Coded {

    AL("AL", "Alabama"),
    AK("AK", "Alaska"),
    AZ("AZ", "Arizona"),
    AR("AR", "Arkansas"),
    CA("CA", "California"),
    CO("CO", "Colorado"),
    CT("CT", "Connecticut"),
    DE("DE", "Delaware"),
    FL("FL", "Florida"),
    GA("GA", "Georgia"),
    HI("HI", "Hawaii"),
    ID("ID", "Idaho"),
    IL("IL", "Illinois"),
    IN("IN", "Indiana"),
    IA("IA", "Iowa"),
    KS("KS", "Kansas"),
    KY("KY", "Kentucky"),
    LA("LA", "Louisiana"),
    ME("ME", "Maine"),
    MD("MD", "Maryland"),
    MA("MA", "Massachusetts"),
    MI("MI", "Michigan"),
    MN("MN", "Minnesota"),
    MS("MS", "Mississippi"),
    MO("MO", "Missouri"),
    MT("MT", "Montana"),
    NE("NE", "Nebraska"),
    NV("NV", "Nevada"),
    NH("NH", "New Hampshire"),
    NJ("NJ", "New Jersey"),
    NM("NM", "New Mexico"),
    NY("NY", "New York"),
    NC("NC", "North Carolina"),
    ND("ND", "North Dakota"),
    OH("OH", "Ohio"),
    OK("OK", "Oklahoma"),
    OR("OR", "Oregon"),
    PA("PA", "Pennsylvania"),
    RI("RI", "Rhode Island"),
    SC("SC", "South Carolina"),
    SD("SD", "South Dakota"),
    TN("TN", "Tennessee"),
    TX("TX", "Texas"),
    UT("UT", "Utah"),
    VT("VT", "Vermont"),
    VA("VA", "Virginia"),
    WA("WA", "Washington"),
    WV("WV", "West Virginia"),
    WI("WI", "Wisconsin"),
    WY("WY", "Wyoming"),
    DC("DC", "District of Columbia"),
    AS("AS", "American Samoa"),
    GU("GU", "Guam"),
    MP("MP", "Northern Mariana Islands"),
    PR("PR", "Puerto Rico"),
    UM("UM", "United States Minor Outlying Islands"),
    VI("VI", "Virgin Islands");

    private final String code;
    private final String label;

    PostalStateCode(String code, String label) {
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

