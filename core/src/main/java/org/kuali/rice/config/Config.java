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

package org.kuali.rice.config;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Interface encapsulating central config settings. This interface was taken
 * directly from BundleUtility which it replaces.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Config {

	public static final String CLIENT_PROTOCOL = "client.protocol";

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

	public static final String BASE_URL = "base.url";

	public static final String ENVIRONMENT = "environment";

	public static final String INSTITUTIONAL_PLUGIN_DIR = "en.plugin.dir";

	public static final String PLUGIN_DIR = "plugin.dir";

	public static final String EXTRA_CLASSES_DIR = "extra.classes.dir";

	public static final String EXTRA_LIB_DIR = "extra.lib.dir";

	public static final String EDL_CONFIG_LOCATION = "edl.config.loc";

	public static final String INSTITUTIONAL_PLUGIN_NAME = "plugin.institutional.name";

	public static final String MESSAGE_ENTITY = "message.entity";

	public static final String THREAD_POOL_SIZE = "threadPool.size";
	
	public static final String BAM_ENABLED = "bam.enabled";

	public static final String EMBEDDED_PLUGIN_LOCATIAON = "embedded.plugin.location";

	public static final String DATASOURCE_PLATFORM = "datasource.platform";

	public static final String OJB_PLATFORM = "datasource.ojb.platform";

	public static final String NODE_PROPERTIES_PATH = "node.properties.path";

	public static final String DATASOURCE_DRIVER_NAME = "datasource.driver.name";
	public static final String DATASOURCE_URL = "datasource.url";
	public static final String DATASOURCE_POOL_MAXSIZE = "datasource.pool.maxSize";
	public static final String DATASOURCE_POOL_MINSIZE = "datasource.pool.minSize";
	public static final String DATASOURCE_POOL_MAXWAIT = "datasource.pool.maxWait";
	public static final String DATASOURCE_POOL_VALIDATION_QUERY = "datasource.pool.validationQuery";
	public static final String DATASOURCE_USERNAME = "datasource.username";
	public static final String DATASOURCE_PASSWORD = "datasource.password";

	public static final String TRANSACTION_MANAGER_JNDI = "transactionManager.jndi.location";
	public static final String USER_TRANSACTION_JNDI = "transactionManager.jndi.location";
	public static final String DATASOURCE_JNDI = "datasource.jndi.location";

	// Configuration Objects

	public static final String DATASOURCE_OBJ = "datasource";
	public static final String TRANSACTION_MANAGER_OBJ = "transactionManager";
	public static final String USER_TRANSACTION_OBJ = "userTransaction";
	public static final String DEFAULT_NOTE_CLASS = "default.note.class";
	public static final String M_BEANS = "mBeans";
	public static final String ALT_SPRING_FILE = "config.spring.file";
	public static final String ALT_OJB_FILE	= "config.obj.file";

	//bus stuff
	public static final String BUS_DEPLOYED_SERVICES = "bus.services";
	public static final String SERVICE_SERVLET_URL = "serviceServletUrl";
	public static final String MESSAGE_PERSISTENCE = "message.persistence";
	public static final String JMX_SERVICE_URL = "jmx.service.url";
	public static final String STORE_AND_FORWARD = "bus.storeAndForward";
	public static final String JMX_PROTOCOL = "jmx.protocol";
	public static final String REFRESH_RATE = "bus.refresh.rate";
	public static final String DEV_MODE = "dev.mode";
	
	public static final String CREDENTIALS_SOURCE_FACTORY = "credentialsSourceFactory";

	public static final String EMBEDDED_PLUGIN_DEFAULT_CURRENT_CLASS_LOADER = "embedded.plugin.default.current.classloader";

	public static final String FIRST_DAILY_EMAIL_DELIVERY_DATE = "email.daily.firstDeliveryDate";
	public static final String FIRST_WEEKLY_EMAIL_DELIVERY_DATE = "email.weekly.firstDeliveryDate";

	public static final String RUNNING_SERVER_IN_EMBEDDED = "embedded.server";

	public void parseConfig() throws IOException;

	/**
	 * Programmatically override or place a setting in the config properties
	 * @param name
	 * @param value
	 */
	public void overrideProperty(String name, String value);

	public String getDailyEmailFirstDeliveryDate();

	public String getWeeklyEmailFirstDeliveryDate();

	/**
	 * Returns properties explicitly configured in this Config
	 *
	 * @return properties explicitly configured in this Config
	 */
	public Properties getProperties();

	public String getProperty(String key);

	public Map<String, Object> getObjects();

	public Object getObject(String key);

	public String getClientProtocol();

	public String getBaseWebServiceURL();

	public String getBaseWebServiceWsdlPath();

	public String getClientWSDLFullPathAndFileName();

	public String getWebServicesConnectRetry();

	public String getLog4jFileLocation();

	public String getLog4jReloadInterval();

	public String getTransactionTimeout();

	public String getEmailConfigurationPath();

	public String getBaseUrl();

	public String getEnvironment();

	public String getEDLConfigLocation();

	public String getMessageEntity();

	public String getDefaultNoteClass();

	public String getEmbeddedPluginLocation();

	public Integer getRefreshRate();

	public String getEndPointUrl();

	public String getAlternateSpringFile();

	public String getAlternateOJBFile();

	public String getKeystoreAlias();

	public String getKeystorePassword();

	public String getKeystoreFile();

	public String getDocumentLockTimeout();

	public Boolean getRunningEmbeddedServerMode();

	public Boolean getDevMode();
	
	public Boolean getStoreAndForward();
}