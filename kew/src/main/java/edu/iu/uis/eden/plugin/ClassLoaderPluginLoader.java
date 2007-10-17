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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.ResourceLoader;

import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.plugin.manifest.PluginManifest;
import edu.iu.uis.eden.plugin.manifest.PluginManifestParser;

/**
 * A {@link PluginLoader} which creates a {@link Plugin} with the given ClassLoader.
 * 
 * <p>This PluginLoader is used in the cases where the Plugin's ClassLoader was created 
 * by the calling code and doesn't need to be created by the loader.
 * 
 * @see Plugin
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClassLoaderPluginLoader implements PluginLoader {
	
	private String pluginManifestPath;
	private ClassLoader classLoader;
	
	public ClassLoaderPluginLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public Plugin load() throws Exception {
		//for now default the embedded plugin to the M.E. of the current context
		QName name = new QName(Core.getCurrentContextConfig().getMessageEntity(), ResourceLoader.EMBEDDED_PLUGIN);
		Plugin plugin = new Plugin(name, loadPluginManifest(pluginManifestPath), classLoader);
		plugin.bindThread();
		try {
			PluginUtils.installResourceLoader(plugin);
			PluginUtils.installPluginListeners(plugin);
		} finally {
			plugin.unbindThread();
		}
		return plugin;
	}

	public void setPluginManifestPath(String pluginManifestPath) {
		this.pluginManifestPath = pluginManifestPath;
	}
	
	public boolean isRemoved() {
		return false;
	}
	
	public boolean isModified() {
		return false;
	}

    private PluginManifest loadPluginManifest(String pluginManifestPath) {
        PluginManifestParser parser = new PluginManifestParser();
        try {
            PluginManifest pluginManifest  = parser.parse(classLoader.getResource(pluginManifestPath), Core.getCurrentContextConfig());
            pluginManifest.parseConfig();
            return pluginManifest;
        } catch (FileNotFoundException e) {
            throw new PluginException("Could not locate the plugin manifest file at path " + pluginManifestPath, e);
        } catch (IOException ioe) {
            throw new PluginException("Could not read the plugin manifest file", ioe);
        } catch (InvalidXmlException ixe) {
            throw new PluginException("Could not parse the plugin manifest file", ixe);
        }
    }
	
}
