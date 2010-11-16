/*
 * Copyright 2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Acts as the first step in a Chain of XML Filters.  This node does the work
 * of identifying the Document's Schema URI and linking in the ChainedXMLFilter
 * instances that would be required to upgrade the input stream.  It is also
 * capable of reported a default namespace URI as well as reporting an
 * overridden XML Schema URI.
 */
public class InitialXMLFilter extends ChainedXMLFilterBase {
    private XMLImportExportServiceBase xmlImportExportService;
    private String originalSchemaUri = null;
    private String reportedSchemaUri = null;
    private String defaultNamespaceUri = null;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {
        // Set the default namespace URI if one is not set
        String defaultNamespaceUri = getDefaultNamespaceURI();
        if ( uri == null && defaultNamespaceUri != null )
            uri = defaultNamespaceUri;

        // Scan for the Original Schema
        if ( originalSchemaUri == null ) {
            int attrIndex = atts.getIndex("http://www.w3.org/2001/XMLSchema-instance",
                                          "noNamespaceSchemaLocation");
            if ( attrIndex != -1 ) {
                originalSchemaUri = atts.getValue(attrIndex);
                XMLImportExportServiceBase xmlService = getXMLImportExportService();
                ChainedXMLFilter startFilter = xmlService.getFilterForSchemaURI(originalSchemaUri);
                if ( startFilter != null ) {
                    // Insert this chain into the current one
                    XMLReader oldParent = getParent();
                    ChainedXMLFilter current = startFilter;
                    while ( current.getParent() != null ) {
                        current = (ChainedXMLFilter)current.getParent();
                    }
                    current.setParent(oldParent);
                    setParent(startFilter);
                }

                // Override the reported Schema URI
                String reportedSchemaUri = getReportedSchemaURI();
                if ( reportedSchemaUri != null ) {
                    atts = new AttributesImpl(atts);
                    ((AttributesImpl)atts).setValue(attrIndex, reportedSchemaUri);
                }
            }
        }
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // Set the default namespace URI if one is not set
        String defaultNamespaceUri = getDefaultNamespaceURI();
        if ( uri == null && defaultNamespaceUri != null )
            uri = defaultNamespaceUri;

        super.endElement(uri, localName, qName);
    }

    /**
     * Returns the XML Schema URI originally reported by the Document being
     * parsed.  This may be null if the URI has not yet been encountered.
     *
     * @return The Original Schema URI
     */
    public String getOriginalSchemaURI() {
        return originalSchemaUri;
    }

    /**
     * Returns the Schema URI that should be reported to the SAX Parser.
     *
     * @return The Reported Schema URI
     */
    public String getReportedSchemaURI() {
        return reportedSchemaUri;
    }

    /**
     * Sets the Schema URI that should be reported to the SAX Parser.
     *
     * @param reportedSchemaUri The Reported Schema URI
     */
    public void setReportedSchemaURI(String reportedSchemaUri) {
        this.reportedSchemaUri = reportedSchemaUri;
    }

    /**
     * Returns the Default Namespace URI that should be reported if an Element
     * is not already assigned to a namespace.
     *
     * @return The Default Namespace URI
     */
    public String getDefaultNamespaceURI() {
        return defaultNamespaceUri;
    }

    /**
     * Sets the Default Namespace URI that should be reported if an Element
     * is not already assigned to a namespace.
     *
     * @param defaultNamespaceUri The Default Namespace URI
     */
    public void setDefaultNamespaceURI(String defaultNamespaceUri) {
        this.defaultNamespaceUri = defaultNamespaceUri;
    }

    /**
     * Returns the XMLImportExportService used by this Filter
     *
     * @return The XMLImportExportService
     */
    public XMLImportExportServiceBase getXMLImportExportService() {
        return xmlImportExportService;
    }

    /**
     * Sets the XMLImportExportService used by this Filter
     *
     * @param xmlImportExportService The XMLImportExportService
     */
    public void setXMLImportExportService(XMLImportExportServiceBase xmlImportExportService) {
        this.xmlImportExportService = xmlImportExportService;
    }
}
