/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.workflow.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.config.logging.Log4jLifeCycle;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.BaseResourceLoader;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.ServiceLocator;
import org.kuali.rice.resourceloader.SimpleServiceLocator;
import org.kuali.workflow.ojb.OjbConfigurer;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.ServiceHolder;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.JavaServiceDefinition;
import edu.iu.uis.eden.messaging.ServiceDefinition;
import edu.iu.uis.eden.plugin.PluginRegistry;
import edu.iu.uis.eden.util.ClassLoaderUtils;
import edu.iu.uis.eden.util.Utilities;

/**
 * Configures the KEW Rice module.  KEW module initiation proceeds as follows:
 *
 * <ol>
 *   <li>Parse and load configuration for:</li>
 *     <ul>
 *       <li>Client Protocol</li>
 *		 <li>The KSB</li>
 * 		 <li>Webservices</li>
 *		 <li>JMX</li>
 *	   </ul>
 *   </li>
 *   <li>Configure and startup KEW for "Thin Client" mode OR</li>
 *   <li>Configure and startup KEW for "Embedded Mode"</li>
 * </ol>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KEWConfigurer extends ModuleConfigurer {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KEWConfigurer.class);

	public static final String KEW_DATASOURCE_OBJ = "org.kuali.workflow.datasource";
	public static final String KEW_DATASOURCE_JNDI = "org.kuali.workflow.datasource.jndi.location";

	private static final String DEFAULT_JMX_PROTOCOL = "hessian+sig";
	private static final String DEFAULT_JMX_SERVICE_URL = "service:jmx:"+DEFAULT_JMX_PROTOCOL+"://localhost:8080/remoting/jmx";

	private String clientProtocol;

	private List<ServiceHolder> overrideServices;
	private PluginRegistry pluginRegistry;
	private DataSource dataSource;
	private String dataSourceJndiName;

	//bus stuff
	private List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();

	private String serviceServletUrl;

	// webservice related parameters
	private String keystoreAlias;
	private String keystorePassword;
	private String keystoreFile;
	private String webservicesUrl;
	private String webserviceRetry;

	// jmx parameters
	private String jmxProtocol;
	private String jmxServiceUrl;
	private Map mBeans = new HashMap();

	private boolean useDefaultUserService = false;
	private boolean useDefaultWorkgroupService = false;
	private boolean runEmbeddedServer = false;

	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		if (EdenConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(Core.getCurrentContextConfig().getClientProtocol())) {
			lifecycles.add(createThinClientLifecycle());
		} else {
			if (isStandaloneServer()) {
				lifecycles.add(new Log4jLifeCycle());
			}
			lifecycles.add(new OjbConfigurer());
			lifecycles.add(createTempEmbeddedLifeCycle());
		}
		return lifecycles;
	}

	protected boolean isStandaloneServer() {
		return new Boolean(Core.getCurrentContextConfig().getProperty("kew.standalone.server")).booleanValue();
	}

	@Override
	public void start() throws Exception {
		super.start();
		registerOptionalDefaultServices(Core.getCurrentContextConfig());
		configureInjectedOverrideServices(GlobalResourceLoader.getResourceLoader());
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	/**
	 * TODO Because a lot of our lifecycles live behind the embedded plugin and the KEWConfigurer does not, this is a simple
	 * measure to load these without having to deal with the removal of the embedded plugin right away.
	 */
	protected Lifecycle createTempEmbeddedLifeCycle() throws Exception {
		return (Lifecycle)Class.forName("edu.iu.uis.eden.core.TempEmbeddedLifeCycle").newInstance();
	}

	protected Lifecycle createThinClientLifecycle() throws Exception {
		return (Lifecycle)Class.forName("org.kuali.workflow.config.ThinClientLifecycle").newInstance();
	}

	public Config loadConfig(Config parentConfig) throws Exception {
		LOG.info("Starting configuration of KEW for message entity " + getMessageEntity(parentConfig));
		Config currentConfig = parseConfig(parentConfig);
		configureClientProtocol(currentConfig);
		configureDataSource(currentConfig);
		configureBus(currentConfig);
		configureKeystore(currentConfig);
		configureWebservices(currentConfig);
		configureManagement(currentConfig);
		return currentConfig;
	}

	protected Config parseConfig(Config parentConfig) throws Exception {
		List<String> defaultConfigLocations = new ArrayList<String>();
		defaultConfigLocations.add(EdenConstants.DEFAULT_GLOBAL_CONFIG_LOCATION);
		defaultConfigLocations.add(EdenConstants.DEFAULT_APPLICATION_CONFIG_LOCATION);
		Config kewConfig = new SimpleConfig(defaultConfigLocations, parentConfig.getProperties());
		kewConfig.parseConfig();
		mergeDefaultsIntoParentConfig(parentConfig, kewConfig);
		return parentConfig;
	}

	/**
	 * Merges any default configuration into the parent config.  If a property appears in both
	 * places, precedence is given to the parentConfig.  This allows for our defaults to not
	 * override any property which has already been defined.
	 */
	protected void mergeDefaultsIntoParentConfig(Config parentConfig, Config defaultConfig) {
		for (Object keyObj : defaultConfig.getProperties().keySet()) {
			String key = (String)keyObj;
			if (!parentConfig.getProperties().containsKey(key)) {
				parentConfig.getProperties().put(key, defaultConfig.getProperty(key));
			}
		}
	}

	protected String getMessageEntity(Config config) {
		if (StringUtils.isBlank(config.getMessageEntity())) {
			throw new ConfigurationException("The 'message.entity' property was not properly configured.");
		}
		return config.getMessageEntity();
	}

	protected void configureClientProtocol(Config config) {
		if (StringUtils.isBlank(clientProtocol)) {
			clientProtocol = config.getClientProtocol();
			if (clientProtocol == null) {
				clientProtocol = EdenConstants.WEBSERVICE_CLIENT_PROTOCOL;
			}
		}
		// from a client, LOCAL protocol is equivalent to EMBEDDED
		// TODO this was messing up the tests were LOCAL was actually being used
		/*if (EdenConstants.LOCAL_CLIENT_PROTOCOL.equals(clientProtocol)) {
			clientProtocol = EdenConstants.EMBEDDED_CLIENT_PROTOCOL;
		}*/
		if (!EdenConstants.CLIENT_PROTOCOLS.contains(clientProtocol)) {
			throw new ConfigurationException("Invalid client protocol specified '" + clientProtocol + "'.");
		}
		config.getProperties().put(Config.CLIENT_PROTOCOL, clientProtocol);
	}

	protected void configureDataSource(Config config) {
		if (getDataSource() != null) {
			config.getObjects().put(KEW_DATASOURCE_OBJ, getDataSource());
		} else if (!StringUtils.isBlank(getDataSourceJndiName())) {
			config.getProperties().put(KEW_DATASOURCE_JNDI, getDataSourceJndiName());
		}
	}

	@SuppressWarnings("unchecked")
	protected void configureBus(Config config) throws Exception {
		if (getServiceServletUrl() != null) {
			Core.getCurrentContextConfig().overrideProperty(Config.SERVICE_SERVLET_URL, getServiceServletUrl());
		}
		LOG.debug("Configuring services for Message Entity " + getMessageEntity(config) + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		configureServiceList(config, Config.BUS_DEPLOYED_SERVICES, getServices());
	}

	protected void configureKeystore(Config config) {
		if (!Utilities.isEmpty(keystoreAlias)) {
			config.getProperties().put(Config.KEYSTORE_ALIAS, keystoreAlias);
		}
		if (!Utilities.isEmpty(keystorePassword)) {
			config.getProperties().put(Config.KEYSTORE_PASSWORD, keystorePassword);
		}
		if (!Utilities.isEmpty(keystoreFile)) {
			config.getProperties().put(Config.KEYSTORE_FILE, keystoreFile);
		}
		if (Utilities.isEmpty(config.getProperty(EdenConstants.SIMPLE_DOCUMENT_ACTIONS_SECURITY))) {
			config.getProperties().put(EdenConstants.SIMPLE_DOCUMENT_ACTIONS_SECURITY, "true");
		}
	}

	protected void configureWebservices(Config config) {
		if (!Utilities.isEmpty(webservicesUrl)) {
			config.getProperties().put(Config.BASE_WEB_SERVICE_URL_WORKFLOW_CLIENT_FILE, webservicesUrl);
		}
		if (!Utilities.isEmpty(webserviceRetry)) {
			config.getProperties().put(Config.WEB_SERVICE_CONNECT_RETRY, webserviceRetry);
		}
	}

	protected void configureManagement(Config config) {
		// configure JMX Protocol
		String jmxProtocol = getJmxProtocol();
		if (Utilities.isEmpty(jmxProtocol)) {
			jmxProtocol = DEFAULT_JMX_PROTOCOL;
		}
		setJmxProtocol(jmxProtocol);
		config.getProperties().put(Config.JMX_PROTOCOL, jmxProtocol);

		// configure JMX Service Url
		String jmxServiceUrl = getJmxServiceUrl();
		if (Utilities.isEmpty(jmxServiceUrl)) {
			String serviceServletUrl = getServiceServletUrl();
			if (Utilities.isEmpty(serviceServletUrl)) {
				jmxServiceUrl = DEFAULT_JMX_SERVICE_URL;
				LOG.warn("Could not determine the JMX Service URL.  Defaulting to " + jmxServiceUrl);
			} else {
				// derive the jmx url from the service servlet url
				int protocolIndex = serviceServletUrl.indexOf("/");
				if (protocolIndex < 0) {
					throw new WorkflowRuntimeException("Failed to derive jmx url from servlet url: " + serviceServletUrl);
				}
				jmxServiceUrl = "service:jmx:"+getJmxProtocol()+":"+serviceServletUrl.substring(protocolIndex);
				if (!jmxServiceUrl.endsWith("/")) {
					jmxServiceUrl += "/";
				}
				jmxServiceUrl += "jmx";
			}
		}
		setJmxServiceUrl(jmxServiceUrl);
		config.getProperties().put(Config.JMX_SERVICE_URL, jmxServiceUrl);

		// configure mBeans
		if (getMBeans() != null && !getMBeans().isEmpty()) {
			config.getObjects().put(Config.M_BEANS, getMBeans());
		}
	}

	private void configureServiceList(Config config, String key, List<ServiceDefinition> services) throws Exception {
		LOG.debug("Configuring services for Message Entity " + getMessageEntity(config) + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		List<ServiceDefinition> serviceDefinitions = (List<ServiceDefinition>) config.getObject(key);
		if (serviceDefinitions == null) {
			config.getObjects().put(key, services);
		} else if (services != null) {
			LOG.debug("Services already exist.  Adding additional services");
			serviceDefinitions.addAll(services);
		}

		String serviceServletUrl = Core.getCurrentContextConfig().getProperty(Config.SERVICE_SERVLET_URL);
		// if it's empty, then we want to be able to inherit it from the parent configuration
		if (!StringUtils.isEmpty(serviceServletUrl)) {
			config.getObjects().put(Config.SERVICE_SERVLET_URL, serviceServletUrl);
		}
		for (Iterator iter = services.iterator(); iter.hasNext();) {
			ServiceDefinition serviceDef = (ServiceDefinition) iter.next();
			serviceDef.validate();
		}
	}

	protected void registerOptionalDefaultServices(Config config) throws Exception {
		LOG.debug("Checking for optional default workgroup and user service to load from the embedded plugin");
		if (isUseDefaultUserService()) {
			registerEmbeddedDefaultUserService();
		}
		if (isUseDefaultWorkgroupService()) {
			registerEmbeddedDefaultWorkgroupService();
		}
	}

	protected void registerEmbeddedDefaultUserService() throws Exception {
		JavaServiceDefinition serviceDef = new JavaServiceDefinition();
		serviceDef.setServiceName(new QName(KEWServiceLocator.USER_SERVICE));
		serviceDef.setService(KEWServiceLocator.getBean(KEWServiceLocator.OPTIONAL_EMBEDDED_USER_SERVICE));
		serviceDef.validate();
		KSBServiceLocator.getServiceDeployer().registerService(serviceDef, false);
	}

	protected void registerEmbeddedDefaultWorkgroupService() throws Exception {
		JavaServiceDefinition serviceDef = new JavaServiceDefinition();
		serviceDef.setServiceName(new QName(KEWServiceLocator.WORKGROUP_SRV));
		serviceDef.setService(KEWServiceLocator.getBean(KEWServiceLocator.OPTIONAL_EMBEDDED_WORKGROUP_SERVICE));
		serviceDef.validate();
		KSBServiceLocator.getServiceDeployer().registerService(serviceDef, false);
	}

	protected void configureInjectedOverrideServices(ResourceLoader embeddedClientServiceRL) {
		if (this.getOverrideServices() == null) {
			return;
		}
//		ResourceLoader embeddedClientServiceRL = GlobalResourceLoader.getResourceLoader(new QName(ResourceLoader.EMBEDDED_CLIENT_APP_RESOURCE_LOADER));
		//can be null if they've injected they're own RL that doesn't follow our name
		if (embeddedClientServiceRL == null) {
			return;
		}
		if (! (embeddedClientServiceRL instanceof BaseResourceLoader)) {
			LOG.info("Client application has injected its own implmentation of ResourceLoader to override workflow servcies");
			return;
		}
		ServiceLocator sl = ((BaseResourceLoader)embeddedClientServiceRL).getServiceLocator();
		if (! (sl instanceof SimpleServiceLocator)) {
			LOG.info("Client application has used its own implementation of ServiceLocator to override workflow services");
			return;
		}
		for (ServiceHolder serviceHolder : this.getOverrideServices()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loading override service " + serviceHolder.getServiceName() + " " + serviceHolder.getService());
			}
			((SimpleServiceLocator)sl).addService(serviceHolder.getServiceName(), serviceHolder.getService());
		}
	}

	protected void addServiceDefinitionsToRegistry(List<ServiceDefinition> serviceDefinition) {

	}

	public String getClientProtocol() {
		return clientProtocol;
	}

	public void setClientProtocol(String clientProtocol) {
		this.clientProtocol = clientProtocol;
	}

	public String getJmxProtocol() {
		return jmxProtocol;
	}

	public void setJmxProtocol(String jmxProtocol) {
		this.jmxProtocol = jmxProtocol;
	}

	public String getJmxServiceUrl() {
		return jmxServiceUrl;
	}

	public void setJmxServiceUrl(String jmxServiceUrl) {
		this.jmxServiceUrl = jmxServiceUrl;
	}

	public String getKeystoreAlias() {
		return keystoreAlias;
	}

	public void setKeystoreAlias(String keystoreAlias) {
		this.keystoreAlias = keystoreAlias;
	}

	public String getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public Map getMBeans() {
		return mBeans;
	}

	public void setMBeans(Map beans) {
		mBeans = beans;
	}

	public List<ServiceHolder> getOverrideServices() {
		return overrideServices;
	}

	public void setOverrideServices(List<ServiceHolder> overrideServices) {
		this.overrideServices = overrideServices;
	}

	public PluginRegistry getPluginRegistry() {
		return pluginRegistry;
	}

	public void setPluginRegistry(PluginRegistry pluginRegistry) {
		this.pluginRegistry = pluginRegistry;
	}

	public boolean isRunEmbeddedServer() {
		return runEmbeddedServer;
	}

	public void setRunEmbeddedServer(boolean runEmbeddedServer) {
		this.runEmbeddedServer = runEmbeddedServer;
	}

	public List<ServiceDefinition> getServices() {
		return services;
	}

	public void setServices(List<ServiceDefinition> services) {
		this.services = services;
	}

	public String getServiceServletUrl() {
		return serviceServletUrl;
	}

	public void setServiceServletUrl(String serviceServletUrl) {
		this.serviceServletUrl = serviceServletUrl;
	}

	public boolean isUseDefaultUserService() {
		return useDefaultUserService;
	}

	public void setUseDefaultUserService(boolean useDefaultUserService) {
		this.useDefaultUserService = useDefaultUserService;
	}

	public boolean isUseDefaultWorkgroupService() {
		return useDefaultWorkgroupService;
	}

	public void setUseDefaultWorkgroupService(boolean useDefaultWorkgroupService) {
		this.useDefaultWorkgroupService = useDefaultWorkgroupService;
	}

	public String getWebserviceRetry() {
		return webserviceRetry;
	}

	public void setWebserviceRetry(String webserviceRetry) {
		this.webserviceRetry = webserviceRetry;
	}

	public String getWebservicesUrl() {
		return webservicesUrl;
	}

	public void setWebservicesUrl(String webservicesUrl) {
		this.webservicesUrl = webservicesUrl;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataSourceJndiName() {
		return dataSourceJndiName;
	}

	public void setDataSourceJndiName(String jndiDatasourceLocation) {
		this.dataSourceJndiName = jndiDatasourceLocation;
	}



}
