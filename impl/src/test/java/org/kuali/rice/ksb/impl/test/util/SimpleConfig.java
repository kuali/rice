/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.ksb.impl.test.util;

import org.kuali.rice.core.impl.config.property.BaseConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * A simple Config implementation which has no base properties
 * or base objects.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SimpleConfig extends BaseConfig {

	private Properties baseProperties;
	
	public SimpleConfig(Map<String, String> stringMap) {
		super(new ArrayList<String>());
		Properties properties = new Properties();
		properties.putAll(stringMap);
		this.baseProperties = properties;
	}

	@Override
	public Map<String, Object> getBaseObjects() {
		return new HashMap<String, Object>();
	}

	@Override
	public Properties getBaseProperties() {
		if (this.baseProperties == null) {
			return new Properties();
		}
		return this.baseProperties;
	}

    @Override
    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }
	
}
