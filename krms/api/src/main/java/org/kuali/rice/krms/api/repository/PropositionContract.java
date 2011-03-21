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
	 * This is the namespace code. 
	 *
	 * <p>
	 * It provides scope of the KRMS type.
	 * </p>
	 * @return the namespace code of the KRMS type.
	 */
	public String getTypeId();

	/**
	 * This is the Proposition KrmsType of the proposition.
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
	 * @see PropositionParameter
	 * @return the Parameters related to the proposition
	 */
	public List<? extends PropositionParameterContract> getParameters();
	
	// TODO: are these needed at this level or does compound proposition
	//       implement proposition
	//public List<Proposition> getCompoundComponents();
	//public String getCompoundOpCode();
}
