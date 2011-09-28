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

package org.kuali.rice.core.impl.config.property;

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

	private final Properties baseProperties;
	private final Map<String, Object> baseObjects = new HashMap<String, Object>();

	public SimpleConfig() {
		super(new ArrayList<String>());
        this.baseProperties = new Properties();
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
		this(fileLocs, new Properties());
	}

	public SimpleConfig(String fileLoc) {
		this(fileLoc, new Properties());
	}

	public SimpleConfig(String fileLoc, Properties baseProperties) {
		super(fileLoc);
		this.baseProperties = baseProperties;
	}

	@Override
	public Map<String, Object> getBaseObjects() {
		return this.baseObjects;
	}

	@Override
	public Properties getBaseProperties() {
		return this.baseProperties;
	}
}