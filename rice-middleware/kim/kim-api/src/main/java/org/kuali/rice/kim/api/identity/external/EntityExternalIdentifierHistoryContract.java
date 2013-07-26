package org.kuali.rice.kim.api.identity.external;

import org.kuali.rice.core.api.mo.common.Historical;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityExternalIdentifierHistoryContract extends EntityExternalIdentifierContract, Historical {

    /**
     * Gets this {@link EntityExternalIdentifierHistoryContract}'s type.
     * @return the type for this {@link EntityExternalIdentifierHistoryContract}, or null if none has been assigned.
     */
    EntityExternalIdentifierTypeHistoryContract getExternalIdentifierType();
}
