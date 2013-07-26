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
package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

import java.util.List;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityEthnicityHistoryContract extends EntityEthnicityContract, Historical {

    /**
     * Gets a list of this {@link EntityEthnicityContract}'s ethnicity codes.
     * @return the ethnicity codes for this {@link EntityEthnicityContract}, or an empty list if none has been assigned.
     */
    @Override
    CodedAttributeHistoryContract getRaceEthnicityCode();

    /**
     * Gets a list of unmasked {@link EntityEthnicityContract}'s ethnicity codes.
     * @return the unmasked ethnicity codes for this {@link EntityEthnicityContract}, or an empty list if none has been assigned.
     */
    @Override
    CodedAttributeHistoryContract getRaceEthnicityCodeUnmasked();
}
