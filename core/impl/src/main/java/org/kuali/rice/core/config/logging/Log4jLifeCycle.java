/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.config.logging;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.springframework.util.Log4jConfigurer;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;

/**
 * Lifecycle implementation that initializes and shuts down Log4J logging
 */
public class Log4jLifeCycle extends BaseLifecycle {

    private static final String LOG4J_FILE_NOT_FOUND = "log4j settings file not found at location: ";

	/**
     * Convenience constant representing a minute in milliseconds
     */
    private static final int MINUTE = 60 * 1000;

    /**
     * Location of default/automatic Log4J configuration properties, in Spring ResourceUtils resource/url syntax
     */
    private static final String AUTOMATIC_LOGGING_CONFIG_URL = "classpath:org/kuali/rice/core/logging/default-log4j.properties";

    /**
     * Default settings reload interval to use in the case that the settings are reloadable (i.e. they originate from a file)
     */
    private static final int DEFAULT_RELOAD_INTERVAL = 5 * MINUTE; // 5 minutes

    /**
     * Non-static and non-final so that it can be reset after configuration is read
     */
    private Logger log = Logger.getLogger(getClass());

	public void start() throws Exception {
        // obtain the root workflow config
		Config config = ConfigContext.getCurrentContextConfig();

        boolean log4jFileExists = checkPropertiesFileExists(config.getProperty(Config.LOG4J_SETTINGS_PATH));

        // first check for in-line xml configuration
		String log4jconfig = config.getProperty(Config.LOG4J_SETTINGS_XML);
		if (log4jconfig != null) {
			try {
				DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = b.parse(new ByteArrayInputStream(log4jconfig.getBytes()));
				DOMConfigurator.configure(doc.getDocumentElement());
				// now get the reconfigured log instance
				log = Logger.getLogger(getClass());
			} catch (Exception e) {
				log.error("Error parsing Log4J configuration settings: " + log4jconfig, e);
			}
        // next check for in-line properties configuration
		} else if ((log4jconfig = config.getProperty(Config.LOG4J_SETTINGS_PROPS)) != null) {
			Properties p = new Properties(config.getProperties());
			try {
				p.load(new ByteArrayInputStream(log4jconfig.getBytes()));
				PropertyConfigurator.configure(p);
				log = Logger.getLogger(getClass());
			} catch (IOException ioe) {
				log.error("Error loading Log4J configuration settings: " + log4jconfig, ioe);
			}
        // check for an external file location specification
		} else if (log4jFileExists) {
			log.info("Configuring Log4J logging.");
			log4jconfig = config.getProperty(Config.LOG4J_SETTINGS_PATH);

            int reloadInterval = DEFAULT_RELOAD_INTERVAL;

            String log4jReloadInterval = config.getProperty(Config.LOG4J_SETTINGS_RELOADINTERVAL_MINS);
			if (log4jReloadInterval != null) {
				try {
                    reloadInterval = Integer.parseInt(log4jReloadInterval) * MINUTE;
				} catch (NumberFormatException nfe) {
					log.warn("Invalid reload interval: " + log4jReloadInterval + ", using default: 5 minutes");
				}
			}

            // if we are using a specific version of Log4j for which we have written subclasses that allow
            // variable substitution using core config, then use those custom classes to do so
            // otherwise use the log4j api
            if ("1.2.13".equals(getLog4jVersion())) {
                log.info("Using custom Log4j 1.2.13 configurer to make workflow config properties accessible");
                // use custom impl based on 1.2.13 to insert workflow config properties for resolution

                WorkflowLog4j_1_2_13_Configurer.initLoggingWithProperties(config.getProperties(), log4jconfig, reloadInterval);
            } else {
                log.info("Using standard Log4jConfigurer");
                // just use standard log4j api
                PropertyConfigurator.configureAndWatch(log4jconfig, reloadInterval);
            }

			log = Logger.getLogger(getClass());
        // finally fall back to a Log4J configuration shipped with workflow
		} else {

            PropertyConfigurator.configureAndWatch(AUTOMATIC_LOGGING_CONFIG_URL, DEFAULT_RELOAD_INTERVAL);
            log = Logger.getLogger(getClass());
		}
		super.start();
	}

    /**
	 * Checks if the passed in file exists.
	 *
	 * @param log4jSettingsPath the file
	 * @return true if exists
	 */
	private boolean checkPropertiesFileExists(String log4jSettingsPath) {
		if (StringUtils.isBlank(log4jSettingsPath)) {
			return false;
		}
		
		boolean exists;

		try {
			exists = ResourceUtils.getFile(log4jSettingsPath).exists();
		} catch (FileNotFoundException e) {
			exists = false;
		}

		if (!exists) {
			System.out.println(LOG4J_FILE_NOT_FOUND + log4jSettingsPath);
		}

		return exists;
	}

	/**
     * Uses reflection to attempt to obtain the ImplementationVersion of the org.apache.log4j
     * package from the jar manifest.
     * @return the value returned from Package.getPackage("org.apache.log4j").getImplementationVersion()
     * or null if package is not found
     */
    private static String getLog4jVersion() {
        Package p = Package.getPackage("org.apache.log4j");
        if (p == null) return null;
        return p.getImplementationVersion();
    }

    /**
     * Subclasses the Spring Log4jConfigurer to expose a static method which accepts an initial set of
     * properties (to use for variable substitution)
     *
 * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static final class WorkflowLog4j_1_2_13_Configurer extends Log4jConfigurer {
        public static void initLoggingWithProperties(Properties props, String location, long refreshInterval) throws FileNotFoundException {
            File file = ResourceUtils.getFile(location);
            if (!file.exists()) {
                throw new FileNotFoundException("Log4J config file [" + location + "] not found");
            }
            if (location.toLowerCase().endsWith(XML_FILE_EXTENSION)) {
                DOMConfigurator.configureAndWatch(file.getAbsolutePath(), refreshInterval);
            } else {
                assert(props != null);
                WorkflowLog4j_1_2_13_PropertyConfigurator.configureAndWatch(props, file.getAbsolutePath(), refreshInterval);
            }
        }
    }

    /**
     * Subclasses the Log4j 1.2.13 PropertyConfigurator to add a static method which accepts an initial
     * set of properties (to use for variable substitution)
     */
    static final class WorkflowLog4j_1_2_13_PropertyConfigurator extends PropertyConfigurator {
        static public void configureAndWatch(final Properties initialProperties, String configFilename, long delay) {
            // cannot just use a subclass and pass the initial properties to constructor as the super constructor
            // is invoked before the properties member can be set, and doOnChange will be called from constructor
            // with null properties
            // so instead create an anonymous subclass with closure that includes initialProperties
            FileWatchdog watchDog = new FileWatchdog(configFilename) {
                public void doOnChange() {
                    new WorkflowLog4j_1_2_13_PropertyConfigurator().doConfigure(initialProperties, this.filename, LogManager.getLoggerRepository());
                }
            };
            watchDog.setDelay(delay);
            watchDog.start();
        }

        public void doConfigure(Properties initialProperties, String configFileName, LoggerRepository hierarchy) {
          Properties props = new Properties();
          props.putAll(initialProperties);

          try {
            FileInputStream istream = new FileInputStream(configFileName);
            props.load(istream);
            istream.close();
          }
          catch (IOException e) {
            LogLog.error("Could not read configuration file ["+configFileName+"].", e);
            LogLog.error("Ignoring configuration file [" + configFileName+"].");
            return;
          }
          // If we reach here, then the config file is alright.
          doConfigure(props, hierarchy);
        }
    }

    public void stop() throws Exception {
    	// commenting out LogManager.shutdown() for now because it kills logging before shutdown of the rest of the system is complete
    	// so if there are other errors that are encountered during shutdown, they won't be logged!

    	// move this to the standalone initialize listener instead

		//LogManager.shutdown();
		super.stop();
	}

}
