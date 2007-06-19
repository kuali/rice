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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.RootResourceLoaderLifecycle;
import org.kuali.rice.security.credentials.CredentialsSourceFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Used to configure common Rice configuration properties.
 *
 * @author ewestfal
 *
 */
public class RiceConfigurer extends BaseCompositeLifecycle implements Configurer, InitializingBean, DisposableBean {

	private static final Logger LOG = Logger.getLogger(RiceConfigurer.class);

	private String environment = "dev";
	private String messageEntity;
	private DataSource dataSource;
	private String platform;
	private UserTransaction userTransaction;
	private TransactionManager transactionManager;
	private String dataSourceJndiLocation;
	private String userTransactionJndiLocation;
	private String transactionManagerJndiLocation;
	private CredentialsSourceFactory credentialsSourceFactory;
	

	private Config rootConfig;
	private ResourceLoader rootResourceLoader;
	private Properties properties;
	private List<String> configLocations;

	private List<ModuleConfigurer> modules = new LinkedList<ModuleConfigurer>();

	public void afterPropertiesSet() throws Exception {
		start();
	}

	public void destroy() throws Exception {
		stop();
	}

	public void start() throws Exception {
		initializeConfiguration();
		super.start();
		if (getRootResourceLoader() != null	) {
			if (!getRootResourceLoader().isStarted()) {
				getRootResourceLoader().start();
			}
			GlobalResourceLoader.addResourceLoaderFirst(getRootResourceLoader());
		}
	}

	protected List<Lifecycle> loadLifecycles() {
		 List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		 lifecycles.add(new RootResourceLoaderLifecycle(getRootResourceLoader()));
		 for (ModuleConfigurer module : this.modules) {
			 lifecycles.add(module);
		 }
		 return lifecycles;
	}

	@SuppressWarnings("unchecked")
	protected void initializeConfiguration() throws Exception {
		LOG.info("Starting Rice configuration for message entity " + this.messageEntity);
		Config currentConfig = parseConfig();
		configureEnvironment(currentConfig);
		configureMessageEntity(currentConfig);
		configureJta(currentConfig);
		configureDataSource(currentConfig);
		configurePlatform(currentConfig);
		configureCredentialsSourceFactory(currentConfig);
		parseModuleConfigs(currentConfig);
	}

	protected Config parseConfig() throws Exception {
		if (this.rootConfig == null) {
		    this.rootConfig = new SimpleConfig();
		}
		// append current root config to existing core config if config has already been initialized
		Config currentRootConfig = Core.getRootConfig();
		if (currentRootConfig != null) {
			currentRootConfig.getProperties().putAll(this.rootConfig.getProperties());
			this.rootConfig = currentRootConfig;
		} else {
			Core.init(this.rootConfig);
		}
		if (this.configLocations != null) {
			Config config = new SimpleConfig(this.configLocations, this.properties);
			config.parseConfig();
			// merge the configs
			// TODO with a refactoring of the config system, should we move toward a CompositeConfig?  THat way we can preserve the info about where this config was
			// loaded from instead of just copying the properties?
			this.rootConfig.getProperties().putAll(config.getProperties());
			this.rootConfig.getObjects().putAll(config.getObjects());
		} else if (this.properties != null) {
		    this.rootConfig.getProperties().putAll(this.properties);
		}
		return this.rootConfig;
	}
	
	protected void configureCredentialsSourceFactory(final Config rootConfig) {
		if (credentialsSourceFactory != null) {
			rootConfig.getObjects().put(Config.CREDENTIALS_SOURCE_FACTORY, this.credentialsSourceFactory);
		}
		
	}

	protected void parseModuleConfigs(Config rootConfig) throws Exception {
		for (ModuleConfigurer module : this.modules) {
			// TODO should there be a hierarchy here?
			Config moduleConfig = module.loadConfig(rootConfig);
			if (moduleConfig != null) {
			rootConfig.getProperties().putAll(moduleConfig.getProperties());
			rootConfig.getObjects().putAll(moduleConfig.getObjects());
		}
	}
	}

	protected void configureEnvironment(Config config) {
		if (!StringUtils.isBlank(this.environment)) {
			config.getProperties().put(Config.ENVIRONMENT, this.environment);
		}
	}

	protected void configureMessageEntity(Config config) {
		if (!StringUtils.isBlank(this.messageEntity)) {
			config.getProperties().put(Config.MESSAGE_ENTITY, this.messageEntity);
		}
	}

	protected void configurePlatform(Config config) {
		if (!StringUtils.isBlank(this.platform)) {
			String platformClassName = "edu.iu.uis.eden.database.platform."+this.platform+"Platform";
			config.getProperties().setProperty(Config.DATASOURCE_PLATFORM, platformClassName);
			config.getProperties().setProperty(Config.OJB_PLATFORM, this.platform);
		}
	}

	protected void configureDataSource(Config config) {
		if (this.dataSource != null) {
			config.getObjects().put(Config.DATASOURCE_OBJ, this.dataSource);
		} else if (!StringUtils.isBlank(this.dataSourceJndiLocation)) {
			config.getProperties().put(Config.DATASOURCE_JNDI, this.dataSourceJndiLocation);
		}
	}

	/**
	 * If the user injected JTA classes into this configurer, verify that both the
	 * UserTransaction and TransactionManager are set and then attach them to
	 * the configuration.
	 */
	protected void configureJta(Config config) {
		if (this.userTransaction != null) {
			config.getObjects().put(Config.USER_TRANSACTION_OBJ, this.userTransaction);
		}
		if (this.transactionManager != null) {
			config.getObjects().put(Config.TRANSACTION_MANAGER_OBJ, this.transactionManager);
		}
		if (!StringUtils.isEmpty(this.userTransactionJndiLocation)) {
			config.getProperties().put(Config.USER_TRANSACTION_JNDI, this.userTransactionJndiLocation);
		}
		if (!StringUtils.isEmpty(this.transactionManagerJndiLocation)) {
			config.getProperties().put(Config.TRANSACTION_MANAGER_JNDI, this.transactionManagerJndiLocation);
		}
		boolean userTransactionConfigured = this.userTransaction != null || !StringUtils.isEmpty(this.userTransactionJndiLocation);
		boolean transactionManagerConfigured = this.transactionManager != null || !StringUtils.isEmpty(this.transactionManagerJndiLocation);
		if (userTransactionConfigured && !transactionManagerConfigured) {
			throw new ConfigurationException("When configuring JTA, both a UserTransaction and a TransactionManager are required.  Only the UserTransaction was configured.");
		}
		if (transactionManagerConfigured && !userTransactionConfigured) {
			throw new ConfigurationException("When configuring JTA, both a UserTransaction and a TransactionManager are required.  Only the TransactionManager was configured.");
		}
	}

	public String getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getMessageEntity() {
		return this.messageEntity;
	}

	public void setMessageEntity(String messageEntity) {
		this.messageEntity = messageEntity;
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getPlatform() {
		return this.platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public UserTransaction getUserTransaction() {
		return this.userTransaction;
	}

	public void setUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

	public ResourceLoader getRootResourceLoader() {
		return this.rootResourceLoader;
	}

	public void setRootResourceLoader(ResourceLoader rootResourceLoader) {
		this.rootResourceLoader = rootResourceLoader;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List<String> getConfigLocations() {
		return this.configLocations;
	}

	public void setConfigLocations(List<String> configLocations) {
		this.configLocations = configLocations;
	}

	public void setDataSourceJndiLocation(String dataSourceJndiLocation) {
		this.dataSourceJndiLocation = dataSourceJndiLocation;
	}

	public String getTransactionManagerJndiLocation() {
		return this.transactionManagerJndiLocation;
	}

	public void setTransactionManagerJndiLocation(String transactionManagerJndiLocation) {
		this.transactionManagerJndiLocation = transactionManagerJndiLocation;
	}

	public String getUserTransactionJndiLocation() {
		return this.userTransactionJndiLocation;
	}

	public void setUserTransactionJndiLocation(String userTransactionJndiLocation) {
		this.userTransactionJndiLocation = userTransactionJndiLocation;
	}

	public List<ModuleConfigurer> getModules() {
		return this.modules;
	}

	public void setModules(List<ModuleConfigurer> modules) {
		this.modules = modules;
	}

	public Config getRootConfig() {
		return this.rootConfig;
	}

	public void setRootConfig(Config rootConfig) {
		this.rootConfig = rootConfig;
	}

	public CredentialsSourceFactory getCredentialsSourceFactory() {
		return credentialsSourceFactory;
	}

	public void setCredentialsSourceFactory(
			final CredentialsSourceFactory credentialsSourceFactory) {
		this.credentialsSourceFactory = credentialsSourceFactory;
	}	

}