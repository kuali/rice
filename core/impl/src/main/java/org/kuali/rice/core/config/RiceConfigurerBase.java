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
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.resourceloader.RootResourceLoaderLifecycle;
import org.kuali.rice.core.resourceloader.SpringLoader;
import org.kuali.rice.core.util.RiceConstants;
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
public abstract class RiceConfigurerBase extends BaseCompositeLifecycle implements Configurer, InitializingBean, DisposableBean, ApplicationListener<ApplicationEvent> {

	private static final Logger LOG = Logger.getLogger(RiceConfigurerBase.class);

	private Config rootConfig;
	private ResourceLoader rootResourceLoader;
	private Properties properties;
	private List<String> additionalSpringFiles = new ArrayList<String>();
	
	private List<ModuleConfigurer> modules = new LinkedList<ModuleConfigurer>();

	/***
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}

	/***
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	@Override
	public void destroy() throws Exception {
		stop();
	}

	/***
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#start()
	 */
	@Override
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
	@Override
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
	@Override
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
    @Override
	public void onApplicationEvent(ApplicationEvent event) {
    	//Event raised when an ApplicationContext gets initialized or refreshed. 
        if (event instanceof ContextRefreshedEvent) {
            notify(new AfterStartEvent());
        }
        //Event raised when an ApplicationContext gets closed. 
        else if (event instanceof ContextClosedEvent && !super.isStarted()) 
        {
        	notify(new AfterStopEvent());
        }
    }
    
    protected void notify(RiceConfigEvent event) {
        for (ModuleConfigurer module : modules) {
            module.onEvent(event);
        }
    }

	protected void initializeFullConfiguration() throws Exception {
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

	}

	protected Config parseConfig() throws Exception {
		if (this.rootConfig == null) {
		    this.rootConfig = new JAXBConfigImpl();
		}
		// append current root config to existing core config if config has already been initialized
		Config currentRootConfig = ConfigContext.getCurrentContextConfig();
		if (currentRootConfig != null) {
			currentRootConfig.putConfig(rootConfig);
			this.rootConfig = currentRootConfig;
		} else {
			ConfigContext.init(this.rootConfig);
		}
		
		if (this.properties != null) {
		    this.rootConfig.putProperties(this.properties);
		}
		// add the RiceConfigurer into the root ConfigContext for access later by the application
		
		this.rootConfig.putObject( RiceConstants.RICE_CONFIGURER_CONFIG_NAME, this );
		return this.rootConfig;
	}

	protected void parseModuleConfigs(Config rootConfig) throws Exception {
		for (ModuleConfigurer module : this.modules) {
			// TODO should there be a hierarchy here?
			Config moduleConfig = module.loadConfig(rootConfig);
			if (moduleConfig != null) {
				rootConfig.putConfig(moduleConfig);				
			}
		}
	}

	public String getEnvironment() {
		return this.rootConfig.getProperty("environment");
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
