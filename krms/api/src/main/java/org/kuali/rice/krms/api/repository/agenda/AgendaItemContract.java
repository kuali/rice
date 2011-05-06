package org.kuali.rice.krms.api.repository.agenda;

import java.util.List;

import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;

public interface AgendaItemContract {
	/**
	 * This is the ID for the AgendaItem
	 *
	 * <p>
	 * It is a ID of a AgendaItem
	 * </p>
	 * @return ID for AgendaItem
	 */
	public String getId();

	/**
	 * This is the agenda ID for the Agenda associated with this Agenda Item
	 *
	 * <p>
	 * It is the agenda ID for the Agenda object associated with this Agenda Item.
	 * </p>
	 * @return ID for AgendaItem
	 */
	public String getAgendaId();

    /**
     * This is ID of the Rule associated with this AgendaItem.
     * @return ruleId
     */
	public String getRuleId();


	public String getSubAgendaId();
	public String getWhenTrueId();
	public String getWhenFalseId();
	public String getAlwaysId();
	
	public RuleDefinitionContract getRule();
	public AgendaDefinitionContract getSubAgenda();
	public AgendaItemContract getWhenTrue();
	public AgendaItemContract getWhenFalse();
	public AgendaItemContract getAlways();

}
