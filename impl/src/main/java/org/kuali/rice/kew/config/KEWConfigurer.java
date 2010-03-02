/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.JAXBConfigImpl;
import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.SimpleConfig;
import org.kuali.rice.core.config.logging.Log4jLifeCycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.resourceloader.SpringLoader;
import org.kuali.rice.core.util.OrmUtils;
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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KEWConfigurer extends ModuleConfigurer {

	public static final String KEW_DATASOURCE_OBJ = "org.kuali.workflow.datasource";
	public static final String KEW_DATASOURCE_JNDI = "org.kuali.workflow.datasource.jndi.location";
    private static final String ADDITIONAL_SPRING_FILES_PARAM = "kew.additionalSpringFiles";
    
	private String clientProtocol;

	private DataSource dataSource;
	private String dataSourceJndiName;
	/**
	 * 
	 */
	public KEWConfigurer() {
		super();
		setModuleName( "KEW" );
		setHasWebInterface( true );
	}
	
	@Override
	public String getSpringFileLocations(){
		String springFileLocations;
		if (KEWConfigurer.REMOTE_RUN_MODE.equals(getRunMode()) || KEWConstants.WEBSERVICE_CLIENT_PROTOCOL.equals(ConfigContext.getCurrentContextConfig().getClientProtocol())) {
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
    	
    	springLocation += SpringLoader.SPRING_SEPARATOR_CHARACTER;
        
        if (OrmUtils.isJpaEnabled("rice.kew")) {
            springLocation += "classpath:org/kuali/rice/kew/config/KEWJPASpringBeans.xml";
        }
        else {
            springLocation += "classpath:org/kuali/rice/kew/config/KEWOJBSpringBeans.xml";
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
		if ( getRunMode().equals( REMOTE_RUN_MODE ) ) {
			lifecycles.add(createThinClientLifecycle());
		} else { // local or embedded
			lifecycles.add(createEmbeddedLifeCycle());
		}
		return lifecycles;
	}

	protected boolean isStandaloneServer() {
	    return getRunMode().equals( LOCAL_RUN_MODE );
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

	@Override
	public Config loadConfig(Config parentConfig) throws Exception {
		parentConfig = super.loadConfig(parentConfig);
		Config currentConfig = parseConfig(parentConfig);
		configureClientProtocol(currentConfig);
		configureDataSource(currentConfig);
		return currentConfig;
	}

	protected Config parseConfig(Config parentConfig) throws Exception {
		List<String> defaultConfigLocations = new ArrayList<String>();
		defaultConfigLocations.add(KEWConstants.DEFAULT_GLOBAL_CONFIG_LOCATION);
		defaultConfigLocations.add(KEWConstants.DEFAULT_APPLICATION_CONFIG_LOCATION);
		
		// TEST REMOVE ME
		if(parentConfig.getProperties().containsKey(Config.SERVICE_NAMESPACE)){
			System.out.println("Incoming Value of SERVICE_NAMESPACE: " + parentConfig.getProperty(Config.SERVICE_NAMESPACE));
		}
		
		Config kewConfig = new JAXBConfigImpl(defaultConfigLocations, parentConfig.getProperties());
		
		kewConfig.parseConfig();
		// TEST REMOVE ME
		if(kewConfig.getProperties().containsKey(Config.SERVICE_NAMESPACE)){
			System.out.println("kewConfig Value of SERVICE_NAMESPACE: " + kewConfig.getProperty(Config.SERVICE_NAMESPACE));
		}
		
		mergeDefaultsIntoParentConfig(parentConfig, kewConfig);
		
		// TEST REMOVE ME
		if(parentConfig.getProperties().containsKey(Config.SERVICE_NAMESPACE)){
			System.out.println("After Merge Value of SERVICE_NAMESPACE: " + parentConfig.getProperty(Config.SERVICE_NAMESPACE));
		}
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
				parentConfig.putProperty(key, defaultConfig.getProperty(key));
			}
		}
	}

	protected String getServiceNamespace(Config config) {
		if (StringUtils.isBlank(config.getServiceNamespace())) {
			throw new ConfigurationException("The 'service.namespace' property was not properly configured.");
		}
		return config.getServiceNamespace();
	}

	protected void configureClientProtocol(Config config) {
		if (StringUtils.isBlank(clientProtocol)) {
			clientProtocol = config.getClientProtocol();
			if (StringUtils.isBlank(clientProtocol)) {
			    // if not explcitly set, set the protocol based on the run mode
			    if ( getRunMode().equals( REMOTE_RUN_MODE ) ) {
			        clientProtocol = KEWConstants.WEBSERVICE_CLIENT_PROTOCOL;
			    } else {
			        clientProtocol = KEWConstants.LOCAL_CLIENT_PROTOCOL;
			    }
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
		config.putProperty(Config.CLIENT_PROTOCOL, clientProtocol);
	}

	protected void configureDataSource(Config config) {
		if (getDataSource() != null) {
			config.putObject(KEW_DATASOURCE_OBJ, getDataSource());
		} else if (!StringUtils.isBlank(getDataSourceJndiName())) {
			config.putProperty(KEW_DATASOURCE_JNDI, getDataSourceJndiName());
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
