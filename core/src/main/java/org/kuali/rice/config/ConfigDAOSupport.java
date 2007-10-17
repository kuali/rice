/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.config;

import org.kuali.rice.core.Core;

/**
 * A convienent superclass or utility for DAOs which read and write data from the various components in
 * the configuration framework.  Also allows for access to the NodeSettings service which
 * allows for persistence of configuration parameters.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ConfigDAOSupport {

//	private NodeSettings nodeSettings;
	
	/**
	 * Returns a String property from the current context config.
	 */
	public String getStringProperty(String name) {
		String property = getConfig().getProperty(name);
//		if (property == null) {
//			property = getNodeSettings().getSetting(name);
//		}
		return property;
	}
	
	public Integer getIntProperty(String name) {
		String property = getStringProperty(name);
		if (property == null) {
			return null;
		}
		return Integer.valueOf(property);
	}
	
	public Long getLongProperty(String name) {
		String property = getStringProperty(name);
		if (property == null) {
			return null;
		}
		return Long.valueOf(property);
	}
	
	public Boolean getBooleanProperty(String name) {
		String property = getStringProperty(name);
		if (property == null) {
			return null;
		}
		return Boolean.valueOf(property);
	}
	
	public Boolean getBooleanProperty(String name, Boolean defaultValue) {
		Boolean property = getBooleanProperty(name);
		if (property == null) {
			return defaultValue;
		}
		return property;
	}
	
	public Object getObjectProperty(String name) {
		return getConfig().getObject(name);
	}
	
//	public NodeSettings getNodeSettings() {
//		return this.nodeSettings;
//	}
//	
//	public void setNodeSettings(NodeSettings nodeSettings) {
//		this.nodeSettings = nodeSettings;
//	}
	
	public Config getConfig() {
		return Core.getCurrentContextConfig();
	}
	
}
