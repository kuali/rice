package org.kuali.rice.kew.framework.validation;

import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.api.validation.RuleValidationContext;
import org.kuali.rice.kew.api.validation.ValidationResults;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 *  Service responsible for exposing custom RuleValidationAttribute functionality.
 *  This service is exposed by the node hosting the specified custom attribute.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.RULE_VALIDATION_ATTRIBUTE_EXPORTER_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RuleValidationAttributeExporterService {
   /**
	 * Validates the rule within the given RuleValidationContext.
	 *
	 * @return a ValidationResults object representing the results of the validation, if this is
	 * empty or <code>null</code> this signifies that validation was successful.
	 */
   @WebMethod(operationName = "validate")
   @WebResult(name = "validationResults")
   @XmlElement(name = "validationResults", required = false)
	public ValidationResults validate(
                                @WebParam(name = "attributeName") String attributeName,
                                @WebParam(name = "validationContext") RuleValidationContext validationContext) throws Exception;
}