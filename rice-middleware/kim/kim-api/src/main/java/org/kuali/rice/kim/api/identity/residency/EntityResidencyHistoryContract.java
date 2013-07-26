package org.kuali.rice.kim.api.identity.residency;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityResidencyHistoryContract extends EntityResidencyContract, Historical {
    @Override
    CodedAttributeHistoryContract getResidencyStatus();

    @Override
    CodedAttributeHistoryContract getResidencyType();
}
