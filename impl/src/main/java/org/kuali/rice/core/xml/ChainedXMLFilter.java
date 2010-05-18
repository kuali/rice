package org.kuali.rice.core.xml;

import org.xml.sax.XMLFilter;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;

/**
 * A ChainedXMLFilter extends a standard SAX XMLFilter to provide hooks for
 * chaining the processing of filtration.  After custom processing, the
 * methods of an implementing class should call the same method for the next()
 * link in the chain.
 */
public interface ChainedXMLFilter extends XMLFilter, EntityResolver,
        DTDHandler, ContentHandler, ErrorHandler {
    void setNextFilter(ChainedXMLFilter nextFilter);
    ChainedXMLFilter getNextFilter();
    ChainedXMLFilter next();
}
