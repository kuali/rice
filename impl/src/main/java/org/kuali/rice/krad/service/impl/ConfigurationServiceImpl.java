/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krad.service.impl;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.util.ImmutableProperties;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.properties.PropertyHolder;
import org.kuali.rice.krad.web.struts.action.KualiPropertyMessageResources;
import org.kuali.rice.krad.web.struts.action.KualiPropertyMessageResourcesFactory;

import java.util.Properties;


public class ConfigurationServiceImpl implements ConfigurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ConfigurationServiceImpl.class);
    private PropertyHolder propertyHolder;

    /**
     * Harcoding the configFileName, by request.
     */
    public ConfigurationServiceImpl() {
        this.propertyHolder = new PropertyHolder();
        this.propertyHolder.getHeldProperties().putAll(ConfigContext.getCurrentContextConfig().getProperties());

        KualiPropertyMessageResourcesFactory propertyMessageFactory = new KualiPropertyMessageResourcesFactory();

        // create default KualiPropertyMessageResources
        KualiPropertyMessageResources messageResources = (KualiPropertyMessageResources)propertyMessageFactory.createResources("");

        //Add Kuali Properties to property holder
        this.propertyHolder.getHeldProperties().putAll(messageResources.getKualiProperties(null));
    }

    public boolean isProductionEnvironment() {
	return getPropertyString(KRADConstants.PROD_ENVIRONMENT_CODE_KEY).equalsIgnoreCase(
		getPropertyString(KRADConstants.ENVIRONMENT_KEY));
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getPropertyString(java.lang.String)
     */
    public String getPropertyString(String key) {
        LOG.debug("getPropertyString() started");

        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }

        return this.propertyHolder.getProperty(key);
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getPropertyAsBoolean(java.lang.String)
     */
    public boolean getPropertyAsBoolean(String key) {
        LOG.debug("getPropertyAsBoolean() started");

        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }
        String property = this.propertyHolder.getProperty(key);
        if ( property != null ) {
            property = property.trim();
            if ((property.equalsIgnoreCase( "true" )
                    || property.equalsIgnoreCase( "yes" )
                    || property.equalsIgnoreCase( "on" )
                    || property.equalsIgnoreCase( "1" )) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getAllProperties()
     */
    public Properties getAllProperties() {
        LOG.debug("getAllProperties() started");

        return new ImmutableProperties(propertyHolder.getHeldProperties());
    }

}
