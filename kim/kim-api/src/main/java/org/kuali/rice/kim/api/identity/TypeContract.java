package org.kuali.rice.kim.api.identity;


import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.ImmutableInactivatable;

public interface TypeContract extends Versioned, GloballyUnique, ImmutableInactivatable, Coded {

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
