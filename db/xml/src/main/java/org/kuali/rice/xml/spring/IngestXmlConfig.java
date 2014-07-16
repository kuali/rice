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
package org.kuali.rice.xml.spring;

import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.spring.env.EnvironmentService;
import org.kuali.common.util.spring.event.ApplicationEventListenerConfig;
import org.kuali.common.util.spring.event.ExecutableApplicationEventListener;
import org.kuali.common.util.spring.service.SpringServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;

/**
 * Sets up a {@code SmartApplicationListener} that ingests workflow XML when it receives a
 * {@code ContextRefreshedEvent}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ SpringServiceConfig.class })
public class IngestXmlConfig implements ApplicationEventListenerConfig {

    private static final String ORDER_KEY = "rice.ingest.order";

    /**
     * The ingestion executable.
     */
    @Autowired
    IngestXmlExecConfig config;

    /**
     * The Spring environment.
     */
	@Autowired
	EnvironmentService env;

    /**
     * {@inheritDoc}
     */
	@Override
	@Bean
	public SmartApplicationListener applicationEventListener() {
		Executable executable = config.ingestXmlExecutable();
        Integer order = env.getInteger(ORDER_KEY, Integer.valueOf(Ordered.LOWEST_PRECEDENCE));

		return ExecutableApplicationEventListener.builder(executable, ContextRefreshedEvent.class).order(order.intValue()).build();
	}

}