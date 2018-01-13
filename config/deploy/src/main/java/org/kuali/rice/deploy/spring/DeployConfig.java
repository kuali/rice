/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.deploy.spring;

import org.kuali.common.deploy.spring.DefaultDeployConfig;
import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.execute.spring.ExecutableConfig;
import org.kuali.rice.sql.spring.SourceSqlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ DefaultDeployConfig.class, SourceSqlConfig.class })
public class DeployConfig implements ExecutableConfig {

	@Autowired
	DefaultDeployConfig config;

	@Override
	@Bean(initMethod = "execute")
	public Executable executable() {
		return config.deployExecutable();
	}

}