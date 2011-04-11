package org.kuali.rice.krms.api.repository;

import java.util.List;

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
	 * This is the id of the prior item in the tree of agenda items
	 * that make up the agenda. 
	 *
	 * <p>
	 * priorItemId - the id of the prior item in the tree structure of agenda items
	 * </p>
	 * @return the id of the prior item in the agenda item tree structure
	 */
	public String getPriorItemId();

	/**
	 * This code identifies the condition (the result of the previous agenda item)
	 * that led to choosing this item in the tree structure.
	 * TODO: more discussion here
	 * <p>
	 * The entry condition of the AgendaItem
	 * </p>
	 * @return the entry condition code of the AgendaItem
	 */
	public String getEntryCondition();

    /**
     * This is ID of the Rule associated with this AgendaItem.
     * @return ruleId
     */
	public String getRuleId();

	/**
	 * This is the KrmsType of the AgendaItem
	 *
	 * @return id for KRMS type related of the AgendaItem
	 */
	public String getSubAgendaId();
	
	public RuleContract getRule();
	public AgendaDefinitionContract getSubAgenda();
	public AgendaItemContract getNextTrue();
	public AgendaItemContract getNextFalse();
	public AgendaItemContract getNextAfter();

}
