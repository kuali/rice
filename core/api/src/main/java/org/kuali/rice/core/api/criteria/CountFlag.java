package org.kuali.rice.core.api.criteria;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines possible directives for how a query is requested to produce count values in it's results.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@XmlType(name = "CountFlagType")
@XmlEnum(String.class)
public enum CountFlag {

	/**
	 * Indicates that no row count should be returned with the query results.
	 */
	@XmlEnumValue(value="NONE") NONE("NONE"),
	
	/**
	 * Indicates that the row count of the query should be returned with the query results.
	 */
	@XmlEnumValue(value="INCLUDE") INCLUDE("INCLUDE"),
	
	/**
	 * Indicates that *only* the row count should be returned with the query results.  The
	 * result should not include the actual rows returned from the query.
	 */
	@XmlEnumValue(value="ONLY") ONLY("ONLY");
	
	private final String flag;
	
	private CountFlag(final String flag) {
		this.flag = flag;
	}
	
	/**
	 * Returns the value of the count flag.
	 * 
	 * @return the flag
	 */
	public String getFlag() {
		return flag;
	}
	
}
