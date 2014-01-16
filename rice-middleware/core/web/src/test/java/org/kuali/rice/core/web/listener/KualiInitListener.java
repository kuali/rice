/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.core.web.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.core.web.util.PropertySources;
import org.kuali.rice.kew.api.KewApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.google.common.base.Optional;

/**
 * A ServletContextListener responsible for initializing a Kuali Rice application.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KualiInitListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(KualiInitListener.class);

    private static final String WEB_BOOTSTRAP_SPRING_KEY = "web.bootstrap.spring.file";
    private static final String WEB_BOOTSTRAP_SPRING_PSC_KEY = "web.bootstrap.spring.psc";

    private XmlWebApplicationContext context;

    /**
     * ServletContextListener interface implementation that schedules the start of the lifecycle
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // Preserve startup time
        long start = System.currentTimeMillis();
        LOG.info("Initializing Kuali Rice Application...");

        // Stop Quartz from "phoning home" on every startup
        System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");

        try {
            // Initialize the Rice config context
            Config config = initializeConfigContext(sce);
            // Get a handle to a fully configured Spring application context
            context = getWebAppContext(sce, config);
            // Refresh the context
            context.refresh();
        } catch (RuntimeException e) {
            String msg = "problem during context.refresh()";
            LOG.error(msg, e);
            throw new IllegalStateException(msg, e);
        }

        // Start the context
        context.start();

        // Emit a log message confirming that startup completed ok
        LOG.info("...Kuali Rice Application initialized, startup took " + (System.currentTimeMillis() - start) + " ms.");
    }

    protected XmlWebApplicationContext getWebAppContext(ServletContextEvent sce, Config config) {
        // Commented out because the configLocations variable was declared but never being used in KualiInitializeListener
        // List<String> configLocations = getConfigLocations();

        XmlWebApplicationContext context = new XmlWebApplicationContext();

        // Check for a bootstrap Spring XML config file
        Optional<String> bootstrapSpringBeans = PropertySources.getProperty(sce, WEB_BOOTSTRAP_SPRING_KEY);
        if (bootstrapSpringBeans.isPresent()) {
            context.setConfigLocation(bootstrapSpringBeans.get());
        }

        // KULRICE-11259 Provide an optional method for bootstrapping a Spring property source
        Optional<PropertySource<?>> ps = PropertySources.getPropertySource(sce, WEB_BOOTSTRAP_SPRING_PSC_KEY);
        if (ps.isPresent()) {
            PropertySources.addFirst(context, ps.get());
        }

        // Add the reference to the current servlet context
        context.setServletContext(sce.getServletContext());

        // Return the configured application context
        return context;
    }

    protected Config initializeConfigContext(ServletContextEvent sce) {
        Properties baseProps = new Properties();
        baseProps.putAll(PropertySources.convert(sce.getServletContext()));
        baseProps.putAll(System.getProperties());
        JAXBConfigImpl config = new JAXBConfigImpl(baseProps);
        ConfigContext.init(config);
        return config;
    }

    protected List<String> getConfigLocations() {
        List<String> configLocations = new ArrayList<String>();
        String additionalConfigLocations = System.getProperty(KewApiConstants.ADDITIONAL_CONFIG_LOCATIONS_PARAM);
        if (!StringUtils.isBlank(additionalConfigLocations)) {
            String[] additionalConfigLocationArray = additionalConfigLocations.split(",");
            for (String additionalConfigLocation : additionalConfigLocationArray) {
                configLocations.add(additionalConfigLocation);
            }
        }
        return configLocations;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Shutting down Kuali Rice...");
        if (context != null) {
            context.close();
        }
        LOG.info("...completed shutdown of Kuali Rice.");
        Log4jConfigurer.shutdownLogging();
    }

    public XmlWebApplicationContext getContext() {
        return context;
    }

}
