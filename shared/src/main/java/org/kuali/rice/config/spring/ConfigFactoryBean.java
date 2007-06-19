package org.kuali.rice.config.spring;

import java.util.List;
import java.util.Properties;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.FactoryBean;

public class ConfigFactoryBean implements FactoryBean {

	private List<String> configLocations;

	public static String CONFIG_OVERRIDE_LOCATION;

	public Object getObject() throws Exception {
		if (getConfigLocations() == null) {
			throw new ConfigurationException("No config locations declared, at least one is required");
		}
		Properties baseProperties = new Properties();
		if (Core.getCurrentContextConfig() != null) {
			baseProperties = Core.getCurrentContextConfig().getProperties();
		}
		SimpleConfig config = null;
		if (CONFIG_OVERRIDE_LOCATION != null) {
			config = new SimpleConfig(CONFIG_OVERRIDE_LOCATION, baseProperties);
		} else {
			config = new SimpleConfig(getConfigLocations(), baseProperties);
		}

		config.parseConfig();
		return config;
	}

	public Class getObjectType() {
		return Config.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public List<String> getConfigLocations() {
		return this.configLocations;
	}

	public void setConfigLocations(List<String> configLocations) {
		this.configLocations = configLocations;
	}

}
