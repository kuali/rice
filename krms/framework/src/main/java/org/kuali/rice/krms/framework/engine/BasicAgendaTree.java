package org.kuali.rice.krms.framework.engine;

import java.util.Arrays;
import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public final class BasicAgendaTree implements AgendaTree {
	
	private final List<AgendaTreeEntry> entries;
	
	public BasicAgendaTree(AgendaTreeEntry... entries) {
		this.entries = Arrays.asList(entries);
	}
	
	public BasicAgendaTree(List<AgendaTreeEntry> entries) {
		if (entries == null) {
			throw new IllegalArgumentException("entries list was null");
		}
		this.entries = entries;		
	}
	
	public void execute(ExecutionEnvironment environment) {
		for (AgendaTreeEntry entry : entries) {
			entry.execute(environment);
		}
	}
	
}
