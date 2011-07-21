package org.kuali.rice.core.api.uif;

/**
 * A Control with a size.
 */
public interface Sized {

    /**
     * The size value to make the control.  This field can be null. Cannot be less than 1.
     *
     * @return the size value or null.
     */
    Integer getSize();
}
