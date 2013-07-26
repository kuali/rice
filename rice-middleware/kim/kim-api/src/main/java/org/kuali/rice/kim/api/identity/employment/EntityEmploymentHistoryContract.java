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
package org.kuali.rice.kim.api.identity.employment;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistoryContract;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityEmploymentHistoryContract extends EntityEmploymentContract, Historical {
    /**
     * Gets this {@link EntityEmploymentContract}'s identity affiliation.
     * @return the identity affiliation for this {@link EntityEmploymentContract}, or null if none has been assigned.
     */
    @Override
    EntityAffiliationHistoryContract getEntityAffiliation();

    /**
     * Gets this {@link EntityEmploymentContract}'s employee status.
     * @return the employee status for this {@link EntityEmploymentContract}, or null if none has been assigned.
     */
    @Override
    CodedAttributeHistoryContract getEmployeeStatus();

    /**
     * Gets this {@link EntityEmploymentContract}'s employee type.
     * @return the employee type for this {@link EntityEmploymentContract}, or null if none has been assigned.
     */
    @Override
    CodedAttributeHistoryContract getEmployeeType();
}
