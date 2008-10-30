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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.ojb.BaseOjbConfigurer;
import org.kuali.rice.core.resourceloader.ResourceLoader;

/**
 * A base module configurer that defines module name and test mode flag 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseModuleConfigurer extends ModuleConfigurer {
    /**
     * Protected logger for use by subclasses
     */
    protected final Logger LOG = Logger.getLogger(getClass());

    protected final String moduleName;
    protected boolean testMode;

    /**
     * Constructor that accepts the module name
     * @param moduleName the module name
     */
    public BaseModuleConfigurer(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return whether the module is in test mode
     */
    public boolean isTestMode() {
        return this.testMode;
    }

    /**
     * Sets whether the module is in test mode
     * @param testMode whether the module should be in test mode
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * @return the module name
     */
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * Simply returns the config unchanged
     * @see org.kuali.rice.core.config.ModuleConfigurer#loadConfig(org.kuali.rice.core.config.Config)
     */
    @Override
    public Config loadConfig(Config parentConfig) throws Exception {
        LOG.info("Starting configuration of " + getModuleName() + " for message entity " + parentConfig.getServiceNamespace());
        return ConfigContext.getCurrentContextConfig();
    }
    
    @Override
	public String getSpringFileLocations(){
		return "";
	}
	
    /**
     * Registers an OjbConfigurer and ResourceLoader for the module, adding it first to the GlobalResourceLoader.
     * @see org.kuali.rice.core.lifecycle.BaseCompositeLifecycle#loadLifecycles()
     */
    @Override
    protected List<Lifecycle> loadLifecycles() throws Exception {
        LOG.info("Loading " + getModuleName() + " module lifecycles");
        List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
        lifecycles.add(new BaseOjbConfigurer(getModuleName()));
        return lifecycles;
    }

    /**
     * Template method for creation of the module resource loader.  Subclasses should override
     * and return an appropriate resource loader for the module.  If 'null' is returned, no
     * resource loader is added to the lifecycles by default.  The caller {@link #loadLifecycles()}
     * implementation will add the ResourceLoader to the GlobalResourceLoader, so that it is not
     * necessary to do so in the subclass.
     * @return a resource loader for the module, or null
     */
    protected ResourceLoader createResourceLoader() {
        return null;
    }
}