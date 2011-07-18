package org.kuali.rice.core.api.util.xml;

import org.kuali.rice.core.api.CoreConstants;

import javax.xml.ws.WebFault;

@WebFault(name = "XmlFault", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
public class XmlException extends RuntimeException {
    
    private static final long serialVersionUID = 5859837720372502809L;

    public XmlException(String message) {
        super(message);
    }

    public XmlException(Throwable t) {
        super(t);
    }

    public XmlException(String message, Throwable t) {
        super(message, t);
    }
}
