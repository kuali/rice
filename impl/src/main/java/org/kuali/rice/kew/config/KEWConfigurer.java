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
package org.kuali.rice.kew.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.SimpleConfig;
import org.kuali.rice.core.config.logging.Log4jLifeCycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.kew.lifecycle.EmbeddedLifeCycle;
import org.kuali.rice.kew.plugin.PluginRegistry;
import org.kuali.rice.kew.plugin.PluginRegistryFactory;
import org.kuali.rice.kew.resourceloader.CoreResourceLoader;
import org.kuali.rice.kew.util.KEWConstants;


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
    private static final String ADDITIONAL_SPRING_FILES_PARAM = "kew.additionalSpringFiles";
    
	private String clientProtocol;

	private DataSource dataSource;
	private String dataSourceJndiName;
	
	@Override
	public String getSpringFileLocations(){
		String springFileLocations;
		if (KEWConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(ConfigContext.getCurrentContextConfig().getClientProtocol())) {
			springFileLocations = "";
		} else {
			springFileLocations = getEmbeddedSpringFileLocation();
		}

		return springFileLocations;
	}
	
    public String getEmbeddedSpringFileLocation(){
    	String springLocation = ConfigContext.getCurrentContextConfig().getAlternateSpringFile();
    	if (springLocation == null) {
    	    springLocation = "classpath:org/kuali/rice/kew/config/KEWSpringBeans.xml";
    	}
    	String additionalSpringFiles = ConfigContext.getCurrentContextConfig().getProperty(ADDITIONAL_SPRING_FILES_PARAM);
    	if(StringUtils.isNotEmpty(additionalSpringFiles) && 	additionalSpringFiles.contains(","))
    		StringUtils.split(additionalSpringFiles, ",");
    	String[] springLocations;
    	if (!StringUtils.isEmpty(additionalSpringFiles)) {
    		springLocations = new String[2];
    		springLocations[0] = "," + additionalSpringFiles;
    	}
    	return springLocation;
    }

	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		if (KEWConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(ConfigContext.getCurrentContextConfig().getClientProtocol())) {
			lifecycles.add(createThinClientLifecycle());
		} else {
			if (isStandaloneServer()) {
				lifecycles.add(new Log4jLifeCycle());
			}
			lifecycles.add(createEmbeddedLifeCycle());
		}
		return lifecycles;
	}

	protected boolean isStandaloneServer() {
		return new Boolean(ConfigContext.getCurrentContextConfig().getProperty("kew.standalone.server")).booleanValue();
	}

	/**
	 * TODO Because a lot of our lifecycles live behind the embedded plugin and the KEWConfigurer does not, this is a simple
	 * measure to load these without having to deal with the removal of the embedded plugin right away.
	 */
	protected Lifecycle createEmbeddedLifeCycle() throws Exception {
		return new EmbeddedLifeCycle();
	}

	protected Lifecycle createThinClientLifecycle() throws Exception {
		return new ThinClientLifecycle();
	}

	public Config loadConfig(Config parentConfig) throws Exception {
		LOG.info("Starting configuration of KEW for message entity " + getServiceNamespace(parentConfig));
		Config currentConfig = parseConfig(parentConfig);
		configureClientProtocol(currentConfig);
		configureDataSource(currentConfig);
		return currentConfig;
	}

	protected Config parseConfig(Config parentConfig) throws Exception {
		List<String> defaultConfigLocations = new ArrayList<String>();
		defaultConfigLocations.add(KEWConstants.DEFAULT_GLOBAL_CONFIG_LOCATION);
		defaultConfigLocations.add(KEWConstants.DEFAULT_APPLICATION_CONFIG_LOCATION);
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

	protected String getServiceNamespace(Config config) {
		if (StringUtils.isBlank(config.getServiceNamespace())) {
			throw new ConfigurationException("The 'message.entity' property was not properly configured.");
		}
		return config.getServiceNamespace();
	}

	protected void configureClientProtocol(Config config) {
		if (StringUtils.isBlank(clientProtocol)) {
			clientProtocol = config.getClientProtocol();
			if (clientProtocol == null) {
				clientProtocol = KEWConstants.WEBSERVICE_CLIENT_PROTOCOL;
			}
		}
		// from a client, LOCAL protocol is equivalent to EMBEDDED
		// TODO this was messing up the tests were LOCAL was actually being used
		/*if (KEWConstants.LOCAL_CLIENT_PROTOCOL.equals(clientProtocol)) {
			clientProtocol = KEWConstants.EMBEDDED_CLIENT_PROTOCOL;
		}*/
		if (!KEWConstants.CLIENT_PROTOCOLS.contains(clientProtocol)) {
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

	public ResourceLoader getResourceLoaderToRegister() throws Exception{
		// create the plugin registry
		PluginRegistry registry = null;
		String pluginRegistryEnabled = ConfigContext.getCurrentContextConfig().getProperty("plugin.registry.enabled");
		if (!StringUtils.isBlank(pluginRegistryEnabled) && Boolean.valueOf(pluginRegistryEnabled)) {
			registry = new PluginRegistryFactory().createPluginRegistry();
		}

		CoreResourceLoader coreResourceLoader = 
			new CoreResourceLoader(RiceResourceLoaderFactory.getSpringResourceLoader(), registry);
		coreResourceLoader.start();

		//wait until core resource loader is started to attach to GRL;  this is so startup
		//code can depend on other things hooked into GRL without incomplete KEW resources
		//messing things up.

		GlobalResourceLoader.addResourceLoader(coreResourceLoader);

		// now start the plugin registry if there is one
		if (registry != null) {
			registry.start();
			// the registry resourceloader is now being handled by the CoreResourceLoader
			//GlobalResourceLoader.addResourceLoader(registry);
		}
		return coreResourceLoader;
	}

	public String getClientProtocol() {
		return clientProtocol;
	}

	public void setClientProtocol(String clientProtocol) {
		this.clientProtocol = clientProtocol;
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
