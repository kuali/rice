package org.kuali.rice.core.xml;

import java.util.Map;

/**
 * Represents a Map between a Schema URI and the ChainedXMLFilter
 * implementation required to migrate a Document from the Previous version.
 */
public class XMLInputFilterEntry {
    private String schemaURI;
    private Class<? extends ChainedXMLFilter> filterClass;
    private Map<String, Object> properties;

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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
