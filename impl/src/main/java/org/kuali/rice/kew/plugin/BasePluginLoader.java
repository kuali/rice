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
package org.kuali.rice.kew.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.util.Utilities;


/**
 * Abstract base PluginLoader implementation.
 * Delegates to template methods to obtain plugin ClassLoader and plugin config file URL,
 * then load the config under the plugin ClassLoader, and constructs a Plugin object.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class BasePluginLoader implements PluginLoader {
    private static final Logger LOG = Logger.getLogger(BasePluginLoader.class);

    private static final String META_INF_PATH = "META-INF";
    private static final String PLUGIN_CONFIG_PATH = META_INF_PATH + "/workflow.xml";

    protected final String simplePluginName;
    protected final boolean institutionalPlugin;
    protected String logPrefix;

    protected final ClassLoader parentClassLoader;
    protected final Config parentConfig;
    protected final File sharedPluginDirectory;
    protected String pluginConfigPath = PLUGIN_CONFIG_PATH;

    public BasePluginLoader(String simplePluginName, File sharedPluginDirectory, ClassLoader parentClassLoader, Config parentConfig, boolean institutionalPlugin) {
        this.sharedPluginDirectory = sharedPluginDirectory;
        if (parentClassLoader == null) {
            parentClassLoader = ClassLoaderUtils.getDefaultClassLoader();
        }
        this.parentClassLoader = parentClassLoader;
        this.parentConfig = parentConfig;
        this.institutionalPlugin = institutionalPlugin;
        this.simplePluginName = simplePluginName;
        this.logPrefix = simplePluginName;
    }

    protected String getLogPrefix() {
        return logPrefix;
    }
    
    public String getPluginName() {
        return simplePluginName;
    }

    public void setPluginConfigPath(String pluginConfigPath) {
        this.pluginConfigPath = pluginConfigPath;
    }

    protected String getSimplePluginName() {
    	return simplePluginName;
    }

    /**
     * Template method that subclasses should implement to supply an appropriate
     * plugin ClassLoader
     * @return an appropriate PluginClassLoader
     * @throws IOException if anything goes awry
     */
    protected abstract PluginClassLoader createPluginClassLoader() throws IOException;
    /**
     * Template method that subclasses should implement to supply an appropriate
     * URL to the plugin's configuration
     * @return an appropriate URL to the plugin's configuration
     * @throws IOException if anything goes awry
     */
    protected abstract URL getPluginConfigURL() throws PluginException, IOException;

    /**
     * Loads and creates the Plugin.
     */
    public Plugin load() throws Exception {
        PluginClassLoader classLoader = createPluginClassLoader();
        LOG.info("Created plugin ClassLoader: " + classLoader);
        ContextClassLoaderBinder.bind(classLoader);
        try {
            return loadWithinContextClassLoader(classLoader);
        } finally {
            ContextClassLoaderBinder.unbind();
        }
    }

    public boolean isRemoved() {
    	return false;
    }

    /**
     * Executes loading of the plugin within the current context classloader set to the Plugin's classloader.
     */
    protected Plugin loadWithinContextClassLoader(PluginClassLoader classLoader) throws PluginException, IOException {
    	URL url = getPluginConfigURL();
        PluginConfig pluginConfig = loadPluginConfig(url);
        QName qPluginName = getPluginName(pluginConfig);
        classLoader.setConfig(pluginConfig);
        ConfigContext.init(classLoader, pluginConfig);
        configureExtraClasspath(classLoader, pluginConfig);
        this.logPrefix = PluginUtils.getLogPrefix(qPluginName, institutionalPlugin).toString();
        LOG.info("Constructing plugin '" + simplePluginName + "' with classloader: " + classLoader);
        Plugin plugin = new Plugin(qPluginName, pluginConfig, classLoader);
        installResourceLoader(plugin);
        installPluginListeners(plugin);
        return plugin;
    }

    protected void installResourceLoader(Plugin plugin) {
    	PluginUtils.installResourceLoader(plugin);
    }

    protected void installPluginListeners(Plugin plugin) {
    	PluginUtils.installPluginListeners(plugin);
    }

    protected void configureExtraClasspath(PluginClassLoader classLoader, PluginConfig config) throws MalformedURLException {
		String extraClassesDirs = config.getProperty(Config.EXTRA_CLASSES_DIR);
		if (!Utilities.isEmpty(extraClassesDirs)) {
			String[] extraClasses = extraClassesDirs.split(",");
			for (int index = 0; index < extraClasses.length; index++) {
				File extraClassesDir = new File(extraClasses[index]);
				if (extraClassesDir.exists()) {
					classLoader.addClassesDirectory(extraClassesDir);
				}
			}
		}
		String extraLibDirs = config.getProperty(Config.EXTRA_LIB_DIR);
		if (!Utilities.isEmpty(extraLibDirs)) {
			String[] extraLibs = extraLibDirs.split(",");
			for (int index = 0; index < extraLibs.length; index++) {
				File extraLibDir = new File(extraLibs[index]);
				if (extraLibDir.exists()) {
					classLoader.addLibDirectory(extraLibDir);
				}
			}
		}
	}


    protected QName getPluginName(PluginConfig pluginConfig) {
    	String messageEntity = pluginConfig.getMessageEntity();
    	QName qPluginName = null;
        if (messageEntity == null) {
        	qPluginName = new QName(ConfigContext.getCurrentContextConfig().getMessageEntity(), simplePluginName);
        } else {
        	qPluginName = new QName(messageEntity, simplePluginName);
        }
    	return qPluginName;
    }

    protected PluginConfig loadPluginConfig(URL url) {
        PluginConfigParser parser = new PluginConfigParser();
        try {
            PluginConfig pluginConfig  = parser.parse(url, parentConfig);
            pluginConfig.parseConfig();
            return pluginConfig;
        } catch (FileNotFoundException e) {
            throw new PluginException(getLogPrefix() + " Could not locate the plugin config file at path " + url, e);
        } catch (IOException ioe) {
            throw new PluginException(getLogPrefix() + " Could not read the plugin config file", ioe);
        } catch (InvalidXmlException ixe) {
            throw new PluginException(getLogPrefix() + " Could not parse the plugin config file", ixe);
        }
    }
}