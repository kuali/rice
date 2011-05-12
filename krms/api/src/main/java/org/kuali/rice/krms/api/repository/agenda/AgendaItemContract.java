package org.kuali.rice.krms.api.repository.agenda;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.krms.api.repository.rule.RuleDefinitionContract;

public interface AgendaItemContract extends Identifiable {

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
