/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.shareddata.framework.county

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.shareddata.api.county.County
import org.kuali.rice.shareddata.api.county.CountyContract
import org.kuali.rice.shareddata.framework.country.CountryEbo
import org.kuali.rice.shareddata.framework.state.StateEbo

class CountyEbo implements Inactivateable, CountyContract, ExternalizableBusinessObject {
    def String code
    def String countryCode
    def String stateCode
    def String name
    def boolean active
    def CountryEbo country;
    def StateEbo state;
    def Long versionNumber

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static County to(CountyEbo bo) {
        if (bo == null) {
            return null
        }

        return County.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static CountyEbo from(County im) {
        if (im == null) {
            return null
        }

        CountyEbo bo = new CountyEbo()
        bo.code = im.code
        bo.name = im.name
        bo.countryCode = im.countryCode
        bo.stateCode = im.stateCode
        bo.active = im.active
        bo.versionNumber = im.versionNumber

        return bo
    }

    void refresh() { }
}
