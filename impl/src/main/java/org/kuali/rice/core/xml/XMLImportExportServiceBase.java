package org.kuali.rice.core.xml;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;

/**
 * Base implementation of an XMLImportExportService.  Will be extracted into
 * an interface when completed.
 */
public class XMLImportExportServiceBase {
    private static final Logger LOG = Logger.getLogger(XMLImportExportServiceBase.class);
            
    private enum State { INIT, LINKING }
    
    protected List<XMLInputFilterEntry> filters;
    
    public List<XMLInputFilterEntry> getFilters() {
        return filters;
    }

    public void setFilters(List<XMLInputFilterEntry> filters) {
        this.filters = filters;
    }

    public <T extends Class> T unmarshal(T clazz, Unmarshaller unmarshaller, InputStream in) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        InitialXMLFilter filter = new InitialXMLFilter();
        filter.setXMLImportExportService(this);
        filter.setParent(spf.newSAXParser().getXMLReader());

        UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
        filter.setContentHandler(handler);

        filter.parse(new InputSource(in));

        return (T)handler.getResult();
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
                else if ( state == State.LINKING && entry.getFilterClass() != null ) {
                    try {
                        ChainedXMLFilter filter = entry.getFilterClass().newInstance();
                        if ( prevFilter != null )
                            prevFilter.setParent(filter);
                        if ( result == null )
                            result = filter;
                        prevFilter = filter;
                    }
                    catch ( Exception e ) {
                        throw new RuntimeException("Could not instantiate ChainedXMLFilter", e);
                    }
                }
            }
        }
        return result != null ? result : new PassthruXMLFilter();
    }
}
