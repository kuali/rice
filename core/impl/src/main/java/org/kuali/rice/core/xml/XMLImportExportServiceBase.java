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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of an XMLImportExportService.  Will be extracted into
 * an interface when completed.
 */
public class XMLImportExportServiceBase {
    private static final Logger LOG = Logger.getLogger(XMLImportExportServiceBase.class);
            
    private enum State { INIT, LINKING }
    
    private List<XMLInputFilterDefinition> definitions;
    
    // TODO: This method is going to have to be changed to support creating
    //       multiple instances of the given Class.  It should also not
    //       return an instance, because the import will yield multiple
    //       instances that need to be saved to the data store.
    public <T extends Class> T unmarshal(T clazz, Unmarshaller unmarshaller, InputStream in) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        InitialXMLFilter filter = new InitialXMLFilter();
        filter.setXMLImportExportService(this);

        // Set the InitialXMLFilter's reported Schema URI
        List<XMLInputFilterDefinition> definitions = getDefinitions();
        if ( filter != null && definitions.size() > 0 ) {
            XMLInputFilterDefinition definition = definitions.get(definitions.size()-1);
            filter.setReportedSchemaURI(definition.getSchemaURI());
        }

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
        List<XMLInputFilterDefinition> definitions = getDefinitions();
        if ( definitions == null )
            return new PassthruXMLFilter();
        
        ChainedXMLFilter result = null, prevFilter = null;
        State state = State.INIT;
        for ( XMLInputFilterDefinition definition : definitions ) {
            if ( state == State.INIT && definition.getSchemaURI().equals(schemaURI) ) {
                // We actually want to skip this entry and start linking
                // at the next one, as we don't need to convert to self.
                state = State.LINKING;
            }
            else if ( state == State.LINKING && definition.getEntries() != null ) {
                for ( XMLInputFilterEntry entry : definition.getEntries() ) {
                    if ( entry.getFilterClass() == null )
                        continue;
                    try {
                        ChainedXMLFilter filter = entry.getFilterClass().newInstance();
                        configureFilter(entry, filter);
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

    protected void configureFilter(XMLInputFilterEntry entry,
                                   ChainedXMLFilter filter) {
        Map<String, Object> properties = entry.getProperties();
        if ( properties != null ) {
            try {
                for ( Map.Entry<String, Object> property : properties.entrySet() ) {
                    PropertyUtils.setProperty(filter, property.getKey(),
                                              property.getValue());
                }
            }
            catch ( Exception e ) {
                throw new RuntimeException("Cannot configure Filter", e);
            }
        }
    }

    /**
     * Returns the XML Filter Definitions for this Import/Export Service
     *
     * @return The Service's XMLInputFilterDefinition List
     */
    public List<XMLInputFilterDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * Set the XML Filter Definitions for this Import/Export Service
     *
     * @param definitions The Service's XMLInputFilterDefinition List
     */
    public void setDefinitions(List<XMLInputFilterDefinition> definitions) {
        this.definitions = definitions;
    }
}
