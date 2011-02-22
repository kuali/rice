package org.kuali.rice.shareddata.api.campus;

public interface CampusTypeContract {
	
	/**
	 * This is the campus type code for the CampusType.  This is cannot be a null or a blank string.
	 *
	 * <p>
	 * It is a unique abreviation of a campus type.
	 * </p>
	 * @return code for CampusType.  Will never be null or an empty string.
	 */
	public String getCode();


	/**
	 * This is the name for the CampusType. 
	 *
	 * <p>
	 * It is a name a campus type.
	 * </p>
	 * @return name for CampusType.
	 */
	public String getName();


	/**
	 * This is the active flag for the CampusType. 
	 *
	 * <p>
	 * It is a flag that determines if a campus type is active or not.
	 * </p>
	 * @return active boolean for CampusType.
	 */
	public boolean isActive();

}
