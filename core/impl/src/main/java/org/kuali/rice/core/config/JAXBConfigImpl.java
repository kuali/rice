/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.core.config;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.xsd.Param;
import org.kuali.rice.core.util.ImmutableProperties;
import org.kuali.rice.core.util.RiceUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This implementation of the Config interface uses JAXB to parse the config file and
 * maintains an internal copy of all properties in their "raw" form (without any nested
 * properties resolved).  This allows properties to be added in stages and still alter
 * values of properties previously read in.
 * It also has settings for whether system properties should override all properties or
 * only serve as default when the property has not been defined.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class JAXBConfigImpl extends AbstractBaseConfig {

    private static final Logger LOG = Logger.getLogger(JAXBConfigImpl.class);

    private static final String IMPORT_NAME = "config.location";
    private static final String INDENT = "  ";
    private static final String PLACEHOLDER_REGEX = "\\$\\{([^{}]+)\\}";

    // keep the same random
    private static final Random RANDOM = new Random();

    private final List<String> fileLocs = new ArrayList<String>();

    private final Map<String, Object> objects = new LinkedHashMap<String, Object>();
    private final Properties rawProperties = new Properties();
    private final Properties resolvedProperties = new Properties();

    // compile pattern for regex once
    private final Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);

    private boolean systemOverride = false;
    
    public JAXBConfigImpl(){}
    
    public JAXBConfigImpl(org.kuali.rice.core.config.Config config) {
    	this.copyConfig(config);
    }
    
    public JAXBConfigImpl(String fileLoc, org.kuali.rice.core.config.Config config) {
    	this.copyConfig(config);
    	this.fileLocs.add(fileLoc);
    }
    
	public JAXBConfigImpl(List<String> fileLocs, org.kuali.rice.core.config.Config config) {
    	this.copyConfig(config);
    	this.fileLocs.addAll(fileLocs);
    	
    }
    
    public JAXBConfigImpl(String fileLoc) {
        this.fileLocs.add(fileLoc);
    }

    public JAXBConfigImpl(List<String> fileLocs) {
        this.fileLocs.addAll(fileLocs);
    }

    public JAXBConfigImpl(Properties properties) {    	   
    	this.putProperties(properties);
    }
    
    public JAXBConfigImpl(String fileLoc, Properties properties) {
        this.fileLocs.add(fileLoc);    
        this.putProperties(properties);
    }

    public JAXBConfigImpl(List<String> fileLocs, Properties properties) {
        this.fileLocs.addAll(fileLocs);        
        this.putProperties(properties);
    }
    
    /*****************************************************/

    /*
     * We need the ability to take a config object and copy the raw + cached data into
     * this config object. 
     */
    private void copyConfig(Config config){
    	if(config == null) {
			return;
		}
    	 		
    	this.putProperties(config.getProperties());
    		
    	if(config.getObjects() != null) {
			this.objects.putAll(config.getObjects());
		}
    }
    
    @Override
	public Object getObject(String key) {
        return objects.get(key);
    }

    @Override
	public Map<String, Object> getObjects() {
    	return Collections.unmodifiableMap(objects);
    }

    @Override
	public Properties getProperties() {
    	return new ImmutableProperties(resolvedProperties);
    }

    @Override
	public String getProperty(String key) {
        return resolvedProperties.getProperty(key);
    }
   
    /**
     * 
     * This overrided the property. Takes the place of the now depricated overrideProperty
     * 
     * @see org.kuali.rice.core.config.Config#putProperty(java.lang.String, java.lang.Object)
     */
	@Override
	public void putProperty(String key, String value) {
        this.setProperty(key, replaceVariable(key, value));
        resolveRawToCache();
	}

	@Override
	public void putProperties(Properties properties) {
        if (properties != null) {
            for(Object o : properties.keySet()) {
        	    this.setProperty((String)o, replaceVariable((String)o, properties.getProperty((String)o)));
            }
            
    	    resolveRawToCache();
        }
    }

    @Override
	public void parseConfig() throws IOException {

        if (fileLocs.size() > 0) {

            if (LOG.isInfoEnabled()) {
                LOG.info("Loading Rice configs: " + StringUtils.join(fileLocs, ", "));
            }

            JAXBContext jaxbContext;
            Unmarshaller unmarshaller;

            try {
                jaxbContext = JAXBContext.newInstance(org.kuali.rice.core.config.xsd.Config.class);
                unmarshaller = jaxbContext.createUnmarshaller();
            } catch (Exception ex) {
                throw new ConfigurationException("Error initializing JAXB for config", ex);
            }

            // add these first so they can be overridden
            configureBuiltIns();

            // parse all config files, but do not resolve any right hand side variables
            for (String s : fileLocs) {
                parseConfig(s, unmarshaller, 0);
            }

            // now that all properties have been loaded, resolve the right hand side from
            // the raw properties into the resolved properties.  This will also replace properties
            // defined in the files with system properties if systemOverride==true.
            resolveRawToCache();

            if (LOG.isInfoEnabled()) {
            	final StringBuilder log = new StringBuilder();
            	log.append("\n");
            	log.append("####################################\n");
            	log.append("#\n");
            	log.append("# Properties used after config override/replacement\n");
            	log.append("# " + StringUtils.join(fileLocs, ", ") + "\n");
            	log.append("#\n");
            	log.append("####################################\n");
                //commented out to backport to java 5
                //SortedSet<String> sorted = new TreeSet<String>(properties.stringPropertyNames());
                
                SortedSet<String> sorted = new TreeSet<String>();
                CollectionUtils.addAll(sorted, rawProperties.propertyNames());
                
                for (String s : sorted) {
                	log.append("Using config Prop " + s + "=[" + ConfigLogger.getDisplaySafeValue(s, this.getProperty(s)) + "]\n");
                }
                LOG.info(log);
            }

        } else {
            LOG.info("Loading Rice configs: No config files specified");
        }
    }

    protected void parseConfig(String filename, Unmarshaller unmarshaller, int depth) throws IOException {

        InputStream in = null;

        // have to check for empty filename because getResource will
        // return non-null if passed ""
        if (StringUtils.isNotEmpty(filename)) {
            in = RiceUtilities.getResourceAsStream(filename);
        }

        if (in == null) {
        	final StringBuilder log = new StringBuilder();
        	log.append("\n");
        	log.append("####################################\n");
        	log.append("#\n");
        	log.append("# Configuration file '" + filename + "' not found!\n");
        	log.append("#\n");
        	log.append("####################################\n");
        	LOG.warn(log);
        } else {

            final String prefix = StringUtils.repeat(INDENT, depth);            
            LOG.info(prefix + "+ Parsing config: " + filename);            
            org.kuali.rice.core.config.xsd.Config config;

            try {
                config = unmarshal(unmarshaller, in);
            } catch (Exception ex) {
                throw new ConfigurationException("Error parsing config file: " + filename, ex);
            }

            for (Param p : config.getParamList()) {

                String name = p.getName();
                                
                if (name.equals(IMPORT_NAME)) {
                    String configLocation = parseValue(p.getValue(), new HashSet<String>());
                    // Remove new lines and white space.
                    if(configLocation != null){
                    	configLocation = configLocation.trim();
                    }
                    parseConfig(configLocation, unmarshaller, depth + 1);
                } else if(p.isSystem()){
                	if (p.isOverride() || !(System.getProperty(name) != null)){
                		if(p.isRandom()){
                			String randStr = String.valueOf(generateRandomInteger(p.getValue()));
                			System.setProperty(name, randStr);
                            this.setProperty(p.getName(), randStr); 
                        	if(LOG.isInfoEnabled())
                        	{	
                        		LOG.info("generating random string " + randStr + " for system property " + p.getName());
                        	}    
                		}else{
                			// resolve and set system params immediately so they can override
                            // existing system params. Add to rawProperties resolved as well to
                            // prevent possible mismatch
                            HashSet<String> set = new HashSet<String>();
                            set.add(p.getName());
                            String value = parseValue(p.getValue(), set);
                            System.setProperty(name, value);
                            this.setProperty(name, value);
                		}
                	}
                }
                else if (p.isOverride() || !rawProperties.containsKey(name)) {

                	if (p.isRandom()) {
                    
                    	String randStr = String.valueOf(generateRandomInteger(p.getValue()));
                        this.setProperty(p.getName(), randStr); 
                    	if(LOG.isInfoEnabled())
                    	{	
                    		LOG.info("generating random string " + randStr + " for property " + p.getName());
                    	}
                    } else {
                    	
                    	/*
                    	 * myProp = dog
                    	 * We have a case where you might want myProp = ${myProp}:someOtherStuff:${foo}
                    	 * This would normally overwrite the existing myProp with ${myProp}:someOtherStuff:${foo}
                    	 * but what we want is:
                    	 * myProp = dog:someOtherStuff:${foo}
                    	 * so that we put the existing value of myProp into the new value. Basically how path works.
                    	 */
                    	String value = replaceVariable(name, p.getValue());                       
                    	
                    	this.setProperty(name, value);                    	
                    }
                }
            }

            LOG.info(prefix + "- Parsed config: " + filename);
        }
    }
    
    /*
     * This will set the property. No logic checking so what you pass in gets set.
     * We use this as a focal point for debugging the raw config changes.
     */
    protected void setProperty(String name, String value){
    	if(LOG.isInfoEnabled()){
    		String oldProp = rawProperties.getProperty(name);
    		if(oldProp != null && !oldProp.equals(value)){
    			LOG.info("Raw Config Override: " + name + "=[" + ConfigLogger.getDisplaySafeValue(name,oldProp) +"]->[" + ConfigLogger.getDisplaySafeValue(name,value) +"]");
    		}
    	}
    	rawProperties.setProperty(name, value);
    }    

    protected String resolve(String key) {
    	return resolve(key, null);
    }
    
    /**
     * This method will determine the value for a property by looking it up in the raw properties.  If the
     * property value contains a nested property (foo=${nested}) it will start the recursion by
     * calling parseValue().
     * It will also check for a system property of the same name and, based on the value of systemOverride,
     * 'override with' the system property or 'default to' the system property if not found in the raw properties.
     * This method only determines the resolved value, it does not modify the properties in the resolved or raw
     * properties objects.
     * 
     * @param key they key of the property for which to determine the value
     * @param keySet contains all keys used so far in this recursion.  used to check for circular references.
     * @return
     */
    protected String resolve(String key, Set keySet) {
    	
        // check if we have already resolved this key and have circular reference
        if (keySet != null && keySet.contains(key)) {
            throw new ConfigurationException("Circular reference in config: " + key);
        }
        
        String value = this.rawProperties.getProperty(key);
        
        if ((value == null || systemOverride) && System.getProperties().containsKey(key)) {
            value = System.getProperty(key);
        }
        
        if (value != null && value.contains("${")) {
        	if(keySet == null) {
        		keySet = new HashSet<String>();
        	}
            keySet.add(key);

            value = parseValue(value, keySet);
            
            keySet.remove(key);
        }
        
        if(value == null) {
        	value = "";
        	LOG.warn("Property key: '" + key + "' is not available and hence set to empty");
        }

        return value;
    }
 
    /**
     * This method parses the value string to find all nested properties (foo=${nested}) and
     * replaces them with the value returned from calling resolve().  It does this in a new
     * string and does not modify the raw or resolved properties objects.
     * 
     * @param value the string to search for nest properties
     * @param keySet contains all keys used so far in this recursion.  used to check for circular references.
     * @return
     */
    protected String parseValue(String value, Set<String> keySet) {
        String result = value;

        Matcher matcher = pattern.matcher(value);

        while (matcher.find()) {

            // get the first, outermost ${} in the string.  removes the ${} as well.
            String key = matcher.group(1);

            String resolved = resolve(key, keySet);

            result = matcher.replaceFirst(Matcher.quoteReplacement(resolved));
            matcher = matcher.reset(result);
        }

        return result;
    }
    
    
    /**
     * This method is used when reading in new properties to check if there is a direct reference to the
     * key in the value.  This emulates operating system environment variable setting behavior 
     * and replaces the reference in the value with the current value of the property from the rawProperties.
     * <pre>
     * ex:
     * path=/usr/bin;${someVar}
     * path=${path};/some/other/path
     * 
     * resolves to:
     * path=/usr/bin;${someVar};/some/other/path
     * </pre>
     * 
     * It does not resolve the the value from rawProperties as it could contain nested properties that might change later.
     * If the property does not exist in the rawProperties it will check for a default system property now to
     * prevent a circular reference error.
     * 
     * @param name the property name
     * @param value the value to check for nested property of the same name
     * @return
     */
    protected String replaceVariable(String name, String value){
    	String regex = "(?:\\$\\{"+ name +"\\})";
    	String temporary = null;
    	
    	// Look for a property in the map first and use that.  If system override is true
    	// then it will get overridden during the resolve phase.  If the value is null
    	// we need to check the system now so we don't throw an error.
    	if(value.contains("${" + name + "}")) {
    		if( (temporary = rawProperties.getProperty(name)) == null ) {
    			temporary = System.getProperty(name);
    		}
    		
    		if(temporary != null) {
    			return value.replaceAll(regex,  Matcher.quoteReplacement(temporary));
    		}
    	}   
    	
    	return value;
    }
    
    /**
     * This method iterates through the raw properties and stores their resolved values in the
     * resolved properties map, which acts as a cache so we don't have to run the recursion every
     * time getProperty() is called.
     */
    protected void resolveRawToCache() {
    	if(rawProperties.size() > 0) {
    		Properties oldProps = new Properties(new ImmutableProperties(resolvedProperties));  
    		//oldProps.putAll(new ImmutableProperties(resolvedProperties));
    		resolvedProperties.clear();
    		
    		for(Object o : rawProperties.keySet()) {    			
    			String resolved = resolve((String)o);
    			
    			if(LOG.isInfoEnabled()){
    				String oldResolved = oldProps.getProperty((String)o);
    				if(oldResolved != null && !oldResolved.equals(resolved)){
    					String key = (String)o;
    					String unResolved = rawProperties.getProperty(key);
    					
    					if(unResolved.contains("$")){
    						LOG.info("Resolved Config Override: " + key + "(" + unResolved +")=[" + ConfigLogger.getDisplaySafeValue(key,oldResolved) +"]->[" + ConfigLogger.getDisplaySafeValue(key,resolved) +"]");     					
    					}else{
    						LOG.info("Resolved Config Override: " + key + "=[" + ConfigLogger.getDisplaySafeValue(key,oldResolved) +"]->[" + ConfigLogger.getDisplaySafeValue(key,resolved) +"]"); 
    					}    					
    				}
    			}    			
    			resolvedProperties.setProperty((String)o, resolved);
    		}
    	}
    }

    /**
     * Configures built-in properties.
     */
    protected void configureBuiltIns() {
    	this.setProperty("host.ip", RiceUtilities.getIpNumber());
    	this.setProperty("host.name", RiceUtilities.getHostName());
    }

    /**
     * Generates a random integer in the range specified by the specifier, in the format: min-max
     * 
     * @param rangeSpec
     *            a range specification, 'min-max'
     * @return a random integer in the range specified by the specifier, in the format: min-max
     */
    protected int generateRandomInteger(String rangeSpec) {
        String[] range = rangeSpec.split("-");
        if (range.length != 2) {
            throw new RuntimeException("Invalid range specifier: " + rangeSpec);
        }
        int from = Integer.parseInt(range[0].trim());
        int to = Integer.parseInt(range[1].trim());
        if (from > to) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        int num;
        // not very random huh...
        if (from == to) {
            num = from;
            if(LOG.isInfoEnabled())
            {
            	LOG.info("from==to, so not generating random value for property.");
            }
        } else {
            num = from + RANDOM.nextInt((to - from) + 1);
        }
        return num;
    }
    
    public boolean isSystemOverride() {
        return systemOverride;
    }
    
    /**
     * If set to true then system properties will always be checked first, disregarding
     * any values in the config.
     * 
     * The default is false.
     * 
     * @param systemOverride
     */
    public void setSystemOverride(boolean systemOverride) {
        this.systemOverride = systemOverride;
    }

    protected org.kuali.rice.core.config.xsd.Config unmarshal(Unmarshaller unmarshaller, InputStream in) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        XMLFilter filter = new ConfigNamespaceURIFilter();
        filter.setParent(spf.newSAXParser().getXMLReader());

        UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
        filter.setContentHandler(handler);

        filter.parse(new InputSource(in));

        return (org.kuali.rice.core.config.xsd.Config)handler.getResult();
    }

    /**
     *  This is a SAX filter that adds the config xml namespace to the document if the document
     *  does not have a namespace (for backwards compatibility).  This filter assumes unqualified
     *  attributes and does not modify their namespace (if any).
     *  
     *   This could be broken out into a more generic class if Rice makes more use of JAXB.
     * 
     * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     *
     */
    public class ConfigNamespaceURIFilter extends XMLFilterImpl {

        public static final String CONFIG_URI="http://rice.kuali.org/xsd/core/config";
        
        @Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = CONFIG_URI;
            }
            
            super.startElement(uri, localName, qName, atts);
        }

        @Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = CONFIG_URI;
            }
            
            super.endElement(uri, localName, qName);
        }
    }

	
	@Override
	public void putObject(String key, Object value) {
		this.objects.put(key, value);		
	}
	
	@Override
	public void putObjects(Map<String, Object> objects) {
		this.objects.putAll(objects);	
	}
	
	@Override
	public void removeObject(String key){
		this.objects.remove(key);
	}
	
	@Override
	public void removeProperty(String key){
		this.rawProperties.remove(key);
    	    	
    	resolveRawToCache();
	}

	@Override
	public void putConfig(Config config) {
		this.copyConfig(config);
	}
	
    
}
