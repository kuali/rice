package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.Historical;

import java.util.List;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityBioDemographicsHistoryContract extends EntityBioDemographicsContract, Historical {

    /**
     * Get the {@link EntityBioDemographicsContract}'s disabilities
     */
    @Override
    List<? extends EntityDisabilityHistoryContract> getDisabilities();

    /**
     * Returns if the {@link EntityBioDemographicsContract}'s entity is disabled.
     * This value is determined by the contents of the {@link EntityBioDemographicsContract}'s disabilities values
     */
    @Override
    List<? extends EntityMilitaryHistoryContract> getMilitaryRecords();
}
