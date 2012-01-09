/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.location.framework.postalcode

import org.kuali.rice.krad.bo.ExternalizableBusinessObject

import org.kuali.rice.location.api.postalcode.PostalCode
import org.kuali.rice.location.api.postalcode.PostalCodeContract
import org.kuali.rice.location.framework.country.CountryEbo
import org.kuali.rice.location.framework.county.CountyEbo
import org.kuali.rice.location.framework.state.StateEbo
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable

//@ToString
class PostalCodeEbo implements MutableInactivatable, PostalCodeContract, ExternalizableBusinessObject {
    def String code;
    def String countryCode;
    def String cityName;
    def String stateCode;
    def String countyCode;
    def boolean active;
    def CountryEbo country;
    def StateEbo state;
    def CountyEbo county;
    def Long versionNumber;

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static PostalCode to(PostalCodeEbo bo) {
        if (bo == null) {
            return null
        }

        return PostalCode.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static PostalCodeEbo from(PostalCode im) {
        if (im == null) {
            return null
        }

        PostalCodeEbo bo = new PostalCodeEbo()
        bo.code = im.code
        bo.countryCode = im.countryCode
        bo.cityName = im.cityName
        bo.active = im.active
        bo.stateCode = im.stateCode
        bo.cityName = im.cityName
        bo.versionNumber = im.versionNumber

        return bo
    }

    void refresh() { }
}
