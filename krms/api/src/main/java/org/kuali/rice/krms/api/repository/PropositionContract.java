package org.kuali.rice.krms.api.repository;

import java.util.List;


//import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public interface PropositionContract {
	/**
	 * This is the ID for the KRMS proposition
	 *
	 * <p>
	 * It is the ID of a KRMS proposition.
	 * </p>
	 * @return ID for KRMS proposition.
	 */
	public String getPropId();

	/**
	 * This is the description text for the KRMS proposition
	 *
	 * <p>
	 * It is a description of the proposition
	 * </p>
	 * @return description for KRMS type.
	 */
	public String getDescription();

	/**
	 * This is the Proposition KrmsType of the proposition.
	 *
	 * <p>
	 * It provides scope of the KRMS type.
	 * </p>
	 * @return the id of the KRMS type.
	 */
	public String getTypeId();

	/**
	 * There are three main types of Propositions:
	 *   Compound Propositions - a proposition consisting of other propositions 
	 *   	and a boolean algebra operator (AND, OR) defining how to evaluate 
	 *   	those propositions.
     *   Parameterized Propositions - a proposition which is parameterized by 
     *      some set of values, evaluation logic is implemented by hand and 
     *      returns true or false
     *   Simple Propositions - a proposition of the form lhs op rhs where 
     *   	lhs=left-hand side, rhs=right-hand side, and op=operator
	 * 
	 * @return the proposition type code of the proposition
	 *      Valid values are C = compound, P = parameterized, S = simple
	 */
	public String getPropositionTypeCode();
	
	/**
	 * This is the parameter list of the proposition.
	 * Parameters are listed in Reverse Polish Notation.
	 * Parameters may be constants, terms, or functions.
	 * 
	 * Compound Propositions will have an empty parameter list.
	 *
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
	public List<? extends PropositionContract> getCompoundComponents();
}
