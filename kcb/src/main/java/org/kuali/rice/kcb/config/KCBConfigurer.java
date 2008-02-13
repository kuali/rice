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
package org.kuali.rice.kcb.config;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.ojb.BaseOjbConfigurer;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;

/**
 * This class handles the Spring based KCB configuration that is part of the Rice Configurer that must 
 * exist in all Rice based systems and clients.
 * The Spring context file that the KCBConfigurer loads depends on whether the "test.mode" Rice configuration
 * parameter has been set.
 *
 * TODO: Module-specific configurer can probably be mostly eliminated in preference of a base module configurer that
 * just uses some conventions:
 * Support test mode toggle which toggles between two spring context locations, named by convention modulenameSpringBeans[-test].xml
 * Registers a BaseOjbConfigurer that derives jcdAliases and metadata locations from modulename 
 * Registers a SpringResourceLoader in a conventional fashion (using a standard naming convention, and the above context file)
 * 
 * ISSUES: anything the proposed base module configurer does needs to have classes it relies on in whatever module it lives in, e.g. shared
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KCBConfigurer extends ModuleConfigurer {
    /**
     * Some module name and path constants
     */
    private static final String MODULE_NAME = "kcb";
    private static final String STANDARD_CONTEXT_FILE = "KCBSpringBeans.xml";
    private static final String TEST_CONTEXT_FILE = "KCBSpringBeans-test.xml";
    private static final String KCB_SPRING_RESOURCE_LOADER_LOCAL_NAME = "KCB_SPRING_RESOURCE_LOADER";
    
    private static final Logger LOG = Logger.getLogger(KCBConfigurer.class);

    private boolean testMode = false;

    /**
     * Allows the client to configure whether the module is in test mode.  When test mode
     * is set, a different Spring context file is loaded. 
     *  
     * @param testMode whether the module is in test mode
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

	/**
	 * This overridden method handles setting up the KCB specific configuration.
	 * 
	 * @see org.kuali.rice.config.ModuleConfigurer#loadConfig(org.kuali.rice.config.Config)
	 */
	@Override
	public Config loadConfig(Config parentConfig) throws Exception {
    	LOG.info("Starting configuration of KCB for message entity " + parentConfig.getMessageEntity());
		
		Config currentConfig = Core.getCurrentContextConfig();
		// ANY NEW CONFIG ELEMENTS NEED TO BE ADDED HERE
		return currentConfig;
	}
	
	/**
	 * @see org.kuali.rice.lifecycle.BaseCompositeLifecycle#loadLifecycles()
	 */
	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();

		lifecycles.add(new BaseOjbConfigurer(MODULE_NAME));

		String context;
	    if (testMode) {
	        context = TEST_CONTEXT_FILE;
	    } else {
	        context = STANDARD_CONTEXT_FILE;
	    }

		ResourceLoader resourceLoader = new SpringResourceLoader(new QName(Core.getCurrentContextConfig().getMessageEntity(), KCB_SPRING_RESOURCE_LOADER_LOCAL_NAME), context);
		GlobalResourceLoader.addResourceLoaderFirst(resourceLoader);

		lifecycles.add(resourceLoader);

		return lifecycles;
	}
}