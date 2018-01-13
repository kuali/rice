/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Central configuration file for launching a local version of Rice for the workflow XML ingestion process.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@ImportResource({ LaunchRiceConfig.JTA, LaunchRiceConfig.DATASOURCE, LaunchRiceConfig.RICE })
public class LaunchRiceConfig {

    /**
     * The location of the JTA Spring beans.
     */
	protected static final String JTA = "classpath:org/kuali/rice/core/RiceJTASpringBeans.xml";

    /**
     * The location of the data source Spring beans.
     */
	protected static final String DATASOURCE = "classpath:org/kuali/rice/core/RiceDataSourceSpringBeans.xml";

    /**
     * The location of the Rice Spring beans.
     */
	protected static final String RICE = "classpath:org/kuali/rice/config/RiceSpringBeans.xml";

    /**
     * Returns a new instance of a {@link PropertySourcesPlaceholderConfigurer}.
     *
     * @return a new instance of a {@link PropertySourcesPlaceholderConfigurer}
     */
	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}