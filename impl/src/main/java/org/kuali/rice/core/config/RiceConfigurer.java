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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.event.AfterStartEvent;
import org.kuali.rice.core.config.event.AfterStopEvent;
import org.kuali.rice.core.config.event.BeforeStartEvent;
import org.kuali.rice.core.config.event.BeforeStopEvent;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.resourceloader.RootResourceLoaderLifecycle;
import org.kuali.rice.core.resourceloader.SpringLoader;
import org.kuali.rice.core.security.credentials.CredentialsSourceFactory;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kcb.config.KCBConfigurer;
import org.kuali.rice.ken.config.KENConfigurer;
import org.kuali.rice.kew.config.KEWConfigurer;
import org.kuali.rice.kim.config.KIMConfigurer;
import org.kuali.rice.kns.config.KNSConfigurer;
import org.kuali.rice.ksb.messaging.config.KSBConfigurer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Used to configure common Rice configuration properties.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RiceConfigurer extends BaseCompositeLifecycle implements Configurer, InitializingBean, DisposableBean, ApplicationListener {

	private static final Logger LOG = Logger.getLogger(RiceConfigurer.class);

	private String environment = "dev";
	private String serviceNamespace;
	private DataSource dataSource;
	private DataSource nonTransactionalDataSource;
	private String platform;
	private UserTransaction userTransaction;
	private TransactionManager transactionManager;
    private String dataSourceJndiLocation;
    private String nonTransactionalDataSourceJndiLocation;
	private String userTransactionJndiLocation;
	private String transactionManagerJndiLocation;
	private CredentialsSourceFactory credentialsSourceFactory;
	

	private Config rootConfig;
	private ResourceLoader rootResourceLoader;
	private Properties properties;
	private List<String> configLocations = new ArrayList<String>();
	private List<String> additionalSpringFiles = new ArrayList<String>();

	private KSBConfigurer ksbConfigurer;
	private KNSConfigurer knsConfigurer;
	private KIMConfigurer kimConfigurer;
	private KCBConfigurer kcbConfigurer;
	private KEWConfigurer kewConfigurer;
	private KENConfigurer kenConfigurer;
	
	private List<ModuleConfigurer> modules = new LinkedList<ModuleConfigurer>();

	/***
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		start();
	}

	/***
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		stop();
	}

	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#start()
	 */
	public void start() throws Exception {
		//Add the configurers to modules list in the desired sequence.
		// and at the beginning if any other modules were specified
		int index = 0;
		if(getKsbConfigurer()!=null) modules.add(index++,getKsbConfigurer());
		if(getKnsConfigurer()!=null) modules.add(index++,getKnsConfigurer());
		if(getKimConfigurer()!=null) modules.add(index++,getKimConfigurer());
		if(getKcbConfigurer()!=null) modules.add(index++,getKcbConfigurer());
		if(getKewConfigurer()!=null) modules.add(index++,getKewConfigurer());
		if(getKenConfigurer()!=null) modules.add(index++,getKenConfigurer());

	    notify(new BeforeStartEvent());
		initializeConfiguration();
		initializeResourceLoaders();
		super.start();
		addModulesResourceLoaders();
		if (getRootResourceLoader() != null	) {
			if (!getRootResourceLoader().isStarted()) {
				getRootResourceLoader().start();
			}
			GlobalResourceLoader.addResourceLoader(getRootResourceLoader());
		}
	}
	
	/**
	 * 
	 * This method initializes root resource loader and spring context.
	 * 
	 * @throws Exception
	 */
	private void initializeResourceLoaders() throws Exception {
		(new RootResourceLoaderLifecycle(getRootResourceLoader())).start();
		loadSpringContext();
	}

	/**
	 * 
	 * This method decides the sequence of module resource loaders to be added to global resource loader (GRL).
	 * It asks the individual module configurers for the resource loader they want to register and adds them to GRL.
	 * 
	 * @throws Exception
	 */
	private void addModulesResourceLoaders() throws Exception {
		if(getKewConfigurer()!=null){
			// TODO: Check - In the method getResourceLoaderToRegister of KewConfigurer, 
			// does the call registry.start() depend on the preceding line GlobalResourceLoader.addResourceLoader(coreResourceLoader)?
			// Ideally we would like to register the resource loader into GRL over here
			getKewConfigurer().getResourceLoaderToRegister();
		}
		if(getKsbConfigurer()!=null){
			GlobalResourceLoader.addResourceLoader(getKsbConfigurer().getResourceLoaderToRegister());
		}
	}

	/**
	 * 
	 * This method:
	 * 1) Creates a spring application context, using the spring files from the modules. 
	 * 2) Wraps the context in a ResourceLoader and adds it to GRL. 
	 * 
	 * @throws Exception
	 */
	public ResourceLoader loadSpringContext() throws Exception {
		String springFileLocations = "";
		for(ModuleConfigurer module: modules){
			if(StringUtils.isNotBlank(module.getSpringFileLocations())) 
				springFileLocations += module.getSpringFileLocations()+SpringLoader.SPRING_SEPARATOR_CHARACTER;
		}
		for (String springFile : additionalSpringFiles) {
			springFileLocations += springFile + SpringLoader.SPRING_SEPARATOR_CHARACTER;	
		}
		ResourceLoader resourceLoader = RiceResourceLoaderFactory.createRootRiceResourceLoader(springFileLocations);
		resourceLoader.start();
		GlobalResourceLoader.addResourceLoader(resourceLoader);
		return resourceLoader;
	}

	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#stop()
	 */
	public void stop() throws Exception {
	    notify(new BeforeStopEvent());
	    super.stop();
	    GlobalResourceLoader.stop();
	}

	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
	 */
	protected List<Lifecycle> loadLifecycles() throws Exception {
		 List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		 for (ModuleConfigurer module : this.modules) {
			 lifecycles.add(module);
		 }
		 return lifecycles;
	}
	
	/***
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
    public void onApplicationEvent(ApplicationEvent event) {
        try {
        	//Event raised when an ApplicationContext gets initialized or refreshed. 
            if (event instanceof ContextRefreshedEvent) {
                notify(new AfterStartEvent());
            }
            //Event raised when an ApplicationContext gets closed. 
            else if (event instanceof ContextClosedEvent) {
                notify(new AfterStopEvent());
            }
        } catch (Exception e) {
            throw new RiceRuntimeException(e);
        }
    }
    
    protected void notify(RiceConfigEvent event) throws Exception {
        for (ModuleConfigurer module : modules) {
            module.onEvent(event);
        }
    }

	@SuppressWarnings("unchecked")
	protected void initializeConfiguration() throws Exception {
		if ( LOG.isInfoEnabled() ) {
			LOG.info("Starting Rice configuration for service namespace " + this.serviceNamespace);
		}
		Config currentConfig = parseConfig();
		configureEnvironment(currentConfig);
		configureServiceNamespace(currentConfig);
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
		Config currentRootConfig = ConfigContext.getRootConfig();
		if (currentRootConfig != null) {
			currentRootConfig.getProperties().putAll(this.rootConfig.getProperties());
			this.rootConfig = currentRootConfig;
		} else {
			ConfigContext.init(this.rootConfig);
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
		// add the RiceConfigurer into the root ConfigContext for access later by the application
		this.rootConfig.getObjects().put( RiceConstants.RICE_CONFIGURER_CONFIG_NAME, this );
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

	protected void configureServiceNamespace(Config config) {
		if (!StringUtils.isBlank(this.serviceNamespace)) {
			config.getProperties().put(Config.SERVICE_NAMESPACE, this.serviceNamespace);
		}
	}

	protected void configurePlatform(Config config) {
		if (!StringUtils.isBlank(this.platform)) {
			String platformClassName = "org.kuali.rice.core.database.platform."+this.platform+"Platform";
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
        if (this.nonTransactionalDataSource != null) {
            config.getObjects().put(Config.NON_TRANSACTIONAL_DATASOURCE_OBJ, this.nonTransactionalDataSource);
        } else if (!StringUtils.isBlank(this.nonTransactionalDataSourceJndiLocation)) {
            config.getProperties().put(Config.NON_TRANSACTIONAL_DATASOURCE_JNDI, this.nonTransactionalDataSourceJndiLocation);
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

	public String getServiceNamespace() {
		return this.serviceNamespace;
	}

	public void setServiceNamespace(String ServiceNamespace) {
		this.serviceNamespace = ServiceNamespace;
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

    public DataSource getNonTransactionalDataSource() {
        return this.nonTransactionalDataSource;
    }

    public void setNonTransactionalDataSource(DataSource nonTransactionalDataSource) {
        this.nonTransactionalDataSource = nonTransactionalDataSource;
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

    public void setNonTransactionalDataSourceJndiLocation(String nonTransactionalDataSourceJndiLocation) {
        this.nonTransactionalDataSourceJndiLocation = nonTransactionalDataSourceJndiLocation;
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

	/**
	 * @return the kcbConfigurer
	 */
	public KCBConfigurer getKcbConfigurer() {
		return this.kcbConfigurer;
	}

	/**
	 * @param kcbConfigurer the kcbConfigurer to set
	 */
	public void setKcbConfigurer(KCBConfigurer kcbConfigurer) {
		this.kcbConfigurer = kcbConfigurer;
	}

	/**
	 * @return the kenConfigurer
	 */
	public KENConfigurer getKenConfigurer() {
		return this.kenConfigurer;
	}

	/**
	 * @param kenConfigurer the kenConfigurer to set
	 */
	public void setKenConfigurer(KENConfigurer kenConfigurer) {
		this.kenConfigurer = kenConfigurer;
	}

	/**
	 * @return the kewConfigurer
	 */
	public KEWConfigurer getKewConfigurer() {
		return this.kewConfigurer;
	}

	/**
	 * @param kewConfigurer the kewConfigurer to set
	 */
	public void setKewConfigurer(KEWConfigurer kewConfigurer) {
		this.kewConfigurer = kewConfigurer;
	}

	/**
	 * @return the kimConfigurer
	 */
	public KIMConfigurer getKimConfigurer() {
		return this.kimConfigurer;
	}

	/**
	 * @param kimConfigurer the kimConfigurer to set
	 */
	public void setKimConfigurer(KIMConfigurer kimConfigurer) {
		this.kimConfigurer = kimConfigurer;
	}

	/**
	 * @return the knsConfigurer
	 */
	public KNSConfigurer getKnsConfigurer() {
		return this.knsConfigurer;
	}

	/**
	 * @param knsConfigurer the knsConfigurer to set
	 */
	public void setKnsConfigurer(KNSConfigurer knsConfigurer) {
		this.knsConfigurer = knsConfigurer;
	}

	/**
	 * @return the ksbConfigurer
	 */
	public KSBConfigurer getKsbConfigurer() {
		return this.ksbConfigurer;
	}

	/**
	 * @param ksbConfigurer the ksbConfigurer to set
	 */
	public void setKsbConfigurer(KSBConfigurer ksbConfigurer) {
		this.ksbConfigurer = ksbConfigurer;
	}

	/**
	 * @return the modules
	 */
	public List<ModuleConfigurer> getModules() {
		return this.modules;
	}

	/**
	 * @param modules the modules to set
	 */
	public void setModules(List<ModuleConfigurer> modules) {
		this.modules = modules;
	}

	/**
	 * @return the additionalSpringFiles
	 */
	public List<String> getAdditionalSpringFiles() {
		return this.additionalSpringFiles;
	}

	/**
	 * @param additionalSpringFiles the additionalSpringFiles to set.  list members can be 
	 * filenames, or comma separated lists of filenames.
	 */
	@SuppressWarnings("unchecked")
	public void setAdditionalSpringFiles(List<String> additionalSpringFiles) {
		// check to see if we have a single string with comma separated values
		if (null != additionalSpringFiles && 
				additionalSpringFiles.size() >= 1) {
			
			// we'll shove these into a new list, so we can expand comma separated entries
			this.additionalSpringFiles = new ArrayList<String>();
			
			for (String fileName : additionalSpringFiles) {
				if (fileName.contains(",")) { // if it's comma separated
					this.additionalSpringFiles.addAll(new ArrayList<String>(Arrays.asList(fileName.split(","))));
				} else { // plain old filename
					this.additionalSpringFiles.add(fileName);
				}
			}
		} else {
			this.additionalSpringFiles = Collections.emptyList();
		}
	}	

}