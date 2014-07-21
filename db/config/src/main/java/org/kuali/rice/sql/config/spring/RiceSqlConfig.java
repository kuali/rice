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
package org.kuali.rice.sql.config.spring;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.kuali.common.util.metainf.model.MetaInfContext;
import org.kuali.common.util.metainf.model.MetaInfResource;
import org.kuali.common.util.metainf.model.MetaInfResourceLocationComparator;
import org.kuali.common.util.metainf.service.MetaInfUtils;
import org.kuali.common.util.metainf.spring.MetaInfConfigUtils;
import org.kuali.common.util.metainf.spring.MetaInfContextsConfig;
import org.kuali.common.util.metainf.spring.MetaInfDataLocation;
import org.kuali.common.util.metainf.spring.MetaInfDataType;
import org.kuali.common.util.metainf.spring.MetaInfExecutableConfig;
import org.kuali.common.util.metainf.spring.MetaInfGroup;
import org.kuali.common.util.nullify.NullUtils;
import org.kuali.common.util.project.ProjectUtils;
import org.kuali.common.util.project.model.Build;
import org.kuali.common.util.project.model.Project;
import org.kuali.common.util.project.spring.AutowiredProjectConfig;
import org.kuali.common.util.spring.SpringUtils;
import org.kuali.common.util.spring.env.EnvironmentService;
import org.kuali.common.util.spring.service.SpringServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Defines the configuration for creating the sql property files that define how the database is created.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({AutowiredProjectConfig.class, MetaInfExecutableConfig.class, SpringServiceConfig.class})
public class RiceSqlConfig implements MetaInfContextsConfig {

    private static final Boolean DEFAULT_GENERATE_RELATIVE_PATHS = Boolean.TRUE;
    private static final String RELATIVE_KEY = MetaInfUtils.PROPERTY_PREFIX + ".sql.relative";
    private static final String PREFIX = "sql";
    private static final String DEFAULT_VENDORS = "mysql,oracle";
    private static final String VENDORS_KEY = MetaInfUtils.PROPERTY_PREFIX + ".db.vendors";

    // All paths must have the hardcoded separator to be consistent for deployment
    private static final String PATH_SEPARATOR = "/";

    private static final String INITIAL_SQL_PATH = "initial-sql" + PATH_SEPARATOR + "2.3.0";
    private static final String UPGRADE_SQL_PATH = "upgrades" + PATH_SEPARATOR + "*";
    private static final String SCHEMA_SQL_PATH = "rice-" + MetaInfGroup.SCHEMA.name().toLowerCase() + ".sql";
    private static final String CONSTRAINTS_SQL_PATH = "rice-" + MetaInfGroup.CONSTRAINTS.name().toLowerCase() + ".sql";
    private static final String ALL_SQL_PATH = "*.sql";

    /**
     * The Spring environment.
     */
    @Autowired
    EnvironmentService env;

    /**
     * The Rice Maven project.
     */
    @Autowired
    Project project;

    /**
     * The build information.
     */
    @Autowired
    Build build;

    /**
     * {@inheritDoc}
     *
     * <p>
     * All of the initial data (the data included in the {@code MetaInfGroup.SCHEMA}, {@code MetaInfGroup.CONSTRAINTS},
     * or {@code MetaInfGroup.DATA} groups) needs to be added before the update data (the data included in the
     * {@code MetaInfGroup.OTHER} group).
     * </p>
     */
    @Override
    @Bean
    public List<MetaInfContext> metaInfContexts() {
        List<MetaInfContext> metaInfContexts = Lists.newArrayList();

        List<MetaInfDataType> types = Lists.newArrayList(MetaInfDataType.BOOTSTRAP, MetaInfDataType.DEMO, MetaInfDataType.TEST);
        List<String> vendors = SpringUtils.getNoneSensitiveListFromCSV(env, VENDORS_KEY, DEFAULT_VENDORS);
        List<MetaInfGroup> groups = Lists.newArrayList(MetaInfGroup.SCHEMA, MetaInfGroup.DATA, MetaInfGroup.CONSTRAINTS);

        for (MetaInfDataType type : types) {
            for (MetaInfDataLocation location : MetaInfDataLocation.values()) {
                for (String vendor : vendors) {
                    for (MetaInfGroup group : groups) {
                        List<MetaInfContext> contexts = getMetaInfContexts(group, INITIAL_SQL_PATH, vendor,
                                location, type);
                        metaInfContexts.addAll(contexts);
                    }
                }
            }
        }

        for (MetaInfDataType type : types) {
            for (MetaInfDataLocation location : MetaInfDataLocation.values()) {
                for (String vendor : vendors) {
                    List<MetaInfContext> contexts = getMetaInfContexts(MetaInfGroup.OTHER, UPGRADE_SQL_PATH, vendor,
                            location, type);
                    metaInfContexts.addAll(contexts);
                }
            }
        }

        return ImmutableList.copyOf(metaInfContexts);
    }

    /**
     * Creates a list of META-INF contexts for the given {@code group}, {@code qualifier}, {@code vendor},
     * {@code location}, and {@code type}.
     *
     * @param group the group of the data to create the context for
     * @param qualifier the prefix to add to the initial resource path
     * @param vendor the database vendor to create the context for
     * @param location the location of the data to create the context for
     * @param type the type of data to create the context for
     *
     * @return a list of META-INF contexts
     */
    protected List<MetaInfContext> getMetaInfContexts(MetaInfGroup group, String qualifier, String vendor, MetaInfDataLocation location, MetaInfDataType type) {
        List<MetaInfContext> metaInfContexts = Lists.newArrayList();

        File scanDir = build.getOutputDir();
        String encoding = build.getEncoding();

        Comparator<MetaInfResource> comparator = new MetaInfResourceLocationComparator();

        String includesKey = MetaInfConfigUtils.getIncludesKey(group, PREFIX) + "." + vendor;
        String excludesKey = MetaInfConfigUtils.getExcludesKey(group, PREFIX) + "." + vendor;

        Boolean relativePaths = env.getBoolean(RELATIVE_KEY, DEFAULT_GENERATE_RELATIVE_PATHS);

        List<String> pathQualifiers = MetaInfUtils.getQualifiers(scanDir, project, Lists.newArrayList(qualifier), Lists.<String> newArrayList());

        for (String pathQualifier : pathQualifiers) {
            File outputFile = MetaInfUtils.getOutputFile(project, build, Optional.of(pathQualifier + PATH_SEPARATOR + vendor),
                    Optional.of(location), Optional.of(type), group.name().toLowerCase());

            Map<MetaInfGroup, String> defaultIncludes = getDefaultIncludes(pathQualifier, vendor, location, type);
            Map<MetaInfGroup, String> defaultExcludes = getDefaultExcludes(defaultIncludes);
            List<String> includes = SpringUtils.getNoneSensitiveListFromCSV(env, includesKey, defaultIncludes.get(group));
            List<String> excludes = SpringUtils.getNoneSensitiveListFromCSV(env, excludesKey, defaultExcludes.get(group));

            MetaInfContext context = MetaInfContext.builder(outputFile, encoding, scanDir).comparator(comparator)
                    .includes(includes).excludes(excludes).relativePaths(relativePaths.booleanValue()).build();
            metaInfContexts.add(context);
        }

        return metaInfContexts;
    }

    /**
     * Generates the default mapping of included paths from the given {@code qualifier}, {@code vendor}, and
     * {@code type}.
     *
     * @param qualifier the prefix to add to the initial resource path
     * @param vendor the database vendor to include
     * @param location the location of the data to include
     * @param type the type of data to include
     *
     * @return the map of included paths
     */
    protected Map<MetaInfGroup, String> getDefaultIncludes(String qualifier, String vendor, MetaInfDataLocation location, MetaInfDataType type) {
        Map<MetaInfGroup, String> defaultIncludes = Maps.newEnumMap(MetaInfGroup.class);

        String resourcePath = ProjectUtils.getResourcePath(project.getGroupId(), project.getArtifactId());
        List<String> paths = Lists.newArrayList(resourcePath, qualifier, vendor, location.name().toLowerCase(), type.name().toLowerCase());
        String value = StringUtils.join(paths, PATH_SEPARATOR);

        defaultIncludes.put(MetaInfGroup.SCHEMA, value + PATH_SEPARATOR + SCHEMA_SQL_PATH);
        defaultIncludes.put(MetaInfGroup.DATA, value + PATH_SEPARATOR + ALL_SQL_PATH);
        defaultIncludes.put(MetaInfGroup.CONSTRAINTS, value + PATH_SEPARATOR + CONSTRAINTS_SQL_PATH);
        defaultIncludes.put(MetaInfGroup.OTHER, value + PATH_SEPARATOR + ALL_SQL_PATH);

        return defaultIncludes;
    }

    /**
     * Generates the default mapping of excluded paths from the {@code defaultIncludes} map.
     *
     * <p>
     * Generally, nothing is excluded, but there is a special case with {@code MetaInfGroup.DATA} where it does not
     * include either the {@code MetaInfGroup.SCHEMA} or {@code MetaInfGroup.CONSTRAINTS}.
     * </p>
     *
     * @param defaultIncludes the map of included paths
     *
     * @return the map of excluded paths
     */
    protected Map<MetaInfGroup, String> getDefaultExcludes(Map<MetaInfGroup, String> defaultIncludes) {
        Map<MetaInfGroup, String> defaultExcludes = Maps.newEnumMap(MetaInfGroup.class);

        List<String> dataExcludes = Lists.newArrayList(defaultIncludes.get(MetaInfGroup.SCHEMA), defaultIncludes.get(MetaInfGroup.CONSTRAINTS));

        defaultExcludes.put(MetaInfGroup.SCHEMA, NullUtils.NONE);
        defaultExcludes.put(MetaInfGroup.DATA, StringUtils.join(dataExcludes, ","));
        defaultExcludes.put(MetaInfGroup.CONSTRAINTS, NullUtils.NONE);
        defaultExcludes.put(MetaInfGroup.OTHER, NullUtils.NONE);

        return defaultExcludes;
    }

}