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
package edu.iu.uis.eden.core;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.SpringLoader;
import edu.iu.uis.eden.plugin.PluginRegistry;
import edu.iu.uis.eden.plugin.PluginRegistryFactory;

/**
 * Start the GlobalResourceLoader from the AppliationInitializeListener.  When the KEW webapp starts up
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WebApplicationGlobalResourceLifecycle extends BaseLifecycle {

	public void start() throws Exception {

		// create the plugin registry
		PluginRegistry registry = null;
		String pluginRegistryEnabled = Core.getCurrentContextConfig().getProperty("plugin.registry.enabled");
		if (!StringUtils.isBlank(pluginRegistryEnabled) && Boolean.valueOf(pluginRegistryEnabled)) {
			registry = new PluginRegistryFactory().createPluginRegistry();
		}

		CoreResourceLoader coreResourceLoader = new CoreResourceLoader(SpringLoader.getInstance(), registry);
		coreResourceLoader.start();

		//wait until core resource loader is started to attach to GRL;  this is so startup
		//code can depend on other things hooked into GRL without incomplete KEW resources
		//messing things up.

		GlobalResourceLoader.addResourceLoaderFirst(coreResourceLoader);

		// now start the plugin registry if there is one
		if (registry != null) {
			registry.start();
			// the registry resourceloader is now being handled by the CoreResourceLoader
			//GlobalResourceLoader.addResourceLoader(registry);
		}

		super.start();
	}

	public void stop() throws Exception {
		GlobalResourceLoader.stop();
		super.stop();
	}
}