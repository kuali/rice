package org.kuali.rice.shareddata.framework.postalcode

import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.shareddata.api.postalcode.PostalCode
import org.kuali.rice.shareddata.api.postalcode.PostalCodeContract
import org.kuali.rice.shareddata.framework.country.CountryEbo
import org.kuali.rice.shareddata.framework.county.CountyEbo
import org.kuali.rice.shareddata.framework.state.StateEbo

class PostalCodeEbo implements Inactivateable, PostalCodeContract, ExternalizableBusinessObject {
    def String code;
    def String countryCode;
    def String cityName;
    def String stateCode;
    def String countyCode;
    def boolean active;
    def CountryEbo country;
    def StateEbo state;
    def CountyEbo county;

    /**
     * Converts a mutable bo to it's immutable counterpart
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
     * Converts a immutable object to it's mutable bo counterpart
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

        return bo
    }

    void refresh() { }
}
