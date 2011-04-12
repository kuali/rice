package org.kuali.rice.krms.framework.engine;

import java.util.List;


public class FalseTriggeredRule extends BasicRule {

	public FalseTriggeredRule(Proposition proposition, List<Action> actions) {
		super(proposition, actions);
	}

	@Override
	protected boolean shouldExecuteAction(boolean ruleExecutionResult) {
		return !ruleExecutionResult;
	}

}
