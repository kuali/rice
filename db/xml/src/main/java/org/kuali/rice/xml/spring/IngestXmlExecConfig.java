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
import freemarker.core.Configurable;
import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.metainf.model.ConfigurablePathComparator;
import org.kuali.common.util.metainf.model.PathComparator;
import org.kuali.common.util.metainf.service.MetaInfUtils;
import org.kuali.common.util.metainf.spring.MetaInfDataLocation;
import org.kuali.common.util.metainf.spring.MetaInfDataType;
import org.kuali.common.util.metainf.spring.RiceXmlConfig;
import org.kuali.common.util.spring.env.EnvironmentService;
import org.kuali.rice.xml.ingest.IngestXmlExecutable;
import org.kuali.rice.xml.project.XmlProjectConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Central configuration file for launching the workflow XML ingestion process.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ IngestXmlConfig.class })
public class IngestXmlExecConfig {

	@Autowired
	EnvironmentService env;

	private static final String SKIP_KEY = "rice.ingest.skip";
	private static final String RESOURCES_KEY = "rice.ingest.resources";

	@Bean
	public Executable ingestXmlExecutable() {
        String qualifier = "upgrades" + File.separator + "*";
        List<String> locations = new ArrayList<String>();

        for (MetaInfDataType type : getTypes()) {
            List<String> resources = MetaInfUtils.getPatternedClasspathResources(XmlProjectConstants.ID,
                    Optional.of(qualifier), Optional.<MetaInfDataLocation> absent(), Optional.of(type), RiceXmlConfig.INGEST_FILENAME);
            locations.addAll(resources);
        }

        ConfigurablePathComparator comparator = ConfigurablePathComparator.builder().typeOrder(getTypes()).build();
        Collections.sort(locations, comparator);

		// Setup the executable
		boolean skip = env.getBoolean(SKIP_KEY, false);
		return new IngestXmlExecutable.Builder(locations).skip(skip).build();
	}

    private List<MetaInfDataType> getTypes() {
        return Lists.newArrayList(MetaInfDataType.BOOTSTRAP, MetaInfDataType.DEMO, MetaInfDataType.TEST);
    }

}
