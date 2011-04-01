package org.kuali.rice.core.api.parameter;

import org.kuali.rice.core.api.mo.GloballyUnique;
import org.kuali.rice.core.api.mo.Versioned;

/**
 * Defines the contract for a parameter type.  The parameter type is largely indicative of the way in which the
 * parameter will be used by clients.  Examples might include a parameter which is used for configuration purposes
 * or one which is used to define parameters used during validation. 
 */
public interface ParameterTypeContract extends Versioned, GloballyUnique {
    
	/**
     * This is the code value for the ParameterType.  It cannot be null or a blank string.
     * 
     * @return the code for the ParameterType, will never be null or blank
     */
    String getCode();

    /**
     * This the name for the ParameterType.  This can be null or a blank string.
     *
     * @return the name of the ParameterType
     */
    String getName();

   /**
     * This the active flag for the ParameterType.
     *
     * @return the active flag of the ParameterType
     */
    boolean isActive();

}
