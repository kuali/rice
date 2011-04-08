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
	
}
