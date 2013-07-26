/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.api.identity.affiliation;

import org.kuali.rice.core.api.mo.common.Historical;

/**
 * This contract represents an affiliation for an Entity.
 * Each person must have at least one affiliation associated with it.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface EntityAffiliationHistoryContract extends EntityAffiliationContract, Historical {
    /**
     * Gets this {@link EntityAffiliationHistory}'s type.
     * @return the type for this {@link EntityAffiliationHistory}, or null if none has been assigned.
     */
    @Override
    EntityAffiliationTypeHistoryContract getAffiliationType();
}
