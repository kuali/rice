/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.location.framework.state

import org.kuali.rice.krad.bo.ExternalizableBusinessObject

import org.kuali.rice.location.api.state.State
import org.kuali.rice.location.api.state.StateContract
import org.kuali.rice.location.framework.country.CountryEbo
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable

//@ToString
class StateEbo implements MutableInactivatable, StateContract, ExternalizableBusinessObject {
    def String code;
    def String countryCode;
    def String name;
    def boolean active;
    def CountryEbo country;
    def Long versionNumber;

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static State to(StateEbo bo) {
        if (bo == null) {
            return null
        }

        return State.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static StateEbo from(State im) {
        if (im == null) {
            return null
        }

        StateEbo bo = new StateEbo()
        bo.code = im.code
        bo.countryCode = im.countryCode
        bo.name = im.name
        bo.active = im.active
        bo.versionNumber = im.versionNumber

        return bo
    }

    void refresh() { }
}
