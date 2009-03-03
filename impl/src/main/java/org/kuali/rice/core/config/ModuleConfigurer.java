/*
 * Copyright 2007 The Kuali Foundation
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.springframework.beans.factory.InitializingBean;

public abstract class ModuleConfigurer extends BaseCompositeLifecycle implements Configurer, InitializingBean {
    /**
     * Protected logger for use by subclasses
     */
    protected final Logger LOG = Logger.getLogger(getClass());

	public static final String LOCAL_RUN_MODE = "local";
	public static final String EMBEDDED_RUN_MODE = "embedded";
	public static final String REMOTE_RUN_MODE = "remote";
	protected final List<String> VALID_RUN_MODES = new ArrayList<String>();
	
	private String runMode = LOCAL_RUN_MODE;	
    protected String moduleName = "!!!UNSET!!!";	
	protected String webModuleConfigName = "";
	protected String webModuleConfigurationFiles = "";
	protected boolean webInterface = false;
	
	/**
	 * 
	 */
	public ModuleConfigurer() {
		VALID_RUN_MODES.add( LOCAL_RUN_MODE );
		VALID_RUN_MODES.add( EMBEDDED_RUN_MODE );
		VALID_RUN_MODES.add( REMOTE_RUN_MODE );
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
			if ( StringUtils.isBlank( webModuleConfigurationFiles ) ) {
				setWebModuleConfigurationFiles( "/" + getModuleName().toLowerCase() + "/WEB-INF/struts-config.xml" );
			}
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

	
	public abstract Config loadConfig(Config parentConfig) throws Exception;

	/**
	 * 
	 * This method returns a comma separated string of spring file locations for this module.
	 * 
	 * @throws Exception
	 */
	public abstract String getSpringFileLocations() throws Exception;
	
	/**
	 * 
	 * This method returns a resource loader that this module might want to register with the global resource loader.
	 * 
	 * @throws Exception
	 */
	public ResourceLoader getResourceLoaderToRegister() throws Exception{
		return null;
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

}
