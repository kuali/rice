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
package edu.iu.uis.eden.clientapp;

import java.util.Map;
import java.util.Properties;

import org.kuali.rice.config.BaseConfig;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.EdenConstants;

/**
 * Configuration object for client applications.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClientConfig extends BaseConfig {

	public ClientConfig() {
		super(EdenConstants.DEFAULT_APPLICATION_CONFIG_LOCATION);
		Core.init(this);
	}

	public Properties getBaseProperties() {
		//so workflow can use this config for client and point to the workflow.xml with server settings (so everyone can
		//rock the workflow.xml file

		if (Core.getRootConfig() != null) {
			return Core.getRootConfig().getProperties();
		}
		return null;
	}

	public Map getBaseObjects() {
		if (Core.getRootConfig() != null) {
			return Core.getRootConfig().getObjects();
		}
		return null;
	}
}
