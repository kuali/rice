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
		logger.info("Using {} distinct properties", loaded.size());

		// Use JAXBConfigImpl in order to perform Rice's custom placeholder resolution logic now that everything is loaded
		return new JAXBConfigImpl(loaded);

	}

	/**
	 * Iterate over the list of key/value pairs from {@code properties} and invoke {@code config.putProperty(key,value)}
	 */
	public static void putProperties(Config config, Properties properties) {
		SortedSet<String> keys = Sets.newTreeSet(properties.stringPropertyNames());
		for (String key : keys) {
			config.putProperty(key, properties.getProperty(key));
		}
	}

	/**
	 * Parse the configuration stored at {@code location}
	 */
	public static JAXBConfigImpl parseConfig(String location) {
		return parseConfig(location, ImmutableProperties.of());
	}

	/**
	 * Parse the configuration stored at {@code location}
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
	 * Parse the configuration stored at {@code location} AND invoke {@code ConfigContext.init(config)}
	 */
	public static JAXBConfigImpl parseAndInit(String location) {
		JAXBConfigImpl config = parseConfig(location);
		ConfigContext.init(config);
		return config;
	}

	public static Properties getProperties(Config config) {
		if (config instanceof JAXBConfigImpl) {
			JAXBConfigImpl jci = (JAXBConfigImpl) config;
			return jci.getRawProperties();
		} else {
			logger.warn("Unable to access raw Rice config properties.");
			return config.getProperties();
		}
	}

	public static void addAndOverride(Properties oldProps, Properties newProps) {
		add(oldProps, newProps);
		override(oldProps, newProps);
	}

	public static void override(Properties oldProps, Properties newProps) {
		SortedSet<String> commonKeys = Sets.newTreeSet(Sets.intersection(newProps.stringPropertyNames(), oldProps.stringPropertyNames()));
		if (commonKeys.size() == 0) {
			return;
		}
		logger.debug("{} keys in common", commonKeys.size());
		for (String commonKey : commonKeys) {
			String oldValue = oldProps.getProperty(commonKey);
			String newValue = newProps.getProperty(commonKey);
			if (!newValue.equals(oldValue)) {
				Object[] args = { commonKey, toLogMsg(commonKey, oldValue), toLogMsg(commonKey, newValue) };
				logger.info("Overriding - [{}]=[{}]->[{}]", args);
				oldProps.setProperty(commonKey, newValue);
			}
		}
	}

	public static void add(Properties oldProps, Properties newProps) {
		SortedSet<String> newKeys = Sets.newTreeSet(Sets.difference(newProps.stringPropertyNames(), oldProps.stringPropertyNames()));
		if (newKeys.size() == 0) {
			return;
		}
		logger.info("Adding {} properties", newKeys.size());
		for (String newKey : newKeys) {
			String value = newProps.getProperty(newKey);
			logger.debug("Adding - [{}]=[{}]", newKey, toLogMsg(newKey, value));
			oldProps.setProperty(newKey, value);
		}
	}

	protected static String toLogMsg(String key, String value) {
		return Str.flatten(ConfigLogger.getDisplaySafeValue(key, value));
	}
}
