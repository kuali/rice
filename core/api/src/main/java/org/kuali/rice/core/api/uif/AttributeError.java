package org.kuali.rice.core.api.uif;

import java.util.List;

/**
 * Defines a list of one or more errors for an attribute.
 */
public interface AttributeError {

    /**
     * The name of the attribute.  Will never be a blank or null string.
     * @return attribute name
     */
    String getAttributeName();

    /**
     * A list of errors associated with an attribute.  Will never return null or an empty list.
     *
     *
     * @return list of errors.
     */
    List<String> getErrors();

}
