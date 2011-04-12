package org.kuali.rice.krms.api.engine;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class SelectionCriteria {

	private final Long effectiveExecutionTime;
	private final String eventName;
	private final Map<String, String> contextQualifiers;
	private final Map<String, String> agendaQualifiers;
	
	private SelectionCriteria(String eventName, Date effectiveDate) {
		if (eventName == null || "".equals(eventName.trim())) {
			throw new IllegalArgumentException("Event name must not be null or empty.");
		}
		this.eventName = eventName;
		
		if (effectiveDate != null) {
			this.effectiveExecutionTime = effectiveDate.getTime();
		} else {
			this.effectiveExecutionTime = null;
		}
		
		this.contextQualifiers = new HashMap<String, String>();
		this.agendaQualifiers = new HashMap<String, String>();
	}
	
	/**
	 * This static factory method creates a SelectionCriteria used to select an Agenda to execute.
	 * 
	 * @param eventName the event that is triggering rule execution.  Must not be null.
	 * @param effectiveExecutionTime the time that the rule is being executed at.  If null, the time of engine execution will be used.
	 * @param contextQualifiers qualifiers used to select the context
	 * @param agendaQualifiers qualifiers used to select the agenda from the context
	 * @return the {@link SelectionCriteria}
	 */
	public static SelectionCriteria createCriteria(String eventName, Date effectiveExecutionTime, Map<String, String> contextQualifiers, Map<String, String> agendaQualifiers) {
		SelectionCriteria criteria = new SelectionCriteria(eventName, effectiveExecutionTime);
		criteria.contextQualifiers.putAll(contextQualifiers);
		criteria.agendaQualifiers.putAll(agendaQualifiers);
		return criteria;
	}

	public String getEventName() {
		return eventName;
	}
	
	/**
	 * This method gets the effective date/time in epoch time, suitable for 
	 * converting to a {@link java.util.Date} via {@link java.util.Date#Date(long)}
	 * @return the epoch time for effective execution, or null 
	 * (which defers to the {@link Engine} but implies that the actual time when execution begins will be used).
	 */
	public Long getEffectiveExecutionTime() {
		return effectiveExecutionTime;
	}

	public Map<String, String> getContextQualifiers() {
		return Collections.unmodifiableMap(contextQualifiers);
	}

	public Map<String, String> getAgendaQualifiers() {
		return Collections.unmodifiableMap(agendaQualifiers);
	}

}
