package org.kuali.rice.shareddata.api.postalcode;

/**
 * This is the contract for a Postal Code.  A postal code is assigned to different geographic regions
 * in order to give each region an identifier.
 *
 * Examples of postal codes are Zip Codes in the United States and FSALDU in Canada.
 */
public interface PostalCodeContract {
    /**
     * This the county code for the PostalCode.  This cannot be null or a blank string.
     *
     * @return code
     */
    String getCode();

    /**
     * This the postal country code for the PostalCode.  This cannot be null or a blank string.
     *
     * @return postal country code
     */
    String getCountryCode();

    /**
     * This the postal state code for the PostalCode.  This can be null.
     *
     * @return postal state code
     */
    String getStateCode();

    /**
     * This the postal state code for the PostalCode.  This can be null.
     *
     * @return postal state code
     */
    String getCityName();

    /**
     * This the county code for the PostalCode.  This cannot be null.
     *
     * @return postal state code
     */
    String getCountyCode();

    /**
     * This the active flag for the PostalCode.
     *
     * @return the active flag of the PostalCode
     */
    boolean isActive();
}
