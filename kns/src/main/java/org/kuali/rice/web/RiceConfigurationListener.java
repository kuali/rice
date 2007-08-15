/*
 * Copyright 2007 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.EdenConstants;

public class RiceConfigurationListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent sce) {

    }

    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("Constants", new EdenConstants());

        List<String> configLocations = new ArrayList<String>();
        // use the system prop as an override of the default packaged META-INF/workflow.xml
        String altCoreConfigLocation = System.getProperty(EdenConstants.DEFAULT_CONFIG_LOCATION_PARAM);
        if (altCoreConfigLocation != null) {
            configLocations.add(altCoreConfigLocation);
        }
        else {
            addDefaultConfigLocation(sce.getServletContext(), configLocations);
        }

        // use the system property to add additional configurations (useful for testing)
        String additionalConfigLocations = System.getProperty(EdenConstants.ADDITIONAL_CONFIG_LOCATIONS_PARAM);
        if (!StringUtils.isEmpty(additionalConfigLocations)) {
            String[] additionalConfigLocationArray = additionalConfigLocations.split(",");
            for (String additionalConfigLocation : additionalConfigLocationArray) {
                configLocations.add(additionalConfigLocation);
            }
        }

        try {
            SimpleConfig serverConfig = new SimpleConfig(configLocations);
            serverConfig.parseConfig();
            Core.init(serverConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Workflow failed to start properly.  Exiting.", e);
        }
    }


    /**
     * Configures the default config location by first checking the init params for default locations and then falling back to the
     * standard default config location.
     */
    protected void addDefaultConfigLocation(ServletContext context, List<String> configLocations) {
        String defaultConfigLocation = context.getInitParameter(EdenConstants.DEFAULT_CONFIG_LOCATION_PARAM);
        if (!StringUtils.isEmpty(defaultConfigLocation)) {
            String[] locations = defaultConfigLocation.split(",");
            for (String location : locations) {
                configLocations.add(location);
            }
        }
        else {
        	// TODO: say what? Why workflow.xml?
            configLocations.add("classpath:META-INF/workflow.xml");
        }
    }
}
