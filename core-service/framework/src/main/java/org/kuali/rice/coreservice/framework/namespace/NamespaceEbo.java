package org.kuali.rice.coreservice.framework.namespace;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.coreservice.api.namespace.NamespaceContract;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * TODO: Likely should remove all methods from this interface after KULRICE-7170 is fixed
 */
public interface NamespaceEbo extends NamespaceContract,
		ExternalizableBusinessObject, MutableInactivatable {

    /**
     * This the id of the application which owns this Namespace.  If this namespace has no application owner,
     * then this method will return null.
     *
     * <p>
     * It is a way of assigning the Namespace to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application id
     */
    String getApplicationId();

    /**
     * This the name for the Namespace.  This can be null or a blank string.
     *
     * @return name
     */
    String getName();
	
	/**
	 * Returns the version number for this object.  In general, this value should only
	 * be null if the object has not yet been stored to a persistent data store.
	 * This version number is generally used for the purposes of optimistic locking.
	 * 
	 * @return the version number, or null if one has not been assigned yet
	 */
	Long getVersionNumber();
	
	/**
	 * Return the globally unique object id of this object.  In general, this value should only
	 * be null if the object has not yet been stored to a persistent data store.
	 * 
	 * @return the objectId of this object, or null if it has not been set yet
	 */
	String getObjectId();
	
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
    
    /**
	 * The code value for this object.  In general a code value cannot be null or a blank string.
	 *
	 * @return the code value for this object.
	 */
	String getCode();
}
