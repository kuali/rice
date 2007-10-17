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
package edu.iu.uis.eden.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoaderContainer;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * A base class for {@link PluginRegistry} implementations.  Is essentially a ResourceLoader 
 * implementation that ensures plugins are the only ResourceLoaders used.  Also maintains
 * information about the PluginEnvironments of the loaded plugins in this registry.
 * 
 * @see Plugin
 * @see PluginEnvironment
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class BasePluginRegistry extends ResourceLoaderContainer implements PluginRegistry {
	
	private List<PluginEnvironment> pluginEnvironments = Collections.synchronizedList(new ArrayList<PluginEnvironment>());
	
	public BasePluginRegistry() {
		super(new QName(Core.getCurrentContextConfig().getMessageEntity(), ResourceLoader.PLUGIN_REGISTRY_LOADER_NAME));
	}
	
	public BasePluginRegistry(QName name) {
		super(name);
	}
	    
    public PluginEnvironment getPluginEnvironment(QName pluginName) {
    	for (PluginEnvironment environment : pluginEnvironments) {
    		if (environment.getPlugin().getName().equals(pluginName)) {
    			return environment;
    		}
    	}
    	return null;
    }
	
	public void addPluginEnvironment(PluginEnvironment pluginEnvironment) {
		// chances are that this plugin has already been added to the resource loader
		if (!containsResourceLoader(pluginEnvironment.getPlugin())) {
			addResourceLoader(pluginEnvironment.getPlugin());
		}
		pluginEnvironments.add(pluginEnvironment);
	}
	
	public PluginEnvironment removePluginEnvironment(QName pluginName) {
		super.removeResourceLoader(pluginName);
		PluginEnvironment environment = getPluginEnvironment(pluginName);
		if (environment == null) {
			return null;
		}
		if (!pluginEnvironments.remove(environment)) {
			return null;
		}
		return environment;
	}

	public Plugin getPlugin(QName pluginName) {
		return (Plugin)getResourceLoader(pluginName);
	}

	public List<QName> getPluginNames() {
		return super.getResourceLoaderNames();
	}

//	public PluginEnvironment removePlugin(QName pluginName) {
//		super.removeResourceLoader(pluginName);
//		for (Iterator<PluginEnvironment> iterator = pluginEnvironments.iterator(); iterator.hasNext();) {
//			PluginEnvironment environment = iterator.next();
//			if (environment.getPlugin().getName().equals(pluginName)) {
//				iterator.remove();
//				return environment;
//			}
//		}
//		return null;
//	}

	public Plugin getInstitutionalPlugin() {
		return null;
	}
	
	public List<PluginEnvironment> getPluginEnvironments() {
		return Collections.unmodifiableList(pluginEnvironments);
	}
}