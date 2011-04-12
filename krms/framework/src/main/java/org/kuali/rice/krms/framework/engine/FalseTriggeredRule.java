package org.kuali.rice.krms.framework.engine;

import java.util.List;

import org.kuali.rice.krms.api.engine.Proposition;

public class FalseTriggeredRule extends BasicRule {

	public FalseTriggeredRule(Proposition proposition, List<Action> actions) {
		super(proposition, actions);
	}

	@Override
	protected boolean shouldExecuteAction(boolean ruleExecutionResult) {
		return !ruleExecutionResult;
	}

}
