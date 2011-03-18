package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.Agenda;
import org.kuali.rice.krms.api.ExecutionEnvironment;
import org.kuali.rice.krms.api.Rule;

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
