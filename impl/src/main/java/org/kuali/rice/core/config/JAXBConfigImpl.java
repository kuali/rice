package org.kuali.rice.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
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

    // keep the same random
    private static final Random RANDOM = new Random();

    private List<String> fileLocs = new ArrayList<String>();

    private Map<String, Object> objects = new LinkedHashMap<String, Object>();
    private Properties properties = new Properties();

    // compile pattern for regex once
    private String regex = "\\$\\{([^{}]+)\\}";
    private Pattern pattern = Pattern.compile(regex);

    private boolean loadDefaults = true;
    private boolean runtimeResolution = false;
    private boolean systemOverride = false;

    public JAXBConfigImpl(String fileLoc) {
        this.fileLocs.add(fileLoc);
    }

    public JAXBConfigImpl(List<String> fileLocs) {
        this.fileLocs.addAll(fileLocs);
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
        return objects;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        if(runtimeResolution) {
            return resolve(key);
        }
        
        return properties.getProperty(key);
    }

    public void overrideProperty(String name, String value) {
        
        if(!runtimeResolution) {
            HashSet<String> keySet = new HashSet<String>();
            keySet.add(name);
            value = parseValue(value, keySet);
        }
        
        properties.put(name, value);
    }

    public void addProperties(Properties properties) {
        if (properties != null) {
            this.properties.putAll(properties);
            
            if(!runtimeResolution) {
                for (Object o : properties.keySet()) {
                    properties.put((String)o, resolve((String)o));
                }
            }
        }
    }

    public void parseConfig() throws IOException {

        // these all have override set to false and come from legacy code
        if (loadDefaults) {
            fileLocs.add(CONFIG_CODED_DEFAULTS);
        }

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
                    properties.put((String)o, resolve((String)o));
                }
            }

            if (LOG.isInfoEnabled()) {
                LOG.info("");
                LOG.info("####################################");
                LOG.info("#");
                LOG.info("# Properties used after config override/replacement");
                LOG.info("# " + StringUtils.join(fileLocs, ", "));
                LOG.info("#");
                LOG.info("####################################");
                LOG.info("");
                //commented out to backport to java 5
                //SortedSet<String> sorted = new TreeSet<String>(properties.stringPropertyNames());
                
                SortedSet<String> sorted = new TreeSet<String>();
                CollectionUtils.addAll(sorted, properties.propertyNames());
                
                for (String s : sorted) {
                    LOG.info("Using config Prop " + s + "=[" + ConfigLogger.getDisplaySafeValue(s, (String) properties.get(s)) + "]");
                }
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
            LOG.warn("###############################");
            LOG.warn("#");
            LOG.warn("# Configuration file '" + filename + "' not found!");
            LOG.warn("#");
            LOG.warn("###############################");
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
                        properties.put(p.getName(), String.valueOf(generateRandomInteger(p.getValue())));
                    } else if (p.isSystem()) {
                        // resolve and set system params immediately so they can override
                        // existing system params. Add to paramMap resolved as well to
                        // prevent possible mismatch
                        HashSet<String> set = new HashSet<String>();
                        set.add(p.getName());
                        String value = parseValue(p.getValue(), set);
                        System.setProperty(name, value);
                        properties.put(name, value);
                    } else {
                        properties.put(p.getName(), p.getValue());
                    }
                }
            }

            LOG.info(prefix + "- Parsed config: " + filename);
        }
    }

    protected String resolve(String key) {
        String value = (String) properties.get(key);

        if (systemOverride && System.getProperties().containsKey(key)) {
            value = System.getProperty(key);
        }
        
        if (value != null && value.contains("${")) {
            HashSet<String> keySet = new HashSet<String>();
            keySet.add(key);

            value = parseValue(value, keySet);
        }

        return value;
    }

    protected String parseValue(String value, HashSet<String> keySet) {
        String result = value;

        Matcher matcher = pattern.matcher(value);

        while (matcher.find()) {

            // get the first, outermost ${} in the string.  removes the ${} as well.
            String key = matcher.group(1);

            // add the key to the hashSet, if the key is already in there
            // we have a circular reference/infinite loop.
            if (!keySet.add(key)) {
                throw new ConfigurationException("Circular reference in config: " + key);
            }

            String resolved = parseValue(key, keySet);

            if (systemOverride && System.getProperties().containsKey(resolved)) {
                resolved = System.getProperty(resolved);
            } else if (properties.containsKey(resolved)) {
                resolved = (String) properties.get(resolved);
            } else if (!systemOverride && System.getProperties().containsKey(resolved)) {
                resolved = System.getProperty(resolved);
            } else {
                // implement behavior for missing property here...e.g. return ""
                // returning null will result in the substitutor not substituting
                LOG.warn("Property key: '" + resolved + "' is not available and hence set to empty");

                resolved = "";
            }

            result = matcher.replaceFirst(Matcher.quoteReplacement(resolved));
            matcher = matcher.reset(result);

            keySet.remove(key);
        }

        return result;
    }

    /**
     * Configures built-in properties.
     */
    protected void configureBuiltIns() {
        properties.put("host.ip", RiceUtilities.getIpNumber());
        properties.put("host.name", RiceUtilities.getHostName());
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

    public boolean isLoadDefaults() {
        return loadDefaults;
    }

    /**
     * If set to true then default properties taken for the old BaseConfig will
     * be loaded.  These properties are datasource/ojb related and located in
     * classpath:org/kuali/rice/core/config-coded-defaults.xml
     * 
     * The default is true.
     * 
     * @param loadDefaults
     */
    public void setLoadDefaults(boolean loadDefaults) {
        this.loadDefaults = loadDefaults;
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
}
