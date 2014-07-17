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

import java.util.List;

/**
 * Applies a filter to the combination of a {@link MetaInfGroup}, a {@link MetaInfDataLocation}, and a
 * {@link MetaInfDataType} data set.
 *
 * TODO: Push to kuali-util
 */
public interface MetaInfFilterConfig {

    /**
     * Returns whether to include the data for the {@link MetaInfGroup}, {@link MetaInfDataLocation}, and
     * {@link MetaInfDataType}.
     *
     * @param group the {link MetaInfGroup} to check
     * @param location the {@link MetaInfDataLocation} to check
     * @param type the {@link MetaInfDataType} to check
     *
     * @return true if the data set should be included, false otherwise
     */
    boolean isIncluded(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type);

    /**
     * Returns whether to exclude the data for the {@link MetaInfGroup}, {@link MetaInfDataLocation}, and
     * {@link MetaInfDataType}.
     *
     * @param group the {link MetaInfGroup} to check
     * @param location the {@link MetaInfDataLocation} to check
     * @param type the {@link MetaInfDataType} to check
     *
     * @return true if the data set should be excluded, false otherwise
     */
    boolean isExcluded(MetaInfGroup group, MetaInfDataLocation location, MetaInfDataType type);

}