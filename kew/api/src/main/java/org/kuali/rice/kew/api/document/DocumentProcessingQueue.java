package org.kuali.rice.kew.api.document;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Defines the contract for a message queue that handles indexing of workflow documents.  The indexing process is
 * intended to look at the content associated with the workflow document and extra requested attributes for indexing
 * alongside the document.  These values can then be accessed and/or searched.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "documentProcessingQueueSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface DocumentProcessingQueue {

    @WebMethod(operationName = "process")
    void process(@WebParam(name = "documentId") String documentId) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "processWithOptions")
    void processWithOptions(@WebParam(name = "documentId") String documentId, @WebParam(name = "options") DocumentProcessingOptions options)
         throws RiceIllegalArgumentException;

}
