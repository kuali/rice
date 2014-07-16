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

import java.util.Properties;

import com.google.common.collect.Lists;
import org.kuali.common.util.execute.Executable;
import org.kuali.common.util.properties.spring.EnvironmentPropertySourceConfig;
import org.kuali.common.util.runonce.smart.RunOnce;
import org.kuali.common.util.runonce.smart.RunOnceExecutable;
import org.kuali.common.util.spring.SpringExecUtils;
import org.kuali.common.util.spring.event.ApplicationEventListenerConfig;
import org.kuali.common.util.spring.event.ExecutableApplicationEventListener;
import org.kuali.common.util.spring.service.SpringService;
import org.kuali.common.util.spring.service.SpringServiceConfig;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.xml.ingest.ParameterServiceRunOnce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.PropertySource;

/**
 * Set up an {@code SmartApplicationListener} that ingests XML when it receives a {@code ContextRefreshedEvent}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Import({ SpringServiceConfig.class, EnvironmentPropertySourceConfig.class })
public class IngestXmlRunOnceConfig implements ApplicationEventListenerConfig {

    private static final String ORDER_KEY = "rice.ingest.order";
    private static final String NAMESPACE_KEY = "rice.ingest.param.namespace";
    private static final String COMPONENT_KEY = "rice.ingest.param.component";
    private static final String NAME_KEY = "rice.ingest.param.name";
    private static final String DESCRIPTION_KEY = "rice.ingest.param.description";
    private static final String RUN_ON_MISSING_PARAMETER_KEY = "rice.ingest.runOnMissingParameter";

    private static final String NAMESPACE = "KR-WKFLW";
    private static final String COMPONENT = "All";
    private static final String NAME = "INGEST_XML_AT_STARTUP_IND";
    private static final String DESCRIPTION = "Set this to 'Y' to ingest XML documents at application startup";
    private static final Boolean RUN_ON_MISSING_PARAMETER = Boolean.FALSE;

    /**
     * The Spring loader.
     */
    @Autowired
    SpringService service;

    /**
     * The final list of properties.
     */
    @Autowired
    PropertySource<?> propertySource;

    /**
     * {@inheritDoc}
     *
     * <p>
     * The {@link org.kuali.rice.coreservice.api.parameter.Parameter} properties need to come from the
     * {@link ConfigContext} instead of the {@link org.kuali.common.util.spring.env.EnvironmentService} for the scenario
     * where nobody has wired in a bootstrap PSC in order to help manage the resetting of the database via RunOnce.
     * </p>
     */
	@Override
	@Bean
	public SmartApplicationListener applicationEventListener() {
		Properties properties = ConfigContext.getCurrentContextConfig().getProperties();
		String applicationId = properties.getProperty(CoreConstants.Config.APPLICATION_ID);
		String namespace = properties.getProperty(NAMESPACE_KEY, NAMESPACE);
		String component = properties.getProperty(COMPONENT_KEY, COMPONENT);
		String name = properties.getProperty(NAME_KEY, NAME);
		String description = properties.getProperty(DESCRIPTION_KEY, DESCRIPTION);
		boolean runOnMissingParameter = Boolean.parseBoolean(properties.getProperty(RUN_ON_MISSING_PARAMETER_KEY, RUN_ON_MISSING_PARAMETER.toString()));
        int order = Integer.parseInt(properties.getProperty(ORDER_KEY, String.valueOf(Ordered.LOWEST_PRECEDENCE)));

		RunOnce runOnce = ParameterServiceRunOnce.builder(applicationId, namespace, component, name)
                .description(description).runOnMissingParameter(runOnMissingParameter).build();
		Executable springExecutable = SpringExecUtils.getSpringExecutable(service, propertySource, IngestXmlExecConfig.class,
                Lists.newArrayList("master"));
        Executable executable = RunOnceExecutable.builder(springExecutable, runOnce).build();

		return ExecutableApplicationEventListener.builder(executable, ContextRefreshedEvent.class).order(order).build();
	}

}