package org.kuali.rice.kew.api.rule;

import java.util.List;

public interface RuleResponsibilityContract {

    String getResponsibilityId();
    String getActionRequestedCd();
    Integer getPriority();
    String getApprovePolicy();

    String getPrincipalId();
    String getGroupId();
    String getRoleName();
    List<? extends RuleDelegationContract> getDelegationRules();
}
