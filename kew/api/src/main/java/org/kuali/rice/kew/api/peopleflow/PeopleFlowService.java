package org.kuali.rice.kew.api.peopleflow;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.KewApiConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name = "peopleFlowServiceSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface PeopleFlowService {

    @WebMethod(operationName = "getPeopleFlow")
    @WebResult(name = "peopleFlow")
    PeopleFlowDefinition getPeopleFlow(@WebParam(name = "peopleFlowId") String peopleFlowId)
            throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getPeopleFlowByName")
    @WebResult(name = "peopleFlow")
    PeopleFlowDefinition getPeopleFlowByName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name)
        throws RiceIllegalArgumentException;



}
