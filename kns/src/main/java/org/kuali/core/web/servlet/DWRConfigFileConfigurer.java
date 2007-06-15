package org.kuali.core.web.servlet;

import org.kuali.rice.config.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;

public class DWRConfigFileConfigurer implements InitializingBean {
	
	private String configFile;

	public void afterPropertiesSet() throws Exception {
		if (configFile == null) {
			throw new ConfigurationException("property configFile is null.");
		}
		KualiDWRServlet.HACK_ADDITIONAL_FILES.add(getConfigFile());
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

}
