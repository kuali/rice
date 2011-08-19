package org.kuali.rice.kew.api.rule;

import java.util.List;

public interface RuleResponsibilityContract {
    /**
     * Unique Id for RuleResponsibility.
     *
     * <p>
     * This is the unique Id of the RuleResponsibility
     * </p>
     *
     * @return responsibilityId
     */
    String getResponsibilityId();

    /**
     * code for the Action Requested
     *
     * <p>
     * This code matches the unique code for an ActionRequest
     * </p>
     *
     * @return actionRequestedCd
     */
    String getActionRequestedCd();

    /**
     * integer representation of the priority of the RuleResponsibility
     *
     * @return priority
     */
    Integer getPriority();

    /**
     * approval policy for the RuleResponsibility
     *
     * @return approvalPolicy
     */
    String getApprovePolicy();

    /**
     * unique id of the Principal for the RuleResponsibility
     *
     * @return principalId
     */
    String getPrincipalId();

    /**
     * unique id of the Group for the RuleResponsibility
     *
     * @return groupId
     */
    String getGroupId();

    /**
     * unique name of the Role for the RuleResponsibility
     *
     * @return groupId
     */
    String getRoleName();

    /**
     * list of RuleDelegations for the RuleResponsibility
     *
     * @return delegationRules
     */
    List<? extends RuleDelegationContract> getDelegationRules();
}
