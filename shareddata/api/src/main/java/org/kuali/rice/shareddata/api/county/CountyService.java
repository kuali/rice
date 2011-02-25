package org.kuali.rice.shareddata.api.county;

import java.util.List;

public interface CountyService {

    /**
     * Gets a {@link County} from a postal country code and postal code value.
     *
     * <p>
     *   This method will return null if the state does not exist.
     * </p>
     *
     * <p>
     *     This method will return active or inactive counties.
     * </p>
     *
     * @param countryCode country code. cannot be blank.
     * @param stateCode postal state code. cannot be blank.
     * @param code county code. cannot be blank
     * @return a {@link County} or null
     * @throws IllegalArgumentException country code, postal state code, or county code is blank
     */
    County getCounty(String countryCode, String stateCode, String code);

    /**
     * Gets all the {@link County County} for postal country code & postal state code.
     *
     * <p>
     *   This method will always return an <b>immutable</b> Collection
     *   even when no values exist.
     * </p>
     *
     * <p>
     *     This method will only return active counties.
     * </p>
     *
     * @param countryCode state code. cannot be blank.
     * @param stateCode postal state code. cannot be blank.
     * @return an immutable collection of states
     * @throws IllegalArgumentException country code, postal state code is blank
     */
    List<County> getAllPostalCodes(String countryCode, String stateCode);
}
