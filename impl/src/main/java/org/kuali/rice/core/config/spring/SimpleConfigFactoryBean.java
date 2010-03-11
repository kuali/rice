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
package org.kuali.rice.core.config.spring;

import org.kuali.rice.core.config.Config;
import org.springframework.beans.factory.FactoryBean;

/**
 * Allows a config object to set and {@link Config#parseConfig()} to be called before it is returned.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SimpleConfigFactoryBean implements FactoryBean {

	private Config config;
	
	/**
	 * Gets the config.
	 * @return config the config
	 */
	public Config getConfig() {
		return this.config;
	}

	/**
	 * Sets the config.
	 * @param config the config
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/** 
	 * Returns a config object after calling {@link Config#parseConfig()}.
	 * {@inheritDoc}
	 */
	public Config getObject() throws Exception {
		if (config == null) {
			throw new IllegalStateException("config has not been set");
		}
		
		config.parseConfig();
		
		return config;
	}

	/** {@inheritDoc} */
	public Class<Config> getObjectType() {
		return Config.class;
	}

	/** {@inheritDoc} */
	public boolean isSingleton() {
		return true;
	}
}
