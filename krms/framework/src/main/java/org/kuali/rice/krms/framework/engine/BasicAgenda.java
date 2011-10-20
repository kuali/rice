package org.kuali.rice.krms.framework.engine;

import java.util.Map;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;

public class BasicAgenda implements Agenda {

	private Map<String, String> qualifiers;
	private AgendaTree agendaTree;
	
	public BasicAgenda(Map<String, String> qualifiers, AgendaTree agendaTree) {
		this.qualifiers = qualifiers;
		this.agendaTree = agendaTree;
	}
	
	@Override
	public void execute(ExecutionEnvironment environment) {
		agendaTree.execute(environment);
	}

	@Override
	public boolean appliesTo(ExecutionEnvironment environment) {
        for (String agendaQualifierName : qualifiers.keySet()) {
            String qualifierValue = qualifiers.get(agendaQualifierName);
            String environmentQualifierValue = environment.getSelectionCriteria().getAgendaQualifiers().get(agendaQualifierName);
            if (!qualifierValue.equals(environmentQualifierValue)) {
                return false;
            }
        }
		return true;
	}

}
