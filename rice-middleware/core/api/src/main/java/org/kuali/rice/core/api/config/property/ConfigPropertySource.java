/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.config.property;

import org.springframework.core.env.EnumerablePropertySource;

/**
 * Defines an {@link EnumerablePropertySource} that can hold Rice config parameters.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigPropertySource extends EnumerablePropertySource<Config> {

	private final Config source;

	public ConfigPropertySource(String name, Config source) {
		super(name, source);
		this.source = source;
	}

	@Override
	public String[] getPropertyNames() {
		return source.getProperties().keySet().toArray(EMPTY_NAMES_ARRAY);
	}

	@Override
	public Object getProperty(String name) {
		return source.getProperty(name);
	}

}
