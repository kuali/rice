/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;

import edu.iu.uis.eden.config.BaseConfig;

public class RiceConfigurer extends BaseConfig implements InitializingBean {
    
	private static String configurationFile;
    public static final String DEFAULT_CONFIGURATION_FILE = "classpath:knsConfig.xml";
	
    public RiceConfigurer() {
		super(getConfigurationFile());
	}
    
	@Override
	public Map<String, Object> getBaseObjects() {
		return null;
	}
	@Override
	public Properties getBaseProperties() {
		return null;
	}
	
	public void afterPropertiesSet() throws Exception {
		this.parseConfig();
	}
	
    public static String getConfigurationFile() {
        if (configurationFile == null) {
            return DEFAULT_CONFIGURATION_FILE;
        }
        return configurationFile;
    }

    public static void setConfigurationFile(String overrideConfigurationFile) {
        RiceConfigurer.configurationFile = overrideConfigurationFile;
    }
}