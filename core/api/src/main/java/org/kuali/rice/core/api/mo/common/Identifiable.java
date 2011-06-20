package org.kuali.rice.core.api.mo.common;

/**
 * This interface can be used to identify a model object which has a unique
 * identifier.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Identifiable {

    /**
     * The unique identifier for an object.  This can be null.
     *
     * @return the id
     */
    String getId();
}
