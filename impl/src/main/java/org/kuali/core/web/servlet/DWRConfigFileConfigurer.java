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
package org.kuali.core.web.servlet;

import org.kuali.rice.config.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;

public class DWRConfigFileConfigurer implements InitializingBean {
	
	private String configFile;

	public void afterPropertiesSet() throws Exception {
		if (configFile == null) {
			throw new ConfigurationException("property configFile is null.");
		}
		KualiDWRServlet.HACK_ADDITIONAL_FILES.add(getConfigFile());
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

}
