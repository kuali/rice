package org.kuali.rice.kew.api.rule;

import org.kuali.rice.core.api.delegation.DelegationType;

public interface RuleDelegationContract {

    /**
     * type of delegation for the RuleDelegation
     *
     * <p>Determines what kind of delegation the RuleDelegation is</p>
     *
     * @return delegationType
     */
    DelegationType getDelegationType();

    /**
     * rule associated with the RuleDelegation
     *
     * <p>This rule is run for the original rule as the delegate</p>
     *
     * @return delegationRule
     */
    RuleContract getDelegationRule();
}
