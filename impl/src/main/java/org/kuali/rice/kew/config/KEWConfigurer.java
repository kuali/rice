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

package org.kuali.rice.kew.config;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.impl.config.module.ModuleConfigurer;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.lifecycle.EmbeddedLifeCycle;
import org.kuali.rice.kew.plugin.PluginRegistry;
import org.kuali.rice.kew.plugin.PluginRegistryFactory;
import org.kuali.rice.kew.resourceloader.CoreResourceLoader;
import org.kuali.rice.kew.util.KEWConstants.ClientProtocol;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


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

	private DataSource dataSource;
	
	@Override
	public List<String> getPrimarySpringFiles() {
		final List<String> springFileLocations;
		if (RunMode.REMOTE.equals(getRunMode()) || RunMode.THIN.equals(getRunMode()) ||
				ClientProtocol.WEBSERVICE.equals(getClientProtocol())) {
			springFileLocations = Collections.emptyList();
		} else {
			springFileLocations = getEmbeddedSpringFileLocation();
		}

		return springFileLocations;
	}
	
    private List<String> getEmbeddedSpringFileLocation(){
    	final List<String> springFileLocations = new ArrayList<String>();
    	springFileLocations.add("classpath:org/kuali/rice/kew/config/KEWSpringBeans.xml");

        if ( isExposeServicesOnBus() ) {
        	if (isSetSOAPServicesAsDefault()) {
        		springFileLocations.add("classpath:org/kuali/rice/kew/config/KEWServiceBusSOAPDefaultSpringBeans.xml");
        	} else {
        		springFileLocations.add("classpath:org/kuali/rice/kew/config/KEWServiceBusSpringBeans.xml");
        	}
        }
        
        if (OrmUtils.isJpaEnabled("rice.kew")) {
        	springFileLocations.add("classpath:org/kuali/rice/kew/config/KEWJPASpringBeans.xml");
        }
        else {
        	springFileLocations.add("classpath:org/kuali/rice/kew/config/KEWOJBSpringBeans.xml");
        }

    	return springFileLocations;
    }

	@Override
	public List<Lifecycle> loadLifecycles() throws Exception {
		
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		if ( getRunMode().equals( RunMode.THIN ) ) {
			lifecycles.add(createThinClientLifecycle());
		} else if ( !getRunMode().equals( RunMode.REMOTE ) ) { // local or embedded
			lifecycles.add(createEmbeddedLifeCycle());
		}
		return lifecycles;
	}

	/**
	 * TODO Because a lot of our lifecycles live behind the embedded plugin and the KEWConfigurer does not, this is a simple
	 * measure to load these without having to deal with the removal of the embedded plugin right away.
     * @return Life Cycle
     * @throws Exception if life cycle not created
     */
	private Lifecycle createEmbeddedLifeCycle() throws Exception {
		return new EmbeddedLifeCycle();
	}

	private Lifecycle createThinClientLifecycle() throws Exception {
		return new ThinClientLifecycle();
	}

	@Override
	public void addAdditonalToConfig() {
		configureDataSource();
	}

	private void configureDataSource() {
		if (getDataSource() != null) {
			ConfigContext.getCurrentContextConfig().putObject(KEW_DATASOURCE_OBJ, getDataSource());
		}
	}

	@Override
	public Collection<ResourceLoader> getResourceLoadersToRegister() throws Exception {
		// create the plugin registry
		PluginRegistry registry = null;
		String pluginRegistryEnabled = ConfigContext.getCurrentContextConfig().getProperty("plugin.registry.enabled");
		if (!StringUtils.isBlank(pluginRegistryEnabled) && Boolean.valueOf(pluginRegistryEnabled).booleanValue()) {
			registry = new PluginRegistryFactory().createPluginRegistry();
		}

		final Collection<ResourceLoader> rls = new ArrayList<ResourceLoader>();
		for (ResourceLoader rl : RiceResourceLoaderFactory.getSpringResourceLoaders()) {
			CoreResourceLoader coreResourceLoader = 
				new CoreResourceLoader(rl, registry);
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
			rls.add(coreResourceLoader);
		}

		return rls;
	}

	private ClientProtocol getClientProtocol() {
		return ClientProtocol.valueOf(ConfigContext.getCurrentContextConfig().getProperty("client.protocol"));
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
