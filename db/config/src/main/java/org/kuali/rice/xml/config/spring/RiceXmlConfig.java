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
package org.kuali.rice.xml.config.spring;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.kuali.common.util.metainf.model.MetaInfContext;
import org.kuali.common.util.metainf.model.MetaInfResource;
import org.kuali.common.util.metainf.model.MetaInfResourcePathComparator;
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
 * Defines the configuration for creating the xml property files that define how the database is created.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ AutowiredProjectConfig.class, MetaInfExecutableConfig.class, SpringServiceConfig.class })
public class RiceXmlConfig implements MetaInfContextsConfig {

    private static final Boolean DEFAULT_GENERATE_RELATIVE_PATHS = Boolean.TRUE;
    private static final String RELATIVE_KEY = MetaInfUtils.PROPERTY_PREFIX + ".xml.relative";
    private static final String PREFIX = "xml";
    private static final String INGEST_FILENAME = "ingest";

    // All paths must have the hardcoded separator to be consistent for deployment
    private static final String PATH_SEPARATOR = "/";

    private static final String INITIAL_XML_PATH = "initial-xml" + PATH_SEPARATOR + "2.3.0";
    private static final String UPGRADE_XML_PATH = "upgrades" + PATH_SEPARATOR + "*";
    private static final String ALL_XML_PATH = "**" + PATH_SEPARATOR + "*.xml";

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
     */
    @Override
    @Bean
    public List<MetaInfContext> metaInfContexts() {
        List<MetaInfContext> metaInfContexts = Lists.newArrayList();

        List<MetaInfDataType> types = Lists.newArrayList(MetaInfDataType.BOOTSTRAP, MetaInfDataType.DEMO, MetaInfDataType.TEST);
        List<String> qualifiers = Lists.newArrayList(INITIAL_XML_PATH, UPGRADE_XML_PATH);

        for (MetaInfDataType type : types) {
            for (String qualifier : qualifiers) {
                List<MetaInfContext> contexts = getMetaInfContexts(MetaInfGroup.OTHER, qualifier, type);
                metaInfContexts.addAll(contexts);
            }
        }

        return ImmutableList.copyOf(metaInfContexts);
    }

    /**
     * Creates a list of META-INF contexts for the given {@code group}, {@code qualifier}, and {@code type}.
     *
     * @param group the group of the data to create the contexts for
     * @param qualifier the prefix to add to the initial resource path
     * @param type the type of data to create the contexts for
     *
     * @return a list of META-INF contexts
     */
    protected List<MetaInfContext> getMetaInfContexts(MetaInfGroup group, String qualifier, MetaInfDataType type) {
        List<MetaInfContext> metaInfContexts = Lists.newArrayList();

        File scanDir = build.getOutputDir();
        String encoding = build.getEncoding();

        Comparator<MetaInfResource> comparator = new MetaInfResourcePathComparator();

        String includesKey = MetaInfConfigUtils.getIncludesKey(group, PREFIX);
        String excludesKey = MetaInfConfigUtils.getExcludesKey(group, PREFIX);

        Boolean relativePaths = env.getBoolean(RELATIVE_KEY, DEFAULT_GENERATE_RELATIVE_PATHS);

        List<String> pathQualifiers = MetaInfUtils.getQualifiers(scanDir, project, Lists.newArrayList(qualifier), Lists.<String> newArrayList());

        for (String pathQualifier : pathQualifiers) {
            File outputFile = MetaInfUtils.getOutputFile(project, build, Optional.of(pathQualifier),
                    Optional.<MetaInfDataLocation> absent(), Optional.of(type), INGEST_FILENAME);

            Map<MetaInfGroup, String> defaultIncludes = getDefaultIncludes(pathQualifier, type);
            Map<MetaInfGroup, String> defaultExcludes = getDefaultExcludes();
            List<String> includes = SpringUtils.getNoneSensitiveListFromCSV(env, includesKey, defaultIncludes.get(group));
            List<String> excludes = SpringUtils.getNoneSensitiveListFromCSV(env, excludesKey, defaultExcludes.get(group));

            MetaInfContext context = MetaInfContext.builder(outputFile, encoding, scanDir).comparator(comparator)
                    .includes(includes).excludes(excludes).relativePaths(relativePaths.booleanValue()).build();
            metaInfContexts.add(context);
        }

        return metaInfContexts;
    }

    /**
     * Generates the default mapping of included paths from the given {@code qualifier} and {@code type}.
     *
     * @param qualifier the prefix to add to the initial resource path
     * @param type the type of data to include
     *
     * @return the map of included paths
     */
    protected Map<MetaInfGroup, String> getDefaultIncludes(String qualifier, MetaInfDataType type) {
        Map<MetaInfGroup, String> defaultIncludes = Maps.newEnumMap(MetaInfGroup.class);

        String resourcePath = ProjectUtils.getResourcePath(project.getGroupId(), project.getArtifactId());
        List<String> paths = Lists.newArrayList(resourcePath, qualifier, type.name().toLowerCase(), ALL_XML_PATH);

        defaultIncludes.put(MetaInfGroup.OTHER, StringUtils.join(paths, PATH_SEPARATOR));

        return defaultIncludes;
    }

    /**
     * Generates the default mapping of excluded paths.
     *
     * @return the map of excluded paths
     */
    protected Map<MetaInfGroup, String> getDefaultExcludes() {
        Map<MetaInfGroup, String> defaultExcludes = Maps.newEnumMap(MetaInfGroup.class);

        defaultExcludes.put(MetaInfGroup.OTHER, NullUtils.NONE);

        return defaultExcludes;
    }

}