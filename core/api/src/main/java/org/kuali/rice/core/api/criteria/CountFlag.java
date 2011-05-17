package org.kuali.rice.core.api.criteria;

import org.kuali.rice.core.util.jaxb.EnumStringAdapter;

/**
 * Defines possible directives for how a query is requested to produce count values in it's results.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public enum CountFlag {

	/**
	 * Indicates that no row count should be returned with the query results.
	 */
	NONE,
	
	/**
	 * Indicates that the row count of the query should be returned with the query results.
	 */
	INCLUDE,
	
	/**
	 * Indicates that *only* the row count should be returned with the query results.  The
	 * result should not include the actual rows returned from the query.
	 */
	ONLY;

	/**
	 * Returns the value of the count flag.
	 * 
	 * @return the flag
	 */
	public String getFlag() {
		return toString();
	}
	
	static final class Adapter extends EnumStringAdapter<CountFlag> {
		
		protected Class<CountFlag> getEnumClass() {
			return CountFlag.class;
		}
		
	}
	
}
