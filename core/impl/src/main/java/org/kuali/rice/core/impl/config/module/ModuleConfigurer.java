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

package org.kuali.rice.core.impl.config.module;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.module.Configurer;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.lifecycle.BaseCompositeLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.BaseResourceLoader;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoaderContainer;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.util.RiceConstants;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

// FIXME: this class must be put in an API module somehow
public class ModuleConfigurer extends BaseCompositeLifecycle implements Configurer, InitializingBean, DisposableBean, ApplicationListener<ApplicationEvent>, ServletContextAware {
    protected final Logger LOG = Logger.getLogger(getClass());

    private List<RunMode> validRunModes = new ArrayList<RunMode>();
	private boolean hasWebInterface;
	
	private Properties properties = new Properties();
	private String moduleName;
	private ServletContext servletContext;
	
	@Override
	public final void start() throws Exception {
		super.start();
	}
	
	@Override
	public final void afterPropertiesSet() throws Exception {
		validateConfigurerState();
		addToConfig();
		initializeResourceLoaders();
		start();
	}
	
	@Override
	public final void stop() throws Exception {
		super.stop();
	}
	
	@Override
	public final void destroy() throws Exception {
        stop();
        //FIXME: attempting to see if this fixes the ksb tests...
        GlobalResourceLoader.stop();
	}
	
	@Override
	public List<Lifecycle> loadLifecycles() throws Exception {
		return Collections.emptyList();
		//override in subclasses
	}
	
	public RunMode getRunMode() {
		String propertyName = getModuleName().toLowerCase() + ".mode";
		return RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(propertyName));
	}
	
	public String getWebModuleConfigName() {
		return "config/" + getModuleName().toLowerCase();
	}
	
	public String getWebModuleConfigurationFiles() {
		return ConfigContext.getCurrentContextConfig().getProperty("rice." + getModuleName().toLowerCase() + ".struts.config.files");
	}
	
	/**
	 * This base implementation returns true when the module has a web interface and the
	 * runMode is "local".
	 * 
	 * Subclasses can override this method if there are different requirements for inclusion
	 * of the web UI for the module.
	 */
	public boolean shouldRenderWebInterface() {
		return hasWebInterface() &&	getRunMode().equals( RunMode.LOCAL );
	}
	
	public boolean isSetSOAPServicesAsDefault() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("rice." + getModuleName().toLowerCase() + ".set.soap.services.as.default")).booleanValue();
	}
	
	public boolean isExposeServicesOnBus() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("rice." + getModuleName().toLowerCase() + ".expose.services.on.bus")).booleanValue();
	}
	
	public boolean isIncludeUserInterfaceComponents() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("rice." + getModuleName().toLowerCase() + ".include.user.interface.components")).booleanValue();
	}

	public String getWebModuleBaseUrl() {
		return ConfigContext.getCurrentContextConfig().getProperty(getModuleName().toLowerCase() + ".url");
	}
	
	@Override
	public List<String> getPrimarySpringFiles() {
		return Collections.singletonList(getDefaultSpringBeansPath(getDefaultConfigPackagePath()));
	}

	public boolean hasWebInterface() {
		return this.hasWebInterface;
	}

	public void setHasWebInterface(boolean hasWebInterface) {
		this.hasWebInterface = hasWebInterface;
	}
	
	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List<String> getAdditionalSpringFiles() {
		final String files = ConfigContext.getCurrentContextConfig().getProperty("rice." + getModuleName() + ".additionalSpringFiles");
		return files == null ? Collections.<String>emptyList() : parseFileList(files);
	}
	
	private List<String> parseFileList(String files) {
		final List<String> parsedFiles = new ArrayList<String>();
		for (String file : Arrays.asList(files.split(","))) {
			final String trimmedFile = file.trim();
			if (!trimmedFile.isEmpty()) {
				parsedFiles.add(trimmedFile);	
			}
		}
		
		return parsedFiles;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
    
    /* helper methods for constructors */
    protected String getDefaultConfigPackagePath() {
    	return "classpath:org/kuali/rice/" + getModuleName().toLowerCase() + "/config/";
    }
    protected String getDefaultSpringBeansPath(String configPackagePath) {
        return configPackagePath + getModuleName().toUpperCase() + "SpringBeans.xml"; 
    }
    
	public List<RunMode> getValidRunModes() {
		return this.validRunModes;
	}

	public void setValidRunModes(List<RunMode> validRunModes) {
		this.validRunModes = validRunModes;
	}
	
	@Override
	public final void validateConfigurerState() {
		if (StringUtils.isBlank(this.moduleName)) {
			throw new IllegalStateException("the module name for this module has not been set");
		}
		
		if (CollectionUtils.isEmpty(this.validRunModes)) {
			throw new IllegalStateException("the valid run modes for this module has not been set");
		}
		
		validateRunMode();
		
		doAdditonalConfigurerValidations();
	}
	
	private void validateRunMode() {
		if ( !validRunModes.contains( getRunMode() ) ) {
			throw new IllegalArgumentException( "Invalid run mode for the " + this.getClass().getSimpleName() + ": " + getRunMode() + " - Valid Values are: " + validRunModes );
		}
	}
	
	protected void doAdditonalConfigurerValidations() {
		//override in subclasses
	}
	
	
	/**
	 * This method does the following: 
	 * <ol>
	 *  <li>Places all module specific configurations into the root config</li>
	 *  <li>Adds any additional properties passed into the config into the root config</li>
	 *  <li>Adds any items a subclass wants to put into the config</li>
	 * </ol>
	 */
	@Override
	public final void addToConfig() {
		
		if (this.properties != null) {
			ConfigContext.getCurrentContextConfig().putProperties(this.properties);
		}
		
		registerConfigurerWithConfig();
		addAdditonalToConfig();
	}
	
	protected void addAdditonalToConfig() {
		//override in subclasses
	}
	
	/**
	 * This is a bit of a hack.... fix me
	 *
	 */
	private void registerConfigurerWithConfig() {
		@SuppressWarnings("unchecked")
		Collection<ModuleConfigurer> configurers = (Collection<ModuleConfigurer>) ConfigContext.getCurrentContextConfig().getObject("ModuleConfigurers");
		if (configurers == null) {
			configurers = new ArrayList<ModuleConfigurer>();
		}
		configurers.add(this);
		
		ConfigContext.getCurrentContextConfig().putObject("ModuleConfigurers", configurers);
	}
	
	@Override
	public final void initializeResourceLoaders() throws Exception {
		List<String> files = new ArrayList<String>();
		files.addAll(getPrimarySpringFiles());
		files.addAll(getAdditionalSpringFiles());
		
		ResourceLoader rootResourceLoader = GlobalResourceLoader.getResourceLoader();
		if (rootResourceLoader == null) {
			rootResourceLoader = createRootResourceLoader();
		}
		
		if (!files.isEmpty()) {
			ResourceLoader rl = RiceResourceLoaderFactory.createRootRiceResourceLoader(servletContext, files, getModuleName());
			rl.start();
			GlobalResourceLoader.addResourceLoader(rl);
		}
		
		final Collection<ResourceLoader> rls = getResourceLoadersToRegister();
		
		for (ResourceLoader rl : rls) {
			GlobalResourceLoader.addResourceLoader(rl);
		}
	}
	
	protected Collection<ResourceLoader> getResourceLoadersToRegister() throws Exception {
		return Collections.emptyList();
		//override in subclasses
	}
	
	private ResourceLoader createRootResourceLoader() throws Exception {
		final ResourceLoaderContainer container = new ResourceLoaderContainer(new QName(ConfigContext.getCurrentContextConfig().getServiceNamespace(), RiceConstants.ROOT_RESOURCE_LOADER_CONTAINER_NAME));
		ResourceLoader rootResourceLoader = new BaseResourceLoader(new QName(ConfigContext.getCurrentContextConfig().getServiceNamespace(), RiceConstants.DEFAULT_ROOT_RESOURCE_LOADER_NAME));
		
		container.addResourceLoader(rootResourceLoader);
		GlobalResourceLoader.addResourceLoader(container);
		GlobalResourceLoader.start();
		
		return container;
	}

	@Override
	public final void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			doContextStartedLogic();
		} else if (event instanceof ContextClosedEvent) {
			doContextStoppedLogic();
		}
	}

	@Override
	public final void doContextStartedLogic() {
		doAdditionalContextStartedLogic();
	}

	@Override
	public final void doContextStoppedLogic() {
		doAdditionalContextStoppedLogic();
	}
	
	protected void doAdditionalContextStartedLogic() {
		//override in subclasses
	}

	protected void doAdditionalContextStoppedLogic() {
		//override in subclasses
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
