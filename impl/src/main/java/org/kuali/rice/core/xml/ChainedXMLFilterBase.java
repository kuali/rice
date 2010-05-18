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
public abstract class ChainedXMLFilterBase extends XMLFilterImpl
        implements ChainedXMLFilter {
    protected ChainedXMLFilter nextFilter;

    public void setNextFilter(ChainedXMLFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

    public ChainedXMLFilter getNextFilter() {
        return nextFilter;
    }

    public ChainedXMLFilter next() {
        return getNextFilter();
    }

    @Override
    public void setParent(XMLReader xmlReader) {
        super.setParent(xmlReader);
        next().setParent(xmlReader);
    }

    @Override
    public void setFeature(String s, boolean b) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        super.setFeature(s, b);
        next().setFeature(s, b);
    }

    @Override
    public void setProperty(String s, Object o) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        super.setProperty(s, o);
        next().setProperty(s, o);
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        super.setEntityResolver(entityResolver);
        next().setEntityResolver(entityResolver);
    }

    @Override
    public void setDTDHandler(DTDHandler dtdHandler) {
        super.setDTDHandler(dtdHandler);
        next().setDTDHandler(dtdHandler);
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        super.setContentHandler(contentHandler);
        next().setContentHandler(contentHandler);
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        super.setErrorHandler(errorHandler);
        next().setErrorHandler(errorHandler);        
    }

    @Override
    public void notationDecl(String s, String s1, String s2) throws SAXException {
        next().notationDecl(s, s1, s2);
    }

    @Override
    public void unparsedEntityDecl(String s, String s1, String s2, String s3)
            throws SAXException {
        next().unparsedEntityDecl(s, s1, s2, s3);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        next().setDocumentLocator(locator);        
    }

    @Override
    public void startDocument() throws SAXException {
        next().startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        next().endDocument();
    }

    @Override
    public void startPrefixMapping(String s, String s1) throws SAXException {
        next().startPrefixMapping(s, s1);
    }

    @Override
    public void endPrefixMapping(String s) throws SAXException {
        next().endPrefixMapping(s);
    }

    @Override
    public void startElement(String s, String s1, String s2, Attributes attributes)
            throws SAXException {
        next().startElement(s, s1, s2, attributes);
    }

    @Override
    public void endElement(String s, String s1, String s2) throws SAXException {
        next().endElement(s, s1, s2);
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        next().characters(chars, i, i1);
    }

    @Override
    public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {
        next().ignorableWhitespace(chars, i, i1);
    }

    @Override
    public void processingInstruction(String s, String s1) throws SAXException {
        next().processingInstruction(s, s1);
    }

    @Override
    public void skippedEntity(String s) throws SAXException {
        next().skippedEntity(s);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        next().warning(e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        next().error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        next().fatalError(e);
    }
}
