package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public final class BasicAgendaTreeEntry implements AgendaTreeEntry {
	
	private final Rule rule;
	private final AgendaTree ifTrue;
	private final AgendaTree ifFalse;
	
	public BasicAgendaTreeEntry(Rule rule) {
		this(rule, null, null);
	}
	
	public BasicAgendaTreeEntry(Rule rule, AgendaTree ifTrue, AgendaTree ifFalse) {
		if (rule == null) {
			throw new IllegalArgumentException("rule was null");
		}
		this.rule = rule;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}
	
	@Override
	public void execute(ExecutionEnvironment environment) {
		boolean result = rule.evaluate(environment);
		if (result && ifTrue != null) {
			ifTrue.execute(environment);
		}
		if (!result && ifFalse != null) {
			ifFalse.execute(environment);
		}
	}
	
}
