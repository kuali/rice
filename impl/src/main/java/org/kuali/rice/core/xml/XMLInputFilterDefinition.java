package org.kuali.rice.core.xml;

import java.util.List;

/**
 * Represents a Map between a Schema URI and a set of ChainedXMLFilter
 * implementations required to migrate a Document from the Previous version.
 */
public class XMLInputFilterDefinition {
    private String schemaURI;
    private List<XMLInputFilterEntry> entries;

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
     * Returns the XMLInputFieldEntry list for XMLInputFilterDefinition
     *
     * @return the XMLInputFieldEntry list
     */
    public List<XMLInputFilterEntry> getEntries() {
        return entries;
    }

    /**
     * Sets the XMLInputFieldEntry list for XMLInputFilterDefinition
     *
     * @param entries the XMLInputFieldEntry list
     */    
    public void setEntries(List<XMLInputFilterEntry> entries) {
        this.entries = entries;
    }
}
