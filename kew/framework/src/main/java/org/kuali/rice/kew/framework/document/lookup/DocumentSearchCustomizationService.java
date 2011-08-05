package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MultiValuedStringMapAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.springframework.beans.factory.InitializingBean;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.DOCUMENT_SEARCH_CUSTOMIZATION_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentSearchCustomizationService {

    @WebMethod(operationName = "getDocumentLookupConfiguration")
	@WebResult(name = "documentLookupConfiguration")
	@XmlElement(name = "documentLookupConfiguration", required = false)
    DocumentLookupConfiguration getDocumentLookupConfiguration(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "searchableAttributeNames") List<String> searchableAttributeNames);

    boolean isResultProcessingNeeded(String documentTypeName, String resultProcessorAttributeName);

    @WebMethod(operationName = "validateSearchFieldParameters")
    @WebResult(name = "validateSearchFieldParameters")
    @XmlElementWrapper(name = "searchFieldErrors", required = false)
    @XmlElement(name = "searchFieldError", required = false)
    List<RemotableAttributeError> validateSearchFieldParameters(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "searchableAttributeNames") List<String> searchableAttributeNames,
            @WebParam(name = "parameters")
            @XmlJavaTypeAdapter(MultiValuedStringMapAdapter.class) Map<String, List<String>> parameters
    );

}
