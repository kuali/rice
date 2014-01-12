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
package org.kuali.rice.deploy.spring;

import java.util.ArrayList;
import java.util.List;

import org.kuali.common.deploy.env.model.DeployEnvironment;
import org.kuali.common.deploy.env.spring.DefaultDeployEnvironmentConfig;
import org.kuali.common.deploy.env.spring.DeployEnvironmentConfig;
import org.kuali.common.deploy.project.DeployProjectConstants;
import org.kuali.common.jdbc.project.spring.JdbcProjectConfig;
import org.kuali.common.jdbc.project.spring.JdbcPropertyLocationsConfig;
import org.kuali.common.util.Assert;
import org.kuali.common.util.Mode;
import org.kuali.common.util.project.ProjectService;
import org.kuali.common.util.project.ProjectUtils;
import org.kuali.common.util.project.model.Project;
import org.kuali.common.util.project.model.ProjectIdentifier;
import org.kuali.common.util.project.spring.AutowiredProjectConfig;
import org.kuali.common.util.project.spring.ProjectServiceConfig;
import org.kuali.common.util.properties.Location;
import org.kuali.common.util.properties.PropertiesLocationService;
import org.kuali.common.util.properties.PropertiesService;
import org.kuali.common.util.properties.spring.DefaultPropertiesServiceConfig;
import org.kuali.common.util.properties.spring.PropertiesLocationServiceConfig;
import org.kuali.common.util.property.PropertyFormat;
import org.kuali.common.util.spring.PropertySourceUtils;
import org.kuali.common.util.spring.service.PropertySourceConfig;
import org.kuali.rice.deploy.RiceDeployProjectConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertySource;

@Configuration
@Import({ JdbcProjectConfig.class, JdbcPropertyLocationsConfig.class, DefaultPropertiesServiceConfig.class, PropertiesLocationServiceConfig.class, AutowiredProjectConfig.class,
		DefaultDeployEnvironmentConfig.class, ProjectServiceConfig.class })
public class DeployPSC implements PropertySourceConfig {

	private static final ProjectIdentifier DEPLOY = DeployProjectConstants.ID;

	@Autowired
	JdbcPropertyLocationsConfig jdbc;

	@Autowired
	PropertiesService service;

	@Autowired
	PropertiesLocationService locationService;

	@Autowired
	Project project;

	@Autowired
	ProjectService projectService;

	@Autowired
	DeployEnvironmentConfig deployEnvConfig;

	@Override
	@Bean
	public PropertySource<?> propertySource() {

		DeployEnvironment deployEnv = deployEnvConfig.deployEnvironment();

		// Generic jdbc locations
		List<Location> jdbcLocations = jdbc.jdbcPropertyLocations();

		// Pull in configuration specific to this branch of Rice
		Location branchLoc = getOptionalLocation(RiceDeployProjectConstants.ID, "deploy.properties");

		// Extract the group code
		String groupCode = project.getProperties().getProperty("project.groupId.code");
		Assert.noBlanks(groupCode);

		// Pull in configuration specific to this Rice application
		Location appLoc = getOptionalLocation(DEPLOY, groupCode + "/" + project.getArtifactId() + ".properties");

		// Pull in configuration specific to the environment we are deploying to
		Location envLoc = getOptionalLocation(DEPLOY, groupCode + "/" + deployEnv.getName() + ".properties");

		// Combine them making sure Rice properties go in last
		List<Location> locations = new ArrayList<Location>();
		locations.addAll(jdbcLocations);
		locations.addAll(getKualiDeployLocs());
		locations.add(branchLoc);
		locations.add(appLoc);
		locations.add(envLoc);
		return PropertySourceUtils.getPropertySource(service, locations);
	}

	protected Location getOptionalLocation(ProjectIdentifier pid, String filename) {
		String value = ProjectUtils.getClasspathPrefix(pid) + "/" + filename;
		Project project = projectService.getProject(pid);
		String encoding = ProjectUtils.getEncoding(project);
		return new Location(value, encoding, Mode.INFORM, PropertyFormat.NORMAL, true);

	}

	protected List<Location> getKualiDeployLocs() {
		List<Location> locs = new ArrayList<Location>();
		locs.add(getKualiDeployLoc("common.properties"));
		locs.add(getKualiDeployLoc("appdynamics.properties"));
		locs.add(getKualiDeployLoc("aws.properties"));
		locs.add(getKualiDeployLoc("tomcat.properties"));
		locs.add(getKualiDeployLoc("db.properties"));
		locs.add(getKualiDeployLoc("rice/common.properties"));
		locs.add(getKualiDeployLoc("rice/db.properties"));
		locs.add(getKualiDeployLoc("rice/aws.properties"));
		locs.add(getKualiDeployLoc("rice/appdynamics.properties"));
		return locs;
	}

	protected Location getKualiDeployLoc(String filename) {
		return locationService.getLocation(DEPLOY, filename);
	}

}
