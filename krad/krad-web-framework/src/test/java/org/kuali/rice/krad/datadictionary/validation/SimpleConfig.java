/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.datadictionary.validation;

import org.kuali.rice.core.api.config.property.Config;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceUtilities;
import org.kuali.rice.core.api.util.Truth;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


/**
 * A simple Config implementation which has no base properties
 * or base objects.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SimpleConfig implements Config {

	private Properties propertiesUsed = new Properties();

	private Properties baseProperties;

	private Map<String, Object> objects = new LinkedHashMap<String, Object>();

	public SimpleConfig(Properties properties) {		
		this.baseProperties = properties;
	}

	public Map<String, Object> getBaseObjects() {
		return new HashMap<String, Object>();
	}

	public Properties getBaseProperties() {
		if (this.baseProperties == null) {
			return new Properties();
		}
		return this.baseProperties;
	}

	@Override
	public String getProperty(String key) {
		return getProperties().getProperty(key);
	}	

	/**
	 * Configures built-in properties.
	 */
	protected void configureBuiltIns(Properties properties) {
		properties.put("host.ip", RiceUtilities.getIpNumber());
		properties.put("host.name", RiceUtilities.getHostName());
	}

	public Properties getProperties() {
		return this.propertiesUsed;
	}

	public Map<String, String> getPropertiesWithPrefix(String prefix,
			boolean stripPrefix) {
		Map<String, String> props = new HashMap<String, String>();
		for (Map.Entry entry : getProperties().entrySet()) {
			String key = (String) entry.getKey();
			if (StringUtils.isNotBlank(key) && key.trim().startsWith(prefix)) {
				props.put(stripPrefix ? key.substring(prefix.length()) : key,
						(String) entry.getValue());
			}
		}
		return props;
	}

	public boolean getBooleanProperty(String key, boolean defaultValue) {
		return Truth.strToBooleanIgnoreCase(getProperty(key), defaultValue);
	}

	public Map<String, Object> getObjects() {
		return this.objects;
	}

	public Object getObject(String key) {
		return getObjects().get(key);
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

	public String getEnvironment() {
		return getProperty(ENVIRONMENT);
	}

	public String getEDLConfigLocation() {
		return getProperty(EDL_CONFIG_LOCATION);
	}

	public String getDefaultKewNoteClass() {
		return getProperty(DEFAULT_KEW_NOTE_CLASS);
	}

	public String getEmbeddedPluginLocation() {
		return getProperty(EMBEDDED_PLUGIN_LOCATIAON);
	}

	public Integer getRefreshRate() {
		return Integer.valueOf(ConfigContext.getCurrentContextConfig()
				.getProperty(Config.REFRESH_RATE));
	}

	public String getEndPointUrl() {
		return ConfigContext.getCurrentContextConfig().getProperty(
				Config.SERVICE_SERVLET_URL);
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
		return getProperty(org.kuali.rice.core.api.config.property.Config.FIRST_WEEKLY_EMAIL_DELIVERY_DATE);
	}

	public String getDocumentLockTimeout() {
		return getProperty(Config.DOCUMENT_LOCK_TIMEOUT);
	}

	public Boolean getEmailReminderLifecycleEnabled() {
		return Boolean.valueOf(getProperty(ENABLE_EMAIL_REMINDER_LIFECYCLE));
	}

	public Boolean getXmlPipelineLifeCycleEnabled() {
		return Boolean.valueOf(getProperty(ENABLE_XML_PIPELINE_LIFECYCLE));
	}

	public Boolean getDevMode() {
		return Boolean.valueOf(getProperty(DEV_MODE));
	}

	public Boolean getBatchMode() {
		return new Boolean(getProperty(BATCH_MODE));
	}

	public Boolean getOutBoxOn() {
		if (getProperty(Config.OUT_BOX_MODE) == null) {
			return true;
		}
		return Boolean.valueOf(getProperty(Config.OUT_BOX_MODE));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.kuali.rice.core.api.config.property.Config#getKEWBaseURL()
	 */
	public String getKEWBaseURL() {
		return getProperty(Config.KEW_URL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.kuali.rice.core.api.config.property.Config#getKIMBaseURL()
	 */
	public String getKIMBaseURL() {
		return getProperty(Config.KIM_URL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.kuali.rice.core.api.config.property.Config#getKRBaseURL()
	 */
	public String getKRBaseURL() {
		return getProperty(Config.KR_URL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.kuali.rice.core.api.config.property.Config#getKENBaseURL()
	 */
	public String getKENBaseURL() {
		return getProperty(Config.KEN_URL);
	}
	

	public void putProperties(Properties properties) {
		if (properties != null) {
			getProperties().putAll(properties);
		}
	}

	public void putProperty(String key, String value) {
		this.getProperties().put(key, value);
	}

	public void putObject(String key, Object value) {
		this.objects.put(key, value);
	}

	public void putObjects(Map<String, Object> objects) {
		if (objects != null) {
			this.objects.putAll(objects);
		}

	}

	public void removeObject(String key) {
		this.objects.remove(key);
	}

	public void removeProperty(String key) {
		this.getProperties().remove(key);
	}

	public void putConfig(Config config) {
		putProperties(config.getProperties());
		putObjects(config.getObjects());
	}

	public void parseConfig() throws IOException {
	}

}
