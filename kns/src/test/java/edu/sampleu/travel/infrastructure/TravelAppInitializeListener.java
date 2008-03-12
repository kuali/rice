/*
 * Copyright 2007 The Kuali Foundation
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
package edu.sampleu.travel.infrastructure;

import java.io.FileNotFoundException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.kuali.rice.web.jetty.JettyServer;
import org.springframework.util.Log4jConfigurer;

import uk.ltd.getahead.dwr.util.LoggingOutput;

/**
 * A ServletContextListener that fires up the Spring context.
 * This is used instead of a standard ContextLoaderListener in order to load
 * an extra test context if running in unit tests. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TravelAppInitializeListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(TravelAppInitializeListener.class);

	public void contextDestroyed(ServletContextEvent sce) {

	}

	public void contextInitialized(ServletContextEvent sce) {
    	try {
		    Log4jConfigurer.initLogging("classpath:log4j.properties");
		} catch (FileNotFoundException e) {
		    throw new RuntimeException("Failed to start sample application.", e);
		}

		Object o = sce.getServletContext().getAttribute(JettyServer.JETTYSERVER_TESTMODE_ATTRIB);
		boolean testMode = false;
		if (o != null) {
		    testMode = Boolean.valueOf((String) o);
		}
		LOG.info("Travel webapp starting up in " + (testMode ? "test" : "normal") + " mode");
		// this loads up the Spring context
		TravelServiceLocator.initialize(testMode);
	}
}