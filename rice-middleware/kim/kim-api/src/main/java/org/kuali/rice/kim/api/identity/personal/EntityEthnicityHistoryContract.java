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
