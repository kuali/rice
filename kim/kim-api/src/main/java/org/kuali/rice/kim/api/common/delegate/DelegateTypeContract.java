package org.kuali.rice.kim.api.common.delegate;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;

import java.util.List;

public interface DelegateTypeContract extends Inactivatable {

    String getKimTypeId();
    String getDelegationTypeCode();
    String getDelegationId();
    String getRoleId();
    List<? extends DelegateMemberContract> getMembers();
}
