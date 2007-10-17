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

import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.resourceloader.ResourceLoader;

/**
 * Maintains a registry of Plugins and allows for loading of resources from those plugins.  It is
 * up to the PluginRegistry implementation to determine the resource loading strategy if it contains
 * more than a single plugin.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface PluginRegistry extends ResourceLoader {
    
	public List<PluginEnvironment> getPluginEnvironments();
	
	public void addPluginEnvironment(PluginEnvironment pluginEnvironment);
	
	public PluginEnvironment removePluginEnvironment(QName pluginName);
	
	public PluginEnvironment getPluginEnvironment(QName pluginName);
	
	//public List<Plugin> getPlugins();
	
//    public Collection getPlugins();

//    public Set getPluginNames();

    //public Plugin getPlugin(QName pluginName);
    
//    public void removePlugin(String pluginName);

//    public void addPlugin(Plugin plugin);
    
    public Plugin getInstitutionalPlugin();

    //public void addPluginDirectory(String directoryName);

    //public List getPluginDirectories();

    //public void setPluginDirectories(List pluginDirectories);

    //public Class findClass(String className) throws ClassNotFoundException;

    //public Object loadObject(String className) throws ResourceUnavailableException;

    //public void start();

    //public void stop();

    //public boolean isStarted();
    
    /**
     * Returns a proxy to an extension object which sets the current context classloader appropriatly.
     */
    //public Object proxy(Object extensionObject);
    
}