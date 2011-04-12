package org.kuali.rice.krms.api.repository;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * The rule repository contains all of the information about context definitions,
 * agendas, and business rules.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = "ruleRepositoryService", targetNamespace = RepositoryConstants.Namespaces.KRMS_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RuleRepositoryService {

	/**
	 * Locates a ContextDefinition based on the given map of context qualifiers.
	 * 
	 * @param contextQualifiers
	 * @return
	 */
	@WebMethod(operationName = "selectContext")
	@WebResult(name = "contextDefinition")
	public ContextDefinition selectContext(@WebParam(name = "contextSelectionCriteria") ContextSelectionCriteria contextSelectionCriteria);
	
	/**
	 * Loads the agenda tree for the given agendaId.  The agenda tree includes
	 * the entire agenda definition in the appropriate order and with the
	 * defined agenda branching.
	 * 
	 * @param agendaId the id of the agenda for which to load the agenda tree
	 * @return the agenda tree, or null if no agenda could be located for the given agendaId
	 * 
	 * @throws IllegalArgumentException if the given agendaId is null
	 */
	@WebMethod(operationName = "getAgendaTree")
	@WebResult(name = "agendaTree")
	public AgendaTreeDefinition getAgendaTree(@WebParam(name = "agendaId") String agendaId);
	
}
