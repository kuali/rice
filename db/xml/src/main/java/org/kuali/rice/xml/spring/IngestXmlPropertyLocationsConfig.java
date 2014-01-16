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
package org.kuali.rice.xml.spring;

import java.util.List;

import org.kuali.common.util.properties.Location;
import org.kuali.common.util.properties.PropertiesLocationService;
import org.kuali.common.util.properties.spring.PropertiesLocationServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Defines the property locations for the workflow XML ingestion process.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ PropertiesLocationServiceConfig.class })
public class IngestXmlPropertyLocationsConfig {

	@Autowired
	PropertiesLocationService service;

	@Bean
	public ImmutableList<Location> riceIngestXmlPropertyLocations() {
		List<Location> locations = Lists.newArrayList();
		locations.add(service.getLocation(RiceXmlProperties.DB.getResource()));
		return ImmutableList.copyOf(locations);
	}

}
