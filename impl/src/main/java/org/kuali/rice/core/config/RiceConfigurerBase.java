/*
 * Copyright 2010 The Kuali Foundation
 *
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
package org.kuali.rice.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.event.AfterStartEvent;
import org.kuali.rice.core.config.event.AfterStopEvent;
import org.kuali.rice.core.config.event.BeforeStartEvent;
import org.kuali.rice.core.config.event.BeforeStopEvent;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.config.logging.Log4jLifeCycle;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.resourceloader.RootResourceLoaderLifecycle;
import org.kuali.rice.core.resourceloader.SpringLoader;
import org.kuali.rice.core.util.RiceConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * A common base class that can be used to implement classes which are used to configure
 * various Kuali Rice instances.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class RiceConfigurerBase extends BaseCompositeLifecycle implements Configurer, InitializingBean, DisposableBean, ApplicationListener, BeanFactoryAware {

	private static final Logger LOG = Logger.getLogger(RiceConfigurerBase.class);

	private String environment = "dev";
	private String serviceNamespace;

	private Config rootConfig;
	private ResourceLoader rootResourceLoader;
	private Properties properties;
	private List<String> configLocations = new ArrayList<String>();
	private List<String> additionalSpringFiles = new ArrayList<String>();
	private BeanFactory beanFactory;
	
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
	    notify(new BeforeStartEvent());
		initializeFullConfiguration();
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
	 * <p>The default implementation loops over the list of modules and gets the resource loader to register for
	 * each one.
	 *
	 * @throws Exception
	 */
	protected void addModulesResourceLoaders() throws Exception {
		for (ModuleConfigurer module: modules) {
			GlobalResourceLoader.addResourceLoader(module.getResourceLoaderToRegister());
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
		LOG.info("Stopping Rice...");
	    notify(new BeforeStopEvent());
	    super.stop();
	    GlobalResourceLoader.stop();
	    LOG.info("...Rice stopped successfully.");
	}

	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
	 */
	protected List<Lifecycle> loadLifecycles() throws Exception {
		 List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		 if (isConfigureLogging()) {
			 lifecycles.add(new Log4jLifeCycle());
		 }
		 for (ModuleConfigurer module : this.modules) {
			 lifecycles.add(module);
		 }
		 return lifecycles;
	}
	
	protected boolean isConfigureLogging() {
		return ConfigContext.getCurrentContextConfig().getBooleanProperty(RiceConstants.RICE_LOGGING_CONFIGURE, false);
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
            else if (event instanceof ContextClosedEvent && !super.isStarted()) 
            {
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
	protected void initializeFullConfiguration() throws Exception {
		if ( LOG.isInfoEnabled() ) {
			LOG.info("Starting Rice configuration for environment " + this.environment);
		}
		Config currentConfig = parseConfig();
		initializeBaseConfiguration(currentConfig);
		parseModuleConfigs(currentConfig);
	}
	
	/**
	 * Initializes any base parameters within the Rice configuration system.  Generally, this is
	 * used to put properties that are injected into this configurer into the Rice ConfigContext.
	 * 
	 * <p>This superclass configures the environment code, subclasses can override this but should
	 * be sure to call super.initializeBaseConfiguration.
	 */
	protected void initializeBaseConfiguration(Config currentConfig) throws Exception {
		configureEnvironment(currentConfig);
		configureServiceNamespace(currentConfig);

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

	public Config getRootConfig() {
		return this.rootConfig;
	}

	public void setRootConfig(Config rootConfig) {
		this.rootConfig = rootConfig;
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

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
}
