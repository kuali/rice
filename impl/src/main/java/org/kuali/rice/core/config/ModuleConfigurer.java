/*
 * Copyright 2007-2008 The Kuali Foundation
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
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.config.event.RiceConfigEventListener;
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.springframework.beans.factory.InitializingBean;

public abstract class ModuleConfigurer extends BaseCompositeLifecycle implements Configurer, InitializingBean, RiceConfigEventListener {
    /**
     * Protected logger for use by subclasses
     */
    protected final Logger LOG = Logger.getLogger(getClass());

	public static final String LOCAL_RUN_MODE = "local";
	public static final String EMBEDDED_RUN_MODE = "embedded";
	public static final String REMOTE_RUN_MODE = "remote";
	protected final List<String> VALID_RUN_MODES = new ArrayList<String>();
	
	private String runMode = LOCAL_RUN_MODE;	
    private String moduleName = "!!!UNSET!!!";	
	protected String webModuleConfigName = "";
	protected String webModuleConfigurationFiles = "";
	protected String webModuleBaseUrl = "";
	protected boolean webInterface = false;
    protected boolean testMode;
    protected String springFileLocations = "";
    protected String resourceLoaderName;
	
	/**
	 * 
	 */
	public ModuleConfigurer() {
		VALID_RUN_MODES.add( LOCAL_RUN_MODE );
		VALID_RUN_MODES.add( EMBEDDED_RUN_MODE );
		VALID_RUN_MODES.add( REMOTE_RUN_MODE );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
	 */
	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		return new ArrayList<Lifecycle>(0);
	}
	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if ( getModuleName().equals( "!!!UNSET!!!" ) ) {
			throw new IllegalArgumentException( "Module Name must be given for each ModuleConfigurer instance" );
		}
		if ( hasWebInterface() ) {
			if ( StringUtils.isBlank( webModuleConfigName ) ) {
				setWebModuleConfigName( "config/" + getModuleName().toLowerCase() );
			}
//			if ( StringUtils.isBlank( webModuleConfigurationFiles ) ) {
//				setWebModuleConfigurationFiles( "/" + getModuleName().toLowerCase() + "/WEB-INF/struts-config.xml" );
//			}
		}
	}
	
	
	
	public String getRunMode() {
		return this.runMode;
	}

	public void setRunMode(String runMode) {
		runMode = runMode.trim();
		if ( !VALID_RUN_MODES.contains( runMode ) ) {
			throw new IllegalArgumentException( "Invalid run mode for the " + this.getClass().getSimpleName() + ": " + runMode + " - Valid Values are: " + VALID_RUN_MODES );
		}
		this.runMode = runMode;
	}

	
	public Config loadConfig(Config parentConfig) throws Exception {
    	if ( LOG.isInfoEnabled() ) {
    		LOG.info("Starting configuration of " + getModuleName() + " for service namespace " + parentConfig.getServiceNamespace());
    	}
		Config config = parentConfig;
        if (Boolean.valueOf(config.getProperty("rice." + getModuleName().toLowerCase() + ".testMode"))) {
            testMode = true;
        }		
		configureWebConfiguration(config);
		configureRunMode(config);
		return config;
	}

	protected void configureWebConfiguration( Config config ) throws Exception {
		if ( StringUtils.isBlank( getWebModuleConfigurationFiles() ) ) {
			if ( StringUtils.isBlank( config.getProperty( "rice." + getModuleName().toLowerCase() + ".struts.config.files" ) ) ) {
				setWebModuleConfigurationFiles( "/" + getModuleName().toLowerCase() + "/WEB-INF/struts-config.xml" );
			} else {
				setWebModuleConfigurationFiles( config.getProperty( "rice." + getModuleName().toLowerCase() + ".struts.config.files" ) );
			}
		}
		config.getProperties().put( "rice." + getModuleName().toLowerCase() + ".struts.config.files", getWebModuleConfigurationFiles() );
		if ( StringUtils.isBlank( getWebModuleBaseUrl() ) ) {
			if ( StringUtils.isBlank( config.getProperty( getModuleName().toLowerCase() + ".url" ) ) ) {
				setWebModuleBaseUrl( config.getProperty( "application.url" ) + "/" + getModuleName().toLowerCase() );
			} else {
				setWebModuleBaseUrl( config.getProperty( getModuleName().toLowerCase() + ".url" ) );
			}
		}
		config.getProperties().put( getModuleName().toLowerCase() + ".url", getWebModuleBaseUrl() );
		if ( StringUtils.isEmpty( getSpringFileLocations() ) ) {
			setSpringFileLocations( getDefaultSpringBeansPath(getDefaultConfigPackagePath() ) );
		}
	}
	
	/**
	 * Creates a configuration parameter for the run mode by appending the module name (in lower case) plus ".mode"
	 */
	protected void configureRunMode(Config config) {
		String propertyName = getModuleName().toLowerCase() + ".mode";
		config.getProperties().setProperty(propertyName, getRunMode());
	}
	
	/**
	 * 
	 * This method returns a comma separated string of spring file locations for this module.
	 * 
	 * @throws Exception
	 */
	public String getSpringFileLocations() throws Exception {
		return springFileLocations;
	}
	
    /* helper methods for constructors */
    protected String getDefaultConfigPackagePath() {
    	return "org/kuali/rice/" + getModuleName().toLowerCase() + "/config/";
    }
    protected String getDefaultSpringBeansPath(String configPackagePath) {
        return configPackagePath + getModuleName().toUpperCase() + "SpringBeans.xml"; 
    }
    public String getDefaultResourceLoaderName() {
        return getModuleName().toUpperCase() + "_SPRING_RESOURCE_LOADER";        
    }
    public QName getDefaultResourceLoaderQName() {
        return new QName(ConfigContext.getCurrentContextConfig().getServiceNamespace(), getDefaultResourceLoaderName());
    }
	
	/**
	 * 
	 * This method returns a resource loader that this module might want to register with the global resource loader.
	 * 
	 * @throws Exception
	 */
	public ResourceLoader getResourceLoaderToRegister() throws Exception{
		return null;
	}
	
    /**
     * Template method for creation of the module resource loader.  Subclasses should override
     * and return an appropriate resource loader for the module.  If 'null' is returned, no
     * resource loader is added to the lifecycles by default.  The caller {@link #loadLifecycles()}
     * implementation will add the ResourceLoader to the GlobalResourceLoader, so that it is not
     * necessary to do so in the subclass.
     * @return a resource loader for the module, or null
     */
    /**
     * Constructs a SpringResourceLoader from the appropriate Spring context resource and with the configured
     * resource loader name (and current context config service namespace)
     */
    protected ResourceLoader createResourceLoader() throws Exception {
        String context = getSpringFileLocations();
        ResourceLoader resourceLoader = new SpringResourceLoader(new QName(ConfigContext.getCurrentContextConfig().getServiceNamespace(), resourceLoaderName), context);
        return resourceLoader;
    }

	
	public void onEvent(RiceConfigEvent event) throws Exception {
		if ( LOG.isInfoEnabled() ) {
			LOG.info( "ModuleConfigurer.onEvent() called: " + event );
		}
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getWebModuleConfigName() {
		return this.webModuleConfigName;
	}

	public void setWebModuleConfigName(String webModuleConfigName) {
		this.webModuleConfigName = webModuleConfigName;
	}

	public String getWebModuleConfigurationFiles() {
		return this.webModuleConfigurationFiles;
	}

	public void setWebModuleConfigurationFiles(String webModuleConfigurationFiles) {
		this.webModuleConfigurationFiles = webModuleConfigurationFiles;
	}

	public boolean hasWebInterface() {
		return this.webInterface;
	}

	protected void setHasWebInterface(boolean webInterface) {
		this.webInterface = webInterface;
	}

	/**
	 * This base implementation returns true when the module has a web interface and the
	 * runMode is "local".
	 * 
	 * Subclasses can override this method if there are different requirements for inclusion
	 * of the web UI for the module.
	 */
	public boolean shouldRenderWebInterface() {
		return hasWebInterface() &&	getRunMode().equals( ModuleConfigurer.LOCAL_RUN_MODE );
	}
	
	/**
	 * @return the testMode
	 */
	public boolean isTestMode() {
		return this.testMode;
	}

	/**
	 * @param testMode the testMode to set
	 */
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	/**
	 * @param springFileLocations the springFileLocations to set
	 */
	public void setSpringFileLocations(String springFileLocations) {
		this.springFileLocations = springFileLocations;
	}

	/**
	 * @return the webModuleBaseUrl
	 */
	public String getWebModuleBaseUrl() {
		return this.webModuleBaseUrl;
	}

	/**
	 * @param webModuleBaseUrl the webModuleBaseUrl to set
	 */
	public void setWebModuleBaseUrl(String webModuleBaseUrl) {
		this.webModuleBaseUrl = webModuleBaseUrl;
	}

}
