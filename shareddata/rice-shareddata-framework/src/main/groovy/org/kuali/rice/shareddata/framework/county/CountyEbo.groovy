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

    /**
     * Converts a mutable bo to it's immutable counterpart
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
     * Converts a immutable object to it's mutable bo counterpart
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

        return bo
    }

    void refresh() { }
}
