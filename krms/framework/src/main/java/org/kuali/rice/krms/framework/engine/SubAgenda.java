package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public class SubAgenda implements Rule {

	private Agenda agenda;
	
	public SubAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		agenda.execute(environment);
		return true;
	}

}
