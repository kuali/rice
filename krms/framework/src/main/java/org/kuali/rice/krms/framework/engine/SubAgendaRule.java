package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.engine.Agenda;
import org.kuali.rice.krms.engine.ExecutionEnvironment;
import org.kuali.rice.krms.engine.Rule;

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
