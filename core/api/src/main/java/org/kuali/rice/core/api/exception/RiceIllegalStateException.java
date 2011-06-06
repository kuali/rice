package org.kuali.rice.core.api.exception;

import org.kuali.rice.core.api.CoreConstants;

import javax.xml.ws.WebFault;

/**
 * Subclass of IllegalStateException that has been annotated properly to be (un)marshalled as SOAP fault.
 * This class should be used in place of IllegalArgumentException for all services to be exposed remotely
 * within Rice.
 */
@WebFault(name = "IllegalStateFault", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SuppressWarnings("unused")
public class RiceIllegalStateException extends IllegalStateException {
    public RiceIllegalStateException() {
    }

    public RiceIllegalStateException(String s) {
        super(s);
    }

    public RiceIllegalStateException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RiceIllegalStateException(Throwable throwable) {
        super(throwable);
    }
}
