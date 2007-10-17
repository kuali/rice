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
package edu.iu.uis.eden.plugin.management;

import java.util.List;

import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.plugin.Plugin;
import edu.iu.uis.eden.plugin.PluginEnvironment;
import edu.iu.uis.eden.plugin.PluginUtils;

/**
 * A Management bean for interfacing with the plugin architecture.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginManager {

	public String fetchPlugins() {
		List<PluginEnvironment> plugins = getPluginEnvironments();
		if (plugins.isEmpty()) {
			return "No Plugins Could be located in resource loaders:\n"+
				GlobalResourceLoader.getResourceLoader().getContents("", true);
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("| Name | Is Running | Is Reloadable |\n");
		for (PluginEnvironment pluginEnvironment : plugins) {
			buffer.append(renderPlugin(pluginEnvironment)).append("\n");
		}
		return buffer.toString();
	}
	
	public void stopPlugin(String pluginName) {
		PluginEnvironment pluginEnvironment = getPluginEnvironment(pluginName);
		if (pluginEnvironment == null) {
			throw new IllegalArgumentException("Could not locate plugin with the given name: " + pluginName);
		}
		pluginEnvironment.getPlugin().stop();
	}
	
	public void startPlugin(String pluginName) {
		PluginEnvironment pluginEnvironment = getPluginEnvironment(pluginName);
		if (pluginEnvironment == null) {
			throw new IllegalArgumentException("Could not locate plugin with the given name: " + pluginName);
		}
		pluginEnvironment.getPlugin().start();
	}
	
	public void reloadPlugin(String pluginName) throws Exception {
		PluginEnvironment pluginEnvironment = getPluginEnvironment(pluginName);
		if (pluginEnvironment == null) {
			throw new IllegalArgumentException("Could not locate plugin with the given name: " + pluginName);
		}
		pluginEnvironment.reload();
	}
	
	public int getHotDeployCheckFrequency() {
		// TODO this needs implemented
		return 0;
	}
	
	public void setHotDeployCheckFrequency(int hotDeployCheckFrequency) {
		// TODO this method needs implemented
	}
	
	// TODO add various other attributes related to hot deployment
	
	protected PluginEnvironment getPluginEnvironment(String name) {
		// first compare to QNames
		for (PluginEnvironment pluginEnvironment : getPluginEnvironments()) {
			Plugin plugin = pluginEnvironment.getPlugin();
			if (name.equals(plugin.getName().toString())) {
				return pluginEnvironment;
			}
		}
		// now compare to local names
		for (PluginEnvironment pluginEnvironment : getPluginEnvironments()) {
			Plugin plugin = pluginEnvironment.getPlugin();
			if (name.equals(plugin.getName().getLocalPart())) {
				return pluginEnvironment;
			}
		}
		return null;
	}
	
	protected List<PluginEnvironment> getPluginEnvironments() {
		return PluginUtils.getPluginRegistry().getPluginEnvironments();
	}
	
	protected String renderPlugin(PluginEnvironment pluginEnvironment) {
		return " | " + pluginEnvironment.getPlugin().getName() + " | " + pluginEnvironment.getPlugin().isStarted() + " | " + pluginEnvironment.isReloadable() + " | ";
	}
	
}
