package org.kuali.rice.krms.framework.engine;

import java.util.Map;

import org.kuali.rice.krms.engine.Agenda;
import org.kuali.rice.krms.engine.ExecutionEnvironment;

public class BasicAgenda implements Agenda {

	private String eventName;
	private Map<String, String> qualifiers;
	private AgendaTree agendaTree;
	
	public BasicAgenda(String eventName, Map<String, String> qualifiers, AgendaTree agendaTree) {
		this.eventName = eventName;
		this.qualifiers = qualifiers;
		this.agendaTree = agendaTree;
	}
	
	@Override
	public void execute(ExecutionEnvironment environment) {
		agendaTree.execute(environment);
	}

	@Override
	public boolean appliesTo(ExecutionEnvironment environment) {
		if (eventName.equals(environment.getSelectionCriteria().getEventName())) {
			for (String agendaQualifierName : qualifiers.keySet()) {
				String qualifierValue = qualifiers.get(agendaQualifierName);
				String environmentQualifierValue = environment.getSelectionCriteria().getAgendaQualifiers().get(qualifierValue);
				if (!qualifierValue.equals(environmentQualifierValue)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

}
