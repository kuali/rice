package org.kuali.rice.core.api.exception;

import org.kuali.rice.core.api.CoreConstants;

import javax.xml.ws.WebFault;

/**
 * Subclass of IllegalArgumentException that has been annotated properly to be (un)marshalled as SOAP fault.
 * This class should be used in place of IllegalArgumentException for all services to be exposed remotely
 * within Rice.
 */
@WebFault(name = "IllegalArgumentFault", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
public class RiceIllegalArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 3246611328810430837L;

    public RiceIllegalArgumentException() {
    }

    public RiceIllegalArgumentException(String s) {
        super(s);
    }

    public RiceIllegalArgumentException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RiceIllegalArgumentException(Throwable throwable) {
        super(throwable);
    }
}
