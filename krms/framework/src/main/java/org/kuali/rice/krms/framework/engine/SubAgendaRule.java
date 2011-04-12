package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.Rule;

public class SubAgendaRule implements Rule {

	private Agenda agenda;
	
	public SubAgendaRule(Agenda agenda) {
		this.agenda = agenda;
	}
	
	@Override
	public boolean evaluate(ExecutionEnvironment environment) {
		agenda.execute(environment);
		return true;
	}

}
