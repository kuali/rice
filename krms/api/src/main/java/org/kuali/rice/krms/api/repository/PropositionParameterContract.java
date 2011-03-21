package org.kuali.rice.krms.api.repository;


public interface PropositionParameterContract {
	/**
	 * This is the Id for the proposition parameter
	 *
	 * <p>
	 * It is the Id of a proposition parameter.
	 * </p>
	 * @return Id for proposition parameter.
	 */
	public String getId();

	/**
	 * This is the Id for the parent proposition.
	 *
	 * <p>
	 * It is the Id of the parent proposition.
	 * </p>
	 * @return Id for parent parameter.
	 */
	public String getPropId();

	
	/**
	 * This is the value of the proposition parameter
	 *
	 * <p>
	 * It is the value of the parameter
	 * </p>
	 * @return value of the parameter
	 */
	public String getValue();

	/**
	 * This is the type of the parameter.
	 * Proposition parameters are one of the following types:
	 *    Constant Values:  numbers, strings, dates, etc.
	 *    Terms: data available in the execution environment or provided by an asset resolver
	 *    Functions: custom functions that resolve to a value, 
	 *    	or standard operators (equals, greater than, less than, ...)
	 *
	 * <p>
	 * It identified the type of the parameter.
	 * </p>
	 * @return the parameter type code. Valid values are C, T, and F.
	 */
	public String getParameterType();

	/**
	 * This is the sequence number of the proposition parameter.
	 * Proposition parameters are listed in Reverse Polish Notation.
	 * The sequence number (starting with 1) identifies the position of the 
	 * parameter in the list.
	 * 
	 * @return the sequence number of the proposition parameter
	 */
	public Integer getSequenceNumber();
}
