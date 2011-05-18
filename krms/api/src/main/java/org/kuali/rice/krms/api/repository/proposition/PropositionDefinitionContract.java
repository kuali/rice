package org.kuali.rice.krms.api.repository.proposition;

import java.util.List;

public interface PropositionDefinitionContract {
	/**
	 * This is the ID for the KRMS proposition
	 * @return ID for KRMS proposition.
	 */
	public String getPropId();

	/**
	 * This is the description text for the KRMS proposition
	 * @return description for KRMS type.
	 */
	public String getDescription();

	/**
	 * This is the id of Proposition KrmsType of the proposition.
	 * It provides some context to what type of object of the KRMS type.
	 * @return the id of the KRMS type.
	 */
	public String getTypeId();
	
	/**
	 * This returns the ID of the rule this proposition belongs to.  May be null if this proposition has 
	 * not yet been persisted.
	 * 
	 * @return the ID of the Rule this proposition belongs to.
	 */
	public String getRuleId();

	/**
	 * <p>
	 * There are three main types of Propositions:
	 *   Compound Propositions - a proposition consisting of other propositions 
	 *   	and a boolean algebra operator (AND, OR) defining how to evaluate 
	 *   	those propositions.
     *   Parameterized Propositions - a proposition which is parameterized by 
     *      some set of values, evaluation logic is implemented by hand and 
     *      returns true or false
     *   Simple Propositions - a proposition of the form lhs op rhs where 
     *   	lhs=left-hand side, rhs=right-hand side, and op=operator
	 * </p>
	 * @return the proposition type code of the proposition
	 * <p>
	 *      Valid values are C = compound, P = parameterized, S = simple
	 * </p>
	 */
	public String getPropositionTypeCode();
	
	/**
	 * This is the parameter list of the proposition.
	 * Parameters are listed in Reverse Polish Notation.
	 * Parameters may be constants, terms, or functions.
	 * <p>
	 * Compound Propositions will have an empty parameter list.
	 * </p>
	 * @see PropositionParameter
	 * @return the Parameters related to the proposition
	 */
	public List<? extends PropositionParameterContract> getParameters();
	
	/**
	 * This method returns the op code to be used when evaluating compound
	 * propositions. 
	 * 
	 * @return the compound op code. 
	 *    valid values are A = and, O = or
	 */
	public String getCompoundOpCode();

	/**
	 * 
	 * This method returns the propositions which are contained in a
	 * compound proposition.
	 * 
	 * @return
	 */
	public List<? extends PropositionDefinitionContract> getCompoundComponents();
}
