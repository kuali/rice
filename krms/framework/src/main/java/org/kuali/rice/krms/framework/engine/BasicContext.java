package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.TermResolver;

public final class BasicContext implements Context {
	
	private final List<Agenda> agendas;
	private final List<TermResolver<?>> termResolvers;
	
	public BasicContext(List<Agenda> agendas, List<TermResolver<?>> termResolvers) {
		this.agendas = agendas;
		this.termResolvers = termResolvers;
	}
	
	@Override
	public void execute(ExecutionEnvironment environment) {
		if (termResolvers != null) for (TermResolver<?> termResolver : termResolvers) {
			environment.addTermResolver(termResolver);
		}
		List<Agenda> matchingAgendas = findMatchingAgendas(environment);
		for (Agenda matchingAgenda : matchingAgendas) {
			matchingAgenda.execute(environment);
		}
	}
	
	private List<Agenda> findMatchingAgendas(ExecutionEnvironment environment) {
		List<Agenda> matchingAgendas = new ArrayList<Agenda>();
		for (Agenda agenda : agendas) {
			if (agenda.appliesTo(environment)) {
				matchingAgendas.add(agenda);
			}
		}
		return matchingAgendas;
	}
	
	public List<TermResolver<?>> getTermResolvers() {
		return Collections.unmodifiableList(termResolvers);
	}

}
