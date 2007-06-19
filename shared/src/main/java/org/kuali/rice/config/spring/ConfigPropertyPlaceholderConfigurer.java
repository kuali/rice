package org.kuali.rice.config.spring;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigLogger;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Configures a property placeholder in Spring which will allow access to the properties configured in
 * the workflow configuration.
 *
 * @author ewestfal
 */
public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
        protected final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());

    public ConfigPropertyPlaceholderConfigurer() {
        setProperties();
    }

    private void setProperties() {
        setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        Config config = Core.getCurrentContextConfig();
        if (config != null) {
                log.debug("Replacing parameters in Spring using config:\r\n" + config);
            ConfigLogger.logConfig(config);
            setProperties(config.getProperties());
        }
    }
} 
