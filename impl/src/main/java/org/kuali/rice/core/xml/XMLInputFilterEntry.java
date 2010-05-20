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

    /**
     * Returns the Schema URI associated with this Entry
     *
     * @return The Schema URI
     */
    public String getSchemaURI() {
        return schemaURI;
    }

    /**
     * Set the Schema URI associated with this Entry
     *
     * @param schemaUri The Schema URI
     */
    public void setSchemaURI(String schemaURI) {
        this.schemaURI = schemaURI;
    }

    /**
     * Returns the Class that will be used to upgrade a Document's content
     * from the previous version to this one.
     *
     * @return The Filter Class
     */
    public Class<? extends ChainedXMLFilter> getFilterClass() {
        return filterClass;
    }

    /**
     * Sets the Class that will be used to upgrade a Document's content
     * from the previous version to this one.
     *
     * @param filterClass The Filter Class
     */    
    public void setFilterClass(Class<? extends ChainedXMLFilter> filterClass) {
        this.filterClass = filterClass;
    }

    /**
     * Returns a Map containing the property settings that will be used to
     * configure the filterClass after it has been instantiated.
     *
     * @return The Filter Properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Sets a Map containing the property settings that will be used to
     * configure the filterClass after it has been instantiated.
     *
     * @param properties The Filter Properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
