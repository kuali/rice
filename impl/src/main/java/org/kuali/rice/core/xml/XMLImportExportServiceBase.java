package org.kuali.rice.core.xml;

import org.xml.sax.XMLFilter;

import java.util.List;

/**
 * Base implementation of an XMLImportExportService.  Will be extracted into
 * an interface when completed.
 */
public class XMLImportExportServiceBase {
    protected List<XMLInputFilterEntry> filters;
    
    public List<XMLInputFilterEntry> getFilters() {
        return filters;
    }

    public void setFilters(List<XMLInputFilterEntry> filters) {
        this.filters = filters;
    }

    /**
     * Returns the linked set of ChainedXMLFilter instances required to
     * transform a document of the specified schema to current.
     *
     * @param schemaURI The Schema URI of the current Document
     * @return The starting ChainedXMLFilter for transforming the Document
     */
    protected XMLFilter getFilterForSchemaURI(String schemaURI) {
        return null; // TODO: This
    }
}
