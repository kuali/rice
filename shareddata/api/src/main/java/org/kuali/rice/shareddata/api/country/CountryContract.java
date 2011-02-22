package org.kuali.rice.shareddata.api.country;

/**
 * Contract for a Country. Country is a basic abstraction over a Country, encapsulating its name, country code,
 * postal code, and if its restricted or not
 */
public interface CountryContract {

    /**
     * An abbreviated String representing the unique identifying code for a given country.  This code correlates
     * directly to the alpha-2 country codes from the ISO-3166-1-alpha-2 standard.
     * <p>This property is required to exist.</p>
     *
     * @return The country code for this Country.
     */
    String getPostalCountryCode();

    /**
     * An alternative country code to represent a country. This code correlats directly to the alpha-3 codes
     * from the ISO_3166-1-alpha-3 standard.
     * <p>This property is optional</p>
     *
     * @return The alternate country code if it exists.  null is returned if an alternate code does not exist.
     */
    String getAlternatePostalCountryCode();

    /**
     * A full, familiar, name of a country.
     * <p>This property is optional</p>
     *
     * @return The name of a country if it exists.  null is returned if a full name does not exist.
     */
    String getPostalCountryName();

    /**
     * Value representing whether or not a country is active or not.
     *
     * @return if a country is active
     *
     * @see org.kuali.rice.kns.bo.Inactivateable
     */
    boolean isActive();

    /**
     * Value representing whether a country is restricted.
     * <p>The meaning of restricted for a country varies depending upon the implementer - for instance if a country
     * may not be used in the address of a Vendor.</p>
     *
     * <p>The default value of this property is false.</p>
     * @return if a country is restricted.
     */
    boolean isPostalCountryRestricted();
}