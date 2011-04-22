package org.kuali.rice.core.util.xml;

public class XmlException extends RuntimeException {
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
