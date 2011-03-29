package org.kuali.rice.kns.service;

import org.kuali.rice.core.api.exception.KualiException;

public class ModuleServiceNotFoundException extends KualiException {
    public ModuleServiceNotFoundException(String message) {
        super(message);
    }

    public ModuleServiceNotFoundException(String message, boolean hideIncidentReport) {
        super(message, hideIncidentReport);
    }

    public ModuleServiceNotFoundException(String message, Throwable t) {
        super(message, t);
    }

    public ModuleServiceNotFoundException(Throwable t) {
        super(t);
    }
}
