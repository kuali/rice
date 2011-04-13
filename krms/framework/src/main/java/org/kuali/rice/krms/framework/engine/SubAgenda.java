package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public class SubAgenda implements Rule {

	private AgendaTree agendaTree;
	
	public SubAgenda(AgendaTree agendaTree) {
		this.agendaTree = agendaTree;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		agendaTree.execute(environment);
		return true;
	}

}
