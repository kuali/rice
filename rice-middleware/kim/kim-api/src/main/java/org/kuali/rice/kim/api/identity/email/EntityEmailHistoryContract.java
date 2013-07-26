package org.kuali.rice.kim.api.identity.email;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityEmailHistoryContract extends EntityEmailContract, Historical {
    /**
     * Gets this {@link org.kuali.rice.kim.api.identity.email.EntityEmailHistory}'s type code.
     * @return the type code for this {@link org.kuali.rice.kim.api.identity.email.EntityEmailHistory}, or null if none has been assigned.
     */
    CodedAttributeHistoryContract getEmailType();
}
