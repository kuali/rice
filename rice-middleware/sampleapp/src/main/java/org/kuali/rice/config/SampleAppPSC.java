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
package org.kuali.rice.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.kuali.common.jdbc.project.spring.JdbcPropertyLocationsConfig;
import org.kuali.common.util.log.LoggerUtils;
import org.kuali.common.util.properties.Location;
import org.kuali.common.util.properties.PropertiesService;
import org.kuali.common.util.properties.spring.DefaultPropertiesServiceConfig;
import org.kuali.common.util.spring.service.PropertySourceConfig;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigPropertySource;
import org.kuali.rice.sql.spring.SourceSqlPropertyLocationsConfig;
import org.kuali.rice.xml.ingest.RiceConfigUtils;
import org.kuali.rice.xml.spring.IngestXmlPropertyLocationsConfig;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertySource;

/**
 * Holds the property source for all of the different properties needed for starting up the KRAD
 * Sample App.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({SampleAppProjectConfig.class, JdbcPropertyLocationsConfig.class, DefaultPropertiesServiceConfig.class,
        SourceSqlPropertyLocationsConfig.class, IngestXmlPropertyLocationsConfig.class})
public class SampleAppPSC implements PropertySourceConfig {

    private static final String KR_SAMPLE_APP_CONFIG = "classpath:META-INF/sample-app-config.xml";

    private static final Logger logger = LoggerUtils.make();

    @Autowired
    JdbcPropertyLocationsConfig jdbcConfig;

    @Autowired
    SourceSqlPropertyLocationsConfig sourceSqlConfig;

    @Autowired
    IngestXmlPropertyLocationsConfig ingestXmlConfig;

    @Autowired
    PropertiesService service;

    @Autowired
    ServletContext servletContext;

    @Override
    @Bean
    public PropertySource<?> propertySource() {
        // Combine locations making sure Rice properties go in last
        List<Location> locations = new ArrayList<Location>();
        locations.addAll(jdbcConfig.jdbcPropertyLocations());
        locations.addAll(sourceSqlConfig.riceSourceSqlPropertyLocations());
        locations.addAll(ingestXmlConfig.riceIngestXmlPropertyLocations());

        // Default behavior is load->decrypt->resolve
        // -Dproperties.resolve=false turns off placeholder resolution
        Properties properties = service.getProperties(locations);
        logger.info("Loaded {} regular properties", properties.size());

        // Combine normal properties with Rice properties using Rice's custom placeholder resolution logic to resolve everything
        Config rootCfg = RiceConfigUtils.getRootConfig(properties, KR_SAMPLE_APP_CONFIG, servletContext);

        // Make sure ConfigContext.getCurrentContextConfig() return's the rootCfg object
        ConfigContext.init(rootCfg);

        // Make Spring and Rice use the exact same source for obtaining property values
        return new ConfigPropertySource("riceConfig", rootCfg);
    }

}
