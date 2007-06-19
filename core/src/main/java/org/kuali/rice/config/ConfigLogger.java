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
package org.kuali.rice.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Logs information about the configuration at the DEBUG level.
 *
 * @author Eric Westfall
 * @author rkirkend
 */
public class ConfigLogger {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ConfigLogger.class);

	/**
	 * List of keys to suppress the values for and the associated values to print instead.
	 */
	private static final String[][] SECRET_KEYS = 
	{ 
		{"password", "Top Secret Password"}, 
		{"encryption.key", "Top Secret Encryption Key"}
	};
	
	public static void logConfig(Config config) {
		Map<String, String> safeConfig = getDisplaySafeConfig(config.getProperties());
		String props = "";
		for (Iterator iter = safeConfig.entrySet().iterator(); iter.hasNext();) {
			Map.Entry property = (Map.Entry) iter.next();
			String key = (String) property.getKey();
			String value = (String) property.getValue();
			props += key + "=[" + value + "],";
		}
		LOG.debug("Properties used " + props);
	}
	
	/**
	 * Returns a Map of configuration paramters that have display-safe values.  This allows for the suppression
	 * of sensitive configuration paramters from being displayed (i.e. passwords).
	 */
	public static Map<String, String> getDisplaySafeConfig(Properties properties) {
		Map<String, String> parameters = new HashMap<String, String>();
		for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
			Map.Entry property = (Map.Entry) iter.next();
			String key = (String) property.getKey();
			String value = (String) property.getValue();
			String safeValue = value;
			for (String[] secretKey : SECRET_KEYS) {
				if (key.indexOf(secretKey[0]) > -1) {
					safeValue = secretKey[1];
					break;
				}	
			}
			parameters.put(key, safeValue);
		}
		return parameters;
	}
}
