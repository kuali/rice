/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.sql.spring;

import java.util.ArrayList;
import java.util.List;

import org.kuali.common.deploy.project.DeployProjectConstants;
import org.kuali.common.jdbc.project.spring.JdbcProjectConfig;
import org.kuali.common.jdbc.project.spring.JdbcPropertyLocationsConfig;
import org.kuali.common.util.project.model.ProjectIdentifier;
import org.kuali.common.util.properties.Location;
import org.kuali.common.util.properties.PropertiesLocationService;
import org.kuali.common.util.properties.PropertiesService;
import org.kuali.common.util.properties.spring.DefaultPropertiesServiceConfig;
import org.kuali.common.util.properties.spring.PropertiesLocationServiceConfig;
import org.kuali.common.util.spring.PropertySourceUtils;
import org.kuali.common.util.spring.service.PropertySourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertySource;

@Configuration
@Import({ JdbcProjectConfig.class, JdbcPropertyLocationsConfig.class, DefaultPropertiesServiceConfig.class, PropertiesLocationServiceConfig.class })
public class SourceDbPSC implements PropertySourceConfig {

	private static final ProjectIdentifier DEPLOY = DeployProjectConstants.ID;

	@Autowired
	JdbcPropertyLocationsConfig jdbcConfig;

	@Autowired
	PropertiesService service;

	@Autowired
	PropertiesLocationService locationService;

	@Override
	@Bean
	public PropertySource<?> propertySource() {
		// Rice specific locations
		Location rice1 = locationService.getLocation(DEPLOY, "rice/db.properties");
		Location rice2 = locationService.getLocation(DEPLOY, "rice/initialize-source-db.properties");

		// Generic jdbc locations
		List<Location> jdbc = jdbcConfig.jdbcPropertyLocations();

		// Combine them making sure Rice properties go in last
		List<Location> locations = new ArrayList<Location>();
		locations.addAll(jdbc);
		locations.add(rice1);
		locations.add(rice2);
		return PropertySourceUtils.getPropertySource(service, locations);
	}
}
