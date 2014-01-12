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
package org.kuali.rice.core.impl.config.property;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.springframework.beans.factory.config.PropertiesFactoryBean;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * PropertiesFactoryBean which exposes Rice config as Properties.
 * Supports specification of a property "prefix" in which case all properties with
 * the given prefix will be exposed (with the prefix stripped)
 * Rice Config properties are loaded with (and override) the Resource properties, local properties
 * override these as configured as normal via PropertiesLoaderSupport.
 */
public class ConfigPropertiesFactoryBean extends PropertiesFactoryBean {
    private String prefix;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // override PropertiesLoaderSupport loadProperties; delegate to superclass
    // for base Resource properties support, override with Rice config properties
    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        Map riceProperties;
        if (prefix != null) {
            riceProperties = ConfigContext.getCurrentContextConfig().getPropertiesWithPrefix(prefix, true);
        } else {
            riceProperties = ConfigContext.getCurrentContextConfig().getProperties();
        }
        props.putAll(riceProperties);
    }
}
