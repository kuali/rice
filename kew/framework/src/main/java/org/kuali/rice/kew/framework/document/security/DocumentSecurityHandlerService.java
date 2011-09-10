package org.kuali.rice.kew.framework.document.security;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.jaxb.MultiValuedStringMapAdapter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCriteriaConfiguration;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomization;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultSetConfiguration;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultValues;

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
@WebService(name = KewFrameworkServiceLocator.DOCUMENT_SECURITY_HANDLER_SERVICE, targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentSecurityHandlerService {

    /**
     * TODO...
     *
     * @param principalId
     * @param documentSecurityDirectives
     *
     * @return
     *
     * @throws RiceIllegalArgumentException if the given principalId is a null or blank value
     * @throws RiceIllegalArgumentException if any of the security attributes defined in the given list of security
     * directives cannot be located or loaded
     */
    @WebMethod(operationName = "getAuthorizedDocumentIds")
    @WebResult(name = "authorizedDocumentIds")
    @XmlElementWrapper(name = "authorizedDocumentIds", required = false)
    @XmlElement(name = "documentId", required = false)
    List<String> getAuthorizedDocumentIds(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "documents") List<DocumentSecurityDirective> documentSecurityDirectives)
        throws RiceIllegalArgumentException;

}
