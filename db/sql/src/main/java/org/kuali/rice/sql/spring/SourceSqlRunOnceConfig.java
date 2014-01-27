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

import java.io.File;
import java.util.Properties;

import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.execute.spring.ExecutableConfig;
import org.kuali.common.util.properties.spring.EnvironmentPropertySourceConfig;
import org.kuali.common.util.runonce.smart.PropertiesFileRunOnce;
import org.kuali.common.util.runonce.smart.RunOnce;
import org.kuali.common.util.runonce.smart.RunOnceExecutable;
import org.kuali.common.util.spring.SpringExecUtils;
import org.kuali.common.util.spring.service.SpringService;
import org.kuali.common.util.spring.service.SpringServiceConfig;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertySource;

/**
 * Set up an {@code Executable} that resets the database.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ SpringServiceConfig.class, EnvironmentPropertySourceConfig.class })
public class SourceSqlRunOnceConfig implements ExecutableConfig {

    @Autowired
    SpringService service;

    @Autowired
    PropertySource<?> propertySource;

    private static final String PROJECT_HOME_KEY = "project.home";
    private static final String RUNONCE_FILENAME = "runonce.properties";
    private static final String ENCODING = "UTF-8";
    private static final String PROPERTY_KEY = "project.db.reset";

    @Override
    @Bean(initMethod = "execute")
    public Executable executable() {
        // This needs to come from ConfigContext instead of EnvironmentService for the scenario where nobody
        // has wired in a bootstrap PSC in order to help manage the resetting of the database via RunOnce
        Properties properties = ConfigContext.getCurrentContextConfig().getProperties();
        String projectHome = properties.getProperty(PROJECT_HOME_KEY);

        File file = new File(projectHome, RUNONCE_FILENAME);
        RunOnce runOnce = PropertiesFileRunOnce.builder(file, ENCODING, PROPERTY_KEY).build();
        Executable executable = SpringExecUtils.getSpringExecutable(service, propertySource, SourceSqlExecConfig.class);

        return RunOnceExecutable.builder(executable, runOnce).build();
    }
}
