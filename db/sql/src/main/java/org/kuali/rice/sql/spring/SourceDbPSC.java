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
