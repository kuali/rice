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
package org.kuali.rice.kim.api.identity.type;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;
import org.kuali.rice.kim.api.identity.address.EntityAddressHistoryContract;
import org.kuali.rice.kim.api.identity.email.EntityEmailHistoryContract;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneHistoryContract;

import java.util.List;

/**
 * This is a contract for EntityTypeContactInfo.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public interface EntityTypeContactInfoHistoryContract extends EntityTypeContactInfoContract, Historical {
    /**
     * Gets this identity Type of the {@link EntityTypeContactInfoContract}'s object.
     * @return the identity type for this {@link EntityTypeContactInfoContract}
     */
    @Override
    CodedAttributeHistoryContract getEntityType();

    /**
     * Gets this {@link EntityTypeContactInfoContract}'s List of {@link org.kuali.rice.kim.api.identity.address.EntityAddress}S.
     * @return the List of {@link org.kuali.rice.kim.api.identity.address.EntityAddressContract}S for this {@link EntityTypeContactInfoContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed.
     */
    @Override
    List<? extends EntityAddressHistoryContract> getAddresses();

    /**
     * Gets this {@link EntityTypeContactInfoContract}'s List of {@link org.kuali.rice.kim.api.identity.email.EntityEmailContract}S.
     * @return the List of {@link org.kuali.rice.kim.api.identity.email.EntityEmailContract}S for this {@link EntityTypeContactInfoContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed.
     */
    @Override
    List<? extends EntityEmailHistoryContract> getEmailAddresses();

    /**
     * Gets this {@link EntityTypeContactInfoContract}'s List of {@link org.kuali.rice.kim.api.identity.phone.EntityPhone}S.
     * @return the List of {@link org.kuali.rice.kim.api.identity.phone.EntityPhoneContract}S for this {@link EntityTypeContactInfoContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed.
     */
    @Override
    List<? extends EntityPhoneHistoryContract> getPhoneNumbers();

    /**
     * Returns the default address record for the identity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
    @Override
    EntityAddressHistoryContract getDefaultAddress();

    /**
     *  Returns the default email record for the identity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
    @Override
    EntityEmailHistoryContract getDefaultEmailAddress();

    /**
     * Returns the default phone record for the identity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
    @Override
    EntityPhoneHistoryContract getDefaultPhoneNumber();
}
