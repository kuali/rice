package org.kuali.rice.kim.api.identity.address;

import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

/**
 * address information for a KIM identity with effective date data
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityAddressHistoryContract extends EntityAddressContract, Historical {
    @Override
    CodedAttributeHistoryContract getAddressType();
}
