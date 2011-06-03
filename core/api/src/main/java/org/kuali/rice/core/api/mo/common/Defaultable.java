package org.kuali.rice.core.api.mo.common;


/**
 * Represents an object which has a value that designates the object as the default object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface Defaultable {
    /**
	 * The default value for this object.
	 *
	 * @return the default value for this object
	 */
    boolean isDefaultValue();
}
