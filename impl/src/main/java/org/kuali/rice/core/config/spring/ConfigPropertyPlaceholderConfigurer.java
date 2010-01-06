/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.config.spring;

import java.io.IOException;
import java.util.Properties;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigLogger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Configures a property placeholder in Spring which will allow access to the properties configured in
 * the workflow configuration.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
        protected final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());

    public ConfigPropertyPlaceholderConfigurer() {
        setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {
        // perform standard property resource file loading
        super.loadProperties(props);
        // load the Rice properties
        Config config = ConfigContext.getCurrentContextConfig();
        if (config != null) {
            log.debug("Replacing parameters in Spring using config:\r\n" + config);
            ConfigLogger.logConfig(config);
            props.putAll(config.getProperties());
        }
    }
}
