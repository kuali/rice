package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.engine.BasicRule;
import org.kuali.rice.krms.framework.engine.Proposition;

import java.util.List;

/**
 *
 * A {@link org.kuali.rice.krms.framework.engine.Rule} that executes a {@link Action} when the {@link Proposition} is false,
 * as opposed to {@link BasicRule} which executes its action when the proposition is true.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidationRule extends BasicRule {
    public ValidationRule(String name, Proposition proposition, List<Action> actions) {
        super(name, proposition, actions);
    }

    public ValidationRule(Proposition proposition, List<Action> actions) {
        super(proposition, actions);
    }

    @Override
    protected boolean shouldExecuteAction(boolean ruleExecutionResult) {
        return !ruleExecutionResult;
    }
}
