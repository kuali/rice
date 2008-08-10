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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.rice.core.config.BaseConfig;
import org.kuali.rice.core.config.Config;

/**
 * Class representing a plugin's config, containing configuration
 * settings parsed from the config.
 *
 * @see Config
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PluginConfig extends BaseConfig {

	private String resourceLoaderClassname;
	private List listeners = new ArrayList();
	private Properties parentProperties;
	private Map parentObjects;

	public PluginConfig(URL url, Config parentConfig) {
		super(url.toString());
		this.parentProperties = parentConfig.getProperties();
		this.parentObjects = parentConfig.getObjects();
	}

	public PluginConfig(File configFile, Config parentConfig) throws MalformedURLException {
		this(configFile.toURL(), parentConfig);
	}

	public Properties getBaseProperties() {
		return this.parentProperties;
	}

	public Map getBaseObjects() {
		return this.parentObjects;
	}

	public void addListener(String listenerClass) {
		listeners.add(listenerClass);
	}

	public List getListeners() {
		return listeners;
	}

	public void setResourceLoaderClassname(String resourceLoaderClassname) {
		this.resourceLoaderClassname = resourceLoaderClassname;
	}

	public String getResourceLoaderClassname() {
		return resourceLoaderClassname;
	}

    public String toString() {
        return "[PluginConfig: resourceLoaderClassname: " + getResourceLoaderClassname() + "]";
    }
}