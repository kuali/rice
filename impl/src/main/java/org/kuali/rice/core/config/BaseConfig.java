/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package org.kuali.rice.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.RiceUtilities;

/**
 * Abstract base hierarchical config implementation. Loads a hierarchy configs,
 * resolving placeholders at parse-time of each config, using the current and
 * ancestor configs for resolution.
 * 
 * @see HierarchicalConfigParser
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class BaseConfig implements Config {

    private static final Logger LOG = Logger.getLogger(BaseConfig.class);

    private Map<String, Object> configs = new LinkedHashMap<String, Object>();

    private List<String> fileLocs = new ArrayList<String>();

    private Properties propertiesUsed = new Properties();

    private Map<String, Object> objects = new LinkedHashMap<String, Object>();

    public BaseConfig(String fileLoc) {
        this.fileLocs.add(fileLoc);
    }

    public BaseConfig(List<String> fileLocs) {
        this.fileLocs = fileLocs;
    }
    
    protected void parseWithConfigParserImpl() throws IOException {
        ConfigParserImpl cp = new ConfigParserImpl();
        Map p = new Properties();
        p.putAll(propertiesUsed);
        cp.parse(p, fileLocs.toArray(new String[fileLocs.size()]));
        putPropertiesInPropsUsed(p, StringUtils.join(fileLocs, ", "));
    }

    protected void parseWithHierarchicalConfigParser() throws IOException {
        for (String fileLoc : this.fileLocs) {
            HierarchicalConfigParser configParser = new HierarchicalConfigParser(this.propertiesUsed);
            this.configs.putAll(configParser.parse(fileLoc));
            // get all the properties from all the potentially nested configs in the master set
            // of propertiesUsed. Do it now so that all the values are available for token replacement
            // next iteration
            Set<String> keys = this.configs.keySet();
            //LOG.info("Order of configs: " + StringUtils.join(keys.toArray(), "\r\n"));
            for (Map.Entry<String, Object> config : this.configs.entrySet()) {
                if (config.getValue() instanceof Map) {
                    putPropertiesInPropsUsed((Map) config.getValue(), config.getKey());
                } else {
                    String configValue = (String) config.getValue();
                    if ( LOG.isDebugEnabled() ) {
                    	LOG.debug("-->Putting root config Prop " + config.getKey() + "=[" + configValue + "]");
                    }
                    this.propertiesUsed.put(config.getKey(), configValue);
                }
            }
        }
    }

    public void parseConfig() throws IOException {
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info("Loading Rice configs: " + StringUtils.join(fileLocs, ", "));
    	}
    	Map<String, Object> baseObjects = getBaseObjects();
    	if (baseObjects != null) {
    		this.objects.putAll(baseObjects);
    	}
    	configureBuiltIns(this.propertiesUsed);
    	Properties baseProperties = getBaseProperties();
    	if (baseProperties != null) {
    		this.propertiesUsed.putAll(baseProperties);
    	}

    	parseWithConfigParserImpl();
    	//parseWithHierarchicalConfigParser();

    	//if (!fileLocs.isEmpty()) {
    	if ( LOG.isInfoEnabled() ) {
	    	LOG.info("");
	    	LOG.info("####################################");
	    	LOG.info("#");
	    	LOG.info("# Properties used after config override/replacement");
	    	LOG.info("# " + StringUtils.join(fileLocs, ", "));
	    	LOG.info("#");
	    	LOG.info("####################################");
	    	LOG.info("");
    	}
    	Map<String, String> safePropsUsed = ConfigLogger.getDisplaySafeConfig(this.propertiesUsed);
    	Set<Map.Entry<String,String>> entrySet = safePropsUsed.entrySet();
    	// sort it for display
    	SortedSet<Map.Entry<String,String>> sorted = new TreeSet<Map.Entry<String,String>>(new Comparator<Map.Entry<String,String>>() {
    		public int compare(Map.Entry<String,String> a, Map.Entry<String,String> b) {
    			return a.getKey().compareTo(b.getKey());
    		}
    	});
    	sorted.addAll(entrySet);
    	//}
    	loadDefaults();
    	if ( LOG.isInfoEnabled() ) {
	    	for (Map.Entry<String, String> propUsed: sorted) {
	    		LOG.info("Using config Prop " + propUsed.getKey() + "=[" + propUsed.getValue() + "]");
	    	}
    	}
    }

    protected void putPropertiesInPropsUsed(Map properties, String fileName) {
        // Properties configProperties = (Properties)config.getValue();
        Map<String, String> safeConfig = ConfigLogger.getDisplaySafeConfig(properties);
        if ( LOG.isInfoEnabled() ) {
        	LOG.info("Loading properties for config " + fileName);
        }
        for (Iterator iterator2 = properties.entrySet().iterator(); iterator2.hasNext();) {
            Map.Entry configProp = (Map.Entry) iterator2.next();
            String key = (String) configProp.getKey();
            String value = (String) configProp.getValue();
            String safeValue = safeConfig.get(key);
            if ( LOG.isDebugEnabled() ) {
            	LOG.debug("---->Putting config Prop " + key + "=[" + safeValue + "]");
            }
            this.propertiesUsed.put(key, value);
        }
    }

	private void loadDefaults() {
		// Load defaults if not set in config files : KULRICE-1067
        // This code is just terribly unmaintainable... better way?
		Properties p = getProperties();
        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_POOL_MIN_SIZE))) {
        	p.setProperty(Config.DATASOURCE_POOL_MIN_SIZE, "0");
        }
        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_POOL_MAX_SIZE))) {
        	p.setProperty(Config.DATASOURCE_POOL_MAX_SIZE, "5");
        }
        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_POOL_SIZE))) {
        	p.setProperty(Config.DATASOURCE_POOL_SIZE, "5");
        }
        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_POOL_MAXWAIT))) {
        	p.setProperty(Config.DATASOURCE_POOL_MAXWAIT, "30000");
        }
        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_POOL_VALIDATION_QUERY))) {
        	p.setProperty(Config.DATASOURCE_POOL_VALIDATION_QUERY, "select 1 from dual");
        }
        if (StringUtils.isNotEmpty(p.getProperty(Config.OJB_PLATFORM)) && p.getProperty(Config.OJB_PLATFORM).trim().startsWith("Oracle")) {
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_DRIVER_NAME))) {
	        	p.setProperty(Config.DATASOURCE_DRIVER_NAME, "oracle.jdbc.driver.OracleDriver");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_PLATFORM))) {
	        	p.setProperty(Config.DATASOURCE_PLATFORM, "org.kuali.rice.core.database.platform.OraclePlatform");
	        }	        
        } else if (StringUtils.isNotEmpty(p.getProperty(Config.OJB_PLATFORM)) && p.getProperty(Config.OJB_PLATFORM).trim().startsWith("MySQL")) {
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_DRIVER_NAME))) {
	        	p.setProperty(Config.DATASOURCE_DRIVER_NAME, "com.mysql.jdbc.Driver");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_PLATFORM))) {
	        	p.setProperty(Config.DATASOURCE_PLATFORM, "org.kuali.rice.core.database.platform.MySQLPlatform");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER))) {
	        	p.setProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER, "org.apache.ojb.broker.platforms.KualiMySQLSequenceManagerImpl");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS))) {
	        	p.setProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS, "org.apache.ojb.broker.platforms.KualiMySQLSequenceManagerImpl");
	        }
        } else if (StringUtils.isNotEmpty(p.getProperty(Config.OJB_PLATFORM)) && p.getProperty(Config.OJB_PLATFORM).trim().startsWith("Derby")) {
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_DRIVER_NAME))) {
	        	p.setProperty(Config.DATASOURCE_DRIVER_NAME, "org.apache.derby.jdbc.EmbeddedDriver");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_PLATFORM))) {
	        	p.setProperty(Config.DATASOURCE_PLATFORM, "org.kuali.rice.core.database.platform.DerbyPlatform");
	        }
	        /* TODO: Implement these for Derby
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER))) {
	        	p.setProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER, "");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS))) {
	        	p.setProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS, "");
	        }
	        */
        } else if (StringUtils.isNotEmpty(p.getProperty(Config.OJB_PLATFORM)) && p.getProperty(Config.OJB_PLATFORM).trim().startsWith("McKoi	")) {
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_DRIVER_NAME))) {
	        	p.setProperty(Config.DATASOURCE_DRIVER_NAME, "com.mckoi.JDBCDriver");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_PLATFORM))) {
	        	p.setProperty(Config.DATASOURCE_PLATFORM, "org.kuali.rice.core.database.platform.MckoiPlatform");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER))) {
	        	p.setProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER, "org.apache.ojb.broker.platforms.PlatformMckoiImpl");
	        }
	        if (StringUtils.isEmpty(p.getProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS))) {
	        	p.setProperty(Config.DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS, "org.apache.ojb.broker.platforms.PlatformMckoiImpl");
	        }
        }
	}

    public void overrideProperty(String name, String value) {
        this.propertiesUsed.put(name, value);
    }

    /**
     * Configures built-in properties.
     */
    protected void configureBuiltIns(Properties properties) {
        properties.put("host.ip", RiceUtilities.getIpNumber());
        properties.put("host.name", RiceUtilities.getHostName());
    }

    public abstract Properties getBaseProperties();

    public abstract Map<String, Object> getBaseObjects();

    public Properties getProperties() {
        return this.propertiesUsed;
    }
    
    public Map<String, String> getPropertiesWithPrefix(String prefix, boolean stripPrefix) {
		Map<String, String> props = new HashMap<String, String>();
		for (Map.Entry entry : getProperties().entrySet()) {
			String key = (String) entry.getKey();
			if (StringUtils.isNotBlank(key) && key.trim().startsWith(prefix)) {
				props.put(stripPrefix ? key.substring(prefix.length()) : key, (String) entry.getValue());
			}
		}
		return props;
	}

    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
    	return RiceUtilities.getBooleanValueForString(getProperty(key), defaultValue);
    }

    public Map<String, Object> getObjects() {
        return this.objects;
    }

    public Object getObject(String key) {
        return getObjects().get(key);
    }

    public String getClientProtocol() {
        return getProperty(CLIENT_PROTOCOL);
    }

    public String getBaseWebServiceURL() {
        return getProperty(BASE_WEB_SERVICE_URL_WORKFLOW_CLIENT_FILE);
    }

    public String getBaseWebServiceWsdlPath() {
        return getProperty(BASE_WEB_SERVICE_WSDL_PATH);
    }

    public String getClientWSDLFullPathAndFileName() {
        return getProperty(WSDL_LOCATION_WORKFLOW_CLIENT_FILE);
    }

    public String getWebServicesConnectRetry() {
        return getProperty(WEB_SERVICE_CONNECT_RETRY);
    }

    public String getLog4jFileLocation() {
        return getProperty(LOG4J_SETTINGS_PATH);
    }

    public String getLog4jReloadInterval() {
        return getProperty(LOG4J_SETTINGS_RELOADINTERVAL_MINS);
    }

    public String getTransactionTimeout() {
        return getProperty(TRANSACTION_TIMEOUT);
    }

    public String getEmailConfigurationPath() {
        return getProperty(EMAIL_SECURITY_PATH);
    }

    public String getBaseUrl() {
        return getProperty(BASE_URL);
    }

    public String getEnvironment() {
        return getProperty(ENVIRONMENT);
    }

    public String getEDLConfigLocation() {
        return getProperty(EDL_CONFIG_LOCATION);
    }

    public String getServiceNamespace() {
        return getProperty(SERVICE_NAMESPACE);
    }

    public String getDefaultNoteClass() {
        return getProperty(DEFAULT_NOTE_CLASS);
    }

    public String getEmbeddedPluginLocation() {
        return getProperty(EMBEDDED_PLUGIN_LOCATIAON);
    }

    public Integer getRefreshRate() {
        Integer refreshRate;
        try {
            refreshRate = new Integer(ConfigContext.getCurrentContextConfig().getProperty(Config.REFRESH_RATE));
        } catch (NumberFormatException nfe) {
            LOG.error("Couldn't parse property " + Config.REFRESH_RATE + " to set bus refresh rate. Defaulting to 30 seconds.");
            ConfigContext.getCurrentContextConfig().overrideProperty(Config.REFRESH_RATE, "30");
            return 30;
        }
        return refreshRate;
    }

    public String getEndPointUrl() {
        return ConfigContext.getCurrentContextConfig().getProperty(Config.SERVICE_SERVLET_URL);
    }

    public String getAlternateOJBFile() {
        return getProperty(Config.ALT_OJB_FILE);
    }

    public String getAlternateSpringFile() {
        return getProperty(Config.ALT_SPRING_FILE);
    }

    public String getKeystoreAlias() {
        return getProperty(Config.KEYSTORE_ALIAS);
    }

    public String getKeystorePassword() {
        return getProperty(Config.KEYSTORE_PASSWORD);
    }

    public String getKeystoreFile() {
        return getProperty(Config.KEYSTORE_FILE);
    }

    public String getDailyEmailFirstDeliveryDate() {
        return getProperty(Config.FIRST_DAILY_EMAIL_DELIVERY_DATE);
    }

    public String getWeeklyEmailFirstDeliveryDate() {
        return getProperty(Config.FIRST_WEEKLY_EMAIL_DELIVERY_DATE);
    }

    public String getDocumentLockTimeout() {
        return getProperty(Config.DOCUMENT_LOCK_TIMEOUT);
    }
    
    public Boolean getEmailReminderLifecycleEnabled() {
        return new Boolean(getProperty(ENABLE_EMAIL_REMINDER_LIFECYCLE));
    }

    public Boolean getXmlPipelineLifeCycleEnabled() {
        return new Boolean(getProperty(ENABLE_XML_PIPELINE_LIFECYCLE));
    }

    public Boolean getDevMode() {
        return new Boolean(getProperty(DEV_MODE));
    }

    public Boolean getStoreAndForward() {
        return new Boolean(getProperty(Config.STORE_AND_FORWARD));
    }

    public Boolean getOutBoxOn() {
        if (getProperty(Config.OUT_BOX_MODE) == null) {
            return true;
        } 
        return new Boolean(getProperty(Config.OUT_BOX_MODE));
    }

    public Boolean getOutBoxDefaultPreferenceOn() {
        if (getProperty(Config.OUT_BOX_DEFAULT_PREFERENCE_ON) == null) {
            return true;
        }
        return new Boolean(getProperty(Config.OUT_BOX_DEFAULT_PREFERENCE_ON));
    }
    
    public String toString() {
        return new ToStringBuilder(this).append("fileLocs", fileLocs).toString();
    }
}
