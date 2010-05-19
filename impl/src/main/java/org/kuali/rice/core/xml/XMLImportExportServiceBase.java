package org.kuali.rice.core.xml;

import org.apache.log4j.Logger;
import org.xml.sax.XMLFilter;

import java.util.List;

/**
 * Base implementation of an XMLImportExportService.  Will be extracted into
 * an interface when completed.
 */
public class XMLImportExportServiceBase {
    private static final Logger LOG = Logger.getLogger(XMLImportExportServiceBase.class);
            
    private enum State {
        INIT,
        LINKING
    }
    
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
    protected ChainedXMLFilter getFilterForSchemaURI(String schemaURI) {
        ChainedXMLFilter result = null, prevFilter = null;
        List<XMLInputFilterEntry> filters = getFilters();
        if ( filters != null ) {
            State state = State.INIT;            
            for ( XMLInputFilterEntry entry : filters ) {
                if ( state == State.INIT && entry.getSchemaURI().equals(schemaURI) ) {
                    // We actually want to skip this entry and start linking
                    // at the next one, as we don't need to convert to self.
                    state = State.LINKING;
                }
                else if ( state == State.LINKING ) {
                    ChainedXMLFilter filter = null;
                    if ( entry.getFilterClass() != null ) {
                        try {
                            filter = entry.getFilterClass().newInstance();
                        }
                        catch ( Exception e ) {
                            LOG.error("Could not instantiate ChainedXMLFilter", e);
                        }
                    }
                    if ( filter != null ) {
                        if ( prevFilter != null )
                            prevFilter.setNextFilter(filter);
                        if ( result == null )
                            result = filter;
                        prevFilter = filter;
                    }
                }
            }
        }
        if ( result != null )
            prevFilter.setNextFilter(new TerminalXMLFilter());
        else
            result = new TerminalXMLFilter();
        return result;
    }
}
