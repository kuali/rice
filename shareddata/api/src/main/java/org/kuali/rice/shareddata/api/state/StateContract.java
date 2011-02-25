package org.kuali.rice.shareddata.api.state;

/**
 * This is the contract for a State.
 */
public interface StateContract {

	/**
     * This the postal state code for the State.  This cannot be null or a blank string.
     *
     * @return postal state code
     */
    String getCode();

    /**
     * This the postal name for the State.  This cannot be null or a blank string.
     *
     * @return postal name
     */
    String getName();

    /**
     * This the postal country code for the State.  This cannot be null or a blank string.
     *
     * @return postal country code
     */
    String getCountryCode();

    /**
     * This the active flag for the State.
     *
     * @return the active flag of the State
     */
    boolean isActive();
}
