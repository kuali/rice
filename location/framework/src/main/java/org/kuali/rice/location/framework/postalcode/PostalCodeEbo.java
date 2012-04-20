package org.kuali.rice.location.framework.postalcode;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.location.api.postalcode.PostalCodeContract;

/**
 * TODO: Likely should remove all methods from this interface after KULRICE-7170 is fixed
 */
public interface PostalCodeEbo extends PostalCodeContract, ExternalizableBusinessObject, MutableInactivatable {

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
     * The code value for this object.  In general a code value cannot be null or a blank string.
     *
     * @return the code value for this object.
     */
    String getCode();
    
    /**
     * Returns the version number for this object.  In general, this value should only
     * be null if the object has not yet been stored to a persistent data store.
     * This version number is generally used for the purposes of optimistic locking.
     * 
     * @return the version number, or null if one has not been assigned yet
     */
    Long getVersionNumber();
    
    
    /**
     * The active indicator for an object.
     *
     * @return true if active false if not.
     */
    boolean isActive();
    
    /**
     * Sets the record to active or inactive.
     */
    void setActive(boolean active);
}
