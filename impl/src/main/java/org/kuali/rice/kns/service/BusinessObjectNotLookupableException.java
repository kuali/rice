package org.kuali.rice.kns.service;

import org.kuali.rice.core.KualiException;

/**
 * Thrown when a BusinessObject is expected to implement Lookupable, but does not.
 *
 * @see org.kuali.rice.kns.lookup.Lookupable
 */
public class BusinessObjectNotLookupableException extends KualiException {
    public BusinessObjectNotLookupableException(String message) {
        super(message);
    }

    public BusinessObjectNotLookupableException(String message, boolean hideIncidentReport) {
        super(message, hideIncidentReport);
    }

    public BusinessObjectNotLookupableException(String message, Throwable t) {
        super(message, t);
    }

    public BusinessObjectNotLookupableException(Throwable t) {
        super(t);
    }
}