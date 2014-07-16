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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.metainf.model.PathComparator;
import org.kuali.common.util.metainf.service.MetaInfUtils;
import org.kuali.common.util.metainf.spring.MetaInfDataLocation;
import org.kuali.common.util.metainf.spring.MetaInfDataType;
import org.kuali.common.util.metainf.spring.RiceXmlConfig;
import org.kuali.common.util.spring.env.EnvironmentService;
import org.kuali.rice.db.config.profile.MetaInfDataTypeProfileConfig;
import org.kuali.rice.db.config.profile.RiceServerBootstrapConfig;
import org.kuali.rice.db.config.profile.RiceServerDemoConfig;
import org.kuali.rice.db.config.profile.RiceMasterConfig;
import org.kuali.rice.xml.ingest.IngestXmlExecutable;
import org.kuali.rice.xml.project.XmlProjectConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;

/**
 * Central configuration file for launching the workflow XML ingestion process.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ IngestXmlConfig.class, RiceServerBootstrapConfig.class, RiceServerDemoConfig.class, RiceMasterConfig.class })
public class IngestXmlExecConfig {

    private static final String SKIP_KEY = "rice.ingest.skip";

    // All paths must have the hardcoded separator to be consistent for deployment
    private static final String PATH_SEPARATOR = "/";

    private static final String UPGRADE_SQL_PATH = "upgrades" + PATH_SEPARATOR + "*";

    /**
     * The Spring environment.
     */
	@Autowired
	EnvironmentService env;

    /**
     * The {@link MetaInfDataType} profile.
     */
    @Autowired
    MetaInfDataTypeProfileConfig typeConfig;

    /**
     * Returns the executable for launching the workflow XML ingestion process.
     *
     * @return the executable for launching the workflow XML ingestion process
     */
	@Bean
	public Executable ingestXmlExecutable() {
        List<String> locations = Lists.newArrayList();

        List<MetaInfDataType> types = getTypes();

        PathComparator comparator = new PathComparator();

        for (MetaInfDataType type : types) {
            List<String> resources = MetaInfUtils.getPatternedClasspathResources(XmlProjectConstants.ID,
                    Optional.of(UPGRADE_SQL_PATH), Optional.<MetaInfDataLocation> absent(), Optional.of(type),
                    RiceXmlConfig.INGEST_FILENAME);
            Collections.sort(resources, comparator);
            locations.addAll(resources);
        }

		Boolean skip = env.getBoolean(SKIP_KEY, Boolean.FALSE);

		return IngestXmlExecutable.builder(locations).skip(skip.booleanValue()).build();
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

}