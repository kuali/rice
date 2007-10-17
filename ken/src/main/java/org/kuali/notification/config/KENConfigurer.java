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
package org.kuali.notification.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;

/**
 * The KEN Configurer
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KENConfigurer extends ModuleConfigurer {
    private static final Logger LOG = Logger.getLogger(KENConfigurer.class);
    
    private static final String KEN_TEST_MODE_PARAM = "ken.test.mode";
    private static final String STANDARD_KEN_CONTEXT_FILE = "KENSpringBeans.xml";
    private static final String TEST_KEN_CONTEXT_FILE = "KENSpringBeans-test.xml";
    

    /**
     * @see org.kuali.rice.config.ModuleConfigurer#loadConfig(org.kuali.rice.config.Config)
     */
    @Override
    public Config loadConfig(Config parentConfig) throws Exception {
	LOG.info("Starting configuration of KEN for message entity " + parentConfig.getMessageEntity());
		
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
	
	lifecycles.add(new ConfigurableOjbConfigurer("ken"));
	
	Config currentConfig = Core.getCurrentContextConfig();
	String context;
	String s = currentConfig.getProperty(KEN_TEST_MODE_PARAM);
	if (!Boolean.valueOf(s).booleanValue()) {
	    context = STANDARD_KEN_CONTEXT_FILE;
	} else {
	    context = TEST_KEN_CONTEXT_FILE;
	}
	lifecycles.add(KENResourceLoaderFactory.createRootKENResourceLoader(context));

	return lifecycles;
    }

    // support for injected custom datasource
    /*
     protected void configureDataSource(Config config) {
		if (getDataSource() != null) {
			config.getObjects().put(KEN_DATASOURCE_OBJ, getDataSource());
		} else if (!StringUtils.isBlank(getDataSourceJndiName())) {
			config.getProperties().put(KEN_DATASOURCE_JNDI, getDataSourceJndiName());
		}
	}
	*/
     
}
