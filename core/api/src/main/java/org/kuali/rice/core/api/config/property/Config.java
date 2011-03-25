/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.api.config.property;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Interface encapsulating central config settings. This interface was taken
 * directly from BundleUtility which it replaces.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Config {

	public static final String KEYSTORE_ALIAS = "keystore.alias";
	public static final String KEYSTORE_PASSWORD = "keystore.password";
	public static final String KEYSTORE_FILE = "keystore.file";

	public static final String BASE_WEB_SERVICE_URL_WORKFLOW_CLIENT_FILE = "webservices.settings.url";

	public static final String BASE_WEB_SERVICE_WSDL_PATH = "webservices.settings.wsdl.path";

	public static final String WSDL_LOCATION_WORKFLOW_CLIENT_FILE = "webservices.settings.wsdd.path";

	public static final String WEB_SERVICE_CONNECT_RETRY = "webservices.settings.connect.retry";

	/**
	 * Configuration key under which to specify inlined Log4J configuration in XML/DOM configurer syntax
	 */
	public static final String LOG4J_SETTINGS_XML = "log4j.settings.xml";

	/**
	 * Configuration key under which to specify inlined Log4J configuration in properties configurer syntax
	 */
	public static final String LOG4J_SETTINGS_PROPS = "log4j.settings.props";

	/**
	 * Configuration key under which to specify an external Log4J configuration file path
	 */

	public static final String LOG4J_SETTINGS_PATH = "log4j.settings.path";

	/**
	 * Configuration key under which to specify the Log4J configuration reload interval in minutes
	 */
	public static final String LOG4J_SETTINGS_RELOADINTERVAL_MINS = "log4j.settings.reloadInterval";

	public static final String TRANSACTION_TIMEOUT = "transaction.timeout";

	public static final String DOCUMENT_LOCK_TIMEOUT = "document.lock.timeout";

	public static final String EMAIL_SECURITY_PATH = "email.security.path";

	public static final String ENVIRONMENT = "environment";

	public static final String PLUGIN_DIR = "plugin.dir";

	public static final String EXTRA_CLASSES_DIR = "extra.classes.dir";

	public static final String EXTRA_LIB_DIR = "extra.lib.dir";

	public static final String EDL_CONFIG_LOCATION = "edl.config.loc";

	public static final String SERVICE_NAMESPACE = "service.namespace";

	public static final String THREAD_POOL_SIZE = "threadPool.size";
	
	public static final String BAM_ENABLED = "bam.enabled";

	public static final String EMBEDDED_PLUGIN_LOCATIAON = "embedded.plugin.location";

	public static final String DATASOURCE_PLATFORM = "datasource.platform";

	public static final String OJB_PLATFORM = "datasource.ojb.platform";

	public static final String NODE_PROPERTIES_PATH = "node.properties.path";

	public static final String DATASOURCE_OJB_SEQUENCE_MANAGER = "datasource.ojb.sequenceManager";
	public static final String DATASOURCE_OJB_SEQUENCE_MANAGER_CLASS = "datasource.ojb.sequenceManager.className";
	public static final String DATASOURCE_DRIVER_NAME = "datasource.driver.name";
	public static final String DATASOURCE_URL = "datasource.url";
	public static final String DATASOURCE_POOL_SIZE = "datasource.pool.size";
	public static final String DATASOURCE_POOL_MIN_SIZE = "datasource.pool.minSize";
	public static final String DATASOURCE_POOL_MAX_SIZE = "datasource.pool.maxSize";
	public static final String DATASOURCE_POOL_MAXWAIT = "datasource.pool.maxWait";
	public static final String DATASOURCE_POOL_VALIDATION_QUERY = "datasource.pool.validationQuery";
	public static final String DATASOURCE_USERNAME = "datasource.username";
	public static final String DATASOURCE_PASSWORD = "datasource.password";

	/**
	 * Configuration key under which to specify the base url for workflow
	 */
	public static final String KEW_URL = "kew.url";

	/**
	 * Configuration key under which to specify the base url for rice
	 */
	public static final String KR_URL = "kr.url";

	/**
	 * Configuration key under which to specify the base url for kim
	 */
	public static final String KIM_URL = "kim.url";
	
	/**
	 * Configuration key under which to specify the base url for kim
	 */
	public static final String KEN_URL = "ken.url";

    public static final String DEFAULT_KEW_NOTE_CLASS = "default.kew.note.class";
	public static final String M_BEANS = "mBeans";
	public static final String ALT_SPRING_FILE = "config.spring.file";
	public static final String ALT_OJB_FILE	= "config.obj.file";

	//bus stuff
	public static final String BUS_DEPLOYED_SERVICES = "bus.services";
	public static final String SERVICE_SERVLET_URL = "serviceServletUrl";
	public static final String MESSAGE_PERSISTENCE = "message.persistence";
	public static final String STORE_AND_FORWARD = "bus.storeAndForward";
	public static final String REFRESH_RATE = "bus.refresh.rate";
	public static final String DEV_MODE = "dev.mode";
	public static final String BATCH_MODE = "rice.ksb.batch.mode";
	
	public static final String CREDENTIALS_SOURCE_FACTORY = "credentialsSourceFactory";

	public static final String EMBEDDED_PLUGIN_DEFAULT_CURRENT_CLASS_LOADER = "embedded.plugin.default.current.classloader";

	public static final String FIRST_DAILY_EMAIL_DELIVERY_DATE = "email.daily.firstDeliveryDate";
	public static final String FIRST_WEEKLY_EMAIL_DELIVERY_DATE = "email.weekly.firstDeliveryDate";

	public static final String ENABLE_EMAIL_REMINDER_LIFECYCLE = "email.reminder.lifecycle.enabled";
	public static final String ENABLE_XML_PIPELINE_LIFECYCLE = "xml.pipeline.lifecycle.enabled";
	
	public static final String OUT_BOX_MODE = "actionlist.outbox";

	public void parseConfig() throws IOException;

	public String getDailyEmailFirstDeliveryDate();

	public String getWeeklyEmailFirstDeliveryDate();

	/**
	 * Returns properties explicitly configured in this Config
	 *
	 * @return properties explicitly configured in this Config
	 */
	public Properties getProperties();

	public String getProperty(String key);
	
	public boolean getBooleanProperty(String key, boolean defaultValue);
	
	public Map<String, String> getPropertiesWithPrefix(String prefix, boolean stripPrefix);
	
	public Map<String, Object> getObjects();

	public Object getObject(String key);

	public String getBaseWebServiceURL();

	public String getBaseWebServiceWsdlPath();

	public String getClientWSDLFullPathAndFileName();

	public String getWebServicesConnectRetry();
	
	/**
	 * @return the base URL for KEW
	 */
	public String getKEWBaseURL();
	
	/**
	 * @return the base URL for KIM
	 */
	public String getKIMBaseURL();
	
	/**
	 * @return the base URL for KRice
	 */
	public String getKRBaseURL();

	/**
	 * @return the base URL for KEN
	 */
	public String getKENBaseURL();

	public String getLog4jFileLocation();

	public String getLog4jReloadInterval();

	public String getTransactionTimeout();

	public String getEmailConfigurationPath();

	public String getEnvironment();

	public String getEDLConfigLocation();

	public String getServiceNamespace();

    public String getDefaultKewNoteClass();

    public String getEmbeddedPluginLocation();

	public Integer getRefreshRate();

	public String getEndPointUrl();

	public String getAlternateSpringFile();

	public String getAlternateOJBFile();

	public String getKeystoreAlias();

	public String getKeystorePassword();

	public String getKeystoreFile();

	public String getDocumentLockTimeout();
	
    public Boolean getEmailReminderLifecycleEnabled();

    public Boolean getXmlPipelineLifeCycleEnabled();

	public Boolean getDevMode();
	
	public Boolean getBatchMode();
	
	public Boolean getStoreAndForward();
	public Boolean getOutBoxOn();
	
	/**
	 * 
	 * This method takes a config object and merges it with the current
	 * object.  
	 * 
	 * @param config
	 */
	public void putConfig(Config config);
	
	public void putProperties(Properties properties);
	public void putProperty(String key, String value);
	public void removeProperty(String key);
	public void putObjects(Map<String, Object> objects);
	public void putObject(String key, Object value);
	public void removeObject(String key);	
}
