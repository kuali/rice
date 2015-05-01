/*
 * Copyright 2006-2015 The Kuali Foundation
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
package org.kuali.rice.kew.actionitem.oracle.queue;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
@Conditional(ActionItemChangedOracleConfiguration.Condition.class)
@ImportResource(ActionItemChangedOracleConfiguration.ACTN_ITEM_CHANGED_ORACLE_BEANS)

public class ActionItemChangedOracleConfiguration {

    protected static final String ACTN_ITEM_CHANGED_ORACLE_BEANS =
            "classpath:org/kuali/rice/kew/config/ActionItemChangedQueueOracle.xml";

    protected static final String DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";

    static class Condition implements ConfigurationCondition {
        /**
         * {@inheritDoc}
         */
        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.PARSE_CONFIGURATION;
        }

        /**
         * ActionItemChangedQueueOracle.xml will only be loaded if the rice.kew.enableExternalActnListNotification
         * property is set to true and the datasource.driver.name is oracle.jdbc.OracleDriver.
         *
         * {@inheritDoc}
         */
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            boolean notifyExternalActionList = Boolean.parseBoolean(ConfigContext.getCurrentContextConfig().getProperty(
                    KewApiConstants.ENABLE_EXTERNAL_ACTN_LIST_NOTIFICATION));

            String datasourceDriverName = ConfigContext.getCurrentContextConfig().getProperty(
                    KewApiConstants.DATASOURCE_DRIVER_NAME);

            if (notifyExternalActionList) {
                return DRIVER_CLASS_NAME.equalsIgnoreCase(datasourceDriverName) ? true : false;
            } else {
                return false;
            }
        }
    }
}