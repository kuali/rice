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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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
import org.kuali.common.util.metainf.spring.MetaInfDataLocation;
import org.kuali.common.util.metainf.spring.MetaInfDataType;
import org.kuali.common.util.metainf.spring.MetaInfGroup;
import org.kuali.common.util.project.ProjectService;
import org.kuali.common.util.project.model.Project;
import org.kuali.common.util.project.spring.ProjectServiceConfig;
import org.kuali.rice.db.config.profile.MetaInfDataLocationProfileConfig;
import org.kuali.rice.db.config.profile.MetaInfDataTypeProfileConfig;
import org.kuali.rice.db.config.profile.MetaInfFilterConfig;
import org.kuali.rice.db.config.profile.RiceClientBootstrapConfig;
import org.kuali.rice.db.config.profile.RiceClientDemoConfig;
import org.kuali.rice.db.config.profile.RiceServerDemoConfig;
import org.kuali.rice.db.config.profile.RiceServerDemoFilterConfig;
import org.kuali.rice.db.config.profile.RiceMasterConfig;
import org.kuali.rice.db.config.profile.RiceServerBootstrapConfig;
import org.kuali.rice.sql.project.SqlProjectConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Import({ DbaContextConfig.class, SuppliersFactoryConfig.class, ProjectServiceConfig.class, DefaultJdbcResetConfig.class,
          RiceClientBootstrapConfig.class, RiceClientDemoConfig.class, RiceServerBootstrapConfig.class, RiceServerDemoConfig.class,
          RiceServerDemoFilterConfig.class, RiceMasterConfig.class })
public class SourceSqlConfig implements JdbcContextsConfig {

    /**
     * The DBA context.
     */
	@Autowired
	DbaContextConfig dba;

    /**
     * The factory for creating SQL resources.
     */
	@Autowired
	ResourcesSupplierFactory factory;

    /**
     * The Maven project service.
     */
	@Autowired
	ProjectService projectService;

    /**
     * The vendor of the database to run the SQL against.
     */
	@Autowired
	DatabaseVendor vendor;

    /**
     * The data source configuration.
     */
	@Autowired
	DataSourceConfig dataSources;

    /**
     * The {@link MetaInfDataLocation} profile.
     */
    @Autowired(required = false)
    MetaInfDataLocationProfileConfig locationConfig;

    /**
     * The {@link MetaInfDataType} profile.
     */
    @Autowired(required = false)
    MetaInfDataTypeProfileConfig typeConfig;

    /**
     * The data filtering profile.
     */
    @Autowired(required = false)
    MetaInfFilterConfig serverDemoFilterConfig;

    /**
     * Returns the Rice Maven project.
     *
     * @return the Rice Maven project
     */
	@Bean
	public Project riceSqlProject() {
		return projectService.getProject(SqlProjectConstants.ID);
	}

    /**
     * {@inheritDoc}
     *
     * <p>
     * All of the initial data (the data included in the {@code MetaInfGroup.SCHEMA}, {@code MetaInfGroup.CONSTRAINTS},
     * or {@code MetaInfGroup.DATA} groups) needs to be applied before the update data (the data included in the
     * {@code MetaInfGroup.OTHER} group).
     * </p>
     */
	@Override
	@Bean
	public List<JdbcContext> jdbcContexts() {
        List<JdbcContext> jdbcContexts = Lists.newArrayList();

        List<MetaInfDataType> types = getTypes();
        List<MetaInfDataLocation> locations = getLocations();
        List<MetaInfGroup> groups = Lists.newArrayList(MetaInfGroup.SCHEMA, MetaInfGroup.DATA, MetaInfGroup.CONSTRAINTS);

        jdbcContexts.add(dba.dbaBeforeContext());

        for (MetaInfDataType type : types) {
            for (MetaInfDataLocation location : locations) {
                for (MetaInfGroup group : groups) {
                    jdbcContexts.add(getJdbcContext(group, location, type, true));
                }

                jdbcContexts.add(getJdbcContext(MetaInfGroup.OTHER, location, type, false));
            }
        }

        jdbcContexts.add(dba.dbaAfterContext());

        return ImmutableList.copyOf(jdbcContexts);
	}

    /**
     * Returns the list of {@link MetaInfDataType}s to be applied to the database, returning an empty list if no
     * profiles are active.
     *
     * @return the list of {@link MetaInfDataType}s to be applied to the database (if any)
     */
    protected List<MetaInfDataType> getTypes() {
        return typeConfig != null ? typeConfig.getMetaInfDataTypes() : Lists.<MetaInfDataType> newArrayList();
    }

    /**
     * Returns the list of {@link MetaInfDataLocation}s to be applied to the database, returning an empty list if no
     * profiles are active.
     *
     * @return the list of {@link MetaInfDataLocation}s to be applied to the database (if any)
     */
    protected List<MetaInfDataLocation> getLocations() {
        return locationConfig != null ? locationConfig.getMetaInfDataLocations() : Lists.<MetaInfDataLocation> newArrayList();
    }

    /**
     * Creates the JDBC context for the given {@code group}, {@code location}, and {@code type}.
     *
     * @param group the group of the data to create the context for
     * @param location the location of the data to create the context for
     * @param type the type of data to create the context for
     * @param multithreaded whether or not to run the context in multiple threads
     *
     * @return the JDBC context
     */
	protected JdbcContext getJdbcContext(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type, boolean multithreaded) {
        DataSource dataSource = dataSources.dataSource();

        List<SqlSupplier> suppliers = Lists.newArrayList();

        if (isIncluded(group, location, type) && !isExcluded(group, location, type)) {
            String resourcesLocation = MetaInfUtils.getClasspathResource(riceSqlProject(), Optional.of(vendor.getCode()),
                    Optional.of(location), Optional.of(type), group.name().toLowerCase());
            suppliers.addAll(factory.getSuppliers(resourcesLocation));
        }

        String message = "[" + group.name().toLowerCase() + ":" + (multithreaded ? "concurrent" : "sequential") + "]";

        return new JdbcContext.Builder(dataSource, suppliers).message(message).multithreaded(multithreaded).build();
	}

    /**
     * Returns whether to include the data for the {@link MetaInfGroup}, {@link MetaInfDataLocation}, and
     * {@link MetaInfDataType}.
     *
     * @param group the {link MetaInfGroup} to check
     * @param location the {@link MetaInfDataLocation} to check
     * @param type the {@link MetaInfDataType} to check
     *
     * @return true if the data set should be included, false otherwise
     */
    protected boolean isIncluded(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type) {
        return serverDemoFilterConfig == null || serverDemoFilterConfig.isIncluded(group, location, type);
    }

    /**
     * Returns whether to exclude the data for the {@link MetaInfGroup}, {@link MetaInfDataLocation}, and
     * {@link MetaInfDataType}.
     *
     * @param group the {link MetaInfGroup} to check
     * @param location the {@link MetaInfDataLocation} to check
     * @param type the {@link MetaInfDataType} to check
     *
     * @return true if the data set should be excluded, false otherwise
     */
    protected boolean isExcluded(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type) {
        return serverDemoFilterConfig != null && serverDemoFilterConfig.isExcluded(group, location, type);
    }

}