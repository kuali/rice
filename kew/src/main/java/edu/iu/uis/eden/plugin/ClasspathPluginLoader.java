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
import java.net.URL;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;

/**
 * A {@link PluginLoader} which creates and Loads a {@link Plugin} from the given
 * location on the classpath.
 *
 * @see Plugin
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClasspathPluginLoader extends BasePluginLoader {
	private static final Logger LOG = Logger.getLogger(ClasspathPluginLoader.class);

    private final String classpathLocation;

	public ClasspathPluginLoader(String pluginName, String classpathLocation, File sharedPluginDirectory, ClassLoader parentClassLoader, Config parentConfig, boolean institutionalPlugin) {
		super(pluginName, sharedPluginDirectory, parentClassLoader, parentConfig, institutionalPlugin);
        this.classpathLocation = classpathLocation;
    }

	/**
	 * A Plugin which is loaded from the classpath will never change.
	 */
	public boolean isModified() {
		return false;
	}

	protected PluginClassLoader createPluginClassLoader() {
        LOG.info(getLogPrefix() + " Initiating loading of plugin from classpath location: " + classpathLocation);
        return new EmbeddedPluginClassLoader(parentClassLoader, classpathLocation);
    }

    protected URL getPluginManifestURL() {
        String fullManifestPath = classpathLocation + "/" + pluginManifestPath;
        // NOTE: consider using the plugin's classloader (or context classloader which should
        // probably be set to plugin's classloader at this point
        // works because fullManifestPath is always prefixed with the plugin's classpath location
        // prefix
        return parentClassLoader.getResource(fullManifestPath);
    }
}