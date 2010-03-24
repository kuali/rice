package org.kuali.rice.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.xsd.Config;
import org.kuali.rice.core.config.xsd.Param;
import org.kuali.rice.core.util.ImmutableProperties;
import org.kuali.rice.core.util.RiceUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This implementation of the Config interface uses JAXB to parse the config file and
 * does not resolve variables in the property values until after all the files have been
 * parsed.
 * It has settings for runtime resolution which will not resolve variables in property
 * values until they are accessed.
 * It also has settings for whether system properties should override all properties or
 * only serve as default when the property has not been defined.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class JAXBConfigImpl extends AbstractBaseConfig {

    private static final Logger LOG = Logger.getLogger(JAXBConfigImpl.class);

    private static final String CONFIG_CODED_DEFAULTS = "classpath:org/kuali/rice/core/config-coded-defaults.xml";
    private static final String IMPORT_NAME = "config.location";
    private static final String INDENT = "  ";
    private static final String PLACEHOLDER_REGEX = "\\$\\{([^{}]+)\\}";

    // keep the same random
    private static final Random RANDOM = new Random();

    private final List<String> fileLocs = new ArrayList<String>();

    private final Map<String, Object> objects = new LinkedHashMap<String, Object>();
    private final Properties properties = new Properties();

    // compile pattern for regex once
    private final Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);
    
    private boolean runtimeResolution = false;
    private boolean systemOverride = false;
    
    public JAXBConfigImpl(){}
    
    
    public JAXBConfigImpl(String fileLoc) {
        this.fileLocs.add(fileLoc);
    }

    public JAXBConfigImpl(List<String> fileLocs) {
        this.fileLocs.addAll(fileLocs);
    }

    public JAXBConfigImpl(Properties properties) {    	    	
    	this.properties.putAll(properties);
    }
    
    public JAXBConfigImpl(String fileLoc, Properties properties) {
        this.fileLocs.add(fileLoc);        
        this.properties.putAll(properties);
    }

    public JAXBConfigImpl(List<String> fileLocs, Properties properties) {
        this.fileLocs.addAll(fileLocs);        
        this.properties.putAll(properties);
    }

    public Object getObject(String key) {
        return objects.get(key);
    }

    public Map<String, Object> getObjects() {
    	return Collections.unmodifiableMap(objects);
    }

    public Properties getProperties() {
    	return new ImmutableProperties(properties);
        //return properties;
    }

    public String getProperty(String key) {
        if(runtimeResolution) {
            return resolve(key);
        }
        
        return properties.getProperty(key);
    }

    public void overrideProperty(String name, String value) {        
        this.putProperty(name, value);
    }
   
    /**
     * 
     * This overrided the property. Takes the place of the now depricated overrideProperty
     * 
     * @see org.kuali.rice.core.config.Config#putProperty(java.lang.String, java.lang.Object)
     */
	public void putProperty(String key, Object value) {
		if(!runtimeResolution) {
            HashSet<String> keySet = new HashSet<String>();
            keySet.add(key);
            value = parseValue(value.toString(), keySet);
        }
        
        setProperty(key, value.toString());		
	}

	public void putProperties(Properties properties) {
        if (properties != null) {

            this.properties.putAll(properties);
            
            if(!runtimeResolution) {
                for (Object o : properties.keySet()) {
                    setProperty((String)o, resolve((String)o));
                }
            }
        }
    }

    public void parseConfig() throws IOException {

        if (fileLocs.size() > 0) {

            if (LOG.isInfoEnabled()) {
                LOG.info("Loading Rice configs: " + StringUtils.join(fileLocs, ", "));
            }

            JAXBContext jaxbContext;
            Unmarshaller unmarshaller;

            try {
                jaxbContext = JAXBContext.newInstance(Config.class);
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

            // now that all properties have been loaded, resolve the right hand side
            // if runtimeResolution is not enabled.  This will also replace properties
            // defined in the files with system properties if systemOverride==true.
            if(!runtimeResolution) {
                for (Object o : properties.keySet()) {
                    setProperty((String)o, resolve((String)o));
                }
            }

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
                CollectionUtils.addAll(sorted, properties.propertyNames());
                
                for (String s : sorted) {
                	log.append("Using config Prop " + s + "=[" + ConfigLogger.getDisplaySafeValue(s, (String) properties.get(s)) + "]\n");
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
            Config config;

            try {
                config = unmarshal(unmarshaller, in);
            } catch (Exception ex) {
                throw new ConfigurationException("Error parsing config file: " + filename, ex);
            }

            for (Param p : config.getParamList()) {

                String name = p.getName();

                if (name.equals(IMPORT_NAME)) {
                    String configLocation = parseValue(p.getValue(), new HashSet<String>());
                    parseConfig(configLocation, unmarshaller, depth + 1);
                } else if (p.isOverride() || !properties.containsKey(name)) {

                    if (p.isRandom()) {
                        setProperty(p.getName(), String.valueOf(generateRandomInteger(p.getValue())));
                    } else if (p.isSystem()) {
                        // resolve and set system params immediately so they can override
                        // existing system params. Add to paramMap resolved as well to
                        // prevent possible mismatch
                        HashSet<String> set = new HashSet<String>();
                        set.add(p.getName());
                        String value = parseValue(p.getValue(), set);
                        System.setProperty(name, value);
                        setProperty(name, value);
                    } else {
                    	
                    	/*
                    	 * myProp = dog
                    	 * We have a case where you might want myProp = ${myProp}:someOtherStuff:${foo}
                    	 * This would normally overwrite the existing myProp with ${myProp}:someOtherStuff:${foo}
                    	 * but what we want is:
                    	 * myProp = dog:someOtherStuff:${foo}
                    	 * so that we put the existing value of myProp into the new value. Basically how path works.
                    	 */
                    	String value = p.getValue();
                    	String regex = "(?:\\$\\{"+ name +"\\})";
                    	if((properties.containsKey(name)  || System.getProperties().containsKey(name))
                    			&& value.indexOf("\\$\\{"+ name +"\\}") != 0){                    		                    		
                    		// This is a special in-line replacement that doesn't call the resolve so system properties
                    		// haven't been set yet.  Because of this, overwrite any existing value in properties 
                    		// with the system var, if it exists
                    		String replacement = properties.getProperty(name);
                    		if(System.getProperties().containsKey(name)){
                    			replacement = System.getProperty(name);
                    		}
                    		value = value.replaceAll(regex,  Matcher.quoteReplacement(replacement));                    		                    		
                    	}                                        
                    	
                    	setProperty(p.getName(), value);                    	
                    }
                }
            }

            LOG.info(prefix + "- Parsed config: " + filename);
        }
    }
    
    /*
     * This will set the property. No logic checking so what you pass in gets set.
     */
    protected void setProperty(String name, String value){
    	properties.setProperty(name, value);
    }    

    protected String resolve(String key) {
    	return resolve(key, null);
    }
    
    protected String resolve(String key, Set keySet) {
    	
        // check if we have already resolved this key and have circular reference
        if (keySet != null && keySet.contains(key)) {
            throw new ConfigurationException("Circular reference in config: " + key);
        }
        
        String value = this.properties.getProperty(key);
        
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
    
    
    protected String replaceVariable(String baseString, String name, String value){
    	String pattern = "${"+ name +"}";    	
    	return baseString.replaceAll(Matcher.quoteReplacement(pattern), value);
    }

    /**
     * Configures built-in properties.
     */
    protected void configureBuiltIns() {
        setProperty("host.ip", RiceUtilities.getIpNumber());
        setProperty("host.name", RiceUtilities.getHostName());
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
    
    public boolean isRuntimeResolution() {
        return runtimeResolution;
    }
    
    /**
     * If set to true then properties with values that contain properties will be resolved
     * on every getProperty() call.  If set to false then the property values will be resolved
     * and cached at the end of parseConfig().
     * 
     * The default is false.
     * 
     * @param runtimeResolution
     */
    public void setRunitmeResolution(boolean runtimeResolution) {
        this.runtimeResolution = runtimeResolution;
    }

    protected Config unmarshal(Unmarshaller unmarshaller, InputStream in) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);

        XMLFilter filter = new ConfigNamespaceURIFilter();
        filter.setParent(spf.newSAXParser().getXMLReader());

        UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
        filter.setContentHandler(handler);

        filter.parse(new InputSource(in));

        return (Config)handler.getResult();
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
        
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = CONFIG_URI;
            }
            
            super.startElement(uri, localName, qName, atts);
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(StringUtils.isBlank(uri)) {
                uri = CONFIG_URI;
            }
            
            super.endElement(uri, localName, qName);
        }
    }

	
	public void putObject(String key, Object value) {
		this.objects.put(key, value);		
	}
	
	public void putObjects(Map<String, Object> objects) {
		this.objects.putAll(objects);	
	}
	
	public void removeObject(String key){
		this.objects.remove(key);
	}
	public void removeProperty(String key){
		this.properties.remove(key);
	}
    
}
