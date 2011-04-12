package org.kuali.rice.krms.framework.engine;

import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public class AgendaTree {
	
	private List<Rule> rules;
	private AgendaTree ifTrue;
	private AgendaTree ifFalse;
	private AgendaTree afterAgenda;
	
	public AgendaTree(List<Rule> rules, AgendaTree ifTrue, AgendaTree ifFalse, AgendaTree afterAgenda) {
		this.rules = rules;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
		this.afterAgenda = afterAgenda;
		if (rules.isEmpty()) {
			throw new IllegalArgumentException("The rule list must contain at least one rule, but list is empty.");
		}
	}
	
	public void execute(ExecutionEnvironment environment) {
		boolean lastRuleResult = true;
		for (Rule rule : rules) {
			lastRuleResult = rule.evaluate(environment);
		}
		if (lastRuleResult && ifTrue != null) {
			ifTrue.execute(environment);
		}
		if (!lastRuleResult && ifFalse != null) {
			ifFalse.execute(environment);
		}
		if (afterAgenda != null) {
			afterAgenda.execute(environment);
		}
	}
	
}
