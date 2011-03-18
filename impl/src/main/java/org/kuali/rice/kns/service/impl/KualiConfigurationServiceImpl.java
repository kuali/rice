/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.*;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.service.KualiConfigurationService;
import org.kuali.rice.core.util.ImmutableProperties;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.properties.PropertyHolder;
import org.kuali.rice.kns.web.struts.action.KualiPropertyMessageResources;
import org.kuali.rice.kns.web.struts.action.KualiPropertyMessageResourcesFactory;


public class KualiConfigurationServiceImpl implements KualiConfigurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiConfigurationServiceImpl.class);
    private PropertyHolder propertyHolder;

    /**
     * Harcoding the configFileName, by request.
     */
    public KualiConfigurationServiceImpl() {
        this.propertyHolder = new PropertyHolder();
        this.propertyHolder.getHeldProperties().putAll(ConfigContext.getCurrentContextConfig().getProperties());

        KualiPropertyMessageResourcesFactory propertyMessageFactory = new KualiPropertyMessageResourcesFactory();

        // create default KualiPropertyMessageResources
        KualiPropertyMessageResources messageResources = (KualiPropertyMessageResources)propertyMessageFactory.createResources("");

        //Add Kuali Properties to property holder
        this.propertyHolder.getHeldProperties().putAll(messageResources.getKualiProperties(null));
    }

    public boolean isProductionEnvironment() {
	return getPropertyString(KNSConstants.PROD_ENVIRONMENT_CODE_KEY).equalsIgnoreCase(
		getPropertyString(KNSConstants.ENVIRONMENT_KEY));
    }

    /**
     * @see org.kuali.rice.core.service.KualiConfigurationService#getPropertyString(java.lang.String)
     */
    public String getPropertyString(String key) {
        LOG.debug("getPropertyString() started");

        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }

        return this.propertyHolder.getProperty(key);
    }

    /**
     * @see org.kuali.rice.core.service.KualiConfigurationService#getPropertyAsBoolean(java.lang.String)
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
     * @see org.kuali.rice.core.service.KualiConfigurationService#getAllProperties()
     */
    public Properties getAllProperties() {
        LOG.debug("getAllProperties() started");

        return new ImmutableProperties(propertyHolder.getHeldProperties());
    }

}
