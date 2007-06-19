/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigHolder;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.util.ClassLoaderUtils;

/**
 * Singleton that holds references to global engine objects.
 *
 *
 * @author rkirkend
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class Core {
    private static final Logger LOG = Logger.getLogger(Core.class);

    /**
     * A convenience reference to what we should consider the "core" classloader.  This is most likely
     * going to be whatever webapp classloader the workflow engine resides in, but to make it crystal
     * clear, this is defined here for easy reference.  Note that this is NOT the institutional plugin
     * classloader.
     */
    private static final WeakReference<ClassLoader> CORE_CLASSLOADER = new WeakReference<ClassLoader>(Core.class.getClassLoader());

    /**
     * Concurrency utility which allows other, loosely coupled, components to wait for configuration initialization
     * to complete before proceeding (namely the SpringServiceLocator, before it initializes Spring)
     */
    private static ContextualConfigLock initialized = new ContextualConfigLock("ConfigurationInitialized");
    private static Map<ClassLoader, Config> CONFIGS = new HashMap<ClassLoader, Config>();

    private Core() {
        // nothing to do here
    }

    /**
     * Convenience method to return what all code should consider the "core" workflow engine classloader.  Note that this is NOT
     * the institutional plugin classloader.
     * @return the "core" workflow engine classloader
     */
    public static ClassLoader getCoreClassLoader() {
        return CORE_CLASSLOADER.get();
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
     * Initializes the Core with the given Config and binds it to the given ClassLoader.
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
            LOG.warn("Destroy on un-initialized Core was ignored.");
            return;
        }
        CONFIGS.clear();
        initialized.reset();
    }

    /**
     * Returns the "root" Config object
     * @return the "root" Config object
     * @deprecated this really isn't working as intended at the moment but it still needs to work the concept of a root config
     * 	may need to go away
     */
    public static Config getRootConfig() {
    	return getCurrentContextConfig();
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
     * are present.  For now, this just validates the message entity.
     */
    public static void validateCoreConfiguration() {
    	Config config = getRootConfig();
    	if (StringUtils.isEmpty(config.getMessageEntity())) {
    		throw new ConfigurationException("The " + Config.MESSAGE_ENTITY + " configuration parameter is required.");
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
    		Object object = getRootConfig().getObject(name);
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
    		Object object = getRootConfig().getObject(name);
    		if (object != null) {
    			objects.add(object);
    		}
    		return;
    	}
    	getObjectFromClassLoader(name, classLoader.getParent(), objects);
    }

    /**
     * Returns the map of config objects with their associated classloaders.  
     * Alter this map at your own risk.  This map is the cornerstone of all 
     * service and resource acquisition.
     * 
     * @return
     */
	public static Map<ClassLoader, Config> getCONFIGS() {
		return CONFIGS;
	}

}
