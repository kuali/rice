package org.kuali.rice.krms.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SelectionCriteria {

	private final String eventName;
	private final Map<String, String> contextQualifiers;
	private final Map<String, String> agendaQualifiers;
	
	private SelectionCriteria(String eventName) {
		if (eventName == null || "".equals(eventName.trim())) {
			throw new IllegalArgumentException("Event name must not be null or empty.");
		}
		this.eventName = eventName;
		this.contextQualifiers = new HashMap<String, String>();
		this.agendaQualifiers = new HashMap<String, String>();
	}
	
	public static SelectionCriteria createCriteria(String eventName, Map<String, String> contextQualifiers, Map<String, String> agendaQualifiers) {
		SelectionCriteria criteria = new SelectionCriteria(eventName);
		criteria.contextQualifiers.putAll(contextQualifiers);
		criteria.agendaQualifiers.putAll(agendaQualifiers);
		return criteria;
	}

	public String getEventName() {
		return eventName;
	}

	public Map<String, String> getContextQualifiers() {
		return Collections.unmodifiableMap(contextQualifiers);
	}

	public Map<String, String> getAgendaQualifiers() {
		return Collections.unmodifiableMap(agendaQualifiers);
	}

}
