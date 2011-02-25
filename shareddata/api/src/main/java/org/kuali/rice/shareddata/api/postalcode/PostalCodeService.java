package org.kuali.rice.shareddata.api.postalcode;

import java.util.List;

/**
 * Service for interacting with {@link PostalCode PostalCodes}.
 */
public interface PostalCodeService {

    /**
     * Gets a {@link PostalCode} from a postal country code and postal code value.
     *
     * <p>
     *   This method will return null if the state does not exist.
     * </p>
     *
     * <p>
     *     This method will return active or inactive postal codes.
     * </p>
     *
     * @param countryCode country code. cannot be blank.
     * @param code postal code value. cannot be blank.
     * @return a {@link PostalCode} or null
     * @throws IllegalArgumentException country code or postal code value is blank
     */
    PostalCode getPostalCode(String countryCode, String code);

    /**
     * Gets all the {@link PostalCode PostalCode} for postal country code.
     *
     * <p>
     *   This method will always return an <b>immutable</b> Collection
     *   even when no values exist.
     * </p>
     *
     *  <p>
     *     This method will only return active postal codes.
     * </p>
     *
     * @param countryCode state code. cannot be blank.
     * @return an immutable collection of states
     * @throws IllegalArgumentException country code is blank
     */
    List<PostalCode> getAllPostalCodes(String countryCode);
}
