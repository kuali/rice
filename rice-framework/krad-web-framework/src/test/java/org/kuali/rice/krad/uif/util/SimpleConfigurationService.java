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
package org.kuali.rice.krad.uif.util;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Properties-based configuration service for supporting simple unit testing scenarios.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SimpleConfigurationService implements ConfigurationService {

    private Properties properties;

    /**
     * Get the configuration properties.
     * 
     * @return The properties.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Set the configuration properties.
     * 
     * @param properties The properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getPropertyValueAsString(java.lang.String)
     */
    @Override
    public String getPropertyValueAsString(String key) {
        return properties.getProperty(key);
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getPropertyValueAsBoolean(java.lang.String)
     */
    @Override
    public boolean getPropertyValueAsBoolean(String key) {
        return "true".equals(properties.getProperty(key));
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getAllProperties()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getAllProperties() {
        return Collections.<String, String> unmodifiableMap((Map<String, String>) ((Map<?, ?>) properties));
    }

}
