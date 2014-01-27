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
package org.kuali.rice.sql.spring;

import java.util.List;

import javax.sql.DataSource;

import org.kuali.common.jdbc.model.context.JdbcContext;
import org.kuali.common.jdbc.reset.DefaultJdbcResetConfig;
import org.kuali.common.jdbc.service.spring.DataSourceConfig;
import org.kuali.common.jdbc.sql.spring.DbaContextConfig;
import org.kuali.common.jdbc.sql.spring.JdbcContextsConfig;
import org.kuali.common.jdbc.suppliers.ResourcesSupplierFactory;
import org.kuali.common.jdbc.suppliers.SqlSupplier;
import org.kuali.common.jdbc.suppliers.spring.SuppliersFactoryConfig;
import org.kuali.common.jdbc.vendor.model.DatabaseVendor;
import org.kuali.common.util.metainf.service.MetaInfUtils;
import org.kuali.common.util.metainf.spring.MetaInfGroup;
import org.kuali.common.util.project.ProjectService;
import org.kuali.common.util.project.model.Project;
import org.kuali.common.util.project.spring.ProjectServiceConfig;
import org.kuali.rice.sql.project.SqlProjectConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.collect.ImmutableList;

/**
 * Used by developers (to reset their local db), CI (to validate changes), and by the deploy process to reset the
 * database for instances of the running application.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ DbaContextConfig.class, SuppliersFactoryConfig.class, ProjectServiceConfig.class, DefaultJdbcResetConfig.class })
public class SourceSqlConfig implements JdbcContextsConfig {

	@Autowired
	DbaContextConfig dba;

	@Autowired
	ResourcesSupplierFactory factory;

	@Autowired
	ProjectService projectService;

	@Autowired
	DatabaseVendor vendor;

	@Autowired
	DataSourceConfig dataSources;

	@Bean
	public Project riceSqlProject() {
		return projectService.getProject(SqlProjectConstants.ID);
	}

	@Override
	@Bean
	public List<JdbcContext> jdbcContexts() {
		JdbcContext before = dba.dbaBeforeContext();
		JdbcContext schema = getJdbcContext(MetaInfGroup.SCHEMA, true);
		JdbcContext data = getJdbcContext(MetaInfGroup.DATA, true);
		JdbcContext constraints = getJdbcContext(MetaInfGroup.CONSTRAINTS, true);
		JdbcContext other = getJdbcContext(MetaInfGroup.OTHER, false);
		JdbcContext after = dba.dbaAfterContext();
		return ImmutableList.of(before, schema, data, constraints, other, after);
	}

	protected JdbcContext getJdbcContext(MetaInfGroup group, boolean multithreaded) {
		String resourcesLocation = MetaInfUtils.getClasspathResource(riceSqlProject(), vendor.getCode(), group);
		List<SqlSupplier> suppliers = factory.getSuppliers(resourcesLocation);
		DataSource dataSource = dataSources.dataSource();
		String message = "[" + group.name().toLowerCase() + ":" + (multithreaded ? "concurrent" : "sequential") + "]";
		return new JdbcContext.Builder(dataSource, suppliers).message(message).multithreaded(multithreaded).build();
	}
}
