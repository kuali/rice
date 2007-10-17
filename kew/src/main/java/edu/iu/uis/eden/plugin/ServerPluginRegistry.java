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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.ResourceLoader;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledFuture;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.iu.uis.eden.plugin.PluginUtils.PluginZipFileFilter;
import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * A PluginRegistry implementation which loads plugins from the file system on the server.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServerPluginRegistry extends BasePluginRegistry {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ServerPluginRegistry.class);

	private List<String> pluginDirectories = new ArrayList<String>();
	private File sharedPluginDirectory;
	//consider removing this from here and using the super class to get it.
	private Plugin institutionalPlugin;
	private Reloader reloader;
	private HotDeployer hotDeployer;

	private ScheduledExecutorService scheduledExecutor;
	private ScheduledFuture reloaderFuture;
	private ScheduledFuture hotDeployerFuture;


	public ServerPluginRegistry() {
		super(new QName(Core.getCurrentContextConfig().getMessageEntity(), ResourceLoader.PLUGIN_REGISTRY_LOADER_NAME));
	}

	public void start() throws Exception {
		scheduledExecutor = Executors.newScheduledThreadPool(2);
		sharedPluginDirectory = loadSharedPlugin();
		reloader = new Reloader();
		hotDeployer = new HotDeployer(PluginUtils.getPluginRegistry(), sharedPluginDirectory, pluginDirectories);
		loadPlugins(sharedPluginDirectory);
		// TODO make the delay configurable
		this.reloaderFuture = scheduledExecutor.scheduleWithFixedDelay(reloader, 5, 5, TimeUnit.SECONDS);
		this.hotDeployerFuture = scheduledExecutor.scheduleWithFixedDelay(hotDeployer, 5, 5, TimeUnit.SECONDS);
		super.start();
	}

	public void stop() throws Exception {
		stopReloader();
		stopHotDeployer();
		reloader = null;
		hotDeployer = null;

		// cleanup reference to institutional plugin
		institutionalPlugin = null;
		if (scheduledExecutor != null) {
			scheduledExecutor.shutdownNow();
			scheduledExecutor = null;
		}
		super.stop();
	}

	protected void stopReloader() {
		if (reloaderFuture != null) {
			if (!reloaderFuture.cancel(true)) {
				LOG.warn("Failed to cancel the plugin reloader.");
			}
			reloaderFuture = null;
		}
	}

	protected void stopHotDeployer() {
		if (hotDeployerFuture != null) {
			if (!hotDeployerFuture.cancel(true)) {
				LOG.warn("Failed to cancel the hot deployer.");
			}
			hotDeployerFuture = null;
		}
	}

	protected void loadPlugins(File sharedPluginDirectory) {
        Map<String, File> pluginLocations = new TreeMap<String, File>(new PluginNameComparator(PluginUtils.getInstitutionalPluginName()));
		PluginZipFileFilter pluginFilter = new PluginZipFileFilter();
        //PluginDirectoryFilter pluginFilter = new PluginDirectoryFilter(sharedPluginDirectory);
        Set<File> visitedFiles = new HashSet<File>();
        for (String pluginDir : pluginDirectories) {
            LOG.info("Reading plugins from " + pluginDir);
            File file = new File(pluginDir);
            if (visitedFiles.contains(file)) {
                LOG.info("Skipping visited directory: " + pluginDir);
                continue;
            }
            visitedFiles.add(file);
            if (!file.exists() || !file.isDirectory()) {
                LOG.warn(file.getAbsoluteFile()+" is not a valid plugin directory.");
                continue;
            }
            File[] pluginZips = file.listFiles(pluginFilter);
            for (int i = 0; i < pluginZips.length; i++) {
                File pluginZip = pluginZips[i];
                int indexOf = pluginZip.getName().lastIndexOf(".zip");
                String pluginName = pluginZip.getName().substring(0, indexOf);
                if (pluginLocations.containsKey(pluginName)) {
                	LOG.warn("There already exists an installed plugin with the name '"+ pluginName + "', ignoring plugin " + pluginZip.getAbsolutePath());
                	continue;
                }
                pluginLocations.put(pluginName, pluginZip);
            }
        }
        for (String pluginName : pluginLocations.keySet()) {
        	File pluginZipFile = pluginLocations.get(pluginName);
        	// now execute the loading of the plugins
        	boolean isInstitutionalPlugin = PluginUtils.isInstitutionalPlugin(pluginName);
        	try {
        		LOG.info("Loading "+(isInstitutionalPlugin ? "Institutional " : "")+"plugin '" + pluginName + "'");
        		ClassLoader parentClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        		Config parentConfig = Core.getCurrentContextConfig();
        		if (institutionalPlugin != null) {
        			parentClassLoader = institutionalPlugin.getClassLoader();
        			parentConfig = institutionalPlugin.getConfig();
        		}
        		ZipFilePluginLoader loader = new ZipFilePluginLoader(pluginZipFile,
        				sharedPluginDirectory,
        				parentClassLoader,
        				parentConfig,
        				isInstitutionalPlugin);
        		PluginEnvironment environment = new PluginEnvironment(loader, this);
        		environment.load();
        		// TODO consider moving this inside either the loader or the environment?  Because this will need to be able
        		// to be reset if the institutional plugin is "hot deployed"
        		if (isInstitutionalPlugin) {
        			setInstitutionalPlugin(environment.getPlugin());
        		}
        		addPluginEnvironment(environment);
        	} catch (Exception e) {
        		LOG.error("Failed to read workflow plugin '"+pluginName+"'", e);
        		if (isInstitutionalPlugin) {
        			throw new PluginException("Failed to load the institutional plugin with name '" + pluginName +"'.", e);
        		}
        	}
        }
    }

	@Override
	public void addPluginEnvironment(PluginEnvironment pluginEnvironment) {
		super.addPluginEnvironment(pluginEnvironment);
		reloader.addReloadable(pluginEnvironment);
	}

	@Override
	public PluginEnvironment removePluginEnvironment(QName pluginName) {
		PluginEnvironment environment = super.removePluginEnvironment(pluginName);
		reloader.removeReloadable(environment);
		return environment;
	}

	public File loadSharedPlugin() {
		return PluginUtils.findSharedDirectory(pluginDirectories);
	}

	public void setPluginDirectories(List<String> pluginDirectories) {
		this.pluginDirectories = pluginDirectories;
	}

	public void setSharedPluginDirectory(File sharedPluginDirectory) {
		this.sharedPluginDirectory = sharedPluginDirectory;
	}

	public Plugin getInstitutionalPlugin() {
		return institutionalPlugin;
	}

	public void setInstitutionalPlugin(Plugin institutionalPlugin) {
		this.institutionalPlugin = institutionalPlugin;
	}

	protected HotDeployer getHotDeployer() {
		return hotDeployer;
	}

	protected Reloader getReloader() {
		return reloader;
	}

}