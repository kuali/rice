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
package org.kuali.rice.db.config.profile;

import org.kuali.common.util.metainf.spring.MetaInfDataLocation;
import org.kuali.common.util.metainf.spring.MetaInfDataType;
import org.kuali.common.util.metainf.spring.MetaInfGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Defines the filter that removes the client demo data from the server demo data set.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Configuration
@Profile("server-demo")
public class RiceServerDemoFilterConfig implements MetaInfFilterConfig {

    /**
     * {@inheritDoc}
     *
     * <p>
     * Initially include all data sets.
     * </p>
     */
    @Override
    public boolean isIncluded(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Exclude the client demo data set.
     * </p>
     */
    @Override
    public boolean isExcluded(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type) {
        return MetaInfDataLocation.CLIENT.equals(location) && MetaInfDataType.DEMO.equals(type);
    }

}