package org.kuali.rice.krms.api.repository;

import java.util.Map;

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

	public ContextDefinition selectContext(Map<String, String> contextQualifiers);
	
}
