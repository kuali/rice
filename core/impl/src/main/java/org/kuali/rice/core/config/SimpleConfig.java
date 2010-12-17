/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private Map<String, Object> baseObjects;

	public SimpleConfig() {
		super(new ArrayList<String>());
	}

	public SimpleConfig(Properties properties) {
		super(new ArrayList<String>());
		this.baseProperties = properties;
	}

	public SimpleConfig(List<String> fileLocs, Properties baseProperties) {
		super(fileLocs);
		this.baseProperties = baseProperties;
	}

	public SimpleConfig(List<String> fileLocs) {
		super(fileLocs);
	}

	public SimpleConfig(String fileLoc) {
		this(fileLoc, null);
	}

	public SimpleConfig(String fileLoc, Properties baseProperties) {
		super(fileLoc);
		this.baseProperties = baseProperties;
	}

	@Override
	public Map<String, Object> getBaseObjects() {
		if (this.baseObjects == null) {
		    this.baseObjects = new HashMap<String, Object>();
		}
		return this.baseObjects;
	}

	@Override
	public Properties getBaseProperties() {
		if (this.baseProperties == null) {
			return new Properties();
		}
		return this.baseProperties;
	}
	
}
