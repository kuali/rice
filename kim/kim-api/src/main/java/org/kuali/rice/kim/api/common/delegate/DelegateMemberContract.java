package org.kuali.rice.kim.api.common.delegate;

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromTo;

public interface DelegateMemberContract extends Versioned, InactivatableFromTo {

    String getDelegationMemberId();

    String getDelegationId();

    String getRoleMemberId();

    String getTypeCode();

    String getMemberId();
}
