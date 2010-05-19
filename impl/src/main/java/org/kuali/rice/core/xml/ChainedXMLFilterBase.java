package org.kuali.rice.core.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;

/**
 * Basic implementation of ChainedXMLFilter.  This class will automatically
 * call the next() method for any non-implemented call.  Subclassers must be
 * careful to do the same for overridden methods or filter processing will
 * short-circuit.
 */
public class ChainedXMLFilterBase extends XMLFilterImpl implements ChainedXMLFilter {
    public void setParent(XMLReader parent) {
        super.setParent(parent);
        if ( parent != null ) {
            parent.setEntityResolver(this);
            parent.setDTDHandler(this);
            parent.setContentHandler(this);
            parent.setErrorHandler(this);
        }
    }
}
