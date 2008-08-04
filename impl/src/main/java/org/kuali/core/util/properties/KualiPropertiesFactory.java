/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.exceptions.PropertiesException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class assembles a PropertyHolder containing all Properties from a set of PropertySources listed in an XML configuration
 * file.
 * 
 * 
 */
public class KualiPropertiesFactory {
    // config-file tags
    private static final String CONFIG_ROOT = "configuration/";
    private static final String CONFIG_PROPERTIES = CONFIG_ROOT + "properties";

    // config-file tag attribute
    private static final String CONFIG_PROPERTIES_ATTR_FILENAME = "fileName";
    private static final String CONFIG_PROPERTIES_ATTR_OVVERRIDE = "allowOverrides";
    

    private static Log log = LogFactory.getLog(KualiPropertiesFactory.class);


    private String configurationFileName;


    /**
     * Constructor with ConfigurationFile Name passed
     * 
     * @param configurationFileName The path to the configuration file
     * @throws IllegalArgumentException if the configurationFileName is blank
     */
    public KualiPropertiesFactory(String configurationFileName) {
        this.configurationFileName = configurationFileName;
    }

    /**
     * Load the XML configuration file, processes all of the (known) PropertySources declared in that file, and returns a
     * PropertyHolder containing all of the properties from all of those sources.
     * 
     * @param startingProperties a PropertyHolder containing predefined properties, which will be used as the starting point for the
     *        returned PropertyHolder; may be null
     * @return a PropertyHolder containing all properties from all sources listed in the config file
     * @throws DuplicateKeyException if any source defines a key which has already been defined by an earlier source
     * @throws PropertiesException if the config file can't be loaded, or if a PropertySource can't load its properties
     */
    public PropertyHolder getProperties(PropertyHolder startingProperties) {
    	
        // open stream to configFile
        InputStream input = null;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(getConfigurationFileName());
            if (url != null) {
                input = url.openStream();
            }
        }
        catch (IOException e) {
            throw new PropertiesException("exception caught opening configFile '" + getConfigurationFileName() + "'", e);
        }
        if (input != null) {
	
	        // create and init digester
	        PropertyHolderBuilder builder = new PropertyHolderBuilder(startingProperties);
	        Digester digester = buildDigester(builder);
	
	        // populate the PropertyHolderBuilder with all sources listed in the
	        // config file
	        try {
	            digester.parse(input);
	            input.close();
	        }
	        catch (SAXException saxe) {
	            log.error("SAX Exception caught", saxe);
	            throw new PropertiesException("SAX Exception caught", saxe);
	        }
	        catch (IOException ioe) {
	            log.error("IO Exception caught", ioe);
	            throw new PropertiesException("IO Exception caught", ioe);
	        }
	
	        // create and return the merged PropertyHolder
	        return builder.mergeProperties();
        }
        else {
        	return new PropertyHolder();
        }
    }

    private Digester buildDigester(Object rootObject) {
        Digester digester = new Digester();

        digester.setNamespaceAware(false);
        digester.setValidating(false);
        digester.setUseContextClassLoader(true);

        // set parsing rules
        setupDigesterInstance(digester, CONFIG_PROPERTIES, new FilePropertySourceFactory(FilePropertySource.class));

        digester.push(rootObject);

        return digester;
    }

    /**
     * @return name of the configurationFile
     */
    public String getConfigurationFileName() {
        return configurationFileName;
    }


    /**
     * Sets up digester rules used to process the config file. Should be called once for each distinct PropertySource tag/type.
     * 
     * @param digester the current digester
     * @param matchString the pattern to match with this rule
     * @param factory an ObjectCreationFactory instance to use for creating new objects
     */
    protected void setupDigesterInstance(Digester digester, String matchString, ObjectCreationFactory factory) {
        digester.addFactoryCreate(matchString, factory);
        digester.addSetProperties(matchString);
        digester.addSetNext(matchString, "addPropertySource", PropertySource.class.getName());
    }


    /**
     * A tiny inner class that allows the digester to construct properly-initialized FilePropertySource objects. You'll need one of
     * these foreach different type of PropertySource.
     */
    public static class FilePropertySourceFactory extends AbstractObjectCreationFactory {
        private Class clazz;


        public FilePropertySourceFactory(Class clazz) {
            this.clazz = clazz;
        }

        public Object createObject(Attributes attributes) throws Exception {
            FilePropertySource source = (FilePropertySource) clazz.newInstance();
            source.setFileName(attributes.getValue(CONFIG_PROPERTIES_ATTR_FILENAME));
            source.setFileName(attributes.getValue(CONFIG_PROPERTIES_ATTR_OVVERRIDE));
            log.info("Created FilePropertySource '" + source.getFileName() + "'");

            return source;
        }
    }

    /**
     * An internally used helper class for accumulating PropertySources and merging their contents
     */
    public static class PropertyHolderBuilder {
        PropertyHolder startingProperties;
        private ArrayList sourceList;

        /**
         * Default constructor.
         */
        public PropertyHolderBuilder(PropertyHolder startingProperties) {
            this.sourceList = new ArrayList();
            this.startingProperties = startingProperties;
        }

        /**
         * Adds the given PropertySource to the list. Called by Digester.
         * 
         * @param source
         */
        public void addPropertySource(PropertySource source) {
            this.sourceList.add(source);
        }

        /**
         * Assembles and returns the complete PropertyHolder
         * 
         * @return PropertyHolder containing properties from the accumulated PropertySources
         */
        public PropertyHolder mergeProperties() {
            PropertyHolder mergedProperties = startingProperties;
            if (mergedProperties == null) {
                mergedProperties = new PropertyHolder();
            }

            for (Iterator i = this.sourceList.iterator(); i.hasNext();) {
                PropertySource source = (PropertySource) i.next();
                mergedProperties.loadProperties(source);
            }

            return mergedProperties;
        }
    }
}