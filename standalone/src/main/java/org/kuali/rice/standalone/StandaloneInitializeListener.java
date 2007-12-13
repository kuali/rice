/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.rice.standalone;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.MessageFetcher;

/**
 * A ServletContextListener responsible for intializing the Standalone Kuali
 * Rice application.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StandaloneInitializeListener implements ServletContextListener {
    private static final long serialVersionUID = -6603009920502691099L;

    private static final Logger LOG = Logger.getLogger(StandaloneInitializeListener.class);

    private static final String DEFAULT_SPRING_BEANS = "org/kuali/rice/standalone/config/StandaloneSpringBeans.xml";
    private static final String RICE_BASE = "rice.base";
    private static final String CATALINA_BASE = "catalina.base";
    private static final String DEFAULT_LOG4J_CONFIG = "org/kuali/rice/standalone/logging/default-log4j.properties";

    private ConfigurableApplicationContext context = null;

    /**
     * ServletContextListener interface implementation that schedules the start
     * of the lifecycle
     */
    public void contextInitialized(ServletContextEvent sce) {
	long startInit = System.currentTimeMillis();
	try {
	    Properties p = new Properties();
	    p.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_LOG4J_CONFIG));
	    PropertyConfigurator.configure(p);
	} catch (Exception e) {
	    throw new WorkflowRuntimeException(e);
	}

	LOG.info("Initializing Kuali Rice Standalone...");

	sce.getServletContext().setAttribute("Constants", new EdenConstants());

	List<String> configLocations = new ArrayList<String>();
	String additionalConfigLocations = System.getProperty(EdenConstants.ADDITIONAL_CONFIG_LOCATIONS_PARAM);
	if (!StringUtils.isBlank(additionalConfigLocations)) {
	    String[] additionalConfigLocationArray = additionalConfigLocations.split(",");
	    for (String additionalConfigLocation : additionalConfigLocationArray) {
		configLocations.add(additionalConfigLocation);
	    }
	}

	String bootstrapSpringBeans = DEFAULT_SPRING_BEANS;
	if (!StringUtils.isBlank(System.getProperty(EdenConstants.BOOTSTRAP_SPRING_FILE))) {
	    bootstrapSpringBeans = System.getProperty(EdenConstants.BOOTSTRAP_SPRING_FILE);
	} else if (!StringUtils.isBlank(sce.getServletContext().getInitParameter(EdenConstants.BOOTSTRAP_SPRING_FILE))) {
	    bootstrapSpringBeans = sce.getServletContext().getInitParameter(EdenConstants.BOOTSTRAP_SPRING_FILE);
	    LOG.info("Found bootstrap Spring Beans file defined in servlet context: " + bootstrapSpringBeans);
	}
	try {
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
	    // Resources.  Spring can coerce string <value>s into Resources, but not vice-versa
	    if (StringUtils.isEmpty(additionalConfigLocations)) {
		baseProps.setProperty(EdenConstants.ADDITIONAL_CONFIG_LOCATIONS_PARAM, "");
	    }
	    SimpleConfig config = new SimpleConfig(baseProps);
	    config.parseConfig();
	    Core.init(config);

	    context = new ClassPathXmlApplicationContext(bootstrapSpringBeans);
	    context.start();

	    // execute the MessageFetcher to grab any messages that were being processed
	    // when the system shut down originally
	    MessageFetcher messageFetcher = new MessageFetcher((Integer) null);
	    KSBServiceLocator.getThreadPool().execute(messageFetcher);
	} catch (Exception e) {
	    LOG.fatal("Kuali Rice Standalone startup failed!", e);
	    throw new RuntimeException("Startup failed.  Exiting.", e);
	}
	long endInit = System.currentTimeMillis();
	LOG.info("...Kuali Rice Standalone successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }

    /**
     * Transalates context parameters from the web.xml into entries in a Properties file.
     */
    protected Properties getContextParameters(ServletContext context) {
	Properties properties = new Properties();
	Enumeration paramNames = context.getInitParameterNames();
	while (paramNames.hasMoreElements()) {
	    String paramName = (String)paramNames.nextElement();
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
     * Configures the default config location by first checking the init params
     * for default locations and then falling back to the standard default
     * config location.
     */
    protected void addDefaultConfigLocation(ServletContext context, List<String> configLocations) {
	String defaultConfigLocation = context.getInitParameter(EdenConstants.DEFAULT_CONFIG_LOCATION_PARAM);
	if (!StringUtils.isEmpty(defaultConfigLocation)) {
	    String[] locations = defaultConfigLocation.split(",");
	    for (String location : locations) {
		configLocations.add(location);
	    }
	} else {
	    configLocations.add(EdenConstants.DEFAULT_SERVER_CONFIG_LOCATION);
	}
    }

    public void contextDestroyed(ServletContextEvent sce) {
	LOG.info("Shutting down Kuali Rice Standalone.");
	try {
	    if (context != null) {
		context.close();
	    }
	} catch (Exception e) {
	    throw new RuntimeException("Failed to shutdown Kuali Rice Standalone.", e);
	}
    }

}