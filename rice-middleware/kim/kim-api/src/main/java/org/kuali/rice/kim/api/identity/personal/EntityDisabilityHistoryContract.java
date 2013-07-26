package org.kuali.rice.kim.api.identity.personal;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

import java.util.List;
import java.util.Set;

public interface EntityDisabilityHistoryContract extends EntityDisabilityContract, Historical {
    @Override
    CodedAttributeHistoryContract getConditionType();

    @Override
    CodedAttributeHistoryContract getDeterminationSourceType();

    @Override
    List<? extends CodedAttributeHistoryContract> getAccommodationsNeeded();
}
