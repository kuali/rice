package org.kuali.rice.kim.api.common.delegate;

public interface DelegateMemberContract {

    DelegateContract getDelegate();
    String getMemberName();
    String getMemberNamespaceCode();
    String getDelegationMemberId();
    String getRoleId();
}
