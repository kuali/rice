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
