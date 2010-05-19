package org.kuali.rice.core.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Acts as the first step in a Chain of XML Filters.  This node does the work
 * of identifying the Document's Schema URI and linking in the ChainedXMLFilter
 * instances that would be required to upgrade the input stream. 
 */
public class InitialXMLFilter extends ChainedXMLFilterBase {
    private XMLImportExportServiceBase xmlService;
    private String schemaUri = null;

    public InitialXMLFilter() {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {
        if ( schemaUri == null ) {
            schemaUri = atts.getValue("http://www.w3.org/2001/XMLSchema-instance",
                                      "noNamespaceSchemaLocation");
            if ( schemaUri != null ) {
                XMLImportExportServiceBase xmlService = getXMLImportExportService();
                ChainedXMLFilter startFilter = xmlService.getFilterForSchemaURI(schemaUri);
                if ( startFilter != null ) {
                    XMLReader oldParent = getParent();
                    ChainedXMLFilter current = startFilter;
                    while ( current.getParent() != null ) {
                        current = (ChainedXMLFilter)current.getParent();
                    }
                    if ( oldParent != null )
                        current.setParent(oldParent);
                    setParent(startFilter);
                }
            }
        }
        super.startElement(uri, localName, qName, atts);
    }

    public String getSchemaURI() {
        return schemaUri;
    }
    
    public XMLImportExportServiceBase getXMLImportExportService() {
        return xmlService;
    }

    public void setXMLImportExportService(XMLImportExportServiceBase xmlService) {
        this.xmlService = xmlService;
    }
}
