package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MultiValuedStringMapAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KewFrameworkServiceLocator.DOCUMENT_LOOKUP_CUSTOMIZATION_HANDLER_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentLookupCustomizationHandlerService {

    @WebMethod(operationName = "getDocumentLookupConfiguration")
	@WebResult(name = "documentLookupConfiguration")
	@XmlElement(name = "documentLookupConfiguration", required = false)
    DocumentLookupConfiguration getDocumentLookupConfiguration(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "searchableAttributeNames") List<String> searchableAttributeNames
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "validateSearchFieldParameters")
    @WebResult(name = "searchFieldErrors")
    @XmlElementWrapper(name = "searchFieldErrors", required = false)
    @XmlElement(name = "searchFieldError", required = false)
    List<RemotableAttributeError> validateSearchFieldParameters(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "searchableAttributeNames") List<String> searchableAttributeNames,
            @WebParam(name = "parameters")
            @XmlJavaTypeAdapter(MultiValuedStringMapAdapter.class) Map<String, List<String>> parameters
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "customizeCriteria")
    @WebResult(name = "documentLookupCriteria")
    @XmlElement(name = "documentLookupCriteria", required = false)
    DocumentLookupCriteria customizeCriteria(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "customizeClearCriteria")
    @WebResult(name = "documentLookupCriteria")
    @XmlElement(name = "documentLookupCriteria", required = false)
    DocumentLookupCriteria customizeClearCriteria(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "customizeResultSetFields")
    @WebResult(name = "resultSetFields")
    @XmlElementWrapper(name = "resultSetFields", required = false)
    @XmlElement(name = "resultSetField", required = false)
    List<RemotableAttributeField> customizeResultSetFields(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "defaultResultSetFields") List<RemotableAttributeField> defaultResultSetFields,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "customizeResults")
    @WebResult(name = "results")
    @XmlElementWrapper(name = "results", required = false)
    @XmlElement(name = "result", required = false)
    List<DocumentLookupResult> customizeResults(
            @WebParam(name = "documentLookupCriteria") DocumentLookupCriteria documentLookupCriteria,
            @WebParam(name = "defaultResults") List<DocumentLookupResult> defaultResults,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getEnabledCustomizations")
    @WebResult(name = "enabledCustomizations")
    @XmlElementWrapper(name = "enabledCustomizations", required = false)
    @XmlElement(name = "enabledCustomization", required = false)
    Set<DocumentLookupCustomization> getEnabledCustomizations(
            @WebParam(name = "documentTypeName") String documentTypeName,
            @WebParam(name = "customizerName") String customizerName
    ) throws RiceIllegalArgumentException;

}
