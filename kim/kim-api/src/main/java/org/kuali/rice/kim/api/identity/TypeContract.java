package org.kuali.rice.kim.api.identity;


import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface TypeContract extends Versioned, GloballyUnique, Inactivatable {

    /**
     * This is the code value for the AddressType.  It cannot be null or a blank string.
     *
     * @return the code for the AddressType, will never be null or blank
     */
    String getCode();

    /**
     * This the name for the AddressType.  This can be null or a blank string.
     *
     * @return the name of the AddressType
     */
	String getName();

    /**
     * This the sort code for the AddressType.  This can be null or a blank string.
     *
     * @return the sort code of the AddressType
     */
    String getSortCode();
}
