package org.kuali.rice.shareddata.api.campus;


//import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public interface CampusContract {
	/**
	 * This is the campus code for the Campus.  This is cannot be a null or a blank string.
	 *
	 * <p>
	 * It is a unique abreviation of a campus.
	 * </p>
	 * @return code for Campus.  Will never be null or an empty string.
	 */
	public String getCode();

	/**
	 * This is the name for the Campus. 
	 *
	 * <p>
	 * It is a name a campus.
	 * </p>
	 * @return name for Campus.
	 */
	public String getName();

	/**
	 * This is the short name for the Campus. 
	 *
	 * <p>
	 * It is a shorter name for a campus.
	 * </p>
	 * @return short name for Campus.
	 */
	public String getShortName();

	/**
	 * This is the campus type for the Campus. 
	 *
	 * <p>
	 * It is a object that defines the type of a campus.
	 * </p>
	 * @return short name for Campus.
	 */
	public CampusTypeContract getCampusType();

	/**
	 * @return the active
	 */
	public boolean isActive();

}
