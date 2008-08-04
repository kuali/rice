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
package org.kuali.rice.config.spring;

import java.util.List;
import java.util.Properties;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.FactoryBean;

public class ConfigFactoryBean implements FactoryBean {

	private List<String> configLocations;

	public static String CONFIG_OVERRIDE_LOCATION;

	public Object getObject() throws Exception {
		if (getConfigLocations() == null) {
			throw new ConfigurationException("No config locations declared, at least one is required");
		}
		Properties baseProperties = new Properties();
		if (Core.getCurrentContextConfig() != null) {
			baseProperties = Core.getCurrentContextConfig().getProperties();
		}
		SimpleConfig config = null;
		if (CONFIG_OVERRIDE_LOCATION != null) {
			config = new SimpleConfig(CONFIG_OVERRIDE_LOCATION, baseProperties);
		} else {
			config = new SimpleConfig(getConfigLocations(), baseProperties);
		}

		config.parseConfig();
		return config;
	}

	public Class getObjectType() {
		return Config.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public List<String> getConfigLocations() {
		return this.configLocations;
	}

	public void setConfigLocations(List<String> configLocations) {
		this.configLocations = configLocations;
	}

}
