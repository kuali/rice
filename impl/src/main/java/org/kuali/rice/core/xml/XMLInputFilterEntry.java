package org.kuali.rice.core.xml;

import org.xml.sax.XMLFilter;

/**
 * Represents a Map between a Schema URI and the ChainedXMLFilter
 * implementation required to migrate a Document from the Previous version.
 */
public class XMLInputFilterEntry {
    protected String schemaURI;
    protected Class<? extends ChainedXMLFilter> filterClass;

    public String getSchemaURI() {
        return schemaURI;
    }

    public void setSchemaURI(String schemaURI) {
        this.schemaURI = schemaURI;
    }

    public Class<? extends ChainedXMLFilter> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends ChainedXMLFilter> filterClass) {
        this.filterClass = filterClass;
    }
}
