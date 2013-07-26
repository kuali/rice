package org.kuali.rice.kim.api.identity.phone;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityPhoneHistoryContract extends EntityPhoneContract, Historical {
    /**
     * Gets this {@link EntityPhoneHistory}'s type code.
     * @return the type code for this {@link EntityPhoneHistory}, or null if none has been assigned.
     */
    @Override
    CodedAttributeHistoryContract getPhoneType();
}
