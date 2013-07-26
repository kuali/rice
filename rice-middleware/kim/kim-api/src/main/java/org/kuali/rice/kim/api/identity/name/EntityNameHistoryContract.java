package org.kuali.rice.kim.api.identity.name;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityNameHistoryContract extends EntityNameContract, Historical {
    /**
     * Gets this {@link EntityNameHistoryContract}'s TypeContract.
     * @return the type for this {@link EntityNameHistoryContract}, or null if none has been assigned.
     */
    CodedAttributeHistoryContract getNameType();
}
