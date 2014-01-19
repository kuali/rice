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

import org.kuali.rice.krad.data.jpa.IdClassBase;

public class PostalCodeId extends IdClassBase {

    private static final long serialVersionUID = 5680559929187623010L;

    private String countryCode;
    private String code;

    public PostalCodeId() {}

    public PostalCodeId(String countryCode, String code) {
        this.countryCode = countryCode;
        this.code = code;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCode() {
        return code;
    }

}
