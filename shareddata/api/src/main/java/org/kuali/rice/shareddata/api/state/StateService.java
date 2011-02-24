package org.kuali.rice.shareddata.api.state;

import java.util.List;

/**
 * Service for interacting with {@link State States}.
 */
public interface StateService {

    /**
     * Gets a {@link State} from a postal country code and postal state code.
     *
     * <p>
     *   This method will return null if the state does not exist.
     * </p>
     *
     * @param postalCountryCode country code. cannot be blank.
     * @param postalStateCode state code. cannot be blank.
     * @return a {@link State} or null
     * @throws IllegalArgumentException country code or state code is blank
     */
    State getState(String postalCountryCode, String postalStateCode);

    /**
     * Gets all the {@link State States} for postal country code.
     *
     * <p>
     *   This method will always return an <b>immutable</b> Collection
     *   even when no values exist.
     * </p>
     *
     * @param postalCountryCode country code. cannot be blank.
     * @param postalCountryCode state code. cannot be blank.
     * @return an immutable collection of states
     * @throws IllegalArgumentException country code is blank
     */
    List<State> getAllStates(String postalCountryCode);
}
