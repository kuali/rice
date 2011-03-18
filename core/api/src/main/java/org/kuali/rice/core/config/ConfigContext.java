/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.ClassLoaderUtils;

/**
 * Singleton that holds references to global engine objects.
 *
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigContext {
    private static final Logger LOG = Logger.getLogger(ConfigContext.class);

    /**
     * Concurrency utility which allows other, loosely coupled, components to wait for configuration initialization
     * to complete before proceeding (namely the SpringServiceLocator, before it initializes Spring)
     */
    private static final ContextualConfigLock initialized = new ContextualConfigLock("ConfigurationInitialized");
    private static final Map<ClassLoader, Config> CONFIGS = new HashMap<ClassLoader, Config>();

    private ConfigContext() {
        // nothing to do here
    }

    /**
     * Perform a one-time initialization of the Config system.  This should only be performed by the applicable LifeCycle
     * implementation.
     * @param rootCfg the root config
     */
    public static void init(Config rootCfg) {
    	init(Thread.currentThread().getContextClassLoader(), rootCfg);
    }

    /**
     * Initializes the ConfigContext with the given Config and binds it to the given ClassLoader.
     */
    public static void init(ClassLoader classLoader, Config config) {
    	CONFIGS.put(classLoader, config);
    	initialized.fire();
    }

    /**
     * Destroy method (mostly to aid testing, as core needs to be torn down appropriately).
     */
    public static void destroy() {
        if (!initialized.hasFired()) {
            LOG.warn("Destroy on un-initialized ConfigContext was ignored.");
            return;
        }
        CONFIGS.clear();
        initialized.reset();
    }

    /**
     * Returns the Condition that allows waiting on configuration to complete
     * @return the Condition that allows waiting on configuration to complete
     */
    public static ContextualConfigLock getInitializedCondition() {
        return initialized;
    }

    /**
     * Runs a series of validation checks against the core configuration to ensure that required properties
     * are present.  For now, this just validates the service namespace.
     */
    public static void validateCoreConfiguration() {
    	Config config = getCurrentContextConfig();
    	if (StringUtils.isEmpty(config.getServiceNamespace())) {
    		throw new ConfigurationException("The " + Config.SERVICE_NAMESPACE + " configuration parameter is required.");
    	}
    }



    /**
     * Utility method that all code should call to obtain its appropriate Config object.
     * The Config object which is associated with the caller's context classloader will be
     * returned, being created first if it does not yet exist.
     * @return the Config object which is associated with the caller's context classloader
     */
    public synchronized static Config getCurrentContextConfig() {
    	return CONFIGS.get(Thread.currentThread().getContextClassLoader());
    }

    /**
     * @param cl the classloader whose Config to return
     * @return the Config of a particular class loader
     */
    public synchronized static Config getConfig(ClassLoader cl) {
        return CONFIGS.get(cl);
    }

    public synchronized static Object getObjectFromConfigHierarchy(String name) {
    	return getObjectFromClassLoader(name, ClassLoaderUtils.getDefaultClassLoader());
    }

    private static Object getObjectFromClassLoader(String name, ClassLoader classLoader) {
    	if (classLoader instanceof ConfigHolder) {
    		Object object = ((ConfigHolder)classLoader).getConfig().getObject(name);
    		if (object != null) {
    			return object;
    		}
    	} else {
    		Object object = getCurrentContextConfig().getObject(name);
    		if (object != null) {
    			return object;
    		}
    		return null;
    	}
    	return getObjectFromClassLoader(name, classLoader.getParent());
    }

    public static List<Object> getObjectsFromConfigHierarchy(String name) {
    	List<Object> objects = new ArrayList<Object>();
    	getObjectFromClassLoader(name, ClassLoaderUtils.getDefaultClassLoader(), objects);
    	return objects;
    }

    private static void getObjectFromClassLoader(String name, ClassLoader classLoader, List<Object> objects) {
    	if (classLoader instanceof ConfigHolder) {
    		Object object = ((ConfigHolder)classLoader).getConfig().getObject(name);
    		if (object != null) {
    			objects.add(object);
    		}
    	} else {
    		Object object = getCurrentContextConfig().getObject(name);
    		if (object != null) {
    			objects.add(object);
    		}
    		return;
    	}
    	getObjectFromClassLoader(name, classLoader.getParent(), objects);
    }

    /**
     * @return an immutable view of the Configs entry set
     */
    public static Set<Map.Entry<ClassLoader, Config>> getConfigs() {
        return Collections.unmodifiableSet(CONFIGS.entrySet());
    }

    /**
     * Overrides any existing Config for the classloader
     * @param cl the classloader whose Config should be overridden
     * @param config the config
     */
    public static void overrideConfig(ClassLoader cl, Config config) {
        CONFIGS.put(cl, config);
    }
}
