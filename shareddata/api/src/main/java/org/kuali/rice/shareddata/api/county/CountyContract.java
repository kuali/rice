package org.kuali.rice.shareddata.api.county;

/**
 * This is the contract for a State.
 */
public interface CountyContract {

    /**
     * This the county code for the County.  This cannot be null or a blank string.
     *
     * @return code
     */
    String getCode();

    /**
     * This the postal country code for the County.  This cannot be null or a blank string.
     *
     * @return postal country code
     */
    String getCountryCode();

    /**
     * This the postal state code for the County.  This cannot be null or a blank string.
     *
     * @return postal state code
     */
    String getStateCode();

    /**
     * This the name for the County.  This cannot be null or a blank string.
     *
     * @return name
     */
    String getName();

    /**
     * This the active flag for the County.
     *
     * @return the active flag of the County
     */
    boolean isActive();
}
