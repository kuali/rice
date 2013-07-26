package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

public interface EntityMilitaryHistoryContract extends EntityMilitaryContract, Historical {
    /**
     * Returns the {@link EntityBioDemographicsHistoryContract}'s relationship with the military .
     */
    CodedAttributeHistoryContract getRelationshipStatus();
}
