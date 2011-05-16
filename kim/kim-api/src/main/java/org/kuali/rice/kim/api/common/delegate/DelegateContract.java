package org.kuali.rice.kim.api.common.delegate;

import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface DelegateContract extends Inactivatable {
    String getDelegationTypeCode();
    String getMemberId();
    String getMemberTypeCode();
    Attributes getQualifier();
    String getDelegationId();
	String getRoleMemberId();
}
