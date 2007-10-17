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

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * A PluginRegistry implementation which loads the embedded plugin from the classpath
 * and loads a plugin manifest based on the client's protocol configuration.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClientProtocolPluginRegistry extends BasePluginRegistry {

	private static final String DEFAULT_EMBEDDED_PLUGIN_LOCATION = "classpath:embedded";

	public ClientProtocolPluginRegistry(QName name) {
		super(name);
	}

	public void start() throws Exception {
		Config config = Core.getCurrentContextConfig();
		String pluginName = "embedded";
		PluginLoader loader = null;
		String embeddedPluginLocation = getEmbeddedPluginLocation();
		String path = getPath(embeddedPluginLocation);
		if (isDeferToCurrentClassLoader()) {
			loader = new ClassLoaderPluginLoader(ClassLoaderUtils.getDefaultClassLoader());
		} else if (isClasspath(embeddedPluginLocation)) {
			loader = new ClasspathPluginLoader(pluginName,
				path,
				null,
				ClassLoaderUtils.getDefaultClassLoader(),
				Core.getCurrentContextConfig(),
				false);
		} else {
			throw new PluginException("Failed to start the embedded plugin because the embedded plugin location is invalid, value was: " + embeddedPluginLocation);
		}
		loader.setPluginManifestPath("META-INF/"+config.getClientProtocol()+"-workflow.xml");
		PluginEnvironment environment = new PluginEnvironment(loader, this);
		environment.setSupressStartupFailure(false);
		environment.load();
//		Plugin plugin = loader.load();
//		PluginUtils.installResourceLoader(plugin);
//		PluginUtils.installPluginListeners(plugin);
//		plugin.setSupressStartupFailure(false);
		addPluginEnvironment(environment);
		super.start();
	}

	protected String getEmbeddedPluginLocation() {
		String embeddedPluginLocation = Core.getCurrentContextConfig().getEmbeddedPluginLocation();
		if (StringUtils.isEmpty(embeddedPluginLocation)) {
			embeddedPluginLocation = DEFAULT_EMBEDDED_PLUGIN_LOCATION;
		}
		return embeddedPluginLocation;
	}

	protected boolean isDeferToCurrentClassLoader() {
		return new Boolean(Core.getCurrentContextConfig().getProperty(Config.EMBEDDED_PLUGIN_DEFAULT_CURRENT_CLASS_LOADER));
	}

	protected boolean isClasspath(String location) {
		return location.startsWith("classpath:");
	}

	protected boolean isFile(String location) {
		return location.startsWith("file:");
	}

	protected String getPath(String location) {
		return location.substring(location.indexOf(":")+1);
	}
}