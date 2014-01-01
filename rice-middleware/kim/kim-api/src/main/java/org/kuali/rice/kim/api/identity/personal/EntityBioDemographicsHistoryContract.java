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
package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.Historical;

import java.util.List;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityBioDemographicsHistoryContract extends EntityBioDemographicsContract, Historical {

    /**
     * Get the {@link EntityBioDemographicsContract}'s disabilities
     */
    @Override
    List<? extends EntityDisabilityHistoryContract> getDisabilities();

    /**
     * Returns if the {@link EntityBioDemographicsContract}'s entity is disabled.
     * This value is determined by the contents of the {@link EntityBioDemographicsContract}'s disabilities values
     */
    @Override
    List<? extends EntityMilitaryHistoryContract> getMilitaryRecords();
}
