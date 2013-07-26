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
package org.kuali.rice.kim.api.identity.citizenship;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

/**
 * This contract represents the citizenship information  associated with an Entity.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface EntityCitizenshipHistoryContract extends EntityCitizenshipContract, Historical {
    /**
     * Gets this {@link org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistoryContract}'s citizenship status object.
     * @return the Type object of citizenship status for this {@link org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistoryContract}, or null if none has been assigned.
     */
    @Override
    CodedAttributeHistoryContract getStatus();

    /**
     * Gets this {@link EntityCitizenshipContract}'s citizenship change type object.
     * @return the Type object of citizenship status for this {@link EntityCitizenshipContract}, or null if none has been assigned.
     */
    CodedAttributeHistoryContract getChangeType();
}
