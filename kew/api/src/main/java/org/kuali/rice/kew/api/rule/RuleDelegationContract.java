package org.kuali.rice.kew.api.rule;


public interface RuleDelegationContract {

    /**
     * type of delegation for the RuleDelegation
     *
     * <p>Determines what kind of delegation the RuleDelegation is</p>
     *
     * @return delegationType
     */
    String getDelegationType();

    /**
     * rule associated with the RuleDelegation
     *
     * <p>This rule is run for the original rule as the delegate</p>
     *
     * @return delegationRule
     */
    RuleContract getDelegationRule();
}
