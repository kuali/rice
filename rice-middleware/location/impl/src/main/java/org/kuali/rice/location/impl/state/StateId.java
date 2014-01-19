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
package org.kuali.rice.location.impl.state;

import org.kuali.rice.krad.data.jpa.IdClassBase;

public class StateId extends IdClassBase {

    private static final long serialVersionUID = -5986624272928043193L;

    private String code;
    private String countryCode;

    public StateId() {}

    public StateId(String code, String countryCode) {
        this.code = code;
        this.countryCode = countryCode;
    }

    public String getCode() {
        return code;
    }

    public String getCountryCode() {
        return countryCode;
    }

}


