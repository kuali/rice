package org.kuali.rice.shareddata.framework.state

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.shareddata.api.state.State
import org.kuali.rice.shareddata.api.state.StateContract
import org.kuali.rice.shareddata.framework.country.CountryEbo

class StateEbo implements Inactivateable, StateContract, ExternalizableBusinessObject {
    def String code;
    def String countryCode;
    def String name;
    def boolean active;
    def CountryEbo country;

    /**
     * Converts a mutable bo to it's immutable counterpart
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
     * Converts a immutable object to it's mutable bo counterpart
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

        return bo
    }

    void refresh() { }
}
