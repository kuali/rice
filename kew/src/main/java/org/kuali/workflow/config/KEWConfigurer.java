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
import java.util.LinkedList;
import java.util.List;

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
import org.kuali.workflow.ojb.OjbConfigurer;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.messaging.JavaServiceDefinition;

/**
 * Configures the KEW Rice module.  KEW module initiation proceeds as follows:
 *
 * <ol>
 *   <li>Parse and load configuration for:</li>
 *     <ul>
 *       <li>Client Protocol</li>
 *       <li>Database</li>
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

	private String clientProtocol;

	private DataSource dataSource;
	private String dataSourceJndiName;

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

	public String getClientProtocol() {
		return clientProtocol;
	}

	public void setClientProtocol(String clientProtocol) {
		this.clientProtocol = clientProtocol;
	}

	public boolean isRunEmbeddedServer() {
		return runEmbeddedServer;
	}

	public void setRunEmbeddedServer(boolean runEmbeddedServer) {
		this.runEmbeddedServer = runEmbeddedServer;
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
