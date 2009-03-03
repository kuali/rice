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
package org.kuali.rice.kim.config;

import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.lifecycle.Lifecycle;

/**
 * This class handles the Spring based KIM configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KIMConfigurer extends ModuleConfigurer {
	private static final String KIM_INTERFACE_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMInterfaceSpringBeans.xml";
	private static final String KIM_IMPL_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kim/config/KIMImplementationSpringBeans.xml";
	

	/**
	 * 
	 */
	public KIMConfigurer() {
		super();
		setModuleName( "KIM" );
		setHasWebInterface( true );
	}
	
	/**
	 * This overridden method handles setting up the KIM specific configuration.
	 * 
	 * @see org.kuali.rice.core.config.ModuleConfigurer#loadConfig(org.kuali.rice.core.config.Config)
	 */
	@Override
	public Config loadConfig(Config parentConfig) throws Exception {
		if ( LOG.isInfoEnabled() ) {
			LOG.info("Starting configuration of KIM for service namespace " + parentConfig.getServiceNamespace());
		}
		
		Config currentConfig = ConfigContext.getCurrentContextConfig();
		
		// ANY NEW CONFIG ELEMENTS NEED TO BE ADDED HERE

		return currentConfig;
	}

	@Override
	public String getSpringFileLocations() {
		if ( getRunMode().equals( LOCAL_RUN_MODE ) || getRunMode().equals( EMBEDDED_RUN_MODE ) ) {
			return KIM_INTERFACE_SPRING_BEANS_PATH+","+KIM_IMPL_SPRING_BEANS_PATH;
		}
		return KIM_INTERFACE_SPRING_BEANS_PATH;
	}
	
	/**
	 * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
	 * 
	 * TODO - DO I NEED THIS?
	 */
	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		//lifecycles.add(new KIMOjbConfigurer());
		//lifecycles.add(KIMResourceLoaderFactory.createRootKIMResourceLoader());
		return lifecycles;
	}
}