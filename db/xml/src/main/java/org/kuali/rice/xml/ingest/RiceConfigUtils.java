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
package org.kuali.rice.xml.ingest;

import java.io.IOException;
import java.util.Properties;
import java.util.SortedSet;

import javax.servlet.ServletContext;

import org.kuali.common.util.PropertyUtils;
import org.kuali.common.util.Str;
import org.kuali.common.util.log.LoggerUtils;
import org.kuali.common.util.property.ImmutableProperties;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.impl.config.property.ConfigLogger;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.core.web.util.PropertySources;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Utility class to handle {@link PropertySources} and Rice config files.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RiceConfigUtils {

	private static final Logger logger = LoggerUtils.make();

    private RiceConfigUtils() {}

    /**
     * Gets the {@link Config} from both the current configuration and the ones in {@code loaded}, at {@code location},
     * and in the {@code servletContext}.
     *
     * @param loaded the loaded properties
     * @param location the location of additional properties
     * @param servletContext the servlet context in which to add more properties
     *
     * @return the final configuration
     */
	public static Config getRootConfig(Properties loaded, String location, ServletContext servletContext) {
		// Get the Rice config object the listener created
		Config config = ConfigContext.getCurrentContextConfig();
		Preconditions.checkNotNull(config, "'config' cannot be null");
		Properties listenerProperties = getProperties(config);

		// Parse config from the location indicated, using listener properties in the process of doing so
		JAXBConfigImpl parsed = parseConfig(location, listenerProperties);

		// Add and override loaded properties with parsed properties
		addAndOverride(loaded, parsed.getRawProperties());

		// Priority is servlet -> env -> system
		// Override anything we've loaded with servlet, env, and system properties
		Properties servlet = PropertySources.convert(servletContext);
		Properties global = PropertyUtils.getGlobalProperties(servlet);
		addAndOverride(loaded, global);
		logger.info("Using {} distinct properties", Integer.valueOf(loaded.size()));

		// Use JAXBConfigImpl in order to perform Rice's custom placeholder resolution logic now that everything is loaded
		return new JAXBConfigImpl(loaded);

	}

	/**
	 * Parse the configuration stored at {@code location}.
     *
     * @param location the location to get properties from
     *
     * @return the new configuration
     */
	public static JAXBConfigImpl parseConfig(String location) {
		return parseConfig(location, ImmutableProperties.of());
	}

	/**
	 * Parse the configuration stored at {@code location}, adding any additional properties from {@code properties}.
     *
     * @param location the location to get properties from
     * @param properties any additional properties to add
     *
     * @return the new configuration
	 */
	public static JAXBConfigImpl parseConfig(String location, Properties properties) {
		try {
			JAXBConfigImpl config = new JAXBConfigImpl(location, properties);
			config.parseConfig();
			return config;
		} catch (IOException e) {
			throw new IllegalStateException("Unexpected error parsing config", e);
		}
	}

	/**
	 * Parse the configuration stored at {@code location} and initialize.
     *
     * @param location the location to get properties from
     *
     * @return the new configuration
     */
	public static JAXBConfigImpl parseAndInit(String location) {
		JAXBConfigImpl config = parseConfig(location);
		ConfigContext.init(config);
		return config;
	}

    /**
     * Returns the {@link Properties} from the given {@code config}.
     *
     * @param config the {@link Config} to get the {@link Properties} from
     *
     * @return the {@link Properties}
     */
	public static Properties getProperties(Config config) {
		if (config instanceof JAXBConfigImpl) {
			JAXBConfigImpl jci = (JAXBConfigImpl) config;
			return jci.getRawProperties();
		} else {
			logger.warn("Unable to access raw Rice config properties.");
			return config.getProperties();
		}
	}

    /**
     * Put all of the given {@code properties} into the given {@code config}.
     *
     * @param config the {@link Config} to add the {@link Properties} to
     * @param properties the {@link Properties} to add
     */
    public static void putProperties(Config config, Properties properties) {
        SortedSet<String> keys = Sets.newTreeSet(properties.stringPropertyNames());
        for (String key : keys) {
            config.putProperty(key, properties.getProperty(key));
        }
    }

    private static void add(Properties oldProperties, Properties newProperties) {
        SortedSet<String> newKeys = Sets.newTreeSet(Sets.difference(newProperties.stringPropertyNames(), oldProperties.stringPropertyNames()));

        if (newKeys.isEmpty()) {
            return;
        }

        logger.info("Adding {} properties", Integer.valueOf(newKeys.size()));

        for (String newKey : newKeys) {
            String value = newProperties.getProperty(newKey);
            logger.debug("Adding - [{}]=[{}]", newKey, toLogMsg(newKey, value));
            oldProperties.setProperty(newKey, value);
        }
    }

	private static void override(Properties oldProperties, Properties newProperties) {
		SortedSet<String> commonKeys = Sets.newTreeSet(Sets.intersection(newProperties.stringPropertyNames(), oldProperties.stringPropertyNames()));

		if (commonKeys.isEmpty()) {
			return;
		}

		logger.debug("{} keys in common", Integer.valueOf(commonKeys.size()));

        for (String commonKey : commonKeys) {
			String oldValue = oldProperties.getProperty(commonKey);
			String newValue = newProperties.getProperty(commonKey);

			if (!newValue.equals(oldValue)) {
				Object[] args = { commonKey, toLogMsg(commonKey, oldValue), toLogMsg(commonKey, newValue) };
				logger.info("Overriding - [{}]=[{}]->[{}]", args);
                oldProperties.setProperty(commonKey, newValue);
			}
		}
	}

    private static void addAndOverride(Properties oldProperties, Properties newProperties) {
        add(oldProperties, newProperties);
        override(oldProperties, newProperties);
    }

	private static String toLogMsg(String key, String value) {
		return Str.flatten(ConfigLogger.getDisplaySafeValue(key, value));
	}

}