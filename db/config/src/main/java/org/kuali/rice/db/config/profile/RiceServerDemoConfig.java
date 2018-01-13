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
package org.kuali.rice.db.config.profile;

import com.google.common.collect.Lists;
import org.kuali.common.util.metainf.spring.MetaInfDataLocation;
import org.kuali.common.util.metainf.spring.MetaInfDataType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Defines the Rice demo dataset, which is the data behind the sample applications.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Profile("server-demo")
public class RiceServerDemoConfig implements MetaInfDataLocationProfileConfig, MetaInfDataTypeProfileConfig {

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean
    public List<MetaInfDataLocation> getMetaInfDataLocations() {
        return Lists.newArrayList(MetaInfDataLocation.CLIENT, MetaInfDataLocation.SERVER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean
    public List<MetaInfDataType> getMetaInfDataTypes() {
        return Lists.newArrayList(MetaInfDataType.BOOTSTRAP, MetaInfDataType.DEMO);
    }

}