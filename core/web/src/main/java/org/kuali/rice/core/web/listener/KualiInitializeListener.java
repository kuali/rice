/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.JAXBConfigImpl;
import org.kuali.rice.kew.util.KEWConstants;
import org.springframework.web.context.support.XmlWebApplicationContext;



/**
 * A ServletContextListener responsible for intializing the Standalone Kuali Rice application.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KualiInitializeListener implements ServletContextListener {

    private static final Log LOG = LogFactory.getLog(KualiInitializeListener.class);

    //private static final String DEFAULT_SPRING_BEANS = "classpath:org/kuali/rice/standalone/config/StandaloneSpringBeans.xml";
    private static final String DEFAULT_SPRING_BEANS_REPLACEMENT_VALUE = "${bootstrap.spring.file}";
    public static final String RICE_BASE = "rice.base";
    public static final String CATALINA_BASE = "catalina.base";
    private static final String WEB_BOOTSTRAP_SPRING_FILE = "web.bootstrap.spring.file";

    private XmlWebApplicationContext context = null;

    /**
     * ServletContextListener interface implementation that schedules the start of the lifecycle
     */
    @Override
	public void contextInitialized(ServletContextEvent sce) {
        long startInit = System.currentTimeMillis();
        LOG.info("Initializing Kuali Rice Standalone...");

        List<String> configLocations = new ArrayList<String>();
        String additionalConfigLocations = System.getProperty(KEWConstants.ADDITIONAL_CONFIG_LOCATIONS_PARAM);
        if (!StringUtils.isBlank(additionalConfigLocations)) {
            String[] additionalConfigLocationArray = additionalConfigLocations.split(",");
            for (String additionalConfigLocation : additionalConfigLocationArray) {
                configLocations.add(additionalConfigLocation);
            }
        }

        //String bootstrapSpringBeans = DEFAULT_SPRING_BEANS;
        String bootstrapSpringBeans = "";
        if (!StringUtils.isBlank(System.getProperty(WEB_BOOTSTRAP_SPRING_FILE))) {
            bootstrapSpringBeans = System.getProperty(WEB_BOOTSTRAP_SPRING_FILE);
        } else if (!StringUtils.isBlank(sce.getServletContext().getInitParameter(WEB_BOOTSTRAP_SPRING_FILE))) {
            String bootstrapSpringInitParam = sce.getServletContext().getInitParameter(WEB_BOOTSTRAP_SPRING_FILE);
            // if the value comes through as ${bootstrap.spring.beans}, we ignore it
            if (!DEFAULT_SPRING_BEANS_REPLACEMENT_VALUE.equals(bootstrapSpringInitParam)) {
            	bootstrapSpringBeans = bootstrapSpringInitParam;
            	LOG.info("Found bootstrap Spring Beans file defined in servlet context: " + bootstrapSpringBeans);
            }
        }

        String basePath = findBasePath(sce.getServletContext());
        Properties baseProps = new Properties();
        baseProps.putAll(getContextParameters(sce.getServletContext()));
        baseProps.putAll(System.getProperties());
        baseProps.setProperty(RICE_BASE, basePath);
        // HACK: need to determine best way to do this...
        // if the additional config locations property is empty then we need
        // to explicitly set it so that if we use it in a root config
        // a value (an empty value) can be found, and the config parser
        // won't blow up because "additional.config.locations" property
        // cannot be resolved
        // An alternative to doing this at the application/module level would
        // be to push this functionality down into the Rice ConfigFactoryBean
        // e.g., by writing a simple ResourceFactoryBean that would conditionally
        // expose the resource, and then plugging the Resource into the ConfigFactoryBean
        // However, currently, the ConfigFactoryBean operates on String locations, not
        // Resources. Spring can coerce string <value>s into Resources, but not vice-versa
        /*if (StringUtils.isEmpty(additionalConfigLocations)) {
            baseProps.setProperty(KEWConstants.ADDITIONAL_CONFIG_LOCATIONS_PARAM, "");
        }*/
        JAXBConfigImpl config = new JAXBConfigImpl(baseProps);            
        // removing this so that the defaults don't get resolved by default.  
        //config.parseConfig();
        ConfigContext.init(config);
        
        context = new XmlWebApplicationContext();
        if (!StringUtils.isEmpty(bootstrapSpringBeans)) {
            context.setConfigLocation(bootstrapSpringBeans);
        }
        context.setServletContext(sce.getServletContext());
        context.refresh();
        context.start();
        long endInit = System.currentTimeMillis();
        LOG.info("...Kuali Rice Standalone successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }

    /**
     * Translates context parameters from the web.xml into entries in a Properties file.
     */
    protected Properties getContextParameters(ServletContext context) {
        Properties properties = new Properties();
        Enumeration<String> paramNames = context.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            properties.put(paramName, context.getInitParameter(paramName));
        }
        return properties;
    }

    protected String findBasePath(ServletContext servletContext) {
        String realPath = servletContext.getRealPath("/");
        // if cannot obtain real path (because, e.g., deployed as WAR
        // try a reasonable guess
        if (realPath == null) {
            if (System.getProperty(CATALINA_BASE) != null) {
                realPath = System.getProperty(CATALINA_BASE);
            } else {
                realPath = ".";
            }
        }
        String basePath = new File(realPath).getAbsolutePath();
        // append a trailing path separator to make relatives paths work in conjunction
        // with empty ("current working directory") basePath
        if (basePath.length() > 0 && !basePath.endsWith(File.separator)) {
            basePath += File.separator;
        }
        return basePath;
    }

    /**
     * Configures the default config location by first checking the init params for default locations and then falling back
     * to the standard default config location.
     */
    /*protected void addDefaultConfigLocation(ServletContext context, List<String> configLocations) {
        String defaultConfigLocation = context.getInitParameter(KEWConstants.DEFAULT_CONFIG_LOCATION_PARAM);
        if (!StringUtils.isEmpty(defaultConfigLocation)) {
            String[] locations = defaultConfigLocation.split(",");
            for (String location : locations) {
                configLocations.add(location);
            }
        } else {
            configLocations.add(KEWConstants.DEFAULT_SERVER_CONFIG_LOCATION);
        }
    }*/

    @Override
	public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Shutting down Kuali Rice Standalone.");
        if (context != null) {
            context.close();
        }
    }

    public XmlWebApplicationContext getContext() {
        return context;
    }

}
