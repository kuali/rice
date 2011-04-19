package org.kuali.rice.krms.api.repository.term;

import org.kuali.rice.krms.api.repository.BaseAttributeContract;


public interface TermResolverAttributeContract extends BaseAttributeContract {
	
	/**
	 * This is the id of the TermResolver to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a TermResolver related to the attribute.
	 * </p>
	 * @return id for TermResolver related to the attribute.
	 */
	public String getTermResolverId();

}
