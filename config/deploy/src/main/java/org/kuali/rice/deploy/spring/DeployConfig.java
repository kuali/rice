package org.kuali.rice.deploy.spring;

import org.kuali.common.deploy.spring.DefaultDeployConfig;
import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.execute.spring.ExecutableConfig;
import org.kuali.rice.sql.spring.SourceDbConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ DefaultDeployConfig.class, SourceDbConfig.class })
public class DeployConfig implements ExecutableConfig {

	@Autowired
	DefaultDeployConfig config;

	@Override
	@Bean(initMethod = "execute")
	public Executable executable() {
		return config.deployExecutable();
	}

}