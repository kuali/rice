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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.plugin.PluginUtils.PluginZipFileFilter;
import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * Checks for plugins added to or removed from the configured plugin directories.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class HotDeployer implements Runnable {
	private static final Logger LOG = Logger.getLogger(HotDeployer.class);


	private PluginRegistry registry;
	private File sharedPluginDirectory;
	private List<String> pluginDirectories;

	public HotDeployer(PluginRegistry registry, File sharedPluginDirectory, List<String> pluginDirectories) {
		this.registry = registry;
		this.sharedPluginDirectory = sharedPluginDirectory;
		this.pluginDirectories = pluginDirectories;
	}

	public synchronized void run() {
		try {
			LOG.debug("Checking for added and removed plugins...");
			Set<PluginEnvironment> removedPlugins = getRemovedPlugins();
			for (PluginEnvironment pluginContext : removedPlugins) {
				LOG.info("Detected a removed plugin '" + pluginContext.getPlugin().getName() + "', shutting down plugin.");
				try {
					pluginContext.unload();
					registry.removePluginEnvironment(pluginContext.getPlugin().getName());
				} catch (Exception e) {
					LOG.error("Failed to unload plugin '" + pluginContext.getPlugin().getName() + "'", e);
				}
			}
			Set<PluginEnvironment> addedPlugins = getAddedPlugins();
			for (PluginEnvironment pluginContext : addedPlugins) {
				try {
					LOG.info("Detected a new plugin.  Loading plugin...");
					pluginContext.load();
					LOG.info("...plugin '" + pluginContext.getPlugin().getName() + "' loaded.");
					registry.addPluginEnvironment(pluginContext);
				} catch (Exception e) {
					LOG.warn("Failed to load plugin '" + pluginContext.getPlugin().getName() + "'");
				}
			}
		} catch (Exception e) {
			LOG.warn("Failed to check for hot deploy.", e);
		}
//
//
//		for (Iterator iterator = addedPluginDirs.iterator(); iterator.hasNext();) {
//			File pluginDir = (File) iterator.next();
//            LOG.info("Detected a new plugin.  Waiting for plugin in '" + pluginDir + "' to be ready...");
//			if (PluginUtils.waitUntilPluginIsReady(pluginDir)) {
//                LOG.info("Adding new plugin in '" + pluginDir + "'");
//                Plugin plugin = new Plugin(pluginDir, sharedPluginDirectory);
//                plugin.setPluginRegistry(registry);
//                plugin.setParentClassLoader(registry.getInstitutionPlugin().getClassLoader());
//                registry.addPlugin(plugin);
//			} else {
//				LOG.warn("It appears plugin in '" + pluginDir + "' is being modified.  Waiting until next poll interval.");
//			}
//		}
	}

	protected Set<PluginEnvironment> getRemovedPlugins() {
		Set<PluginEnvironment> removedPlugins = new HashSet<PluginEnvironment>();
		for (PluginEnvironment environment : registry.getPluginEnvironments()) {
			if (environment.getLoader().isRemoved()) {
				removedPlugins.add(environment);
			}
		}
//		for (Iterator iterator = registry.getPlugins().iterator(); iterator.hasNext();) {
//			Plugin plugin = (Plugin) iterator.next();
//			if (!plugin.getPluginDirectory().exists() || !plugin.getPluginDirectory().isDirectory()) {
//				removedPlugins.add(plugin);
//			}
//		}
		return removedPlugins;
	}

	protected Set<PluginEnvironment> getAddedPlugins() throws Exception {
		Set<PluginEnvironment> addedPlugins = new HashSet<PluginEnvironment>();
		Set<File> newPluginZipFiles = new HashSet<File>();
		// for now, this implementation should watch the plugin directories for more plugins
		// TODO somehow the code which checks for new plugins and which loads plugins initially needs to be
		// consolidated, maybe with some sort of set of PluginLocators? or something along those lines?
		for (String pluginDirName : pluginDirectories) {
			File pluginDir = new File(pluginDirName);
			if (pluginDir.exists() && pluginDir.isDirectory()) {
				File[] pluginDirFiles = pluginDir.listFiles(new PluginZipFileFilter());
				for (File pluginZip : pluginDirFiles) {
					int indexOf = pluginZip.getName().lastIndexOf(".zip");
					String pluginName = pluginZip.getName().substring(0, indexOf);
					if (PluginUtils.isInstitutionalPlugin(pluginName)) {
						continue;
					}
					// check to see if this plugin has already been loaded
					List<PluginEnvironment> currentEnvironments = registry.getPluginEnvironments();
					boolean pluginExists = false;
					for (PluginEnvironment environment : currentEnvironments) {
						if (environment.getPlugin().getName().getLocalPart().equals(pluginName)) {
							pluginExists = true;
							break;
						}
					}
					if (!pluginExists) {
						// make sure the plugin's not in the process of being copied
						long lastModified1 = pluginZip.lastModified();
						Thread.sleep(100);
						long lastModified2 = pluginZip.lastModified();
						if (lastModified1 == lastModified2) {
							newPluginZipFiles.add(pluginZip);
						} else {
							LOG.warn("Detected that the plugin zip is still being modified, holding off on hot deploy: " + pluginZip.getAbsolutePath());
						}
					}
				}
			}
		}

		// TODO this currently couldn't handle an institutional plugin being "added", should it be able to?!?
		ClassLoader parentClassLoader = ClassLoaderUtils.getDefaultClassLoader();
		Config parentConfig = Core.getCurrentContextConfig();
		Plugin institutionalPlugin = registry.getInstitutionalPlugin();
		if (institutionalPlugin != null) {
			parentClassLoader = institutionalPlugin.getClassLoader();
			parentConfig = institutionalPlugin.getConfig();
		}
		for (File newPluginZipFile : newPluginZipFiles) {
			PluginLoader loader = new ZipFilePluginLoader(newPluginZipFile, sharedPluginDirectory, parentClassLoader, parentConfig, false);
			PluginEnvironment environment = new PluginEnvironment(loader, registry);
			addedPlugins.add(environment);
		}
		return addedPlugins;
	}

}